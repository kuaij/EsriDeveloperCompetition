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
import com.xiaok.winterolympic.view.navigation.RoutePlanActivity;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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


    public static String DEFAULT_CITY = "北京";
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int REQUEST_SUC = 1000;


    public NavigationActivity(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
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




        //规划路线按钮
        btn_plan_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent routeIntent = new Intent(NavigationActivity.this,RoutePlanActivity.class);
                routeIntent.putExtra("startX",startPoint.getX());
                routeIntent.putExtra("startY",startPoint.getY());
                routeIntent.putExtra("endX",endPoint.getX());
                routeIntent.putExtra("endY",endPoint.getY());
                startActivity(routeIntent);

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
