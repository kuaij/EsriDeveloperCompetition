package com.xiaok.winterolympic.view.identify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.utils.ToastUtils;
import com.xiaok.winterolympic.view.profile.UserIdentifyActivity;

import net.qiujuer.genius.ui.widget.Button;

import ch.ielse.view.SwitchView;

/**
 * A simple {@link Fragment} subclass.
 */
public class VolunteerIdentifyFragment extends Fragment {


    public VolunteerIdentifyFragment() {

    }

    private Button btnExit;
    private SwitchView svIsBusying;

    private boolean isBusying = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_volunteer_identify, container, false);
        btnExit = rootView.findViewById(R.id.btn_exit);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //注销认证
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(UserIdentifyActivity.CANCLE_VOLUNTEER);
                getActivity().finish();
            }
        });


        //切换志愿者当前状态
        svIsBusying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBusying){
                    ToastUtils.showSingleToast("当前已进入忙碌状态");
                    pushUserState();
                    isBusying = true;
                }else {
                    isBusying = false;
                }
            }
        });


    }

    private boolean pushUserState(){

        return true;
    }
}
