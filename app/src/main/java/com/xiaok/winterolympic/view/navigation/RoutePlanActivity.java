package com.xiaok.winterolympic.view.navigation;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
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
import com.autonavi.tbt.TrafficFacilityInfo;
import com.esri.arcgisruntime.geometry.Point;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener, AMapNaviListener {

    private MapView mv_route;
    private AMap aMap;
    private RouteSearch routeSearch;

    private String startPosition = "我的位置";
    private String endPosition;


    private double startLatitude;
    private double startLongitude;
    private double endLongitude;
    private double endLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        mv_route = findViewById(R.id.route_mapview);

        //获取起终点坐标
        Intent routeIntent = getIntent();
        startLongitude = routeIntent.getDoubleExtra("startX",116.38376139624);
        startLatitude = routeIntent.getDoubleExtra("startY",39.997936144);
        endLongitude = routeIntent.getDoubleExtra("endX",116.327421);
        endLatitude = routeIntent.getDoubleExtra("endY",39.940024);


        mv_route.onCreate(savedInstanceState);
        //获取地图控制类
        if (aMap == null){
            aMap = mv_route.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，
        //初始化以我的位置地图中心及缩放比
        LatLng initLatLng = new LatLng(startLatitude,startLongitude);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(initLatLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        //初始化路径规划
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        //配置驾车路线参数
        com.amap.api.services.core.LatLonPoint startLatLng = new LatLonPoint(startLatitude,startLongitude);
        com.amap.api.services.core.LatLonPoint endLatLng = new LatLonPoint(endLatitude,endLongitude);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startLatLng,endLatLng);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,0,null,null,"");
        routeSearch.calculateDriveRouteAsyn(query); //开始规划路线


        FloatingActionButton fab_start_navi = findViewById(R.id.fab_navi_start);
        fab_start_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("开始导航");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mv_route != null){
            mv_route.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mv_route != null){
            mv_route.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mv_route != null){
            mv_route.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mv_route != null){
            mv_route.onSaveInstanceState(outState);
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

    //驾车路线
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

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        ToastUtils.showSingleToast("规划成功");
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }
}
