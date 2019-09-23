package com.xiaok.winterolympic.view.identify;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaok.winterolympic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AthlethicIdentifyFragment extends Fragment {


    public AthlethicIdentifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_athlethic_identify, container, false);
    }

}
