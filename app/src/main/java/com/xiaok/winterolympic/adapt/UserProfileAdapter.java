package com.xiaok.winterolympic.adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.model.UserProfile;

import java.util.LinkedList;

public class UserProfileAdapter extends BaseAdapter {

    private LinkedList<UserProfile> aData;
    private Context mContext;

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
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if (convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item,parent,false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.profile_tv_name);
            holder.sayTextView = convertView.findViewById(R.id.profile_tv_value);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.nameTextView.setText(aData.get(position).getaName());
        holder.sayTextView.setText(aData.get(position).getaValue());
        return convertView;
    }
    static class ViewHolder{
        ImageView imageView;
        TextView nameTextView;
        TextView sayTextView;
    }
}
