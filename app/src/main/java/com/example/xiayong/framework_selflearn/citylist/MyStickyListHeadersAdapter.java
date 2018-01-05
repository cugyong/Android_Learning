package com.example.xiayong.framework_selflearn.citylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xiayong.framework_selflearn.R;
import com.example.xiayong.framework_selflearn.opensourcelib.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by xiayong on 2018/1/5.
 */

public class MyStickyListHeadersAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private String[] mCities;
    private LayoutInflater mInflater;

    public MyStickyListHeadersAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mCities = context.getResources().getStringArray(R.array.cities);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.test_list_item, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.mytext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = "" + mCities[position].subSequence(0, 1).charAt(0);
        holder.textView.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mCities[position].subSequence(0, 1).charAt(0);
    }

    @Override
    public int getCount() {
        return mCities.length;
    }

    @Override
    public Object getItem(int position) {
        return mCities[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.test_list_item, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.mytext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mCities[position]);

        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
