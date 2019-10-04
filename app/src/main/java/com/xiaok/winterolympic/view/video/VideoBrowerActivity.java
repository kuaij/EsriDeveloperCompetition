package com.xiaok.winterolympic.view.video;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.VideoBrowerAdapter;
import com.xiaok.winterolympic.model.VideoBrower;
import com.xiaok.winterolympic.utils.FileUtils;

import java.util.LinkedList;
import java.util.List;

public class VideoBrowerActivity extends AppCompatActivity {

    private List<VideoBrower> aDate;
    private int[]videoAvatars;
    private String[]usernames;
    private String[]videoDates;
    private String[]videoDescripation;
    private String[]videoPaths;
    private String[]videoPosition;
    private String videoDescripation_01;

    private ListView lv_video;
    private ImageButton ib_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_brower);

        setCustomActionBar();  //加载自定义ActionBar

        lv_video = findViewById(R.id.video_listview);

        videoDescripation_01 = getIntent().getStringExtra("videoDescripation");

        videoAvatars = new int[]{R.drawable.head_picture,R.mipmap.video_avatar_02,R.mipmap.video_avatar_03,R.mipmap.video_avatar_04};
        usernames = new String[]{"xiaok","小张","王小明","李晓华"};
        videoDates = new String[]{"刚刚","09月17日10:12","08月22日18:15","08月22日17:42"};
        videoDescripation = new String[]{videoDescripation_01,"#雪容融和冰墩墩# 冬奥会和冬残奥会吉祥物来袭！","#北京冬奥# 北京赢了！2022冬奥，不见不散！！！","#冬奥来了# 冬奥宣传视频，浓浓的中国风，超赞！"};
        videoPaths = new String[]{FileUtils.VIDEO_01,FileUtils.VIDEO_02,FileUtils.VIDEO_03,FileUtils.VIDEO_04};
        videoPosition = new String[]{"北京市朝阳区","郑州市中原区","郑州市中原区","郑州市中原区"};

        aDate = new LinkedList<>();
        for (int i=0;i<videoAvatars.length;i++){
            aDate.add(new VideoBrower(videoAvatars[i],usernames[i],videoDates[i],
                    videoDescripation[i],videoPaths[i],videoPosition[i]));
        }

        lv_video.setAdapter(new VideoBrowerAdapter((LinkedList<VideoBrower>)aDate,VideoBrowerActivity.this));
        lv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        //左上角返回
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.record_share_actionbar, null);
        ib_back = mActionBarView.findViewById(R.id.record_ib_back);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

}
