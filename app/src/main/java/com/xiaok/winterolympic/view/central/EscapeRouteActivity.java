package com.xiaok.winterolympic.view.central;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.FileUtils;
import com.xiaok.winterolympic.utils.LayerUtils;
import com.xiaok.winterolympic.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class EscapeRouteActivity extends AppCompatActivity {

    private MapView mv_escape;

    private FloatingActionMenu menu_blue;
    private List<FloatingActionMenu> menus = new ArrayList<>();

    private Handler mUiHandler = new Handler();

    private FeatureLayer MedicalPointsLayer;
    private FeatureLayer Escapeline1Layer;
    private FeatureLayer Escapeline2Layer;
    private FeatureLayer Escapeline3Layer;
    private FeatureLayer Escapeline4Layer;
    private FeatureLayer FireProtectionLayer;
    private FeatureLayer WCboundLayer;
    private FeatureLayer WCwaterpointLayer;
    private FeatureLayer WCwashroomLayer;
    private FeatureLayer WCpassLayer;
    private FeatureLayer WCsitLayer;
    private FeatureLayer WCEnExLayer;
    private FeatureLayer WCmainpartLayer;
    private FeatureLayer WCPolygonLayer;
    private FeatureLayer WCliftLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escape_route);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.central_escape_route));
        }

        menu_blue = findViewById(R.id.escape_menu_blue);
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

        mv_escape = findViewById(R.id.escape_mapview);

        //配置地图，加载图层
        LayerUtils.addTDT(mv_escape); //加载天地图底图
        loadBaseIndoorGeodatebase(); //加载室内基本地图
        loadOptionalGeodatebase(); //加载可选图层
        Point centralPoint = new Point(116.384216, 39.991298);
        mv_escape.setViewpointCenterAsync(centralPoint, 3140f);
        mv_escape.setAttributionTextVisible(false); //去除Esrilogo


        //灭火器
        FloatingActionButton fab_fire = findViewById(R.id.escape_fab_fire);
        fab_fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FireProtectionLayer.isVisible()){
                    FireProtectionLayer.setVisible(false);
                }else {
                    FireProtectionLayer.setVisible(true);
                    ToastUtils.showSingleToast("已显示所有灭火设施位置");
                }
            }
        });


        //医疗点
        FloatingActionButton fab_medical = findViewById(R.id.escape_fab_medical);
        fab_medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MedicalPointsLayer.isVisible()){
                    MedicalPointsLayer.setVisible(false);
                }else {
                    MedicalPointsLayer.setVisible(true);
                    ToastUtils.showSingleToast("已显示所有医疗援助点");
                }
            }
        });


        //逃生路线
        android.support.design.widget.FloatingActionButton fab_escape = findViewById(R.id.escape_fab_route);
        fab_escape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Escapeline1Layer.isVisible() && Escapeline2Layer.isVisible()
                        && Escapeline3Layer.isVisible() && Escapeline4Layer.isVisible()){
                    Escapeline1Layer.setVisible(false);
                    Escapeline2Layer.setVisible(false);
                    Escapeline3Layer.setVisible(false);
                    Escapeline4Layer.setVisible(false);
                }else {
                    Escapeline1Layer.setVisible(true);
                    Escapeline2Layer.setVisible(true);
                    Escapeline3Layer.setVisible(true);
                    Escapeline4Layer.setVisible(true);
                    ToastUtils.showSingleToast("已显示逃生路线");
                }
            }
        });

    }


    private void loadBaseIndoorGeodatebase(){
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

                WCboundLayer = new FeatureLayer(WCboundTable);
                WCwaterpointLayer = new FeatureLayer(WCwaterpointTable);
                WCwashroomLayer = new FeatureLayer(WCwashroomTable);
                WCpassLayer = new FeatureLayer(WCpassTable);
                WCsitLayer = new FeatureLayer(WCsitTable);
                WCEnExLayer = new FeatureLayer(WCEnExTable);
                WCmainpartLayer = new FeatureLayer(WCmainpartTable);
                WCPolygonLayer = new FeatureLayer(WCPolygonTable);
                WCliftLayer = new FeatureLayer(WCliftTable);


                //添加图层
                mv_escape.getMap().getOperationalLayers().add(WCPolygonLayer);
                mv_escape.getMap().getOperationalLayers().add(WCmainpartLayer);
                mv_escape.getMap().getOperationalLayers().add(WCEnExLayer);
                mv_escape.getMap().getOperationalLayers().add(WCsitLayer);
                mv_escape.getMap().getOperationalLayers().add(WCpassLayer);
                mv_escape.getMap().getOperationalLayers().add(WCwashroomLayer);
                mv_escape.getMap().getOperationalLayers().add(WCwaterpointLayer);
                mv_escape.getMap().getOperationalLayers().add(WCboundLayer);
                mv_escape.getMap().getOperationalLayers().add(WCliftLayer);



            } else {
                Toast.makeText(EscapeRouteActivity.this, "基本地图加载失败!", Toast.LENGTH_LONG).show();
                Logger.e("基本地图加载失败!");
            }
        });
    }

    private void loadOptionalGeodatebase(){
        // 本地geodatabase文件路径
        String path = FileUtils.ESCAPE_ROUTE_DATEBASE;
        // 创建geodatabase
        final Geodatabase geodatabase = new Geodatabase(path);
        // 异步加载geodatabase
        geodatabase.loadAsync();
        // 当geodatabase读取成功后将geodatabase加载到数据库
        geodatabase.addDoneLoadingListener(() -> {
            if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
                //加载表
                GeodatabaseFeatureTable MedicalPointsTable = geodatabase.getGeodatabaseFeatureTable("MedicalPoints"); //医疗点
                GeodatabaseFeatureTable Escapeline1Table = geodatabase.getGeodatabaseFeatureTable("Escapeline1"); //逃生路线1
                GeodatabaseFeatureTable Escapeline2Table = geodatabase.getGeodatabaseFeatureTable("Escapeline2"); //2
                GeodatabaseFeatureTable Escapeline3Table = geodatabase.getGeodatabaseFeatureTable("Escapeline3"); //3
                GeodatabaseFeatureTable Escapeline4Table = geodatabase.getGeodatabaseFeatureTable("Escapeline4"); //4
                GeodatabaseFeatureTable FireProtectionTable = geodatabase.getGeodatabaseFeatureTable("FireProtection"); //灭火器

                MedicalPointsLayer = new FeatureLayer(MedicalPointsTable);
                Escapeline1Layer = new FeatureLayer(Escapeline1Table);
                Escapeline2Layer = new FeatureLayer(Escapeline2Table);
                Escapeline3Layer = new FeatureLayer(Escapeline3Table);
                Escapeline4Layer = new FeatureLayer(Escapeline4Table);
                FireProtectionLayer = new FeatureLayer(FireProtectionTable);

                //添加图层
                mv_escape.getMap().getOperationalLayers().add(MedicalPointsLayer);
                mv_escape.getMap().getOperationalLayers().add(Escapeline1Layer);
                mv_escape.getMap().getOperationalLayers().add(Escapeline2Layer);
                mv_escape.getMap().getOperationalLayers().add(Escapeline3Layer);
                mv_escape.getMap().getOperationalLayers().add(Escapeline4Layer);
                mv_escape.getMap().getOperationalLayers().add(FireProtectionLayer);

                //医疗点，消防设施默认不可见，逃生路线默认可见
                FireProtectionLayer.setVisible(false);
                MedicalPointsLayer.setVisible(false);
//                Escapeline1Layer.setVisible(false);
//                Escapeline2Layer.setVisible(false);
//                Escapeline3Layer.setVisible(false);
//                Escapeline4Layer.setVisible(false);


            } else {
                Toast.makeText(EscapeRouteActivity.this, "可选图层加载失败", Toast.LENGTH_LONG).show();
                Logger.e("可选图层加载失败!");
            }
        });
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
}
