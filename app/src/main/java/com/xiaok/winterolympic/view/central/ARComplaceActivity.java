package com.xiaok.winterolympic.view.central;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Point;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.CoordinateUtils;
import com.xiaok.winterolympic.view.MainPageActivity;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class ARComplaceActivity extends AppCompatActivity implements SensorEventListener{

    private Point userWgsPoint;
    private Point userMercatorPoint;
    private final Point testPoint = new Point(113.5341055,34.81488975);
    private Point testMercatorPoint;

    private float zDegree;
    private float xDegree;
    private float yDegree;
    private double currentDistance;

    private TextView tv_info;
    private TextView tv_hold_item;
    private TextView tv_distance;
    private TextView tv_seats_num;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //可见
                case 0:
                    syncComplaceInfo(true);
                    break;
                //不可见
                case 1:
                    syncComplaceInfo(false);
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcomplace);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.central_ar_complace));
        }

        tv_info = findViewById(R.id.tv_info);
        tv_hold_item = findViewById(R.id.tv_hold_item);
        tv_distance = findViewById(R.id.tv_distance);
        tv_seats_num = findViewById(R.id.tv_seats_num);


        //初始化Poin为国家会议中心
        userWgsPoint = new Point(116.38376139624,39.997936144); //初始化为国家会议中心的坐标
        //转化为墨卡托坐标
        userMercatorPoint = getMercatorPoint(userWgsPoint);

        //初始化化工学院的墨卡托坐标
        testMercatorPoint = getMercatorPoint(testPoint);

        //开始获取用户坐标
        sendMessageEvery3Second();


        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 注册传感器(Sensor.TYPE_ORIENTATION(方向传感器);SENSOR_DELAY_FASTEST(0毫秒延迟);
        // SENSOR_DELAY_GAME(20,000毫秒延迟)、SENSOR_DELAY_UI(60,000毫秒延迟))
        sm.registerListener(ARComplaceActivity.this,
                sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);
    }


    //传感器报告新的值(方向改变)
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            zDegree = event.values[0];
            xDegree = event.values[1];
            yDegree = event.values[2];
            Logger.e("x旋转角度："+xDegree+"\n"+"y旋转角度："+yDegree+"\n"+"z旋转角度："+zDegree+"\n");
        }
    }
    //传感器精度的改变
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    /*
    * 每隔三秒获取一次当前用户坐标(墨卡托坐标)
    */
    private void sendMessageEvery3Second(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                userMercatorPoint = getMercatorPoint(MainPageActivity.point);
                DecimalFormat df = new DecimalFormat("0.000");
                currentDistance = calculationDistance();
                currentDistance = Double.parseDouble(df.format(currentDistance));
//                if (userDistance<=200){
//
//                }
                if (yDegree >= -30 && yDegree <= 30 && xDegree >=-120 && xDegree <=-60){
                    Message message = Message.obtain();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                else {
                    Message message = Message.obtain();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        },0,1000);
    }

    //将用户坐标转化为墨卡托，便于之后计算相对位置和距离
    private Point getMercatorPoint(Point wgsPoint){
        double []mercatorXY = CoordinateUtils.lngLat2Mercator(wgsPoint.getX(),wgsPoint.getY());
        Point mercatorPoint = new Point(mercatorXY[0],mercatorXY[1]);
        return mercatorPoint;
    }


    //计算用户当前与场馆距离
    private double calculationDistance(){
        double xDistance = userMercatorPoint.getX() - testMercatorPoint.getX();
        double yDistance = userMercatorPoint.getY() - testMercatorPoint.getY();
        //两点间距离公式算距离
        return Math.sqrt(Math.pow(xDistance,2)+Math.pow(yDistance,2));
    }

    //同步场馆信息
    private void syncComplaceInfo(Boolean isShow){
        if (isShow){
            tv_info.setVisibility(View.VISIBLE);
            tv_hold_item.setVisibility(View.VISIBLE);
            tv_distance.setVisibility(View.VISIBLE);
            tv_seats_num.setVisibility(View.VISIBLE);
        }else {
            tv_info.setVisibility(View.INVISIBLE);
            tv_hold_item.setVisibility(View.INVISIBLE);
            tv_distance.setVisibility(View.INVISIBLE);
            tv_seats_num.setVisibility(View.INVISIBLE);
        }
        tv_distance.setText(getString(R.string.ar_distance_text)+currentDistance+"m");
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
