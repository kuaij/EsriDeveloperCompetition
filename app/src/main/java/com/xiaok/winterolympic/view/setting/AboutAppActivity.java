package com.xiaok.winterolympic.view.setting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.xiaok.winterolympic.R;


public class AboutAppActivity extends AppCompatActivity {

    String[] data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("关于"+ getString(R.string.app_name));
        }

        data = new String[]{getString(R.string.app_check_for_undate),
                            getString(R.string.app_new_version),
                            getString(R.string.app_help_page),
                            getString(R.string.setting_opinions_view)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AboutAppActivity.this,
                android.R.layout.simple_list_item_1,data);
        ListView listview = findViewById(R.id.lv_setting2);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((parent, view, position, id) -> {
            //判断用户点击的item
            switch (position){
                //检查版本更新
                case 0:
                    new CheckVesionAsyncTask().execute();
                    break;
                //新版功能介绍
                case 1:
                    Intent intent = new Intent(AboutAppActivity.this,HelpActivity.class);
                    intent.putExtra("title","新版功能介绍");
                    startActivity(intent);
                    break;
                //帮助
                case 2:
                    startActivity(new Intent(AboutAppActivity.this,HelpActivity.class));
                    break;
                //反馈
                case 3:
                    startActivity(new Intent(AboutAppActivity.this,OpinionActivity.class));
                    break;
                default:break;
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

    private class CheckVesionAsyncTask extends AsyncTask<Void, String, Boolean> {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AboutAppActivity.this);
            progressDialog.setTitle("版本检查");
            progressDialog.setMessage("正在检查网络连接...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            publishProgress("正在检查版本...");
            SystemClock.sleep(2500);
            boolean checkState = checkVesion();
            return checkState;
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
                Toast.makeText(AboutAppActivity.this, "当前为最新版本", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AboutAppActivity.this, "检测到有更新的版本，请前往官网更新", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private boolean checkVesion(){
        return true;
    }
}
