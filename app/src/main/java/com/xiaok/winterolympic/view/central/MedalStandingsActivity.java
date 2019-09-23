package com.xiaok.winterolympic.view.central;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.xiaok.winterolympic.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MedalStandingsActivity extends AppCompatActivity {

    private String choosMonth = "2";
    private String chooseDay;

    private ImageView iv_medal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal_standings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.central_medal_standings));
        }

        iv_medal = findViewById(R.id.medal_iv);

        TextView medal_tv_date = findViewById(R.id.medal_tv_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        medal_tv_date.setText(getString(R.string.central_current_data) + simpleDateFormat.format(date));

        MaterialSpinner medal_spinner_month = findViewById(R.id.medal_spinner_month);
        //setItems方法用于设置下拉栏显示的内容。
        medal_spinner_month.setItems("2");
        medal_spinner_month.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                choosMonth =item;
            }
        });

        MaterialSpinner medal_spinnner_day = findViewById(R.id.medal_spinner_day);
        medal_spinnner_day.setItems("4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20");
        medal_spinnner_day.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                chooseDay = item;
                Snackbar.make(view, "当前显示的是"+choosMonth+"/"+chooseDay+"的奖牌榜", Snackbar.LENGTH_SHORT).show();
                requestForMedalPicture(Integer.parseInt(chooseDay));
            }
        });


    }



    //请求奖牌榜图片
    private void requestForMedalPicture(int chooseday){
        if (chooseday >= 4 && chooseday <= 8){
            iv_medal.setImageResource(R.mipmap.medal_2021);
        }else if (chooseday <= 12){
            iv_medal.setImageResource(R.mipmap.medal_2022);
        }else if (chooseday <= 16){
            iv_medal.setImageResource(R.mipmap.medal_2023);
        }else {
            iv_medal.setImageResource(R.mipmap.medal_2024);
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
}
