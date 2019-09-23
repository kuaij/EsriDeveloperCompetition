package com.xiaok.winterolympic.view.hide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.custom.ProgressButton;

public class SearchComplaceActivity extends AppCompatActivity {

    private String chooseDay;
    private String chooseCompetition;
    private int index = 0;

    private MaterialSpinner search_spinner_day;
    private MaterialSpinner search_spinner_competition;
    private ProgressButton btn_search;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View v = (View) msg.obj;
            resultUI(v);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_complace);

        initView(); //初始化控件

        Intent intent = getIntent();
        int name_id = intent.getIntExtra("name_id", R.string.search_no_limit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.main_page_search));
        }


        //初始化为不限
        chooseDay = getString(R.string.search_no_limit);
        chooseCompetition = getString(R.string.search_no_limit);

        //选择比赛日期
        search_spinner_day.setItems(getString(R.string.search_no_limit),"4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20");
        search_spinner_day.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                chooseDay = item;
            }
        });

        //选择搜索的比赛项目
        search_spinner_competition.setItems(
                getString(R.string.search_no_limit),getString(R.string.focus_alpine_skiing),getString(R.string.focus_biathlon),
                getString(R.string.focus_bobsleiqn), getString(R.string.focus_cross_skating),getString(R.string.focus_curling),
                getString(R.string.focus_figure_skating), getString(R.string.focus_free_styleing),getString(R.string.focus_ice_hockey),
                getString(R.string.focus_luge),getString(R.string.focus_nordic_combined),getString(R.string.focus_short_track),
                getString(R.string.focus_skeleton),getString(R.string.focus_ski_jumping),getString(R.string.focus_snowboarding),
                getString(R.string.focus_speed_skating));
        search_spinner_competition.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                chooseCompetition = item;
            }
        });
        //初始化Spinner选中状态
        search_spinner_competition.setSelectedIndex(initSpinnerIndex(name_id));



        btn_search.setBgColor(Color.parseColor("#1e90ff"));
        btn_search.setTextColor(Color.WHITE);
        btn_search.setProColor(Color.WHITE);
        btn_search.setButtonText(getString(R.string.search));
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_search.startAnim();
                Message msg = Message.obtain();
                msg.obj = v;
                mHandler.sendMessageDelayed(msg, 1800);

            }
        });
    }


    private void initView(){
        search_spinner_day = findViewById(R.id.search_spinner_day);
        search_spinner_competition = findViewById(R.id.search_spinner_competition);
        btn_search = findViewById(R.id.search_btn_start);
    }


    private int initSpinnerIndex(int name_id){
        switch (name_id){
            //有舵雪车
            case R.string.focus_bobsleiqn:
                index = 3;
                break;
            //无舵雪车
            case R.string.focus_luge:
                index = 9;
                break;
            //俯式冰橇
            case R.string.focus_skeleton:
                index = 12;
                break;
            //高山滑雪
            case R.string.focus_alpine_skiing:
                index = 1;
                break;
            //花样滑冰
            case R.string.focus_figure_skating:
                index = 6;
                break;
            //速度滑冰
            case R.string.focus_speed_skating:
                index = 15;
                break;
            //短道速滑
            case R.string.focus_short_track:
                index = 11;
                break;
            //冰壶
            case R.string.focus_curling:
                index = 5;
                break;
            //冰球
            case R.string.focus_ice_hockey:
                index = 8;
                break;
            //自由式滑雪
            case R.string.focus_free_styleing:
                index = 7;
                break;
            //冬季两项
            case R.string.focus_biathlon:
                index = 2;
                break;
            //北欧两项
            case R.string.focus_nordic_combined:
                index = 10;
                break;
            //单板滑雪
            case R.string.focus_snowboarding:
                index = 14;
                break;
            //跳台滑雪
            case R.string.focus_ski_jumping:
                index = 13;
                break;
            //越野滑雪
            case R.string.focus_cross_skating:
                index = 4;
                break;
            default:break;

        }

        chooseCompetition = getString(name_id);

        return index;
    }


    //提交用户检索的日期和项目名称
    private boolean requestForResult(String date, String competitionName){
        return true;

    }


    private void resultUI(View v){
        btn_search.stopAnim(new ProgressButton.OnStopAnim() {
            @Override
            public void Stop() {
                boolean isOk = requestForResult(chooseDay, chooseCompetition);
                if (isOk){
                    //日期不限
                    if (chooseDay.equals(getString(R.string.search_no_limit))){
                        //项目无限制
                        if (chooseCompetition.equals(getString(R.string.search_no_limit))){
                            Snackbar.make(v,getString(R.string.search_all)+getString(R.string.search_competition_name)+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(v,getString(R.string.search_all)+chooseCompetition+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
                        }

                    }
                    //日期限制
                    else {
                        if (chooseCompetition.equals(getString(R.string.search_no_limit))){
                            Snackbar.make(v,getString(R.string.search_all)+chooseDay+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(v,getString(R.string.search_date_is)+chooseDay+chooseCompetition+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
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
