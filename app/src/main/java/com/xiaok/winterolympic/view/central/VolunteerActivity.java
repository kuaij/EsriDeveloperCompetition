package com.xiaok.winterolympic.view.central;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.CoordinateUtils;
import com.xiaok.winterolympic.utils.DateSyncUtils;
import com.xiaok.winterolympic.utils.FileUtils;
import com.xiaok.winterolympic.utils.LayerUtils;
import com.xiaok.winterolympic.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xiaok.winterolympic.utils.LayerUtils.CENTRAL_POSITION_LATITUDE;
import static com.xiaok.winterolympic.utils.LayerUtils.CENTRAL_POSITION_LONGITUDE;

public class VolunteerActivity extends AppCompatActivity {

    private Handler mUiHandler = new Handler();

    private static final double INIT_SCALE = 4000000f;

    private List<FloatingActionMenu> menus = new ArrayList<>();
    private FloatingActionMenu menu_blue;
    private FloatingActionButton fab_compass;
    private FloatingActionButton fab_full_screen;
    private FloatingActionButton fab_location;
    private MapView mMapView;

    private static final String VOLUNTEER_ID = "志愿者编号：";
    private static final String VOLUNTEER_PHONE_NUM = "志愿者联系方式：";
    private static final String VOLUNTEER_CURRENT_STATE = "当前状态：";
    private static final String VOLUNTEER_STATE_FREE = "空闲中...";
    private static final String VOLUNTEER_STATE_BUSY = "引导其他游客中...";

    private Point centralPoint;
    private String volunteerID = "000001";  //志愿者编号
    private String phoneNum; //志愿者电话号码
    private String volunteerState; //志愿者当前状态


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        initView(); //初始化控件
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.central_volunteer));
        }

        menu_blue.setIconAnimated(false);
        menu_blue.hideMenuButton(false);
        menus.add(menu_blue);
        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }

        mMapView = findViewById(R.id.volunteer_mapview);
        LayerUtils.addTDT(mMapView); //设置底图为天地图
        mMapView.setAttributionTextVisible(false); //消除Esri Logo
        centralPoint = new Point(CENTRAL_POSITION_LONGITUDE, CENTRAL_POSITION_LATITUDE);
        mMapView.setViewpointCenterAsync(centralPoint, INIT_SCALE);
        loadInitPositionLayer();

        mMapView.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                Logger.e("Scale changed"+"\n"+"当前Scale为："+mapScaleChangedEvent.getSource().getMapScale());
            }
        });

        //添加触摸事件监听
        mMapView.setOnTouchListener(new VolunteerTouchListener(VolunteerActivity.this, mMapView));


        //重置页面旋转角度
        fab_compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.setViewpointRotationAsync(0f);
            }
        });

        //重置地图中心和缩放比
        fab_full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.setViewpointCenterAsync(centralPoint, INIT_SCALE);
            }
        });

        //定位
        fab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showSingleToast("正在定位中...");
            }
        });

    }

    private void loadInitPositionLayer(){
        String geodatabasePath = FileUtils.VOLUNTEER_POSITION;
        final Geodatabase geodatabase = new Geodatabase(geodatabasePath);
        geodatabase.loadAsync();
        // 当geodatabase读取成功后将geodatabase加载到数据库
        geodatabase.addDoneLoadingListener(() -> {
            if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {

                List<GeodatabaseFeatureTable> geodatabaseFeatureTables = geodatabase.getGeodatabaseFeatureTables();
                for (int i=geodatabaseFeatureTables.size()-1;i>=0;i--){
                    GeodatabaseFeatureTable geodatabaseFeatureTable = geodatabaseFeatureTables.get(i);
                    geodatabaseFeatureTable.loadAsync();
                    //创建要素图层
                    final FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);
                    // 添加到地图
                    mMapView.getMap().getOperationalLayers().add(featureLayer);
                }


            } else {
                Toast.makeText(VolunteerActivity.this, "Geodatabase failed to load!", Toast.LENGTH_LONG).show();
                Logger.e("Geodatabase failed to load!");
            }
        });
    }


    private void initView(){
        menu_blue = findViewById(R.id.menu_blue);
        fab_compass = findViewById(R.id.fab_compass);
        fab_full_screen = findViewById(R.id.fab_full_screen);
        fab_location = findViewById(R.id.fab_location);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.resume();
    }

    //左上角返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //地图触碰事件监听类
    private class VolunteerTouchListener extends DefaultMapViewOnTouchListener{

        public VolunteerTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Point currentPoint = CoordinateUtils.screnToMapPoint(mMapView, (int) e.getX(), (int) e.getY());
            double currentX = currentPoint.getX(); //当前用户点击的的精度
            double currentY = currentPoint.getY(); //当前用户点击的纬度
            //用户单击项目名后显示的数据集
            final List<String> mResultAttributes = new ArrayList<>();

            /*
            *接下来是志愿者点的单击响应，用户点击经纬容差为0.0002
             */

            if (currentX >= 116.38925 && currentX <= 116.38965 && currentY >= 39.9935 && currentY <= 39.9939){
                //一号志愿者（116.38945,39.9937）
                volunteerID = "00001";
                phoneNum = "18790875251";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.388727 && currentX <= 116.389127 && currentY >= 39.990443 && currentY <= 39.990843){
                //二号志愿者（116.388927,39.990643）
                volunteerID = "00002";
                phoneNum = "15563428546";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.391479 && currentX <= 116.391879 && currentY >= 39.99024 && currentY <= 39.99064){
                //三号志愿者（116.391679,39.99044）
                volunteerID = "00003";
                phoneNum = "18218110303";
                volunteerState = VOLUNTEER_STATE_BUSY;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.384932 && currentX <= 116.385332 && currentY >= 39.990467 && currentY <= 39.990867){
                //四号志愿者（116.385132, 39.990667）
                volunteerID = "00004";
                phoneNum = "17839975780";
                volunteerState = VOLUNTEER_STATE_BUSY;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.383077 && currentX <= 116.383477 && currentY >= 39.992572 && currentY <= 39.992972){
                //五号志愿者（116.383277， 39.992772）
                volunteerID = "00005";
                phoneNum = "16548975213";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.383623 && currentX <= 116.384023 && currentY >= 39.099182 && currentY <= 39.099582){
                //六号志愿者（116.383823，39.099382）
                volunteerID = "00006";
                phoneNum = "15226104996";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.384625 && currentX <= 116.385025 && currentY >= 39.994589 && currentY <= 39.994989){
                //七号志愿者（116.384825,39.994789）
                volunteerID = "00007";
                phoneNum = "15993837198";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.383064 && currentX <= 116.383464 && currentY >= 39.994973 && currentY <= 39.995373){
                //八号志愿者（116.383264， 39.995173）
                volunteerID = "00008";
                phoneNum = "17896541212";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.391447 && currentX <= 116.391847 && currentY >= 39.992601 && currentY <= 39.993001){
                //三十四号志愿者（116.391647， 39.992801）
                volunteerID = "00034";
                phoneNum = "15684679214";
                volunteerState = VOLUNTEER_STATE_BUSY;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else if (currentX >= 116.384351 && currentX <= 116.384751 && currentY >= 39.99602 && currentY <= 39.99642){
                //三十五号志愿者（116.384551， 39.99622）
                volunteerID = "00035";
                phoneNum = "15846798254";
                volunteerState = VOLUNTEER_STATE_FREE;
                mResultAttributes.add(VOLUNTEER_ID+volunteerID);
                mResultAttributes.add(VOLUNTEER_PHONE_NUM+phoneNum);
                mResultAttributes.add(VOLUNTEER_CURRENT_STATE+volunteerState);
                showVolunteerInfo(mResultAttributes);
            }else {
                //其他点暂不处理
            }


            return super.onSingleTapConfirmed(e);
        }
    }

    //志愿者信息弹出框
    private void showVolunteerInfo(List<String> mResultAttributes){
        new MaterialDialog.Builder(VolunteerActivity.this)
                .title("志愿者信息")
                .canceledOnTouchOutside(false)
                .items(mResultAttributes)
                .positiveText("确定")
                .negativeText("联系TA")
                .neutralText("导航到TA的位置")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.NEUTRAL) {
                            //导航到志愿者身边
                            if (volunteerState.equals(VOLUNTEER_STATE_FREE)){
                                Intent naviIntent = new Intent(VolunteerActivity.this, NavigationActivity.class);
                                naviIntent.putExtra("activityID",DateSyncUtils.VOLUNTEER_ACTIVITY_ID);
                                naviIntent.putExtra("volunteerID", volunteerID);
                                startActivity(naviIntent);
                            }else {
                                confimrmChoice(2);
                            }

                        } else if (which == DialogAction.POSITIVE) {
                            dialog.dismiss();
                        } else if (which == DialogAction.NEGATIVE) {
                            if (volunteerState.equals(VOLUNTEER_STATE_FREE)){
                                //给志愿者打电话
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+phoneNum));
                                startActivity(intent);
                            }else {
                                confimrmChoice(1);
                            }

                        }

                    }
                })
                .show();
    }


    //志愿者繁忙时请求框
    private void confimrmChoice(int choice){
        new MaterialDialog.Builder(VolunteerActivity.this)
                .title("项目信息")
                .canceledOnTouchOutside(false)
                .content("该志愿者当前正在引导其他游客，可能无法及时为您提供帮助，建议您向其他志愿者寻求帮助。")
                .positiveText("联系其他志愿者")
                .negativeText("仍然联系TA")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            //联系其他志愿者
                            dialog.dismiss();
                        } else if (which == DialogAction.NEGATIVE) {
                            switch (choice){
                                case 1:
                                    //给志愿者打电话
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:"+phoneNum));
                                    startActivity(intent);
                                    break;

                                case 2:
                                    Intent naviIntent = new Intent(VolunteerActivity.this, NavigationActivity.class);
                                    naviIntent.putExtra("activityID",DateSyncUtils.VOLUNTEER_ACTIVITY_ID);
                                    naviIntent.putExtra("volunteerID", volunteerID);
                                    startActivity(naviIntent);
                                    break;
                                default:break;
                            }

                        }
                    }
                })
                .show();
    }
    
}
