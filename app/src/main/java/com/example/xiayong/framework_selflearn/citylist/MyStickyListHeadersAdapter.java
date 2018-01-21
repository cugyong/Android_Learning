package com.example.xiayong.framework_selflearn.citylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xiayong.framework_selflearn.R;
import com.example.xiayong.framework_selflearn.opensourcelib.stickylistheaders.StickyListHeadersAdapter;
import com.example.xiayong.framework_selflearn.utils.XYParseJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayong on 2018/1/5.
 */

public class MyStickyListHeadersAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private static final String[] HOT_CITYS = {"北京市", "上海市", "广州市", "杭州市", "南京市",
    "成都市", "南昌市", "武汉市", "沈阳市", "福州市", "宁波市", "重庆市", "深圳市", "厦门市", "吐鲁番地区"};
    public static final String HOT = "热门";
    public static final String LOCATION = "定位";
    private static final String DEFAULT_CITY = "北京市";
    private List<CityListData> mCities;
    private LayoutInflater mInflater;
    private Context mContext;

    public MyStickyListHeadersAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        _init();
    }

    private void _init(){
        mCities = new ArrayList<>();
        //定位城市
        mCities.add(new CityListData(DEFAULT_CITY, LOCATION));
        // 热门城市
        for (int i=0; i<HOT_CITYS.length; i++){
            mCities.add(new CityListData(HOT_CITYS[i], HOT));
        }
        // 所有城市列表
        Map<String, List<String>> parseData = XYParseJson.parseCityDatas(mContext);
        for (String str: parseData.keySet()){
            List<String> citys = parseData.get(str);
            for (String city: citys){
                CityListData data = new CityListData(city, str);
                mCities.add(data);
            }
        }
    }

    public List<CityListData> getCitys(){
        return mCities;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.test_list_head, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.mytext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = mCities.get(position).getId();
        holder.textView.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        String str = mCities.get(position).getId();
        if (str.equals(LOCATION)){
            return (int)'A' - 2;
        }else if (str.equals(HOT)){
            return (int)'A'- 1;
        } else {
            Character character = str.charAt(0);
            return (int)character;
        }
    }

    @Override
    public int getCount() {
        return mCities.size();
    }

    @Override
    public Object getItem(int position) {
        return mCities.get(position).getCity();
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
        holder.textView.setText(mCities.get(position).getCity());

        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
