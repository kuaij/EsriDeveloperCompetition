package com.xiaok.winterolympic.view.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.view.identify.AthlethicIdentifyFragment;
import com.xiaok.winterolympic.view.identify.VolunteerIdentifyFragment;

import net.qiujuer.genius.ui.widget.Button;

public class UserIdentifyActivity extends AppCompatActivity {

    private VolunteerIdentifyFragment volunteerIdentifyFragment;
    private AthlethicIdentifyFragment athlethicIdentifyFragment;

    public static final int CANCLE_VOLUNTEER = 1;
    public static final int CANCLE_ATHLETIC = 2;

    private Fragment[]fragments;
    private int lastFragment;

    private Button btnVolunteer;
    private Button btnAthletic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_identify);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.profile_user_identify));
        }

        initFragment();

        btnVolunteer = findViewById(R.id.identify_btn_volunteer);
        btnVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastFragment!=0)
                {
                    switchFragment(lastFragment,0);
                    btnVolunteer.setBackgroundColor(getColor(R.color.identify_button_background));
                    btnAthletic.setBackgroundColor(getColor(R.color.opinion_text_color));
                    btnVolunteer.setEnabled(false);
                    btnAthletic.setEnabled(true);
                    lastFragment=0;
                }
            }
        });

        btnAthletic = findViewById(R.id.identify_btn_athletic);
        btnAthletic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastFragment!=1)
                {
                    switchFragment(lastFragment,1);
                    //切换颜色
                    btnAthletic.setBackgroundColor(getColor(R.color.identify_button_background));
                    btnVolunteer.setBackgroundColor(getColor(R.color.opinion_text_color));
                    btnVolunteer.setEnabled(true);
                    btnAthletic.setEnabled(false);
                    lastFragment=1;

                }
            }
        });



    }

    private void initFragment(){
        volunteerIdentifyFragment = new VolunteerIdentifyFragment();
        athlethicIdentifyFragment = new AthlethicIdentifyFragment();
        lastFragment = 0;
        fragments = new Fragment[]{volunteerIdentifyFragment,athlethicIdentifyFragment};
        getSupportFragmentManager().beginTransaction().replace(R.id.identify_seat,volunteerIdentifyFragment).show(volunteerIdentifyFragment).commit();
    }


    //切换Fragment
    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.identify_seat,fragments[index]);


        }
        transaction.show(fragments[index]).commitAllowingStateLoss();


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
