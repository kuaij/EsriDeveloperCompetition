package com.xiaok.winterolympic.view.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
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
import com.xiaok.winterolympic.view.central.IndoorMapActivity;
import com.xiaok.winterolympic.view.central.NavigationActivity;
import com.xiaok.winterolympic.view.central.SceneComplaceActivity;
import com.xiaok.winterolympic.view.hide.SearchComplaceActivity;

import java.util.ArrayList;
import java.util.List;

import static com.xiaok.winterolympic.utils.LayerUtils.CENTRAL_POSITION_LATITUDE;
import static com.xiaok.winterolympic.utils.LayerUtils.CENTRAL_POSITION_LONGITUDE;
import static com.xiaok.winterolympic.utils.LayerUtils.INIT_SCALE;

public class MainPageFragment extends Fragment {


    private static final String EVENT_NAME = "项目名：";
    private static final String HOLD_COMPLACE = "举办场馆：";
    private static final String EVENT_INFO = "项目介绍：";
    private static final String VENUE_NAME = "场馆名：";
    private static final String VENUE_POSITION = "场馆位置：";
    private static final String HOLD_PEOPLE = "可容纳人数：";
    private static final String VENUE_INFO = "场馆介绍：";

    private FeatureLayer skiJumpingLayer; //跳台滑雪
    private FeatureLayer biathlonLayer; //冬季两项
    private FeatureLayer nordicCombinedLayer; //北欧两项
    private FeatureLayer nordicCombinedLayer2; //北欧两项2
    private FeatureLayer crossSkatingLayer; //越野滑雪
    private FeatureLayer freeStyleingLayer; //自由式滑雪
    private FeatureLayer freeStyleingLayer2; //自由式滑雪2
    private FeatureLayer snowboardingLayer; //单板滑雪
    private FeatureLayer snowboardingLayer2; //单板滑雪2
    private FeatureLayer alpineSkiingLayer; //高山滑雪
    private FeatureLayer lugeLayer; //无舵雪橇
    private FeatureLayer skeletonLayer; //俯式冰橇/雪车
    private FeatureLayer bobsleiqnLayer; //有舵雪橇
    private FeatureLayer shortTrackLayer; //短道速滑
    private FeatureLayer figureSkatingLayer; //花样滑冰
    private FeatureLayer iceHockeyLayer; //男子冰球
    private FeatureLayer womanIceHockeyLayer; //女子冰球
    private FeatureLayer curlingLayer; //冰壶
    private FeatureLayer speedSkatingLayer; //速度滑冰

    private int event_name_id;

    private MapView mv_main_page;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private FloatingActionMenu menu_blue;
    private FloatingActionButton fab_compass;
    private FloatingActionButton fab_full_screen;
    private FloatingActionButton fab_location;


    private double currentScale = 5900000f;

    private Handler mUiHandler = new Handler();
    private String path;






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_page,container,false);
        mv_main_page = view.findViewById(R.id.mv_main_page);
        menu_blue = view.findViewById(R.id.menu_blue);
        fab_compass = view.findViewById(R.id.fab_compass);
        fab_full_screen = view.findViewById(R.id.fab_full_screen);
        fab_location = view.findViewById(R.id.fab_location);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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




        //消除水印
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");

        /*
        加载地图底图，自定义图层
         */
        LayerUtils.addTDT(mv_main_page); //加载天地图底图
        loadGeodatabase(); //加载赛区图层
        loadComplaceLayer(); //加载场馆图层
        loadProgramIcon(); //加载场馆周围项目图层
        Point centralPoint = new Point(CENTRAL_POSITION_LONGITUDE, CENTRAL_POSITION_LATITUDE);
        mv_main_page.setViewpointCenterAsync(centralPoint, INIT_SCALE);
        mv_main_page.setAttributionTextVisible(false); //去除Esri logo

        mv_main_page.setOnTouchListener(new MyMapTouchListener(getContext(),mv_main_page));

        mv_main_page.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                Logger.e("Scale changed"+"\n"+"当前Scale为："+mapScaleChangedEvent.getSource().getMapScale());
                currentScale = mapScaleChangedEvent.getSource().getMapScale();
            }
        });



        //重置页面旋转角度
        fab_compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv_main_page.setViewpointRotationAsync(0f);
            }
        });

        //重置地图中心和缩放比
        fab_full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv_main_page.setViewpointCenterAsync(centralPoint, 5900000f);
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


    // 加载赛区图层
    private void loadGeodatabase() {
        // 本地geodatabase文件路径
        String path = FileUtils.MAIN_PAGE_DATEBASE;
        // 创建geodatabase
        final Geodatabase geodatabase = new Geodatabase(path);
        // 异步加载geodatabase
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
                    mv_main_page.getMap().getOperationalLayers().add(featureLayer);
                }


            } else {
                Toast.makeText(getContext(), "赛区图层加载失败", Toast.LENGTH_LONG).show();
                Logger.e("赛区图层加载失败");
            }
        });
    }

    //加载场馆图层
    private void loadComplaceLayer(){
        // 本地geodatabase文件路径
        String path = FileUtils.MAIN_PAGE_COMPLACE;
        // 创建geodatabase
        final Geodatabase geodatabase = new Geodatabase(path);
        // 异步加载geodatabase
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
                    mv_main_page.getMap().getOperationalLayers().add(featureLayer);
                }


            } else {
                Toast.makeText(getContext(), "场馆图层加载失败", Toast.LENGTH_LONG).show();
                Logger.e("场馆图层加载失败");
            }
        });
    }

    //加载高放大模式下场馆周围的项目图层
    private void loadProgramIcon(){
        String path = FileUtils.MAIN_PAGE_LARGE_COMPLACE;
        // 创建geodatabase
        final Geodatabase geodatabase = new Geodatabase(path);
        // 异步加载geodatabase
        geodatabase.loadAsync();
        // 当geodatabase读取成功后将geodatabase加载到数据库
        geodatabase.addDoneLoadingListener(() -> {
            if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                //创建数据表
                GeodatabaseFeatureTable skiJumpingTable = geodatabase.getGeodatabaseFeatureTable("跳台滑雪");
                GeodatabaseFeatureTable biathlonTable = geodatabase.getGeodatabaseFeatureTable("冬季两项");
                GeodatabaseFeatureTable nordicCombinedTable = geodatabase.getGeodatabaseFeatureTable("北欧两项");
                GeodatabaseFeatureTable nordicCombinedTable2 = geodatabase.getGeodatabaseFeatureTable("北欧两项2");
                GeodatabaseFeatureTable crossSkatingTable = geodatabase.getGeodatabaseFeatureTable("越野滑雪");
                GeodatabaseFeatureTable freeStyleingTable = geodatabase.getGeodatabaseFeatureTable("自由式滑雪");
                GeodatabaseFeatureTable freeStyleingTable2 = geodatabase.getGeodatabaseFeatureTable("自由式滑雪2");
                GeodatabaseFeatureTable snowboardingTable = geodatabase.getGeodatabaseFeatureTable("单板滑雪");
                GeodatabaseFeatureTable snowboardingTable2 = geodatabase.getGeodatabaseFeatureTable("单板滑雪2");
                GeodatabaseFeatureTable alpineSkiingTable = geodatabase.getGeodatabaseFeatureTable("高山滑雪");
                GeodatabaseFeatureTable lugeTable = geodatabase.getGeodatabaseFeatureTable("无舵雪橇");
                GeodatabaseFeatureTable skeletonTable = geodatabase.getGeodatabaseFeatureTable("雪车");
                GeodatabaseFeatureTable bobsleiquTable = geodatabase.getGeodatabaseFeatureTable("有舵雪橇");
                GeodatabaseFeatureTable shortTrackTable = geodatabase.getGeodatabaseFeatureTable("短道速滑");
                GeodatabaseFeatureTable figureSkatingTable = geodatabase.getGeodatabaseFeatureTable("花样滑冰");
                GeodatabaseFeatureTable manIceHockeyTable = geodatabase.getGeodatabaseFeatureTable("男子冰球");
                GeodatabaseFeatureTable womanIceHockeyTable = geodatabase.getGeodatabaseFeatureTable("女子冰球");
                GeodatabaseFeatureTable curlingTable = geodatabase.getGeodatabaseFeatureTable("冰壶");
                GeodatabaseFeatureTable speedSkatingTable = geodatabase.getGeodatabaseFeatureTable("速度滑冰");

                //创建图层
                skiJumpingLayer = new FeatureLayer(skiJumpingTable);
                biathlonLayer = new FeatureLayer(biathlonTable);
                nordicCombinedLayer = new FeatureLayer(nordicCombinedTable);
                nordicCombinedLayer2 = new FeatureLayer(nordicCombinedTable2);
                crossSkatingLayer = new FeatureLayer(crossSkatingTable);
                freeStyleingLayer = new FeatureLayer(freeStyleingTable);
                freeStyleingLayer2 = new FeatureLayer(freeStyleingTable2);
                snowboardingLayer = new FeatureLayer(snowboardingTable);
                snowboardingLayer2 = new FeatureLayer(snowboardingTable2);
                alpineSkiingLayer = new FeatureLayer(alpineSkiingTable);
                lugeLayer = new FeatureLayer(lugeTable);
                skeletonLayer = new FeatureLayer(skeletonTable);
                bobsleiqnLayer = new FeatureLayer(bobsleiquTable);
                shortTrackLayer = new FeatureLayer(shortTrackTable);
                figureSkatingLayer = new FeatureLayer(figureSkatingTable);
                iceHockeyLayer = new FeatureLayer(manIceHockeyTable);
                womanIceHockeyLayer = new FeatureLayer(womanIceHockeyTable);
                curlingLayer = new FeatureLayer(curlingTable);
                speedSkatingLayer = new FeatureLayer(speedSkatingTable);

                //添加图层
                mv_main_page.getMap().getOperationalLayers().add(skiJumpingLayer);
                mv_main_page.getMap().getOperationalLayers().add(biathlonLayer);
                mv_main_page.getMap().getOperationalLayers().add(nordicCombinedLayer);
                mv_main_page.getMap().getOperationalLayers().add(nordicCombinedLayer2);
                mv_main_page.getMap().getOperationalLayers().add(crossSkatingLayer);
                mv_main_page.getMap().getOperationalLayers().add(freeStyleingLayer);
                mv_main_page.getMap().getOperationalLayers().add(freeStyleingLayer2);
                mv_main_page.getMap().getOperationalLayers().add(snowboardingLayer);
                mv_main_page.getMap().getOperationalLayers().add(snowboardingLayer2);
                mv_main_page.getMap().getOperationalLayers().add(alpineSkiingLayer);
                mv_main_page.getMap().getOperationalLayers().add(lugeLayer);
                mv_main_page.getMap().getOperationalLayers().add(skeletonLayer);
                mv_main_page.getMap().getOperationalLayers().add(bobsleiqnLayer);
                mv_main_page.getMap().getOperationalLayers().add(shortTrackLayer);
                mv_main_page.getMap().getOperationalLayers().add(figureSkatingLayer);
                mv_main_page.getMap().getOperationalLayers().add(iceHockeyLayer);
                mv_main_page.getMap().getOperationalLayers().add(womanIceHockeyLayer);
                mv_main_page.getMap().getOperationalLayers().add(curlingLayer);
                mv_main_page.getMap().getOperationalLayers().add(speedSkatingLayer);

                //初始对所有场馆周围项目图标进行隐藏
                skiJumpingLayer.setVisible(false);
                biathlonLayer.setVisible(false);
                nordicCombinedLayer.setVisible(false);
                nordicCombinedLayer2.setVisible(false);
                crossSkatingLayer.setVisible(false);
                freeStyleingLayer.setVisible(false);
                freeStyleingLayer2.setVisible(false);
                snowboardingLayer.setVisible(false);
                snowboardingLayer2.setVisible(false);
                alpineSkiingLayer.setVisible(false);
                lugeLayer.setVisible(false);
                skeletonLayer.setVisible(false);
                bobsleiqnLayer.setVisible(false);
                shortTrackLayer.setVisible(false);
                figureSkatingLayer.setVisible(false);
                iceHockeyLayer.setVisible(false);
                womanIceHockeyLayer.setVisible(false);
                curlingLayer.setVisible(false);
                speedSkatingLayer.setVisible(false);


            } else {
                Toast.makeText(getContext(), "场馆图层加载失败", Toast.LENGTH_LONG).show();
                Logger.e("场馆图层加载失败");
            }
        });
    }



    private void changeLayerVisibity(FeatureLayer featureLayer){
        if (!featureLayer.isVisible()){
            featureLayer.setVisible(true);
        }else {
            featureLayer.setVisible(false);
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mv_main_page != null){
            mv_main_page.dispose();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mv_main_page != null){
            mv_main_page.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mv_main_page != null){
            mv_main_page.resume();
        }
    }

    private class MyMapTouchListener extends DefaultMapViewOnTouchListener{

        public MyMapTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Point currentPoint = CoordinateUtils.screnToMapPoint(mv_main_page, (int) e.getX(), (int) e.getY());
            double currentX = currentPoint.getX(); //当前用户点击的的精度
            double currentY = currentPoint.getY(); //当前用户点击的纬度
            //用户单击项目名后显示的数据集
            final List<String> mResultAttributes = new ArrayList<>();

            /*
            用户点击误差经纬方向均为0.2度，约合10km
             */
            //场馆点击响应，注意限定地图放缩比为400W及以上
            if (currentX >= 116.2128 && currentX <= 116.6128 && currentY >= 39.7114 && currentY <= 40.1114 && currentScale >= 4000000){
                //点击了北京赛区
                Logger.e("点击了北京赛区");
                Point beijing = new Point(116.4128, 39.9114);
                mv_main_page.setViewpointCenterAsync(beijing, 1600000f);
                loadComplaceLayer(); //放大后加载场馆图层
            }else if (currentX >= 115.9707 && currentX <= 116.3707 && currentY >= 40.32095 && currentY <= 40.72095 && currentScale >= 4000000){
                //点击了延庆赛区
                Logger.e("点击了延庆赛区");
                Point yanqing = new Point(116.1707, 40.52095);
                mv_main_page.setViewpointCenterAsync(yanqing, 1600000f);
                loadComplaceLayer(); //放大后加载场馆图层
            }else if (currentX >= 114.9995 && currentX <= 115.3995 && currentY >= 40.8184 && currentY <= 41.2184 && currentScale >= 4000000){
                //点击了张家口赛区
                Logger.e("点击了张家口赛区");
                Point zhangjiakou = new Point(115.1995, 41.0184);
                mv_main_page.setViewpointCenterAsync(zhangjiakou, 1600000f);
                loadComplaceLayer(); //放大后加载场馆图层

            /*
            *后面开始是对项目名的点击响应，单机弹出项目介绍
            * 经纬误差半径暂定为0.2
             */
            }else if (currentX >= 116.70954 && currentX <= 117.10954 && currentY >= 41.41539 && currentY <= 41.81539){
                //有舵雪车（116.9095494°， 41.6153932）
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_bobsleiqn));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_national_sleigh_center));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_bobsleiqn_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_national_sleigh_center));
                event_name_id = R.string.focus_bobsleiqn;
            }else if (currentX >= 117.45407 && currentX <= 117.85407 && currentY >= 41.43863 && currentY <= 41.83863){
                //无舵雪橇(117.6540732, 41.6386388)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_luge));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_national_sleigh_center));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_luge_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_national_sleigh_center));
                event_name_id = R.string.focus_luge;
            }else if (currentX >= 117.43800 && currentX <= 117.83800 && currentY >= 41.97999 && currentY <= 42.37999){
                //俯式冰橇(117.6380048, 42.1799958)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_skeleton));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_national_sleigh_center));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_skeleton_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_national_sleigh_center));
                event_name_id = R.string.focus_skeleton;
            }else if (currentX >= 116.75334 && currentX <= 117.15334 && currentY >= 41.96932 && currentY <= 42.36932){
                //高山滑雪(116.953342, 42.1693254)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_alpine_skiing));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_national_alpine_skating));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_alpine_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_national_alpine_skating));
                event_name_id = R.string.focus_alpine_skiing;
            }else if (currentX >= 116.14937 && currentX <= 116.54937 && currentY >= 38.68859 && currentY <= 39.08859){
                //花样滑冰(116.349370, 38.8859198)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_figure_skating));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_capital_stadium));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_figure_skating_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_capital_stadium));
                event_name_id = R.string.focus_figure_skating;
            }else if (currentX >= 114.66583 && currentX <= 115.06583 && currentY >= 38.65126 && currentY <= 39.05126){
                //速度滑冰(114.8658324, 38.851261)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_speed_skating));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_national_skating));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_speed_skating_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_national_skating));
                event_name_id = R.string.focus_speed_skating;
            }else if (currentX >= 117.05859 && currentX <= 117.45859 && currentY >= 38.70450 && currentY <= 39.10450){
                //短道速滑(117.2585941, 38.904503)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_short_track));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_capital_stadium));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_short_track_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_capital_stadium));
                event_name_id = R.string.focus_short_track;
            }else if (currentX >= 115.36664 && currentX <= 115.76664 && currentY >= 38.66850 && currentY <= 39.06850){
                //冰壶(115.5666450, 38.8685019)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_curling));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_water_squ));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_curling_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_water_squ));
                event_name_id = R.string.focus_curling;
            }else if (currentX >= 113.8700 && currentX <= 114.2700 && currentY >= 38.6088 && currentY <= 39.0088){
                //冰球(114.0994987, 38.8137643)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_ice_hockey));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.main_man_ice_hockey)+getString(R.string.main_woman_ice_hockey));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_ice_hockey_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_national_stadium));
                event_name_id = R.string.focus_ice_hockey;
            }else if (currentX >= 116.63977 && currentX <= 117.03977 && currentY >= 42.68590 && currentY <= 43.08590){
                //自由式滑雪(116.839774, 42.8859061)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_free_styleing));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_yundingshang));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_free_styleing_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_yundingshang));
                event_name_id = R.string.focus_free_styleing;
            }else if (currentX >= 113.77002 && currentX <= 114.17002 && currentY >= 42.65052 && currentY <= 43.05052){
                //冬季两项(113.9700239, 42.8505241)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_biathlon));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_baithlon_winter));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_biathlon_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_baithlon_winter));
                event_name_id = R.string.focus_biathlon;
            }else if (currentX >= 114.46195 && currentX <= 113.86195 && currentY >= 42.67211 && currentY <= 43.07211){
                //北欧两项(114.6619500, 42.8721120)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_nordic_combined));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_europe_platform)+" "+getString(R.string.indoor_ocean_cross_country));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_nordic_combined_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_europe_platform));
                event_name_id = R.string.focus_nordic_combined;
            }else if (currentX >= 115.14578 && currentX <= 115.54578 && currentY >= 42.69359 && currentY <= 43.09359){
                //单板滑雪(115.34578, 42.89359)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_snowboarding));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_yundingshang));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_skeleton_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_yundingshang));
                event_name_id = R.string.focus_snowboarding;
            }else if (currentX >= 115.86472 && currentX <= 116.26472 && currentY >= 42.68478 && currentY <= 43.08478){
                //跳台滑雪(116.06472, 42.8847815)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_ski_jumping));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_europe_platform));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_skeleton_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_europe_platform));
                event_name_id = R.string.focus_ski_jumping;
            }else if (currentX >= 117.39593 && currentX <= 117.79593 && currentY >= 42.67961 && currentY <= 43.07961){
                //越野滑雪(117.595936, 42.8796106)
                mResultAttributes.add(EVENT_NAME+getString(R.string.focus_cross_skating));
                mResultAttributes.add(HOLD_COMPLACE+getString(R.string.indoor_ocean_cross_country));
                mResultAttributes.add(EVENT_INFO+getString(R.string.main_skeleton_info));
                showProgramInfo(mResultAttributes, getString(R.string.indoor_ocean_cross_country));
                event_name_id = R.string.focus_cross_skating;
            }else {
                //点击了其他地方，暂时不做处理
            }

            //场馆单机时弹出该场馆周围项目
            if (currentScale <= 800000){ //场馆至少要在80W及以下才能进行区分和点击
                if (currentX >= 116.341237 && currentX <= 116.401237 && currentY >= 40.97183 && currentY <= 41.03183){
                    //国家速滑馆（116.371237， 41.00183）
                    changeLayerVisibity(speedSkatingLayer);
                }else if (currentX >= 116.382705 && currentX <= 116.384705 && currentY >= 39.990297 && currentY <= 39.993297){
                    //国家游泳中心（116.383705， 39.991297）
                    changeLayerVisibity(curlingLayer);
                }else if (currentX >= 116.382997 && currentX <= 116.384997 && currentY >= 39.993740 && currentY <= 39.995740){
                    //国家体育馆（116.383997，39.994740）
                    changeLayerVisibity(iceHockeyLayer);
                }else if (currentX >= 116.244795 && currentX <= 116.304795 && currentY >= 39.879925 && currentY <= 39.939925){
                    //五棵松体育中心（116.274795， 39.909925）
                    changeLayerVisibity(womanIceHockeyLayer);
                }else if (currentX >= 116.291413 && currentX <= 116.351413 && currentY >= 39.908682 && currentY <= 39.968682){
                    //首都体育馆（116.321413， 39.938682）
                    changeLayerVisibity(shortTrackLayer);
                    changeLayerVisibity(figureSkatingLayer);
                }else if (currentX >= 115.3415030 && currentX <= 115.4015030 && currentY >= 40.9718341 && currentY <= 41.0318341){
                    //北欧中心越野滑雪场（115.3715030， 41.0018341）
                    changeLayerVisibity(crossSkatingLayer);
                    changeLayerVisibity(nordicCombinedLayer);
                }else if (currentX >= 115.7963571 && currentX <= 116.8363571 && currentY >= 40.4894112 && currentY <= 40.5294112){
                    //国家高山滑雪中心（115.8163571， 40.5094112）容差过大，定为0.02
                    changeLayerVisibity(alpineSkiingLayer);
                }else if (currentX >= 115.8303536 && currentX <= 115.8903536 && currentY >= 40.5066411 && currentY <= 40.5666411){
                    //国家雪车雪橇中心（115.8603536， 40.5366411）
                    changeLayerVisibity(skeletonLayer);
                    changeLayerVisibity(bobsleiqnLayer);
                    changeLayerVisibity(lugeLayer);
                }else if (currentX >= 115.3971393 && currentX <= 115.4571393 && currentY >= 40.9832124 && currentY <= 41.0432124){
                    //云顶滑雪公园场地（115.4271393， 41.0132124）
                    changeLayerVisibity(freeStyleingLayer);
                    changeLayerVisibity(snowboardingLayer);
                }else if (currentX >= 115.3640017 && currentX <= 115.4240017 && currentY >= 40.885068 && currentY <= 40.945068){
                    //冬季两项中心（115.3940017， 40.915068）
                    changeLayerVisibity(biathlonLayer);
                }else if (currentX >= 115.3607979 && currentX <= 115.4207979 && currentY >= 41.0045566 && currentY <= 41.0645566){
                    //北欧中心跳台滑雪场（115.3907979， 41.0345566）
                    changeLayerVisibity(skiJumpingLayer);
                    changeLayerVisibity(nordicCombinedLayer2);
                }else if (currentX >= 115.3895963 && currentX <= 115.3915963 && currentY >= 39.9902558 && currentY <= 39.9922558){
                    //国家体育场/鸟巢（116.3905963，39.9912558） todo 开闭幕式
                }else {
                    //点击了其他地方，不做处理
                }
            }



            Logger.e("X:"+currentPoint.getX()+"\n"+"Y:"+currentPoint.getY()+"\n"+
                    "Z:"+currentPoint.getZ()+"\n"+"M:"+currentPoint.getM());
            return super.onSingleTapConfirmed(e);
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Point currentPoint = CoordinateUtils.screnToMapPoint(mv_main_page, (int) e.getX(), (int) e.getY());
            double currentX = currentPoint.getX(); //当前用户点击的的精度
            double currentY = currentPoint.getY(); //当前用户点击的纬度

            final List<String> mResultAttributes = new ArrayList<>();
            /*
             * 后面开始是对场馆的点击响应
             * 容差 水立方，国家体育馆，鸟巢三个定为0.001 其他场馆容差为0.03
             */
            if (currentScale <= 800000){ //场馆至少要在80W及以下才能进行区分和点击
                if (currentX >= 116.341237 && currentX <= 116.401237 && currentY >= 40.97183 && currentY <= 41.03183){
                    //国家速滑馆（116.371237， 41.00183）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_national_skating));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_chaoyang));
                    mResultAttributes.add(HOLD_PEOPLE+12000);
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_national_skating));
                    showVenueInfo(mResultAttributes, R.string.indoor_national_skating);
                }else if (currentX >= 116.382705 && currentX <= 116.384705 && currentY >= 39.990297 && currentY <= 39.993297){
                    //国家游泳中心（116.383705， 39.991297）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_water_squ));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_chaoyang));
                    mResultAttributes.add(HOLD_PEOPLE+4500);
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_water_squ));
                    showVenueInfo(mResultAttributes, R.string.indoor_water_squ);
                }else if (currentX >= 116.382997 && currentX <= 116.384997 && currentY >= 39.993740 && currentY <= 39.995740){
                    //国家体育馆（116.383997，39.994740）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_national_stadium));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_chaoyang));
                    mResultAttributes.add(HOLD_PEOPLE+18000);
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_national_stadium));
                    showVenueInfo(mResultAttributes, R.string.indoor_national_stadium);
                }else if (currentX >= 116.244795 && currentX <= 116.304795 && currentY >= 39.879925 && currentY <= 39.939925){
                    //五棵松体育中心（116.274795， 39.909925）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_wukesong));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_haidin));
                    mResultAttributes.add(HOLD_PEOPLE+9000);
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_wukesong));
                    showVenueInfo(mResultAttributes, R.string.indoor_wukesong);
                }else if (currentX >= 116.291413 && currentX <= 116.351413 && currentY >= 39.908682 && currentY <= 39.968682){
                    //首都体育馆（116.321413， 39.938682）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_capital_stadium));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_haidin));
                    mResultAttributes.add(HOLD_PEOPLE+18000);
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_capital_stadium));
                    showVenueInfo(mResultAttributes, R.string.indoor_capital_stadium);
                }else if (currentX >= 115.3415030 && currentX <= 115.4015030 && currentY >= 40.9718341 && currentY <= 41.0318341){
                    //北欧中心越野滑雪场（115.3715030， 41.0018341）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_ocean_cross_country));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_zhangjiakou_chongli));
                    mResultAttributes.add(HOLD_PEOPLE+getString(R.string.main_hold_people_10000_5_5));
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_ocean_cross_country));
                    showVenueInfo(mResultAttributes, R.string.indoor_ocean_cross_country);
                }else if (currentX >= 115.7963571 && currentX <= 116.8363571 && currentY >= 40.4894112 && currentY <= 40.5294112){
                    //国家高山滑雪中心（115.8163571， 40.5094112）容差过大，定为0.02
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_national_alpine_skating));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_zhangjiakou_chongli));
                    mResultAttributes.add(HOLD_PEOPLE+getString(R.string.main_hold_people_8500));
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_national_alpine_skating));
                    showVenueInfo(mResultAttributes, R.string.indoor_national_alpine_skating);
                }else if (currentX >= 115.8303536 && currentX <= 115.8903536 && currentY >= 40.5066411 && currentY <= 40.5666411){
                    //国家雪车雪橇中心（115.8603536， 40.5366411）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_national_sleigh_center));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_yanqing));
                    mResultAttributes.add(HOLD_PEOPLE+getString(R.string.main_hold_people_10000_2_8));
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_national_sleigh_center));
                    showVenueInfo(mResultAttributes, R.string.indoor_national_sleigh_center);
                }else if (currentX >= 115.3971393 && currentX <= 115.4571393 && currentY >= 40.9832124 && currentY <= 41.0432124){
                    //云顶滑雪公园场地（115.4271393， 41.0132124）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_yundingshang));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_zhangjiakou_chongli));
                    mResultAttributes.add(HOLD_PEOPLE+getString(R.string.main_hold_people_7500));
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_yundingshang));
                    showVenueInfo(mResultAttributes, R.string.indoor_yundingshang);
                }else if (currentX >= 115.3640017 && currentX <= 115.4240017 && currentY >= 40.885068 && currentY <= 40.945068){
                    //冬季两项中心（115.3940017， 40.915068）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_baithlon_winter));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_zhangjiakou_chongli));
                    mResultAttributes.add(HOLD_PEOPLE+getString(R.string.main_hold_people_10000_5_5));
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_baithlon_winter));
                    showVenueInfo(mResultAttributes, R.string.indoor_baithlon_winter);
                }else if (currentX >= 115.3607979 && currentX <= 115.4207979 && currentY >= 41.0045566 && currentY <= 41.0645566){
                    //北欧中心跳台滑雪场（115.3907979， 41.0345566）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_europe_platform));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_zhangjiakou_chongli));
                    mResultAttributes.add(HOLD_PEOPLE+getString(R.string.main_hold_people_10000_5_5));
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_europe_platform));
                    showVenueInfo(mResultAttributes, R.string.indoor_europe_platform);
                }else if (currentX >= 115.3895963 && currentX <= 115.3915963 && currentY >= 39.9902558 && currentY <= 39.9922558){
                    //国家体育场/鸟巢（116.3905963，39.9912558）
                    mResultAttributes.add(VENUE_NAME+getString(R.string.indoor_bird_nest));
                    mResultAttributes.add(VENUE_POSITION+getString(R.string.main_beijing_chaoyang));
                    mResultAttributes.add(HOLD_PEOPLE+91000);
                    mResultAttributes.add(VENUE_INFO+getString(R.string.main_bird_nest));
                    showVenueInfo(mResultAttributes, R.string.indoor_bird_nest);
                }else {
                    //点击了其他地方，不做处理
                }
            }


            return false; //return false禁用地图双击放大
        }
    }


    //项目信息弹出框
    private void showProgramInfo(List<String> mResultAttributes, String venueName){
        new MaterialDialog.Builder(getContext())
                .title("项目信息")
                .canceledOnTouchOutside(false)
                .items(mResultAttributes)
                .positiveText("确定")
                .negativeText("搜索赛程信息")
                .neutralText("导航到赛区")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.NEUTRAL) {
                            //导航到比赛场馆
                            Intent naviIntent = new Intent(getContext(), NavigationActivity.class);
                            naviIntent.putExtra("activityID",DateSyncUtils.MAIN_PAGE_ID);
                            naviIntent.putExtra("venueName", venueName);
                            startActivity(naviIntent);
                        } else if (which == DialogAction.POSITIVE) {
                            dialog.dismiss();
                        } else if (which == DialogAction.NEGATIVE) {
                            //切换到搜索页面
                            Intent intent = new Intent(getContext(), SearchComplaceActivity.class);
                            intent.putExtra("name_id", event_name_id);
                            startActivity(intent);
                        }

                    }
                })
                .show();
    }

    //场馆信息弹出框
    private void showVenueInfo(List<String> mResultAttributes, int venueID){
        new MaterialDialog.Builder(getContext())
                .title("场馆信息")
                .canceledOnTouchOutside(false)
                .items(mResultAttributes)
                .positiveText("确定")
                .negativeText("三维场馆")
                .neutralText("室内地图")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.NEUTRAL) {
                            //室内地图
                            Intent indoorIntent = new Intent(getContext(), IndoorMapActivity.class);
                            indoorIntent.putExtra("venueID", venueID);
                            startActivity(indoorIntent);
                        } else if (which == DialogAction.POSITIVE) {
                            dialog.dismiss();
                        } else if (which == DialogAction.NEGATIVE) {
                            //三维场馆
                            Intent sceneIntent = new Intent(getContext(), SceneComplaceActivity.class);
                            sceneIntent.putExtra("venueID", venueID);
                            startActivity(sceneIntent);
                        }

                    }
                })
                .show();
    }

}
