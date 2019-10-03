package com.xiaok.winterolympic.view.central;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.ScreenUtils;
import com.xiaok.winterolympic.utils.ToastUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAttentionActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout_item)
    TagFlowLayout tabLayoutItem;
    private Object[] strItem = null;
    private Object[] strHuman = null;

    private HashMap<String, List<String>> stringHashMap = new HashMap<>();

    public static List<Integer> choiceList = new ArrayList<>(); //保存用户选择关注的序号

    private String json; //用户选择结果

    private String startString = null;

    private List<String> list = new ArrayList<>();
    private Gson gson = new Gson();
    private List<String> list1 = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attention);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle(getString(R.string.focus_name));
        }

        startString = getString(R.string.focus_start);

        strItem = new Object[]{
                getString(R.string.focus_start), getString(R.string.focus_alpine_skiing),getString(R.string.focus_biathlon),
                getString(R.string.focus_bobsleiqn), getString(R.string.focus_cross_skating),getString(R.string.focus_curling),
                getString(R.string.focus_figure_skating), getString(R.string.focus_free_styleing),getString(R.string.focus_ice_hockey),
                getString(R.string.focus_luge),getString(R.string.focus_nordic_combined),getString(R.string.focus_short_track),
                getString(R.string.focus_skeleton),getString(R.string.focus_ski_jumping),getString(R.string.focus_snowboarding),
                getString(R.string.focus_speed_skating)};

        tabLayoutItem.setAdapter(new TagAdapter<Object>(strItem) {
            @Override
            public View getView(FlowLayout flowLayout, int i, Object s) {
                if (String.valueOf(s).contains(startString)) {
                    View inflat = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adapter_activity_label_title, null);
                    ImageView adapterIv = inflat.findViewById(R.id.adapter_iv);
                    TextView adapterTitle = inflat.findViewById(R.id.adater_title);
                    if (String.valueOf(s).equals(startString)) {
                        adapterIv.setImageResource(R.drawable.circle);
                        adapterTitle.setText(getString(R.string.focus_choose_event));
                    } else if (String.valueOf(s).equals(getString(R.string.focus_start1))) {
                        adapterIv.setImageResource(R.drawable.circle_pink);
                        adapterTitle.setText(getString(R.string.focus_choose_athlete));
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.width = ScreenUtils.getScreenWidth();
                    inflat.setLayoutParams(layoutParams);
                    return inflat;
                } else {
                    TextView tv = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_view_textview,
                            tabLayoutItem, false);
                    tv.setText(String.valueOf(s));
                    return tv;
                }
            }
        });

        tabLayoutItem.setOnSelectListener(set -> {
            stringHashMap.clear();
            list.clear();
            list1.clear();
            list2.clear();
//            list3.clear();
            for (Integer entry : set) {
                choiceList.add(entry); //存储用户数据
                if (!String.valueOf(strItem[entry]).contains(startString)) {
                    list.add(String.valueOf(strItem[entry]));
                }
                if (entry > 0 && entry < 16) {
                    if (!String.valueOf(strItem[entry]).contains(startString)) {
                        list1.add(String.valueOf(strItem[entry]));
                    }
                }
//                else {
//                    if (!String.valueOf(strAll[entry]).contains(startString)) {
//                        list3.add(String.valueOf(strAll[entry]));
//                    }
//                }
            }
            if (list1.size() > 0) {
                stringHashMap.put("1", list1);
            }
            if (list2.size() > 0) {
                stringHashMap.put("2", list2);
            }
//            if (list3.size() > 0) {
//                stringHashMap.put("3", list3);
//            }
            json = gson.toJson(stringHashMap);

        });



        /*
         * 选择关注的运动员
         */
        strHuman = new Object[]{
                getString(R.string.focus_start), getString(R.string.focus_athlete1), getString(R.string.focus_athlete2),
                getString(R.string.focus_athlete3),getString(R.string.focus_athlete4),getString(R.string.focus_athlete5),
                getString(R.string.focus_athlete6), getString(R.string.focus_athlete7), getString(R.string.focus_athlete8),
                getString(R.string.focus_athlete9),getString(R.string.focus_athlete10),getString(R.string.focus_athlete11),
                getString(R.string.focus_athlete12),getString(R.string.focus_athlete13),getString(R.string.focus_athlete14)};

        TagFlowLayout tabLayoutHuman = findViewById(R.id.tabLayout_human);
        tabLayoutHuman.setAdapter(new TagAdapter<Object>(strHuman) {
            @Override
            public View getView(FlowLayout flowLayout, int i, Object s) {
                if (String.valueOf(s).contains(startString)) {
                    View inflat = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adapter_activity_label_title, null);
                    ImageView adapterIv = inflat.findViewById(R.id.adapter_iv);
                    TextView adapterTitle = inflat.findViewById(R.id.adater_title);
                    if (String.valueOf(s).equals(startString)) {
                        adapterIv.setImageResource(R.drawable.circle_pink);
                        adapterTitle.setText(getString(R.string.focus_choose_athlete));
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.width = ScreenUtils.getScreenWidth();
                    inflat.setLayoutParams(layoutParams);
                    return inflat;
                } else {
                    TextView tv = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_view_textview,
                            tabLayoutHuman, false);
                    tv.setText(String.valueOf(s));
                    return tv;
                }
            }
        });

        tabLayoutHuman.setOnSelectListener(set -> {
            stringHashMap.clear();
            list.clear();
            list1.clear();
            list2.clear();
//            list3.clear();
            for (Integer entry : set) {
                choiceList.add(entry); //存储用户数据
                if (!String.valueOf(strHuman[entry]).contains(startString)) {
                    list.add(String.valueOf(strHuman[entry]));
                }
                if (entry > 0 && entry < 16) {
                    if (!String.valueOf(strHuman[entry]).contains(startString)) {
                        list2.add(String.valueOf(strItem[entry]));
                    }
                }
//                else {
//                    if (!String.valueOf(strAll[entry]).contains(startString)) {
//                        list3.add(String.valueOf(strAll[entry]));
//                    }
//                }
            }
            if (list1.size() > 0) {
                stringHashMap.put("1", list1);
            }
            if (list2.size() > 0) {
                stringHashMap.put("2", list2);
            }
//            if (list3.size() > 0) {
//                stringHashMap.put("3", list3);
//            }
            json = gson.toJson(stringHashMap);



        });


        Button focus_btn_confirm = findViewById(R.id.focus_btn_confirm);
        focus_btn_confirm.setOnClickListener(v -> {
            if (json == null){
                ToastUtils.showSingleToast(getString(R.string.focus_no_choice));
            }else {
                finish();
//                ToastUtils.showSingleToast("数据： " + json);
            }
        });

        TextView focus_tv_skip = findViewById(R.id.focus_tv_skip);
        focus_tv_skip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);  //下划线
        focus_tv_skip.getPaint().setAntiAlias(true);//设置抗锯齿，使线条平滑
        focus_tv_skip.setOnClickListener(v -> finish());


    }
}
