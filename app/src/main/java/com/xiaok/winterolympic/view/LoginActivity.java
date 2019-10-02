package com.xiaok.winterolympic.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xiaok.winterolympic.MyApplication;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.view.login.ForgetPasswordActivity;
import com.xiaok.winterolympic.view.login.RegisterActivity;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int REGISTER_NEW_USER = 1;
    private static final int FORGET_PASSWORD = 2;

    private EditText et_usename;
    private EditText et_password;

    private RequestQueue queue;

    private String currentUsername1;
    private String password1;
    private String currentUsername2;
    private String password2;

    private boolean isFirstRun = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        getUserInfo();

        //设置视频背景的代码代码
        final VideoView videoview = findViewById(R.id.videoview);
        final String videopath = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.login).toString();
        videoview.setVideoPath(videopath);
        videoview.start();
        videoview.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        });
        videoview.setOnCompletionListener(mediaPlayer -> {
            videoview.setVideoPath(videopath);
            videoview.start();
        });

        //登录按钮
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            String usename = et_usename.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            if (TextUtils.isEmpty(usename) || TextUtils.isEmpty(password)){
                Toast.makeText(getApplicationContext(),"用户名或密码不能为空",Toast.LENGTH_SHORT).show();

            }else if (usename.equals(currentUsername1) && password.equals(password1)){
                MyApplication.userName = currentUsername1;
                if (isFirstRun){
                    //第一次运行时选择用户兴趣
                    startActivity(new Intent(getApplicationContext(),ChooseAttentionActivity.class));
                    isFirstRun = false;
                }else {
                    startActivity(new Intent(LoginActivity.this, MainPageActivity.class));
                }


            }else if (usename.equals(currentUsername2) && password.equals(password2)){
                MyApplication.userName = currentUsername2;
                if (isFirstRun){
                    //第一次运行时选择用户兴趣
                    startActivity(new Intent(getApplicationContext(),ChooseAttentionActivity.class));
                    isFirstRun = false;
                }else {
                    startActivity(new Intent(LoginActivity.this, MainPageActivity.class));
                }


            }else {
                Toast.makeText(getApplicationContext(),"用户名或密码错误，注意区分大小写",Toast.LENGTH_SHORT).show();
            }
        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> {
            //注册
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(registerIntent, REGISTER_NEW_USER);
        });

        TextView tv_forget_password = findViewById(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(v -> {
            //忘记密码
            Intent forgetIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivityForResult(forgetIntent, FORGET_PASSWORD);
        });
    }

    private void initView(){
        et_usename = findViewById(R.id.et_usename);
        et_password = findViewById(R.id.et_password);
    }


    private void getUserInfo(){
        SharedPreferences sp1 = getSharedPreferences("user_config",MODE_PRIVATE);
        currentUsername1 = sp1.getString("username","error");
        password1 = sp1.getString("password","error");
        SharedPreferences sp2 = getSharedPreferences("user_config_01",MODE_PRIVATE);
        currentUsername2 = sp2.getString("username","error");
        password2 = sp2.getString("password","error");
    }


    //向服务器上提交数据 todo 服务器搭建完成后进行测试
    private boolean postUserInfo(String username, String password){
        boolean isCorrect = false;
        //构建向服务器上发送的Map对象
        Map<String, String> postMap = new ArrayMap<>();
        postMap.put("usename",username);
        postMap.put("password",password);
        queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.baidu.com",
                new SuccessfulListener(),new ErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                 return postMap;
            }
        };

        return isCorrect;
    }

    private class SuccessfulListener implements Response.Listener{

        @Override
        public void onResponse(Object response) {

        }
    }
    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }


    //回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //注册新用户回调
            case REGISTER_NEW_USER:
                break;
            //忘记密码回调
            case FORGET_PASSWORD:
                break;
        }


    }
}
