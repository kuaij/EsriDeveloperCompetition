package com.xiaok.winterolympic.view.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.teprinciple.mailsender.Mail;
import com.teprinciple.mailsender.MailSender;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.custom.ProgressButton;
import com.xiaok.winterolympic.utils.ToastUtils;
import com.xiaok.winterolympic.view.LoginActivity;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.Random;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_email;
    private EditText et_identify_code;
    private EditText et_password;
    private EditText et_confirm_password;
    private Button btn_get_identify;
    private ProgressButton btn_post;

    private String username;
    private String currentUsername;
    private String email;
    private String identifyCode;
    private String randomIdentifyCode;
    private String password;
    private String confirm_password;

    private SharedPreferences sp;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            resultUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initView(); //初始化控件
        sp = getSharedPreferences("user_config_01",MODE_PRIVATE);

        currentUsername = getCurrentUsername();
        //用户名
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //绑定邮箱
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();
            }
        });

        //验证码输入框
        et_identify_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                identifyCode = s.toString();
            }
        });



        //获取验证码
        btn_get_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //随机一个验证码，用户之后发送和验证
                randomIdentifyCode = getRandomIdentifyCode(6);
                sendEmail();
            }
        });


        //密码
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (username == null){
                    ToastUtils.showSingleToast("用户名不能为空！");
                }else if (email == null){
                    ToastUtils.showSingleToast("邮箱不能为空！");
                }
            }
        });

        //确认密码
        et_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirm_password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //提交
        btn_post.setBgColor(Color.parseColor("#7E57C2"));
        btn_post.setButtonText(getString(R.string.forget_post));
        btn_post.setTextColor(Color.WHITE);
        btn_post.setProColor(Color.WHITE);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post.startAnim();
                Message msg = Message.obtain();
                mHandler.sendMessageDelayed(msg, 1800);

            }
        });

    }

    private void initView(){
        et_username = findViewById(R.id.forget_et_name);
        et_email = findViewById(R.id.forget_et_email);
        et_identify_code = findViewById(R.id.forget_et_identify_code);
        btn_get_identify = findViewById(R.id.forget_btn_get_identify_code);
        et_password = findViewById(R.id.forget_et_password);
        et_confirm_password = findViewById(R.id.forget_confirm_password);
        btn_post = findViewById(R.id.forget_btn_post);
    }


    private String getCurrentUsername(){
        sp = getSharedPreferences("user_config_01",MODE_PRIVATE);
        return sp.getString("username","xiaok");
    }


    private void saveNewUserInfo(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password",password);
        editor.apply();
    }


    private String getRandomIdentifyCode(int codeLength){
        StringBuilder randomCodeString = new StringBuilder();

        Random r = new Random();
        for (int i=0;i<codeLength;i++){
            int num = r.nextInt(10);
            randomCodeString.append(num);
        }
        return randomCodeString.toString();
    }


    private void resultUI(){
        btn_post.stopAnim(new ProgressButton.OnStopAnim() {
            @Override
            public void Stop() {
                if (username == null){
                    ToastUtils.showSingleToast("用户名不能为空！");
                }else if (email == null){
                    ToastUtils.showSingleToast("邮箱不能为空！");
                }else if (identifyCode == null){
                    ToastUtils.showSingleToast("请输入验证码！");
                } else if (password == null){
                    ToastUtils.showSingleToast("请输入密码!");
                }else if (confirm_password == null){
                    ToastUtils.showSingleToast("请再次输入密码进行确认!");
                }else if (!confirm_password.equals(password)){
                    ToastUtils.showSingleToast("两次输入的密码不一致");
                }else {
                    if (identifyCode.equals(randomIdentifyCode)){
                        if (username.equals("admin")){
                            ToastUtils.showSingleToast("您没有权限修改此账户的密码！");
                        }else if (username.equals(currentUsername)){
                            saveNewUserInfo();
                            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                            intent.putExtra("username",username);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }else {
                            ToastUtils.showSingleToast("该用户名不存在，请注意区分大小写！");
                        }

                    }else {
                        ToastUtils.showSingleToast("验证码错误！");
                    }

                }
            }
        });


    }


    private void sendEmail(){
        //接收方邮箱列表
        ArrayList<String> addressList = new ArrayList<>();
        addressList.add(email);

        Mail mail = new Mail();
        mail.setMailServerHost("smtp.qq.com");
        mail.setMailServerPort("587");
        mail.setFromAddress("xiaok.huc@qq.com");
        mail.setPassword("xnomesfsphrsbjaj");
        mail.setToAddress(addressList);
        mail.setSubject(getString(R.string.forget_page_name));
        mail.setContent("您好！\n" +
                "<div>\n" +
                "    您正在找回密码，为确保是你本人操作，请在邮箱验证码输入框输入下方验证码：\n" +
                "</div>\n" +
                "<div style=\"text-align: center;\">\n" +
                "    <font size=\"5\" style=\"\" color=\"#3366ff\">\n" +
                "        <u>\n" +
                randomIdentifyCode+ "\n" +
                "        </u>\n" +
                "    </font>\n" +
                "</div>\n" +
                "<div style=\"text-align: left;\">\n" +
                "        如果这不是你本人所为，请忽略。请勿向任何人泄漏您收到的验证码。\n" +
                "</div>\n" +
                "<div style=\"text-align: left;\">\n" +
                "        <br>\n" +
                "</div>\n" +
                "<div style=\"text-align: left;\">\n" +
                "        冬奥小助 账号团队敬上\n" +
                "</div>");

        // 发送邮箱
        MailSender.getInstance().sendMail(mail, new MailSender.OnMailSendListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ForgetPasswordActivity.this,"成功发送请求，验证码将在60s内发送到您的手机上。",Toast.LENGTH_LONG).show();
                //设置按钮上方倒计时
                new TimeCount(60000,1000).start();
            }

            @Override
            public void onError(Throwable throwable) {
                ToastUtils.showSingleToast("请求失败，请检查网络或稍后重试！");
            }
        });
    }


    //按钮点击后计时
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            //按钮上的文本显示
            btn_get_identify.setText("重新获取");
            //设置按钮可点击
            btn_get_identify.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            //设置按钮不可点击
            btn_get_identify.setClickable(false);
            //设置倒计时时的显示文本
            btn_get_identify.setText(millisUntilFinished/1000 + "秒后重新获取");
        }
    }
}
