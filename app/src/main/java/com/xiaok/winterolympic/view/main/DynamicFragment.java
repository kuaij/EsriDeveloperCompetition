package com.xiaok.winterolympic.view.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.adapt.SearchResultAdapter;
import com.xiaok.winterolympic.custom.ProgressButton;
import com.xiaok.winterolympic.model.SearchResult;
import com.xiaok.winterolympic.model.VideoBrower;
import com.xiaok.winterolympic.view.central.NavigationActivity;
import com.xiaok.winterolympic.view.search.BuyTicketsActivity;

import java.util.LinkedList;
import java.util.List;


public class DynamicFragment extends Fragment  {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dynamic_brower, container,false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

