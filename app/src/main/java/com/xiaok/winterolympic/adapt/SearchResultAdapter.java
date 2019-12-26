package com.xiaok.winterolympic.adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.model.SearchResult;

import java.util.LinkedList;

public class SearchResultAdapter extends BaseAdapter {

    private LinkedList<SearchResult> aData;
    private Context mContext;

    public SearchResultAdapter(LinkedList<SearchResult> aData, Context mContext){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_result_item,parent,false);
            holder = new ViewHolder();
            holder.matchName = convertView.findViewById(R.id.search_name);
            holder.matchDetails = convertView.findViewById(R.id.search_details);
            holder.matchDate = convertView.findViewById(R.id.search_date);
            holder.matchTime = convertView.findViewById(R.id.search_time);
            holder.matchPosition = convertView.findViewById(R.id.search_position);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.matchName.setText(aData.get(position).getMatchName());
        holder.matchDetails.setText(aData.get(position).getMatchDetails());
        holder.matchDate.setText(aData.get(position).getMathchDate());
        holder.matchTime.setText(aData.get(position).getMatchTime());
        holder.matchPosition.setText(aData.get(position).getMatchPosition());
        return convertView;
    }
    static class ViewHolder{
        TextView matchName;
        TextView matchDetails;
        TextView matchDate;
        TextView matchTime;
        TextView matchPosition;
    }
}
