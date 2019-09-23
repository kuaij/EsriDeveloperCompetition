package com.xiaok.winterolympic.view.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;



import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.SearchResultAdapter;
import com.xiaok.winterolympic.custom.ProgressButton;
import com.xiaok.winterolympic.model.SearchResult;
import com.xiaok.winterolympic.view.central.NavigationActivity;
import com.xiaok.winterolympic.view.search.BuyTicketsActivity;
import com.xiaok.winterolympic.view.setting.MyProfileActivity;

import java.util.LinkedList;
import java.util.List;

import static com.blankj.utilcode.util.SizeUtils.dp2px;


public class SearchCompetitionFragment extends Fragment  {

    private String chooseDay;
    private String chooseCompetition;

    /*
    2月5号的数据
     */
    private String[] names_5;
    private String[] details_5;
    private String[] dates_5;
    private String[] times_5;
    private String[] position_5;
    private List<SearchResult> aData_5;

    /*
    2月7号的数据
     */
    private String[] names_7;
    private String[] details_7;
    private String[] dates_7;
    private String[] times_7;
    private String[] position_7;
    private List<SearchResult> aData_7;


    private MaterialSpinner search_spinner_day;
    private MaterialSpinner search_spinner_competition;
    private ProgressButton btn_search;
    private SwipeMenuListView lv_result;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View v = (View) msg.obj;
            resultUI(v);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_competition, container, false);
        search_spinner_day = root.findViewById(R.id.search_spinner_day);
        search_spinner_competition = root.findViewById(R.id.search_spinner_competition);
        btn_search = root.findViewById(R.id.search_btn_start);
        lv_result = root.findViewById(R.id.search_lv_result);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //构造自定义ListView右拉栏
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem naviItem = new SwipeMenuItem(
                        getContext());
                // set item background
                naviItem.setBackground(new ColorDrawable(Color.rgb(0x1e, 0x90,
                        0xff)));
                // set item width
                naviItem.setWidth(200);
                //设置图标
                naviItem.setIcon(R.mipmap.search_navi);

                // add to menu
                menu.addMenuItem(naviItem);

                // create "delete" item
                SwipeMenuItem notifyItem = new SwipeMenuItem(
                        getContext());
                // set item background
                notifyItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                notifyItem.setWidth(200);
                // set a icon
                notifyItem.setIcon(R.mipmap.search_notify);
                // add to menu
                menu.addMenuItem(notifyItem);
            }
        };

        lv_result.setMenuCreator(creator);

        lv_result.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //导航
                        startActivity(new Intent(getContext(),NavigationActivity.class));
                        break;
                    case 1:
                        //设置提醒
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.SET_ALARM");
                        startActivity(intent);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });



        //初始化为不限
        chooseDay = getString(R.string.search_no_limit);
        chooseCompetition = getString(R.string.search_no_limit);

        //选择比赛日期
        search_spinner_day.setItems(getString(R.string.search_no_limit),"4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20");
        search_spinner_day.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                chooseDay = item;
            }
        });

        //选择搜索的比赛项目
        search_spinner_competition.setItems(
                getString(R.string.search_no_limit),getString(R.string.focus_alpine_skiing),getString(R.string.focus_biathlon),
                getString(R.string.focus_bobsleiqn), getString(R.string.focus_cross_skating),getString(R.string.focus_curling),
                getString(R.string.focus_figure_skating), getString(R.string.focus_free_styleing),getString(R.string.focus_ice_hockey),
                getString(R.string.focus_luge),getString(R.string.focus_nordic_combined),getString(R.string.focus_short_track),
                getString(R.string.focus_skeleton),getString(R.string.focus_ski_jumping),getString(R.string.focus_snowboarding),
                getString(R.string.focus_speed_skating));
        search_spinner_competition.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                chooseCompetition = item;
            }
        });


        btn_search.setBgColor(Color.parseColor("#1e90ff"));
        btn_search.setTextColor(Color.WHITE);
        btn_search.setProColor(Color.WHITE);
        btn_search.setButtonText(getString(R.string.search));
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_search.startAnim();
                Message msg = Message.obtain();
                msg.obj = v;
                mHandler.sendMessageDelayed(msg, 1800);

            }
        });

        //初始化数据,2月5号短道速滑数据
        names_5 = new String[]{"短道速滑","短道速滑","短道速滑"};
        details_5 = new String[]{"男子1500m-决赛","女子500m-资格赛","女子3000m接力-资格赛"};
        dates_5 = new String[]{"2月5日","2月5日","2月5日"};
        times_5 = new String[]{"19:00-21:50","19:00-21:50","19:00-21:50"};
        position_5 = new String[]{"国家速滑馆","国家速滑馆","国家速滑馆"};


        aData_5 = new LinkedList<>();
        for (int i=0;i<names_5.length;i++){
            aData_5.add(new SearchResult(names_5[i],details_5[i],dates_5[i],times_5[i],position_5[i]));
        }

        //2月7号花样滑冰
        names_7 = new String[]{"花样滑冰","花样滑冰","花样滑冰"};
        details_7 = new String[]{"团体男子个人单人滑-自由滑","女子个人单人滑-自由滑","冰上舞蹈-自由滑"};
        dates_7 = new String[]{"2月7日","2月7日","2月7日"};
        times_7 = new String[]{"10:00-13:10","10:00-13:10","10:00-13:10"};
        position_7 = new String[]{"首都体育馆","首都体育馆","首都体育馆"};

        aData_7 = new LinkedList<>();
        for (int i=0;i<names_7.length;i++){
            aData_7.add(new SearchResult(names_7[i],details_7[i],dates_7[i],times_7[i],position_7[i]));
        }

    }


    //提交用户检索的日期和项目名称
    private boolean requestForResult(String date, String competitionName){
        return true;

    }



    private void resultUI(View v){
        btn_search.stopAnim(new ProgressButton.OnStopAnim() {
            @Override
            public void Stop() {
                boolean isOk = requestForResult(chooseDay, chooseCompetition);
                if (isOk){
                    //演示视频使用
                    if (chooseDay.equals("5") && chooseCompetition.equals(getString(R.string.focus_short_track))){
                        //2月5号短道速滑
                        lv_result.setAdapter(new SearchResultAdapter((LinkedList<SearchResult>)aData_5,getContext()));
                        lv_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startActivity(new Intent(getContext(), BuyTicketsActivity.class));
                            }
                        });
                    } else if (chooseDay.equals("7") && chooseCompetition.equals(getString(R.string.focus_figure_skating))){
                        //2月7号花样滑冰
                        lv_result.setAdapter(new SearchResultAdapter((LinkedList<SearchResult>)aData_7,getContext()));
                        lv_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startActivity(new Intent(getContext(), BuyTicketsActivity.class));
                            }
                        });
                    }
//                    //日期不限
//                    if (chooseDay.equals(getString(R.string.search_no_limit))){
//                        //项目无限制
//                        if (chooseCompetition.equals(getString(R.string.search_no_limit))){
//                            Snackbar.make(v,getString(R.string.search_all)+getString(R.string.search_competition_name)+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
//                        }else {
//                            Snackbar.make(v,getString(R.string.search_all)+chooseCompetition+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
//                        }
//
//                    }
//                    //日期限制
//                    else {
//                        if (chooseCompetition.equals(getString(R.string.search_no_limit))){
//                            Snackbar.make(v,getString(R.string.search_all)+chooseDay+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
//                        }else {
//                            Snackbar.make(v,getString(R.string.search_date_is)+chooseDay+chooseCompetition+getString(R.string.search_related_info), Snackbar.LENGTH_LONG).show();
//                        }
//                    }
                }
            }
        });

    }


}

