package com.xiaok.winterolympic.view.central;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileScenePackage;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;


public class SceneComplaceActivity extends AppCompatActivity {

    private SceneView mSceneView;
    private ArcGISScene scene;

    private List<FloatingActionMenu> menus = new ArrayList<>();
    private FloatingActionMenu menu_blue;

    private Handler mUiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_complace);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.central_3d_scene));
        }

        menu_blue = findViewById(R.id.menu_blue);
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


        //遥感影像作为三维底图
        scene = new ArcGISScene(Basemap.createImagery());
        mSceneView = findViewById(R.id.scene_sceneview);
        mSceneView.setScene(scene);
        mSceneView.setAttributionTextVisible(false); //关闭Esri logo

        String filepath = FileUtils.SCENE_PATH;
        if (!filepath.isEmpty()) {

            // add a scene service to the scene for viewing buildings
            ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(filepath);
            scene.getOperationalLayers().add(sceneLayer);

            // 设置三维场景视角镜头（camera）
            Camera camera = new Camera(39.991616, 116.3842271,200, 345, 65, 0);
            mSceneView.setViewpointCamera(camera);
        }

        FloatingActionButton fab_beijing = findViewById(R.id.scene_fab_beijing);
        fab_beijing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = new Camera(39.991616, 116.3842271,200, 345, 65, 0);
                mSceneView.setViewpointCamera(camera);
            }
        });

        FloatingActionButton fab_yanqing = findViewById(R.id.scene_fab_yanqing);
        fab_yanqing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = new Camera(40.5366411, 115.8603536,200, 345, 65, 0);
                mSceneView.setViewpointCamera(camera);
            }
        });

        FloatingActionButton fab_zhangjiakou = findViewById(R.id.scene_fab_zhangjiakou);
        fab_zhangjiakou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = new Camera(40.915068,115.3940017,200, 345, 65, 0);
                mSceneView.setViewpointCamera(camera);
            }
        });

    }



    @Override
    protected void onPause() {
        mSceneView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSceneView.resume();
    }

    @Override
    protected void onDestroy() {
        mSceneView.dispose();
        super.onDestroy();
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
