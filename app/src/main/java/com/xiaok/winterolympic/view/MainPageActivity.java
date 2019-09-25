package com.xiaok.winterolympic.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.MyApplication;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.base.NameObserver;
import com.xiaok.winterolympic.model.UIAyncManager;
import com.xiaok.winterolympic.utils.notify.Notificaitons;
import com.xiaok.winterolympic.utils.notify.NotificationsPermission;
import com.xiaok.winterolympic.view.central.EscapeRouteActivity;
import com.xiaok.winterolympic.view.central.GamesScheduleActivity;
import com.xiaok.winterolympic.view.central.IndoorMapActivity;
import com.xiaok.winterolympic.view.central.MedalStandingsActivity;
import com.xiaok.winterolympic.view.central.MyAttentionActivity;
import com.xiaok.winterolympic.view.central.NavigationActivity;
import com.xiaok.winterolympic.view.central.RelatedLinkingActivity;
import com.xiaok.winterolympic.view.central.SceneComplaceActivity;
import com.xiaok.winterolympic.view.central.VolunteerActivity;
import com.xiaok.winterolympic.view.main.MainPageFragment;
import com.xiaok.winterolympic.view.main.MyCentralFragment;
import com.xiaok.winterolympic.view.video.RecordVideoActivity;
import com.xiaok.winterolympic.view.main.SearchCompetitionFragment;
import com.xiaok.winterolympic.view.setting.HelpActivity;
import com.xiaok.winterolympic.view.setting.MyProfileActivity;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.qiujuer.genius.ui.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.xiaok.winterolympic.utils.FileUtils.ZZU_GDB_CHECK;
import static com.xiaok.winterolympic.utils.FileUtils.ZZU_SCENE_CHECK;

public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,NameObserver{

    private static final int NOTIFY_CODE_ATHLETIC = 1;
    private static final int NOTIFY_CODE_ITEM = 2;

    private MainPageFragment mainPageFragment;
    private SearchCompetitionFragment navigationFragment;
    private MyCentralFragment settingeFragment;
    private Fragment[] fragments;
    private BottomNavigationView bottomNavigationView;
    private long mExitTime;
    private int lastFragment;
    private LinearLayout lin_nav_header;

    private NotificationManager mNM;
    private Context mContext;
    private String notifyTitle;
    private String notifyAthleticContent;
    private String notifyItmeContent;
    private String notifyButtonText;

    private TextView main_tv_username;
    private TextView main_tv_user_email;
    private TextView main_tv_day_last;
    private ImageView main_iv_photo;

    private String currentName = "xiaok";

    private Handler deleyNotify = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int notify_code = msg.what;
            switch (notify_code){
                case NOTIFY_CODE_ATHLETIC:
                    //运动员通知
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        Notificaitons.getInstance().sendSimpleNotification(mContext,mNM);
                    }
                    break;
                case NOTIFY_CODE_ITEM:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        Notificaitons.getInstance().sendActionNotification(mContext,mNM,notifyTitle,notifyItmeContent, notifyButtonText);
                    }
                    break;
                default:break;
            }

        }
    };



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化通知栏所需相关参数
        mContext = this;
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyTitle = getString(R.string.notify_title);
        notifyAthleticContent = getString(R.string.notify_content_athletic);
        notifyItmeContent = getString(R.string.notify_content_item);
        notifyButtonText = getString(R.string.notify_action_yes);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        new CopyDataAsyncTask().execute();
        new RelaseDateTask().execute(); //释放初始头像资源

        initFragment();//初始化fragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getPermission(); //动态权限申请
        }

        getNotifyPermission();

        //启动页面后开始计时。暂时内测为启动页面后一分钟后弹出的项目推送，再过一分钟后弹出运动员推送
        //todo 后期正式发布需要定时
        Message notifyAthleticMessage = Message.obtain();
        notifyAthleticMessage.what = NOTIFY_CODE_ATHLETIC;
        deleyNotify.sendMessageDelayed(notifyAthleticMessage,6000);
        Message notifyItmeMessage = Message.obtain();
        notifyItmeMessage.what = NOTIFY_CODE_ITEM;
        deleyNotify.sendMessageDelayed(notifyItmeMessage, 3000);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //左拉导航栏头部点击事件
        View headerView = navigationView.getHeaderView(0);
        LinearLayout linearLayout = headerView.findViewById(R.id.lin_nav_header);

        main_iv_photo = headerView.findViewById(R.id.imageView);

        //初始化用户名和邮箱
        main_tv_username = linearLayout.findViewById(R.id.main_tv_username);
        main_tv_username.setText(MyApplication.userName);
        main_tv_user_email = linearLayout.findViewById(R.id.main_tv_user_email);
        main_tv_user_email.setText(MyApplication.userEmail);

        main_tv_day_last = linearLayout.findViewById(R.id.tv_days_last);

        syncDaysLast();

        linearLayout.setOnClickListener(v -> startActivity(new Intent(MainPageActivity.this,MyProfileActivity.class)));

        //主界面侧栏与个人信息头像同步
        UIAyncManager.BindChangeModel(MyProfileActivity.class, new UIAyncManager.InfoTransferModelInterface<Bitmap>() {
            @Override
            public void changeUIByModel(Bitmap model) {
                MainPageActivity.this.main_iv_photo.setImageBitmap(model);
            }
        });

        //主界面侧栏用户名与个人信息同步
        UIAyncManager.BindChangeModel(MyProfileActivity.class, new UIAyncManager.InfoTransferModelInterface<String>() {
            @Override
            public void changeUIByModel(String model) {
                MainPageActivity.this.main_tv_username.setText(model);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //帮助
        if (id == R.id.main_page_help) {
            startActivity(new Intent(MainPageActivity.this, HelpActivity.class));
            return true;
        }else if (id == R.id.main_record_video){
            startActivity(new Intent(MainPageActivity.this, RecordVideoActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //侧拉栏点击事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            //奖牌榜
            case R.id.central_medal_standings:
                startActivity(new Intent(MainPageActivity.this, MedalStandingsActivity.class));
                break;
            //比赛日程
            case R.id.central_games_schedule:
                startActivity(new Intent(MainPageActivity.this, GamesScheduleActivity.class));
                break;
            //寻找志愿者
            case R.id.central_volunteer:
                startActivity(new Intent(MainPageActivity.this, VolunteerActivity.class));
                break;
            //室内地图
            case R.id.central_indoor_map:
                startActivity(new Intent(MainPageActivity.this, IndoorMapActivity.class));
                break;
            //三维场馆
            case R.id.central_3d_scene:
                startActivity(new Intent(MainPageActivity.this, SceneComplaceActivity.class));
                break;
            //路线规划
            case R.id.central_navigation:
                startActivity(new Intent(MainPageActivity.this, NavigationActivity.class));
                break;
            //相关链接
            case R.id.central_related_linking:
                startActivity(new Intent(MainPageActivity.this, RelatedLinkingActivity.class));
                break;
            //我的关注
            case R.id.central_my_attention:
                startActivity(new Intent(MainPageActivity.this, MyAttentionActivity.class));
                break;
            //逃生路线
            case R.id.central_escape_route:
                startActivity(new Intent(MainPageActivity.this, EscapeRouteActivity.class));
                break;
            default:break;
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getPermission(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainPageActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if (ContextCompat.checkSelfPermission(MainPageActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainPageActivity.this,permissions,1);
        }else{
            requsetLocation();
        }

    }


    //申请通知栏权限
    private void getNotifyPermission(){
        if (!NotificationsPermission.isNotificationEnabled(this)) {
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.show();

            View view = View.inflate(this, R.layout.dialogs_attention, null);
            dialog.setContentView(view);

            Button btn_confirm = view.findViewById(R.id.attention_btn_confirm);
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", MainPageActivity.this.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);

                        localIntent.setClassName("com.android.settings",
                                "com.android.settings.InstalledAppDetails");

                        localIntent.putExtra("com.android.settings.ApplicationPkgName",
                                MainPageActivity.this.getPackageName());
                    }
                    startActivity(localIntent);
                }
            });

            Button btn_close_dialogs = view.findViewById(R.id.attention_btn_cancle);
            btn_close_dialogs.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        }
    }


    //todo 开始定位
    private void requsetLocation(){
    }

    @Override
    public void syncUsename(String newName) {
        main_tv_username.setText(newName);
    }



    //初始化fragment和fragment数组
    private void initFragment()
    {

        mainPageFragment = new MainPageFragment();
        navigationFragment = new SearchCompetitionFragment();
        settingeFragment = new MyCentralFragment();
        fragments = new Fragment[]{mainPageFragment, navigationFragment, settingeFragment};
        lastFragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,mainPageFragment).show(mainPageFragment).commit();
        bottomNavigationView = findViewById(R.id.bnv);
        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }


    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.main_page:
                {
                    if(lastFragment!=0)
                    {
                        switchFragment(lastFragment,0);
                        lastFragment=0;
                    }
                    return true;
                }
                case R.id.main_search:
                {
                    if(lastFragment!=1)
                    {
                        switchFragment(lastFragment,1);
                        lastFragment=1;

                    }

                    return true;
                }
                case R.id.setting:
                {
                    if(lastFragment!=2)
                    {
                        switchFragment(lastFragment,2);
                        lastFragment=2;

                    }

                    return true;
                }


            }


            return false;
        }
    };


    //切换Fragment
    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);


        }
        transaction.show(fragments[index]).commitAllowingStateLoss();


    }

    //倒计时同步
    private void syncDaysLast(){
        String holdDate = "2022-2-4";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String currentDate = simpleDateFormat.format(date);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar=new GregorianCalendar();
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(holdDate);
            d2 = df.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long daysLast = ((d1.getTime()-d2.getTime())/(60*60*1000*24));
        main_tv_day_last.setText(daysLast+" days");
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
                            Toast.makeText(MainPageActivity.this, "必须同意所有权限才能使用本软件", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                    }
                    requsetLocation();
                } else {
                    Toast.makeText(MainPageActivity.this, "发生未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(),"再点一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private class CopyDataAsyncTask extends AsyncTask<Void, String, Boolean> {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainPageActivity.this);
            progressDialog.setTitle("数据部署");
            progressDialog.setMessage("正在准备数据，可能需要几分钟，请勿关闭此界面...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            publishProgress("正在部署数据");
            boolean gdb = copyAssetsDirToSdcard("gdb");
            boolean zip = copyAssetsDirToSdcard("zip");


            publishProgress("正在释放数据");
            unzipFile("scene.zip");

            publishProgress("正在执行清理操作");
            com.blankj.utilcode.util.FileUtils.deleteDir(getExternalFilesDir("zip"));

            publishProgress("正在保存文件状态");
            String gdbFiles = com.blankj.utilcode.util.FileUtils.listFilesInDir(ZZU_GDB_CHECK).toString();
            String sceneFiles = com.blankj.utilcode.util.FileUtils.listFilesInDir(ZZU_SCENE_CHECK).toString();

            return gdb && zip;
        }

        @Override
        protected void onProgressUpdate(String... values) {// String... 表示不定参数，即调用时可以传入多个String对象
            super.onProgressUpdate(values);

            progressDialog.setMessage(values[0] + "，请勿关闭此界面...");


        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


            progressDialog.dismiss();

            if (aBoolean) {

                Toast.makeText(MainPageActivity.this, "数据部署成功", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(MainPageActivity.this, "数据部署失败，请重新打开系统..", Toast.LENGTH_SHORT).show();

                CleanUtils.cleanExternalCache();

            }
        }
    }


    public boolean copyAssetsDirToSdcard(String dir) {


        String desFolder = getExternalFilesDir("") + "/" + dir;
        Logger.d( "copyAssetsDirToSdcard: " + desFolder);

        String[] mAssetsFileList = null;
        try {
            mAssetsFileList = getAssets().list(dir);
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }

        assert mAssetsFileList != null;
        for (String file : mAssetsFileList) {
            File desFile = new File(desFolder + "/" + file);
            if (!desFile.exists())
                try {
                    InputStream is = getAssets().open(dir + "/" + file);
                    FileIOUtils.writeFileFromIS(desFile, is, false);

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;

                }
        }

        return true;
    }

    private boolean unzipFile(String name) {


        File zipFile = getExternalFilesDir("zip/" + name);
//        File destDir = getExternalFilesDir("scene");
        File destDir = getExternalFilesDir("scene");

        assert zipFile != null;
        if (!zipFile.exists())
            return false;

        //魅族报错
//        ZipUtil.unpack(zipFile, destDir, Charset.forName("GBK"));

        try {
            net.lingala.zip4j.core.ZipFile zfile = new ZipFile(zipFile);
            zfile.setFileNameCharset("GBK");//中文支持
            assert destDir != null;
            zfile.extractAll(destDir.getPath());

        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }


    //个人信息界面显示的头像资源释放 todo 正式接入服务器后删除
    private class RelaseDateTask extends AsyncTask<Void, String, Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                relaseDate(String.valueOf(getCacheDir())+"/avatar.jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    private void relaseDate(String strOutFileName) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = getAssets().open("head_picture.jpg");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }




}
