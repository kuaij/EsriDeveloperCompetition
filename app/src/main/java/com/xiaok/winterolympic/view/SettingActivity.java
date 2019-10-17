package com.xiaok.winterolympic.view;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.model.LocalDataModel;
import com.xiaok.winterolympic.view.setting.AboutAppActivity;
import com.xiaok.winterolympic.view.setting.MyProfileActivity;
import com.xiaok.winterolympic.view.setting.OpinionActivity;

import java.io.File;

import static com.blankj.utilcode.util.FileUtils.deleteDir;

public class SettingActivity extends AppCompatActivity {

    String[] data = null;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
        listview = findViewById(R.id.lv_setting);

        //ListView 显示数据
        data  = new String[]{
                getString(R.string.setting_opinions_view),
                getString(R.string.setting_about_app),
                getString(R.string.setting_clear_cache),
                getString(R.string.setting_app_grades),
                getString(R.string.setting_my_profile)};

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.setting_name));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,data);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((parent, view, position, id) -> {
            //判断用户点击的item
            switch (position){
                //意见和反馈
                case 0:
                    startActivity(new Intent(this, OpinionActivity.class));
                    break;
                //关于App
                case 1:
                    startActivity(new Intent(this, AboutAppActivity.class));
                    break;
                //清除缓存
                case 2:
                    new DeleteCacheAsyncTask().execute(); //清理缓存
                    break;
                //给app打分
                case 3:
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }catch(Exception e){
                        Toast.makeText(this, "您的手机没有安装Android应用市场", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                //个人信息
                case 4:
                    startActivity(new Intent(SettingActivity.this, MyProfileActivity.class));
                    break;
                default:break;
            }
        });

        //退出登录
        Button btn_log_out = findViewById(R.id.btn_log_out);
        btn_log_out.setOnClickListener(v -> {
            //回到登录页面
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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

    private DialogInterface.OnClickListener click1 = (arg0, arg1) -> {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://tp.wjx.top/jq/30173809.aspx"));
        startActivity(intent);
    };
    private DialogInterface.OnClickListener click2 = (arg0, arg1) -> arg0.cancel();


    @SuppressLint("StaticFieldLeak")
    private class DeleteCacheAsyncTask extends AsyncTask<Void, String, Boolean> {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SettingActivity.this);
            progressDialog.setTitle("缓存清理");
            progressDialog.setMessage("正在准备执行清理...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            publishProgress("正在执行清理操作...");
            SystemClock.sleep(500);

            publishProgress("正在清除缓存文件...");
            boolean isCleanOK = deleteFilesByDirectory(getCacheDir());

            return isCleanOK;
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
                Toast.makeText(SettingActivity.this, "缓存已清除！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, "缓存清理失败，请稍后重试", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private boolean deleteFilesByDirectory(File directory) {
        /*if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }*/

        if (directory != null && directory.isDirectory()) {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(directory, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return directory.delete();
    }


}
