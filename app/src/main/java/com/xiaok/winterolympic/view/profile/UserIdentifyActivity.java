package com.xiaok.winterolympic.view.profile;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.notify.Notificaitons;

import net.qiujuer.genius.ui.widget.Button;

public class UserIdentifyActivity extends AppCompatActivity {

    private NotificationManager mNM;
    private Context mContext;
    private String notifyTitle;
    private String notifyContent;
    private String notifyButtonText;

    private Handler deleyNotify = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //需要对志愿者进行调配时进行通知
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Notificaitons.getInstance().sendVolunteerActionNotification(mContext,mNM,notifyTitle,notifyContent,notifyButtonText);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_identify);

        //初始化通知栏所需相关参数
        mContext = this;
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyTitle = getString(R.string.notify_title_volunteer);
        notifyContent = getString(R.string.notify_content_volunteer);
        notifyButtonText = getString(R.string.notify_action_yes_volunteer);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.profile_user_identify));
        }

        //启动页面后开始计时。暂时内测为启动页面后3s后弹出志愿者通知
        //todo 后期正式发布需要定时
        Message notifyVolunteerMessage = Message.obtain();
        deleyNotify.sendMessageDelayed(notifyVolunteerMessage,3000);

        Button btnVolunteer = findViewById(R.id.identify_btn_volunteer);
        btnVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button btnAthletic = findViewById(R.id.identify_btn_athletic);
        btnAthletic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
