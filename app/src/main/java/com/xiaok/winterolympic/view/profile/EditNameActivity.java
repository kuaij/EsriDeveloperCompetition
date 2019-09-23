package com.xiaok.winterolympic.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xiaok.winterolympic.MyApplication;
import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.model.UIAyncManager;
import com.xiaok.winterolympic.utils.Postman;
import com.xiaok.winterolympic.view.MainPageActivity;

public class EditNameActivity extends AppCompatActivity {

    private String oldName;
    private String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        //实例化观察者、被观察者
        Postman postman = new Postman();
        MainPageActivity maNameObserver = new MainPageActivity();
        postman.add(maNameObserver);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.profile_change_name));
        }

        //取当前的用户名
        oldName = MyApplication.userName;

        Button profile_save_name = findViewById(R.id.profile_save_name);
        profile_save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 提交到服务器
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newName",newName);
                setResult(1, resultIntent);
                //同步主界面侧栏UI
                UIAyncManager.PostChangeByModel(EditNameActivity.this,newName);
                finish();
            }
        });


        EditText profile_et_name = findViewById(R.id.profile_et_name);
        profile_et_name.setText(oldName);
        profile_et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //判断与之前的用户名不同
                if (s.toString().equals(oldName)){
                    profile_save_name.setEnabled(false);
                }else {
                    profile_save_name.setEnabled(true);
                    newName = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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



}
