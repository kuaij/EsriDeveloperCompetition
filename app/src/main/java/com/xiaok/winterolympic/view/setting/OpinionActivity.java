package com.xiaok.winterolympic.view.setting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.xiaok.winterolympic.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpinionActivity extends AppCompatActivity {

    private EditText et_message,et_address;
    private Button btn_post;

    private String address;
    private String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);

        setCustomActionBar();

        et_message = findViewById(R.id.et_message);
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                message = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (message != null && address != null){
                    btn_post.setEnabled(true);
                }else {
                    btn_post.setEnabled(false);
                }
            }
        });


        et_address = findViewById(R.id.et_address);
        et_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                address = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (message != null && address != null){
                    btn_post.setEnabled(true);
                }else {
                    btn_post.setEnabled(false);
                }
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostMessageAsyncTask().execute();
            }
        });

    }


    //左上方取消按钮对话框的两种选项实现
    private DialogInterface.OnClickListener click_tv1 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            finish();
        }
    };
    private DialogInterface.OnClickListener click_tv2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            arg0.cancel();
        }
    };


    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.opinion_actionbar, null);
        btn_post = mActionBarView.findViewById(R.id.opinion_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("意见与反馈");
    }


    //左上角返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (message != null || address != null){
                    showDialog();
                } else {
                    finish();
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialog(){
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(OpinionActivity.this);
        alertdialogbuilder.setMessage("您的编辑尚未提交，是否退出？");
        alertdialogbuilder.setPositiveButton("确定", click_tv1);
        alertdialogbuilder.setNegativeButton("取消", click_tv2);
        AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class PostMessageAsyncTask extends AsyncTask<Void, String, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OpinionActivity.this);
            progressDialog.setTitle("提交反馈");
            progressDialog.setMessage("检查网络...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            SystemClock.sleep(1200);
            publishProgress("正在进行提交...");
            SystemClock.sleep(2200);
            boolean analyseState = analyResult();
            return analyseState;
        }

        @Override
        protected void onProgressUpdate(String... values) {// String... 表示不定参数，即调用时可以传入多个String对象
            super.onProgressUpdate(values);

            progressDialog.setMessage(values[0] + getString(R.string.opinion_not_close));
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (aBoolean) {
                //提交成功后内容清空
                et_address.setText("");
                et_message.setText("");
                address = null;
                message = null;
                Toast.makeText(getApplicationContext(), getString(R.string.opinion_post_success), Toast.LENGTH_SHORT).show();
                SystemClock.sleep(1000);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.opinion_post_failed), Toast.LENGTH_SHORT).show();

            }

        }
    }

    private boolean analyResult(){
        return true;
    }


    //重写手机自带键盘的返回键，防止用户误碰导致编辑结束
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (message != null || address != null){
                showDialog();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

}
