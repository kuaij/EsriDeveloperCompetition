package com.xiaok.winterolympic.view.central;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RideStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.esri.arcgisruntime.geometry.Point;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.NaviResultAdapter;
import com.xiaok.winterolympic.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener,
        AdapterView.OnItemClickListener {

    private MapView mapView_navi;
    private AMap aMap;
    private SearchView sv_start_position;
    private SearchView sv_end_position;
    private Button btn_plan_way;

    private String startPosition = "我的位置";
    private String endPosition;

    private Point startPoint;
    private Point endPoint;

    private ListView mInputListView;
    private List<Tip> mCurrentTipList;
    private NaviResultAdapter mIntipAdapter;

    private RouteSearch routeSearch;

    public static String DEFAULT_CITY = "北京";
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int REQUEST_SUC = 1000;


    public NavigationActivity(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mapView_navi = findViewById(R.id.navigation_mapview);
        sv_start_position = findViewById(R.id.navi_sv_start);
        sv_end_position = findViewById(R.id.navi_sv_end);
        btn_plan_way = findViewById(R.id.navi_plan_way);


        Intent mainIntent = getIntent();
        String endPoitionStr = mainIntent.getStringExtra("venueName");  //导航至场馆时
        if (endPoitionStr != null){
            sv_end_position.setQuery(endPoitionStr,false); //设置场馆
        }
        double longitude = mainIntent.getDoubleExtra("x",116.38376139624);
        double latitude = mainIntent.getDoubleExtra("y",39.997936144);
        startPoint = new Point(longitude,latitude); //获取我当前位置的经纬坐标

        endPoint = new Point(116.327421,39.940024); //初始化终点坐标

        //标题栏和返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.central_navigation));
        }

        initSearchView(); //初始化起终点SearchView

        mInputListView = findViewById(R.id.inputtip_list);
        mInputListView.setOnItemClickListener(this);

        mapView_navi.onCreate(savedInstanceState);
        //获取地图控制类
        if (aMap == null){
            aMap = mapView_navi.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，
        //初始化以我的位置地图中心及缩放比
        LatLng initLatLng = new LatLng(latitude,longitude);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(initLatLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        //初始化路径规划
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        //配置驾车路线参数
        com.amap.api.services.core.LatLonPoint startLatLng = new LatLonPoint(startPoint.getY(),startPoint.getX());
        com.amap.api.services.core.LatLonPoint endLatLng = new LatLonPoint(endPoint.getY(),endPoint.getX());
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startLatLng,endLatLng);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,0,null,null,"");


        //规划路线按钮
        btn_plan_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (query != null){
                    routeSearch.calculateDriveRouteAsyn(query);
                }

            }
        });

    }

    private void initSearchView(){
        //设置SearchView默认为展开显示
        sv_start_position.setIconified(false);
        sv_start_position.onActionViewExpanded();
        sv_start_position.setIconifiedByDefault(true);
        sv_start_position.setSubmitButtonEnabled(false);
        sv_start_position.setQuery("我的位置", false);
        sv_start_position.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                    InputtipsQuery inputquery = new InputtipsQuery(s, DEFAULT_CITY);
                    Inputtips inputTips = new Inputtips(NavigationActivity.this, inputquery);
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> list, int i) {
                            // 正确返回
                            if (i == REQUEST_SUC) {
                                mCurrentTipList = list;
                                mIntipAdapter = new NaviResultAdapter(getApplicationContext(), mCurrentTipList);
                                mInputListView.setAdapter(mIntipAdapter);
                                mIntipAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(NavigationActivity.this, "错误码 :" + i, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                } else {
                    // 如果输入为空  则清除 listView 数据
                    if (mIntipAdapter != null && mCurrentTipList != null) {
                        mCurrentTipList.clear();
                        mIntipAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });

        //设置SearchView默认为展开显示
        sv_end_position.setIconified(false);
        sv_end_position.onActionViewExpanded();
        sv_end_position.setIconifiedByDefault(false);
        sv_end_position.setSubmitButtonEnabled(false);
        sv_end_position.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                    InputtipsQuery inputquery = new InputtipsQuery(s, DEFAULT_CITY);
                    Inputtips inputTips = new Inputtips(NavigationActivity.this, inputquery);
                    inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                        @Override
                        public void onGetInputtips(List<Tip> list, int i) {
                            // 正确返回
                            if (i == REQUEST_SUC) {
                                mCurrentTipList = list;
                                mIntipAdapter = new NaviResultAdapter(getApplicationContext(), mCurrentTipList);
                                mInputListView.setAdapter(mIntipAdapter);
                                mIntipAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(NavigationActivity.this, "错误码 :" + i, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    inputTips.requestInputtipsAsyn();
                } else {
                    // 如果输入为空  则清除 listView 数据
                    if (mIntipAdapter != null && mCurrentTipList != null) {
                        mCurrentTipList.clear();
                        mIntipAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        if (mapView_navi != null){
            mapView_navi.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView_navi != null){
            mapView_navi.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView_navi != null){
            mapView_navi.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView_navi != null){
            mapView_navi.onSaveInstanceState(outState);
        }
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view.getId() == R.id.navi_sv_end )
            Logger.e("触发");
//        if (mCurrentTipList != null) {
//            Tip tip = (Tip) parent.getItemAtPosition(position);
//            Intent intent = new Intent();
//            intent.putExtra("tip", tip);
//            setResult(RESULT_CODE_INPUTTIPS, intent);
//        }
        Tip tip = (Tip) parent.getItemAtPosition(position);
        sv_end_position.setQuery(tip.getName(),false);
        sv_end_position.clearFocus(); //清除搜索框的焦点
        btn_plan_way.setEnabled(true); //选中终点后允许进行规划路线和导航
        mInputListView.setVisibility(View.GONE); //选中后隐藏ListView
    }



    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        Logger.e("CF", "onBusRouteSearched: " + i);

        aMap.clear();
        //几种公交路线
        List<BusPath> busPathList = busRouteResult.getPaths();
        //选择第一条
        List<BusStep> busSteps = busPathList.get(0).getSteps();

        for (BusStep bs : busSteps) {
            //获取该条路线某段公交路程步行的点
            RouteBusWalkItem routeBusWalkItem = bs.getWalk();
            if(routeBusWalkItem != null){
                List<WalkStep> wsList = routeBusWalkItem.getSteps();
                ArrayList<LatLng> walkPoint = new ArrayList<>();

                for (WalkStep ws :wsList){
                    List<LatLonPoint> points = ws.getPolyline();
                    for (LatLonPoint lp : points){
                        walkPoint.add(new LatLng(lp.getLatitude(),lp.getLongitude()));
                    }
                }
                //添加步行点
                aMap.addPolyline(new PolylineOptions()
                        .addAll(walkPoint)
                        .width(40)
                        //是否开启纹理贴图
                        .setUseTexture(true)
                        //绘制成大地线
                        .geodesic(false)
                        //设置画线的颜色
                        .color(Color.argb(200, 0, 0, 0)));
            }

            //获取该条路线某段公交路路程的点

            List<RouteBusLineItem> rbli = bs.getBusLines();
            ArrayList<LatLng> busPoint = new ArrayList<>();


            for (RouteBusLineItem one: rbli){

                List<LatLonPoint> points = one.getPolyline();

                for (LatLonPoint lp : points){
                    busPoint.add(new LatLng(lp.getLatitude(),lp.getLongitude()));
                }
            }
            //添加公交路线点
            aMap.addPolyline(new PolylineOptions()
                    .addAll(busPoint)
                    .width(40)
                    //是否开启纹理贴图
                    .setUseTexture(true)
                    //绘制成大地线
                    .geodesic(false)
                    //设置画线的颜色
                    .color(Color.argb(200, 0, 0, 0)));
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

        Logger.e("CF", "onDriveRouteSearched: " + i);

        List<DrivePath> pathList = driveRouteResult.getPaths();
        List<LatLng> driverPath = new ArrayList<>();

        for (DrivePath dp : pathList) {

            List<DriveStep> stepList = dp.getSteps();
            for (DriveStep ds : stepList) {

                List<LatLonPoint> points = ds.getPolyline();
                for (LatLonPoint llp : points) {
                    driverPath.add(new LatLng(llp.getLatitude(), llp.getLongitude()));
                }
            }
        }

        aMap.clear();
        aMap.addPolyline(new PolylineOptions()
                .addAll(driverPath)
                .width(40)
                //是否开启纹理贴图
                .setUseTexture(true)
                //绘制成大地线
                .geodesic(false)
                //设置纹理样式
                .setCustomTexture(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.navi_blue_line)))
                //设置画线的颜色
                .color(Color.argb(200, 0, 0, 0)));

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        List<WalkPath> pathList = walkRouteResult.getPaths();
        List<LatLng> walkPaths = new ArrayList<>();

        for (WalkPath dp : pathList) {

            List<WalkStep> stepList = dp.getSteps();
            for (WalkStep ds : stepList) {


                List<LatLonPoint> points = ds.getPolyline();
                for (LatLonPoint llp : points) {
                    walkPaths.add(new LatLng(llp.getLatitude(), llp.getLongitude()));
                }
            }
        }

        aMap.clear();
        aMap.addPolyline(new PolylineOptions()
                .addAll(walkPaths)
                .width(40)
                //是否开启纹理贴图
                .setUseTexture(true)
                //绘制成大地线
                .geodesic(false)
                //设置画线的颜色
                .color(Color.argb(200, 0, 0, 0)));

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        List<RidePath> pathList = rideRouteResult.getPaths();
        List<LatLng> walkPaths = new ArrayList<>();

        for (RidePath dp : pathList) {

            List<RideStep> stepList = dp.getSteps();
            for (RideStep ds : stepList) {
                List<LatLonPoint> points = ds.getPolyline();
                for (LatLonPoint llp : points) {
                    walkPaths.add(new LatLng(llp.getLatitude(), llp.getLongitude()));
                }
            }
        }

        aMap.clear();
        aMap.addPolyline(new PolylineOptions()
                .addAll(walkPaths)
                .width(40)
                //是否开启纹理贴图
                .setUseTexture(true)
                //绘制成大地线
                .geodesic(false)
                //设置画线的颜色
                .color(Color.argb(200, 0, 0, 0)));

    }
}
