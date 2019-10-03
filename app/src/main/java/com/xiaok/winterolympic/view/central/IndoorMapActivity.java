package com.xiaok.winterolympic.view.central;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.clans.fab.FloatingActionMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.DonationAdapter;
import com.xiaok.winterolympic.model.DonationOption;
import com.xiaok.winterolympic.utils.CoordinateUtils;
import com.xiaok.winterolympic.utils.FileUtils;
import com.xiaok.winterolympic.utils.LayerUtils;
import com.xiaok.winterolympic.utils.ToastUtils;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class IndoorMapActivity extends AppCompatActivity {

    private TextView tv_dialogs;
    private ImageView iv_dialogs;
    private TextView tv_floor;
    private ImageButton ib_back;
    private MapView mv_indoor;
    private Button btn_choose_venue;
    private FloatingActionMenu menu_blue;
    private List<FloatingActionMenu> menus = new ArrayList<>();

    private String currentFloor;
    private String floor_one;
    private String floor_two;
    private String floor_three;
    private String floor_four;

    private String navi_enex;
    private String navi_water;
    private String navi_wc;
    private String navi_lift;
    private String navi_pass;

    private int venueID;

    FeatureLayer WCboundLayer;
    FeatureLayer WCwaterpointLayer;
    FeatureLayer WCwashroomLayer;
    FeatureLayer WCpassLayer;
    FeatureLayer WCsitLayer;
    FeatureLayer WCEnExLayer;
    FeatureLayer WCmainpartLayer;
    FeatureLayer WCPolygonLayer;
    FeatureLayer WCliftLayer;
    FeatureLayer WCwaterpointHLLayer;
    FeatureLayer WCwashroomHLLayer;
    FeatureLayer WCliftHLLayer;
    FeatureLayer WCpassHLLayer;
    FeatureLayer WCEnExHLLayer;

    FeatureLayer EnexToLift2Layer;
    FeatureLayer EnexToLiftPoint2Layer;
    FeatureLayer EnToWC1Layer;
    FeatureLayer EnToWC1PLayer;

    private String position_start;
    private String position_end;

    private String geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;

    private Handler mUiHandler = new Handler();

    private int maxNum = 4; //楼层数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_map);

        mv_indoor = findViewById(R.id.indoor_mapview);

        floor_one = getString(R.string.indoor_floor_01);
        floor_two = getString(R.string.indoor_floor_02);
        floor_three = getString(R.string.indoor_floor_03);
        floor_four = getString(R.string.indoor_floor_04);
        currentFloor = floor_one;

        navi_enex = getString(R.string.indoor_navi_enex);
        navi_lift = getString(R.string.indoor_navi_lift);
        navi_pass = getString(R.string.indoor_navi_pass);
        navi_water = getString(R.string.indoor_navi_water);
        navi_wc = getString(R.string.indoor_navi_wc);
        position_start = navi_enex;
        position_end = navi_enex;

        //加载自定义ActionBar
        setCustomActionBar();

        //配置地图，加载图层
        LayerUtils.addTDT(mv_indoor); //加载天地图底图
//        loadPointGeodatebase();
        loadIndoorGeodatebase();//加载室内地图
        loadIndoorNaviRoute(); //加载室内导航路线，初始设置为不可见
        Point centralPoint = new Point(116.384216, 39.991298);
        mv_indoor.setViewpointCenterAsync(centralPoint, 3140f);
        mv_indoor.setAttributionTextVisible(false); //去除Esrilogo


        //获取室内地图路线节点点击后的的弹出款View，
        View dialogsView = View.inflate(IndoorMapActivity.this, R.layout.dialogs_indoor,null);
        tv_dialogs = dialogsView.findViewById(R.id.indoor_tv_dialogs);
        iv_dialogs = dialogsView.findViewById(R.id.indoor_iv_dialogs);

        menu_blue = findViewById(R.id.indoor_menu_blue);
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

        Intent mainIntent = getIntent();
        venueID = mainIntent.getIntExtra("venueID",0);
        if (venueID != 0){
            //todo 通过ID判断选择需要加载的数据库
        }

        //存储每个场馆及其对应楼层数
        HashMap<String,Integer> venueMap = new HashMap<>();
        venueMap.put(getString(R.string.indoor_baithlon_winter), 4); //冬季两项中心
        venueMap.put(getString(R.string.indoor_capital_stadium), 4); //首都体育馆
        venueMap.put(getString(R.string.indoor_europe_platform), 4); //北欧中心跳台滑雪场
        venueMap.put(getString(R.string.indoor_national_alpine_skating), 4); //国家高山滑雪中心
        venueMap.put(getString(R.string.indoor_national_skating), 4); //国家速滑馆
        venueMap.put(getString(R.string.indoor_national_sleigh_center), 4); //国家雪车雪橇中心
        venueMap.put(getString(R.string.indoor_national_stadium), 4); //国家体育馆
        venueMap.put(getString(R.string.indoor_ocean_cross_country), 4); //北欧中心越野滑雪场
        venueMap.put(getString(R.string.indoor_water_squ), 4); //水立方
        venueMap.put(getString(R.string.indoor_wukesong), 4); //五棵松体育馆
        venueMap.put(getString(R.string.indoor_yundingshang), 4); //云顶滑雪公园场地


//        MaterialSpinner spinner_choose_room = findViewById(R.id.indoor_spinner);
//        spinner_choose_room.setItems(
//                getString(R.string.indoor_baithlon_winter),getString(R.string.indoor_capital_stadium),getString(R.string.indoor_europe_platform),
//                getString(R.string.indoor_national_alpine_skating),getString(R.string.indoor_national_skating),getString(R.string.indoor_national_sleigh_center),
//                getString(R.string.indoor_national_stadium),getString(R.string.indoor_ocean_cross_country),getString(R.string.indoor_water_squ),
//                getString(R.string.indoor_wukesong),getString(R.string.indoor_yundingshang));
//
//        spinner_choose_room.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                maxNum = venueMap.get(item);
//            }
//        });

        //选择起点下拉栏
        MaterialSpinner spinnner_start = findViewById(R.id.indoor_spinner_start);
        spinnner_start.setItems(navi_enex,navi_lift,navi_pass,navi_water,navi_wc);
        spinnner_start.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                position_start = item;
            }
        });

        //选择终点下拉栏
        MaterialSpinner spinner_end = findViewById(R.id.indoor_spinner_end);
        spinner_end.setItems(navi_enex,navi_lift,navi_pass,navi_water,navi_wc);
        spinner_end.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                position_end = item;
                if (position_start.equals(position_end)){
                    Snackbar.make(view,"起点和终点不能相同！",Snackbar.LENGTH_LONG);
                } else if (position_start.equals(navi_enex) && position_end.equals(navi_lift)){
                    //出口到电梯
                    LayerUtils.changeLayerVisibity(EnexToLift2Layer);
                    LayerUtils.changeLayerVisibity(EnexToLiftPoint2Layer);
                } else if (position_start.equals(navi_enex) && position_end.equals(navi_wc)){
                    //出口到洗手间
                    LayerUtils.changeLayerVisibity(EnToWC1Layer);
                    LayerUtils.changeLayerVisibity(EnToWC1PLayer);
                }
            }
        });


        //上楼
        FloatingActionButton fab_upstairs = findViewById(R.id.indoor_upstairs);
        fab_upstairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_one)){
                    currentFloor = floor_two;
                    changeLayer(2);
                } else if (currentFloor.equals(floor_two)){
                    currentFloor = floor_three;
                    changeLayer(3);
                } else if (currentFloor.equals(floor_three)){
                    currentFloor = floor_four;
                    changeLayer(4);
                } else {
                    Toast.makeText(IndoorMapActivity.this, getString(R.string.indoor_floor_max), Toast.LENGTH_SHORT).show();
                }

                tv_floor.setText(currentFloor);
            }
        });


        //下楼
        FloatingActionButton fab_downstairs = findViewById(R.id.indoor_downstairs);
        fab_downstairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_four)){
                    currentFloor = floor_three;
                    changeLayer(3);
                }
                else if (currentFloor.equals(floor_three)){
                    currentFloor = floor_two;
                    changeLayer(2);
                }
                else if (currentFloor.equals(floor_two)){
                    currentFloor = floor_one;
                    changeLayer(1);
                }
                else {
                    Toast.makeText(IndoorMapActivity.this, getString(R.string.indoor_floor_min), Toast.LENGTH_SHORT).show();
                }

                tv_floor.setText(currentFloor);
            }
        });


        //选择场馆
        btn_choose_venue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<DonationOption> adapter = new DonationAdapter(IndoorMapActivity.this, loadDonationOptions());
                new LovelyChoiceDialog(IndoorMapActivity.this)
                        .setTopColorRes(R.color.colorAccent)
                        .setTitle(R.string.indoor_choose_spot)
                        .setIcon(R.mipmap.indoor_choose_spot)
                        .setMessage("请选择您想要查看的场馆：")
                        .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<DonationOption>() {
                            @SuppressLint("StringFormatInvalid")
                            @Override
                            public void onItemSelected(int position, DonationOption item) {
                                chooseVenue(position);

                            }
                        })
                        .show();
            }
        });


        /*
        * 接下来是室内地图标注点击高亮响应
        * todo 图层显示&&透明度属性动画，强调效果
        */
        //出口
        com.github.clans.fab.FloatingActionButton fab_exit = findViewById(R.id.indoor_fab_exit);
        fab_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_one)){
                    if (WCEnExHLLayer.isVisible()){
                        WCEnExHLLayer.setVisible(false);
                    }else {
                        WCEnExHLLayer.setVisible(true);
                        ToastUtils.showSingleToast("出口位置已显示");
                    }
                }else {
                    ToastUtils.showSingleToast("无当前楼层图例数据");
                }

            }
        });

        //洗手间
        com.github.clans.fab.FloatingActionButton fab_toilet = findViewById(R.id.indoor_fab_toilet);
        fab_toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_one)){
                    if (WCwashroomHLLayer.isVisible()){
                        WCwashroomHLLayer.setVisible(false);
                    }else {
                        WCwashroomHLLayer.setVisible(true);
                        ToastUtils.showSingleToast("洗手间位置已显示");
                    }
                }else {
                    ToastUtils.showSingleToast("无当前楼层图例数据");
                }
            }
        });

        //饮水机
        com.github.clans.fab.FloatingActionButton fab_drinking_water = findViewById(R.id.indoor_fab_water);
        fab_drinking_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_one)){
                    if (WCwaterpointHLLayer.isVisible()){
                        WCwaterpointHLLayer.setVisible(false);
                    }else {
                        WCwaterpointHLLayer.setVisible(true);
                        ToastUtils.showSingleToast("饮水机位置已显示");
                    }
                }else {
                    ToastUtils.showSingleToast("无当前楼层图例数据");
                }
            }
        });

        //电梯
        com.github.clans.fab.FloatingActionButton fab_evle = findViewById(R.id.indoor_fab_evle);
        fab_evle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_one)){
                    if (WCliftHLLayer.isVisible()){
                        WCliftHLLayer.setVisible(false);
                    }else {
                        WCliftHLLayer.setVisible(true);
                        ToastUtils.showSingleToast("电梯位置已显示");
                    }
                }else {
                    ToastUtils.showSingleToast("无当前楼层图例数据");
                }
            }
        });

        //楼梯
        com.github.clans.fab.FloatingActionButton fab_stairs = findViewById(R.id.indoor_fab_stairs);
        fab_stairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFloor.equals(floor_one)){
                    if (WCpassHLLayer.isVisible()){
                        WCpassHLLayer.setVisible(false);
                    }else {
                        WCpassHLLayer.setVisible(true);
                        ToastUtils.showSingleToast("电梯位置已显示");
                    }
                }else {
                    ToastUtils.showSingleToast("无当前楼层图例数据");
                }
            }
        });





        //比例尺变化监听
        mv_indoor.addMapScaleChangedListener(new MapScaleChangedListener() {
            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                Logger.e("Scale changed"+"\n"+"当前Scale为："+mapScaleChangedEvent.getSource().getMapScale());
            }
        });

        //地图交互
        mv_indoor.setOnTouchListener(new IndoorTouchListener(IndoorMapActivity.this,mv_indoor));

        //左上角返回
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.indoor_actionbar, null);
        tv_floor = mActionBarView.findViewById(R.id.indoor_floor);
        tv_floor.setText(floor_one);
        ib_back = mActionBarView.findViewById(R.id.indoor_ib_back);
        btn_choose_venue = mActionBarView.findViewById(R.id.indoor_btn_choose_spot);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    //todo 切换不同楼层图层
    private void changeLayer(int floorNum){
        switch (floorNum){
            case 1:
                if (geodatabasePath.equals(FileUtils.INDOOR_WATER_CLUB_01)){
                    loadVenueLayer(FileUtils.INDOOR_WATER_CLUB_01);
                }else {
                    loadVenueLayer(FileUtils.INDOOR_NATIONAL_STATION_01);
                }
                break;
            case 2:
                if (geodatabasePath.equals(FileUtils.INDOOR_WATER_CLUB_01)){
                    loadVenueLayer(FileUtils.INDOOR_WATER_CLUB_02);
                }else {
                    loadVenueLayer(FileUtils.INDOOR_NATIONAL_STATION_02);
                }
                break;
            case 3:
                break;
            case 4:
                break;
            default:break;
        }
    }


    private void loadIndoorGeodatebase(){
        // 本地geodatabase文件路径
        String path = FileUtils.INDOOR_WATER_CLUB_01;
        // 创建geodatabase
        final Geodatabase geodatabase = new Geodatabase(path);
        // 异步加载geodatabase
        geodatabase.loadAsync();
        // 当geodatabase读取成功后将geodatabase加载到数据库
        geodatabase.addDoneLoadingListener(() -> {
            if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                //室内地图基本分区
                GeodatabaseFeatureTable WCboundTable = geodatabase.getGeodatabaseFeatureTable("WCbound"); //WCbound
                GeodatabaseFeatureTable WCwaterpointTable = geodatabase.getGeodatabaseFeatureTable("WCwaterpoint"); //WCwaterpoint
                GeodatabaseFeatureTable WCwashroomTable = geodatabase.getGeodatabaseFeatureTable("WCwashroom"); //WCwashroom
                GeodatabaseFeatureTable WCpassTable = geodatabase.getGeodatabaseFeatureTable("WCpass"); //WCpass
                GeodatabaseFeatureTable WCsitTable = geodatabase.getGeodatabaseFeatureTable("WCsit"); //WCsit
                GeodatabaseFeatureTable WCEnExTable = geodatabase.getGeodatabaseFeatureTable("WCEnEx");
                GeodatabaseFeatureTable WCmainpartTable = geodatabase.getGeodatabaseFeatureTable("WCmainpart"); //WCmainpart
                GeodatabaseFeatureTable WCPolygonTable = geodatabase.getGeodatabaseFeatureTable("WCPolygon"); //WCPolygon
                GeodatabaseFeatureTable WCliftTable = geodatabase.getGeodatabaseFeatureTable("WClift"); //WClift
                //高亮强调图层
                GeodatabaseFeatureTable WCwaterpointHLTable = geodatabase.getGeodatabaseFeatureTable("WCwaterpointHL"); //水源
                GeodatabaseFeatureTable WCwashroomHLTable = geodatabase.getGeodatabaseFeatureTable("WCwashroomHL"); //洗手间
                GeodatabaseFeatureTable WCliftHLTable = geodatabase.getGeodatabaseFeatureTable("WCliftHL"); //电梯
                GeodatabaseFeatureTable WCpassHLTable = geodatabase.getGeodatabaseFeatureTable("WCpassHL"); //楼梯
                GeodatabaseFeatureTable WCEnExHLTable = geodatabase.getGeodatabaseFeatureTable("WCEnExHL"); //出口

                WCboundLayer = new FeatureLayer(WCboundTable);
                WCwaterpointLayer = new FeatureLayer(WCwaterpointTable);
                WCwashroomLayer = new FeatureLayer(WCwashroomTable);
                WCpassLayer = new FeatureLayer(WCpassTable);
                WCsitLayer = new FeatureLayer(WCsitTable);
                WCEnExLayer = new FeatureLayer(WCEnExTable);
                WCmainpartLayer = new FeatureLayer(WCmainpartTable);
                WCPolygonLayer = new FeatureLayer(WCPolygonTable);
                WCliftLayer = new FeatureLayer(WCliftTable);

                WCwaterpointHLLayer = new FeatureLayer(WCwaterpointHLTable);
                WCwashroomHLLayer = new FeatureLayer(WCwashroomHLTable);
                WCliftHLLayer = new FeatureLayer(WCliftHLTable);
                WCpassHLLayer = new FeatureLayer(WCpassHLTable);
                WCEnExHLLayer = new FeatureLayer(WCEnExHLTable);

                //添加图层
                mv_indoor.getMap().getOperationalLayers().add(WCPolygonLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCmainpartLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCEnExLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCsitLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCpassLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCwashroomLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCwaterpointLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCboundLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCliftLayer);

                mv_indoor.getMap().getOperationalLayers().add(WCwaterpointHLLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCwashroomHLLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCliftHLLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCpassHLLayer);
                mv_indoor.getMap().getOperationalLayers().add(WCEnExHLLayer);

                //初始化把高亮图层全部不可见
                WCwaterpointHLLayer.setVisible(false);
                WCwashroomHLLayer.setVisible(false);
                WCliftHLLayer.setVisible(false);
                WCpassHLLayer.setVisible(false);
                WCEnExHLLayer.setVisible(false);


            } else {
                Toast.makeText(IndoorMapActivity.this, "Geodatabase failed to load!", Toast.LENGTH_LONG).show();
                Logger.e("Geodatabase failed to load!");
            }
        });
    }

    //加载室内地图导航路线图层
    private void loadIndoorNaviRoute(){
        // 本地geodatabase文件路径
        String path = FileUtils.INDOOR_NAVI_NETWORK;
        // 创建geodatabase
        final Geodatabase geodatabase = new Geodatabase(path);
        // 异步加载geodatabase
        geodatabase.loadAsync();
        // 当geodatabase读取成功后将geodatabase加载到数据库
        geodatabase.addDoneLoadingListener(() -> {
            if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                //获取数据表
                GeodatabaseFeatureTable EnexToLift2Table = geodatabase.getGeodatabaseFeatureTable("EnExToLift2");
                GeodatabaseFeatureTable EnexToLiftPoint2Table = geodatabase.getGeodatabaseFeatureTable("EnExToLift2P");
                GeodatabaseFeatureTable EnToWC1Table =geodatabase.getGeodatabaseFeatureTable("EnToWC1");
                GeodatabaseFeatureTable EnToWC1PTable = geodatabase.getGeodatabaseFeatureTable("EnToWC1P");

                //创建图层
                EnexToLift2Layer = new FeatureLayer(EnexToLift2Table);
                EnexToLiftPoint2Layer = new FeatureLayer(EnexToLiftPoint2Table);
                EnToWC1Layer = new FeatureLayer(EnToWC1Table);
                EnToWC1PLayer = new FeatureLayer(EnToWC1PTable);

                //添加图层
                mv_indoor.getMap().getOperationalLayers().add(EnexToLift2Layer);
                mv_indoor.getMap().getOperationalLayers().add(EnexToLiftPoint2Layer);
                mv_indoor.getMap().getOperationalLayers().add(EnToWC1Layer);
                mv_indoor.getMap().getOperationalLayers().add(EnToWC1PLayer);

                //设置图层可见性
                EnexToLift2Layer.setVisible(false);
                EnexToLiftPoint2Layer.setVisible(false);
                EnToWC1Layer.setVisible(false);
                EnToWC1PLayer.setVisible(false);


            } else {
                Toast.makeText(IndoorMapActivity.this, "Geodatabase failed to load!", Toast.LENGTH_LONG).show();
                Logger.e("Geodatabase failed to load!");
            }
        });
    }


    private List<DonationOption> loadDonationOptions() {
        List<DonationOption> result = new ArrayList<>();
        String[] raw = getResources().getStringArray(R.array.venue_items);
        for (String op : raw) {
            String[] info = op.split("%");
            result.add(new DonationOption(info[1], info[0]));
        }
        return result;
    }


    private void chooseVenue(int position){
        switch (position){
            //水立方
            case 0:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //国家体育馆
            case 1:
                geodatabasePath = FileUtils.INDOOR_NATIONAL_STATION_01;
                break;
            //五棵松体育馆
            case 2:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //首都体育馆
            case 3:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //国家速滑馆
            case 4:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //北欧中心越野滑雪馆
            case 5:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //北欧中心跳台滑雪场
            case 6:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //冬季两项中心
            case 7:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //云顶滑雪公园场地
            case 8:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //国家高山滑雪中心
            case 9:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //国家雪车雪橇中心
            case 10:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            //鸟巢
            case 11:
                geodatabasePath = FileUtils.INDOOR_WATER_CLUB_01;
                break;
            default:break;
        }
        if (geodatabasePath != null){
            loadVenueLayer(geodatabasePath);
            tv_floor.setText(floor_one); //选择场馆后重置楼层为一层
        }
    }

    private void loadVenueLayer(String geodatabasePath){
        //清除地图控件上之前的 图层
        mv_indoor.getMap().getOperationalLayers().clear();

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
                    mv_indoor.getMap().getOperationalLayers().add(featureLayer);
                }

                Point centralPoint = new Point(116.383816, 39.994898);
                mv_indoor.setViewpointCenterAsync(centralPoint, 3140f);

            } else {
                Toast.makeText(IndoorMapActivity.this, "Geodatabase failed to load!", Toast.LENGTH_LONG).show();
                Logger.e("Geodatabase failed to load!");
            }
        });
    }


    //室内地图用户触摸监听
    private class IndoorTouchListener extends DefaultMapViewOnTouchListener {

        public IndoorTouchListener(Context context, MapView mapView) {
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
             *接下来是室内路径规划节点的单击响应，用户点击经纬容差为0.0002
             */

            if (currentX >= 116.38306 && currentX <= 116.38346 && currentY >= 39.991006 && currentY <= 39.991406){
                //西出口(116.383260632, 39.991206496)
                tv_dialogs.setText(getString(R.string.indoor_real_enex_x));
                iv_dialogs.setImageResource(R.mipmap.indoor_real_enex_x);
                showDialogs();
            }else if (currentX >= 116.384785 && currentX <= 116.385185 && currentY >= 39.990662 && currentY <= 39.991062){
                //东南出口（116.384985447，39.9908624213）
                tv_dialogs.setText(getString(R.string.indoor_real_enex_dn));
                iv_dialogs.setImageResource(R.mipmap.indoor_real_enex_dn);
                showDialogs();
            }else if (currentX >= 116.388727 && currentX <= 116.389127 && currentY >= 39.990443 && currentY <= 39.990843){
                //洗手间
                tv_dialogs.setText(getString(R.string.indoor_real_washroom));
                iv_dialogs.setImageResource(R.mipmap.indoor_real_washroom);
                showDialogs();
            }else if (currentX >= 116.383662315 && currentX <= 116.384062315 && currentY >= 39.99112 && currentY <= 39.99152){
                //电梯(选取的为表格中2点，116.383862315，39.9913209491)
                tv_dialogs.setText(getString(R.string.indoor_real_lift));
                iv_dialogs.setImageResource(R.mipmap.indoor_real_lift);
                showDialogs();
            }else {
                //其他点暂不处理

            }


            return super.onSingleTapConfirmed(e);
        }
    }

    private void showDialogs(){
        new MaterialDialog.Builder(IndoorMapActivity.this)
                .title(getString(R.string.indoor_real_pic))
                .customView(R.layout.dialogs_indoor, true)
                .positiveText(getString(R.string.indoor_real_confirm))
                .show();
    }




}
