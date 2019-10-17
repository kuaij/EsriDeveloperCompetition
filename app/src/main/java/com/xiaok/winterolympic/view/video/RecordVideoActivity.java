package com.xiaok.winterolympic.view.video;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.ToastUtils;

import net.qiujuer.genius.ui.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.ielse.view.SwitchView;

public class RecordVideoActivity extends AppCompatActivity {

    private ImageButton ib_back;
    private Button btnUpload;
    private Button btnEdit;
    private Button btnRestart;
    private EditText etDescripation;
    private SwitchView svPosition;
    private TextView tvTopic;
    private TextView tvSaveRecord;
    private VideoView videoView;
    private ImageView iv_play;

    private boolean startPlaying = false;
    private boolean isPositionOK = false;

    private static final int RECORD_VIDEO = 1;

    private String videoDescripation; //视频描述
    private String topicNormalString; //话题分隔符

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        setCustomActionBar();  //加载自定义ActionBar

        initView();


        //视频描述
        etDescripation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                videoDescripation = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //位置信息
        svPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPositionOK){
                    ToastUtils.showSingleToast("显示位置信息");
                    showPositionInfo();
                    isPositionOK = true;
                }else {
                    ToastUtils.showSingleToast("不显示位置信息");
                    isPositionOK = false;
                }
            }
        });

        //重拍，掉起录像功能
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成Intent.
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //启动摄像头应用程序
                startActivityForResult(intent, RECORD_VIDEO);
            }
        });

        //播放
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView != null){
                    iv_play.setImageResource(R.mipmap.ic_record_stop);
                    videoView.start();
                    videoView.setBackground(null);
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            iv_play.setImageResource(R.mipmap.ic_record_play);
                        }
                    });
                }
            }
        });

        //话题
        tvTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topicNormalString = "<font color='#1e90ff'>## </font>"; //话题双#号分隔符
                etDescripation.setText(Html.fromHtml(topicNormalString)+etDescripation.getText().toString().trim());
                etDescripation.setSelection(1);
            }
        });

        //左上角返回
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //发布
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoDescripation.equals(null)) ToastUtils.showSingleToast("视频描述不能为空！");
                else if (uploadVideo()){
                    ToastUtils.showSingleToast("发布成功！");
                    Intent uploadIntent = new Intent(RecordVideoActivity.this,VideoBrowerActivity.class);
                    uploadIntent.putExtra("videoDescripation",videoDescripation);
                    startActivity(uploadIntent);
                }
            }
        });

        tvSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(uri, "r");
                    FileInputStream fis = videoAsset.createInputStream();
                    File tmpFile = new File(Environment.getExternalStorageDirectory()+"/Android/data/com.xiaok.winterolympic/files/scene","video_01.mp4");
                    FileOutputStream fos = new FileOutputStream(tmpFile);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                    fis.close();
                    fos.close();
                } catch (IOException io_e) {
                    // TODO: handle error
                }
            }
        });

    }

    private void initView(){
        btnUpload = findViewById(R.id.record_btn_upload);
        btnEdit = findViewById(R.id.record_btn_edit);
        btnRestart = findViewById(R.id.record_btn_restart);
        etDescripation = findViewById(R.id.record_et_descripation);
        svPosition = findViewById(R.id.record_sv_position);
        tvSaveRecord = findViewById(R.id.record_tv_save);
        tvTopic = findViewById(R.id.record_tv_topic);
        videoView = findViewById(R.id.record_vv_cover);
        iv_play = findViewById(R.id.record_iv_play);
    }

    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.record_actionbar, null);
        ib_back = mActionBarView.findViewById(R.id.record_ib_back);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private boolean uploadVideo(){
        SystemClock.sleep(2000);
        return true;
    }

    private void showPositionInfo(){
        com.orhanobut.logger.Logger.e("显示位置信息");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RECORD_VIDEO:
                uri=data.getData();
                videoView.setVideoURI(uri);
                //加载封面
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(RecordVideoActivity.this, uri);
                Bitmap bitmap = media.getFrameAtTime();
                Drawable drawable = new BitmapDrawable(bitmap);
                videoView.setBackground(drawable);
                break;
        }
    }
}
