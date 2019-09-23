package com.xiaok.winterolympic.view.notify;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaok.winterolympic.R;

public class WatchLiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_linking);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.notify_watch_live));
        }

        WebView wv_offical = findViewById(R.id.related_webview);
        wv_offical.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });
        //支持javascript
        wv_offical.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        wv_offical.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        wv_offical.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        wv_offical.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        wv_offical.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv_offical.getSettings().setLoadWithOverviewMode(true);
        wv_offical.loadUrl("https://tv.cctv.com/cctv5/");
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
