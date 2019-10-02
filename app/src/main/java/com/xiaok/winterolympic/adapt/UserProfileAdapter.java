package com.xiaok.winterolympic.adapt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.model.UserProfile;
import com.xiaok.winterolympic.view.profile.BigAvatarActivity;

import java.io.File;
import java.util.LinkedList;

public class UserProfileAdapter extends BaseAdapter {

    private LinkedList<UserProfile> aData;
    private Context mContext;

    final int TYPE_1 = 0;
    final int TYPE_2 = 1;

    public UserProfileAdapter(LinkedList<UserProfile> aData,Context mContext){
        this.aData = aData;
        this.mContext = mContext;
    }
    @Override
    public int getCount(){
        return aData.size();
    }
    @Override
    public  Object getItem(int position){
        return null;
    }
    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_1;
        }else {
            return TYPE_2;
        }
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        int type = getItemViewType(position);

        if (convertView==null){
            switch (type){
                case TYPE_1:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item_pic,parent,false);
                    holder1 = new ViewHolder1();
                    holder1.tv_item_name = convertView.findViewById(R.id.profile_tv_name2);
                    holder1.iv_avatar = convertView.findViewById(R.id.profile_iv_avatar2);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item,parent,false);
                    holder2 = new ViewHolder2();
                    holder2.tv_item_name2 = convertView.findViewById(R.id.profile_tv_name);
                    holder2.tv_item_value = convertView.findViewById(R.id.profile_tv_value);
                    convertView.setTag(holder2);
                    break;
            }


        }else {
            switch (type){
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
            }

        }

        switch (type){
            case TYPE_1:
                holder1.tv_item_name.setText(aData.get(position).getaName());
                File cachePic = new File(aData.get(position).getaValue());
                if (cachePic.exists()){
                    holder1.iv_avatar.setImageURI(Uri.fromFile(cachePic)); //设置头像路径
                }
                holder1.iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext,BigAvatarActivity.class));
                    }
                });
                break;
            case TYPE_2:
                holder2.tv_item_name2.setText(aData.get(position).getaName());
                holder2.tv_item_value.setText(aData.get(position).getaValue());
        }


        return convertView;
    }
    class ViewHolder1{
        TextView tv_item_name;
        ImageView iv_avatar;
    }

    class ViewHolder2{
        TextView tv_item_name2;
        TextView tv_item_value;
    }
}
