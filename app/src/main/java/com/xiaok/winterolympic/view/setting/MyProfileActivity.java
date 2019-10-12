package com.xiaok.winterolympic.view.setting;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.MyApplication;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.UserProfileAdapter;
import com.xiaok.winterolympic.model.UIAyncManager;
import com.xiaok.winterolympic.model.UserProfile;
import com.xiaok.winterolympic.utils.ToastUtils;
import com.xiaok.winterolympic.utils.notify.Notificaitons;
import com.xiaok.winterolympic.view.profile.ChangeAvatarActivity;
import com.xiaok.winterolympic.view.profile.EditNameActivity;
import com.xiaok.winterolympic.view.profile.MoreProfileActivity;
import com.xiaok.winterolympic.view.profile.UserIdentifyActivity;

import net.qiujuer.genius.ui.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String[] names = null;
    private String[] values = null;

    //requestCode
    private static final int CHANGE_NAME = 1;
    private static final int USER_IDENTIFY = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int OPEN_GALLERY = 4;

    private NotificationManager mNM;
    private Context mContext;
    private String notifyTitle;
    private String notifyContent;
    private String notifyButtonText;

    private ListView list_show;
    private EditText etIdentifyCode;
    private Button btnConfirm;
    private RadioButton rbVolunteer;
    private RadioButton rbAthletic;

    private boolean isVolunteer = false;
    private boolean isAthletic = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.profile_page);
        }


        list_show = findViewById(R.id.profile_lv_show);
//        final LayoutInflater inflater = LayoutInflater.from(this);
//        View headView = inflater.inflate(R.layout.list_header, null, false);
//        View footView = inflater.inflate(R.layout.list_header, null, false);

        names = new String[]{getString(R.string.profile_photo),getString(R.string.profile_name),
                getString(R.string.profile_email),getString(R.string.profile_user_type),getString(R.string.profile_more)};

        values = new String[]{getCacheDir() + "/avatar.jpg","xiaok",
                "1298727334@qq.com",getString(R.string.profile_user_identify),""};

        //初始化同步用户名和邮箱
        values[1] = MyApplication.userName;
        values[2] = MyApplication.userEmail;

        List<UserProfile> aData = new LinkedList<>();
        for (int i=0;i<names.length;i++){
            aData.add(new UserProfile(names[i],values[i]));
        }
//        //添加表头和表尾需要写在setAdapter方法调用之前！！！
//        list_show.addHeaderView(headView);
//        list_show.addFooterView(footView);

        list_show.setAdapter(new UserProfileAdapter((LinkedList<UserProfile>)aData,MyProfileActivity.this));
        list_show.setOnItemClickListener(this);

        //初始化通知栏所需相关参数
        mContext = this;
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyTitle = getString(R.string.notify_title_volunteer);
        notifyContent = getString(R.string.notify_content_volunteer);
        notifyButtonText = getString(R.string.notify_action_yes_volunteer);

        /*
        用户认证弹出框相关事件
         */
        View dialogsView = View.inflate(MyProfileActivity.this,R.layout.dialogs_identify,null);
        etIdentifyCode = dialogsView.findViewById(R.id.identify_et_code);
//        btnConfirm = dialogsView.findViewById(R.id.identify_btn_confirm);
        rbVolunteer = dialogsView.findViewById(R.id.rb_volunteer);
        rbAthletic = dialogsView.findViewById(R.id.rb_athletic);

        //输入框右侧删除图标响应事件
        etIdentifyCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = etIdentifyCode.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > etIdentifyCode.getWidth()
                        - etIdentifyCode.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    Logger.e("删除文本");
                }
                return false;
            }
        });
    }


    private void identifySuccess(String userType){
        //对志愿者进行调配时进行通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notificaitons.getInstance().sendVolunteerActionNotification(mContext,mNM,notifyTitle,notifyContent,notifyButtonText);
        }
        //更新ListView
        values[3] = userType;
        List<UserProfile> aData = new LinkedList<>();
        for (int i=0;i<names.length;i++){
            aData.add(new UserProfile(names[i],values[i]));
        }

        list_show.setAdapter(new UserProfileAdapter((LinkedList<UserProfile>)aData,MyProfileActivity.this));
        list_show.setOnItemClickListener(this);
    }

    private String getUserType(){
        String userType;
        if (isVolunteer){
            userType = getString(R.string.identify_type_volunteer);
        }else {
            userType = getString(R.string.identify_type_athletic);
        }

        return userType;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        switch (position){
            //修改头像
            case 0:
                initPopWindow(view);
                break;
            //用户名
            case 1:
                Intent nameIntent = new Intent(MyProfileActivity.this, EditNameActivity.class);
                String oldName = values[1];
                nameIntent.putExtra("oldName",oldName);
                startActivityForResult(nameIntent,CHANGE_NAME);
                break;
            //绑定邮箱
            case 2:
                Toast.makeText(MyProfileActivity.this, "修改绑定邮箱",Toast.LENGTH_SHORT).show();
                break;
            //用户认证
            case 3:
                if (values[3].equals(getString(R.string.identify_type_volunteer))){
                    startActivity(new Intent(MyProfileActivity.this, UserIdentifyActivity.class));
                }else {
                    showUserIdentifyDialogs();
                }
                break;
            //更多
            case 4:
                startActivity(new Intent(MyProfileActivity.this, MoreProfileActivity.class));
                break;
            default:break;
        }

    }

    //选择图片来源（相机/相册）
    private void initPopWindow(View v) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.avatar_popup_item, null, false);
        Button btn_camera = view.findViewById(R.id.btn_avatar_camera);
        Button btn_gallery = view.findViewById(R.id.btn_avatar_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_avatar_cancel);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        popWindow.setFocusable(true);

//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        popWindow.setBackgroundDrawable(dw);


        // 设置popWindow的显示和消失动画
        popWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);


        //相机
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MyProfileActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent,TAKE_PHOTO);
                }
            }
        });
        //相册
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MyProfileActivity.this, ChangeAvatarActivity.class),OPEN_GALLERY);
            }
        });
        //取消
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
    }



    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MyProfileActivity.this, "必须同意所有权限才能使用本软件", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent,TAKE_PHOTO);
                } else {
                    Toast.makeText(MyProfileActivity.this, "发生未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }

    }


    private void showUserIdentifyDialogs(){
        new MaterialDialog.Builder(MyProfileActivity.this)
                .title(getString(R.string.identify_name))
                .canceledOnTouchOutside(false)
                .customView(R.layout.dialogs_identify, true)
                .positiveText(getString(R.string.identify_confirm))
                .negativeText(getString(R.string.identify_cancle))
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
//                            Logger.e("按钮可以点击");
//                            isVolunteer = rbVolunteer.isChecked();
//                            isAthletic = rbAthletic.isChecked();
//                            if (isAthletic || isVolunteer){
//                                ToastUtils.showSingleToast("请选择认证类型");
//                            }else if (etIdentifyCode.getText().toString().trim() == null){
//                                ToastUtils.showSingleToast("请输入您的认证编号！");
//                            }else if (etIdentifyCode.getText().toString().trim().contains("000")){
//                                identifySuccess(getUserType());
//                            }else {
//                                ToastUtils.showSingleToast("认证编号错误！");
//                            }
                            ToastUtils.showSingleToast("认证通过！");
                            identifySuccess(getString(R.string.identify_type_volunteer));
                        } else if (which == DialogAction.NEGATIVE) {
                            dialog.dismiss();
                        }

                    }
                })
                .show();
    }

    private void syncUserAvatar(Bitmap bitmap){
        //对图片进行裁剪，保留中间部分，保证形状为正方形
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();
        int newHeight,newWidth;
        Bitmap newBitmap;
        if (h >= w){
            newHeight = w;
            newWidth = w;
            newBitmap = Bitmap.createBitmap(bitmap,0,(h-w)/2,newWidth,newHeight);
        }else {
            newHeight = h;
            newWidth = h;
            newBitmap = Bitmap.createBitmap(bitmap,(w-h)/2,0,newWidth,newHeight);
        }
        //先将照片缓存到本地
        File filePic;
        try {
            filePic = new File(getCacheDir() + "/avatar.jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<UserProfile> aData = new LinkedList<>();
        for (int i=0;i<names.length;i++){
            aData.add(new UserProfile(names[i],values[i]));
        }
        list_show.setAdapter(new UserProfileAdapter((LinkedList<UserProfile>) aData,MyProfileActivity.this));
        list_show.setOnItemClickListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            switch (requestCode){
                //修改用户名
                case CHANGE_NAME:

                    String newName = data.getStringExtra("newName"); //获取回传回来的用户名
                    if (newName != null){
                        values[1] = newName; //更新用户名
                        List<UserProfile> aData = new LinkedList<>();
                        for (int i=0;i<names.length;i++){
                            aData.add(new UserProfile(names[i],values[i]));
                        }

                        list_show.setAdapter(new UserProfileAdapter((LinkedList<UserProfile>)aData,MyProfileActivity.this));
                        list_show.setOnItemClickListener(this);
                    }

                    break;
                //拍照，并回调照片
                case TAKE_PHOTO:
                    Bitmap cameraPhoto = data.getParcelableExtra("data");
                    syncUserAvatar(cameraPhoto);
                    UIAyncManager.PostChangeByModel( this,cameraPhoto);
                    break;
                //回调相册图片
                case OPEN_GALLERY:
                    break;
            }
        }

    }
}
