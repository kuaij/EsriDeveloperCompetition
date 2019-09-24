package com.xiaok.winterolympic.view.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xiaok.winterolympic.R;

import net.qiujuer.genius.ui.widget.Button;

public class BigAvatarActivity extends AppCompatActivity {

    private ImageView iv_big_picture;
    private RelativeLayout mContainer;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_avatar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.profile_photo));
        }

        mContainer = findViewById(R.id.big_mContainer);
        iv_big_picture = findViewById(R.id.iv_big_pic);
        iv_big_picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addView();
                Logger.e("添加");
                return false;
            }
        });
    }


    private void addView(){
        btn_save = new Button(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.setMargins(30,0,30,0);

        btn_save.setTextSize(20);
        btn_save.setText("保存图片");
        btn_save.setBackgroundColor(getColor(R.color.identify_button_text_color));
        btn_save.setTextColor(getColor(R.color.record_comment_text));

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取当前照片
                Drawable drawable = iv_big_picture.getDrawable();
                Bitmap bitmap = Bitmap
                        .createBitmap(
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(),
                                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                        : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                saveInLocalDir(bitmap); //保存到本地
            }
        });

        mContainer.addView(btn_save,lp);
    }

    private void saveInLocalDir(Bitmap bitmap){
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "avatar", "description");
        mContainer.removeView(btn_save);

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
