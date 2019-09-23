package com.xiaok.winterolympic.view.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.view.MainPageActivity;

import java.util.ArrayList;
import java.util.List;

public class ChangeAvatarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.profile_more));
        }

        getPermission();

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


    //申请相机权限
    private void getPermission(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(ChangeAvatarActivity.this,Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(ChangeAvatarActivity.this,permissions,1);
        }else{
            Toast.makeText(getApplicationContext(),"尚未获取到权限，无法打开相机",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(ChangeAvatarActivity.this, "尚未授权，无法打开相机！", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                    }
                } else {
                    Toast.makeText(ChangeAvatarActivity.this, "发生未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }

    }
}
