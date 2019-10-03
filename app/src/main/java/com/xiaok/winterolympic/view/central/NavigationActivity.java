package com.xiaok.winterolympic.view.central;

import android.content.Intent;
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
import com.amap.api.maps.MapView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.NaviResultAdapter;

import java.util.List;

public class NavigationActivity extends AppCompatActivity implements AMapNaviViewListener,
        AdapterView.OnItemClickListener {

    private MapView mapView_navi;
    private AMap aMap;
    private SearchView sv_start_position;
    private SearchView sv_end_position;
    private Button btn_plan_way;

    private String startPosition = "我的位置";
    private String endPosition;

    private ListView mInputListView;
    private List<Tip> mCurrentTipList;
    private NaviResultAdapter mIntipAdapter;

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
        String endPoint = mainIntent.getStringExtra("venueName");
        if (endPoint != null){
            endPosition = endPoint;
            btn_plan_way.setEnabled(true);
        }

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

        //todo 定位
//        startLocation(); //开始定位

        //规划路线按钮
        btn_plan_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "规划从"+startPosition+"到"+endPosition+"的路线", Toast.LENGTH_SHORT).show();
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
        sv_end_position.setIconifiedByDefault(true);
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

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

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

}
