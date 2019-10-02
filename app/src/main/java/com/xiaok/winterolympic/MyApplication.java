package com.xiaok.winterolympic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.utils.notify.NotificationChannels;


public class MyApplication extends Application {

    public static Context sInstance;
    public static int event_name_id = R.string.search_no_limit;
    public static String userName = "xiaok";
    public static String userEmail = "kuaijian.huc@qq.com";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Logger
        Logger.addLogAdapter(new AndroidLogAdapter());
        sInstance = getApplicationContext();
        NotificationChannels.createAllNotificationChannels(this);

        //创建sp储存用户名密码，初始存入admin
        SharedPreferences sp = getSharedPreferences("user_config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username","admin");
        editor.putString("password","123456");
        editor.apply();
    }
}
