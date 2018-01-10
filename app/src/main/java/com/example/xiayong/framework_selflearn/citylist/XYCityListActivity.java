package com.example.xiayong.framework_selflearn.citylist;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xiayong.framework_selflearn.R;
import com.example.xiayong.framework_selflearn.opensourcelib.stickylistheaders.ExpandableStickyListHeadersListView;
import com.example.xiayong.framework_selflearn.opensourcelib.stickylistheaders.StickyListHeadersListView;
import com.example.xiayong.framework_selflearn.widget.XYEditView;
import com.example.xiayong.framework_selflearn.widget.XYSideBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiayong on 2018/1/5.
 */

public class XYCityListActivity extends Activity implements XYSideBar.OnItemTouchListener{

    private XYSideBar mSideBar;
    private ListView mShowListView;
    private StickyListHeadersListView mStickyList;
    private MyStickyListHeadersAdapter mStickyAdapter;
    private List<String> mDatas;
    private List<String> mShowCitys = new ArrayList<>();
    private List<String> mAllCitys = new ArrayList<>();
    private XYEditView mXYEditView;

    private XYEditView.XYTextWatcher mXYTextWatcher = new XYEditView.XYTextWatcher() {
        @Override
        public void onTextChanged(XYEditView view, CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(XYEditView view, CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(XYEditView view, Editable s) {
            updateCityData(s);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        _init();
    }

    private void _init(){
        mStickyList = (StickyListHeadersListView) findViewById(R.id.sticky_list);
        mStickyAdapter = new MyStickyListHeadersAdapter(this);
        mStickyList.setAdapter(mStickyAdapter);

        mSideBar = (XYSideBar) findViewById(R.id.side_bar);
        mSideBar.setOnItemTouchListener(this);
        _initData();
        mSideBar.setItems(mDatas);

        mShowListView = (ListView) findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mShowCitys);
        mShowListView.setAdapter(arrayAdapter);

        mXYEditView = (XYEditView) findViewById(R.id.edit_view);
        mXYEditView.setTextWatcher(mXYTextWatcher);

        /**
         * 点击header,分区的展开、隐藏逻辑
         */
        mStickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (currentlySticky){

                }else {
                    ExpandableStickyListHeadersListView listView = (ExpandableStickyListHeadersListView)l;
                    if (listView.isHeaderCollapsed(headerId)){
                        listView.expand(headerId);
                    } else {
                        listView.collapse(headerId);
                    }
                }
            }
        });

        _getAllCitys();
    }

    private void _initData(){
        mDatas = new ArrayList<String>();
        List<CityListData> key_city = mStickyAdapter.getCitys();
        String pre_key = "";
        for (CityListData cityListData: key_city){
            String str = cityListData.getId();
            if (str.equals(pre_key)){
                continue;
            } else {
                mDatas.add(str);
                pre_key = str;
            }
        }
    }

    private void _getAllCitys(){
        List<CityListData> key_city = mStickyAdapter.getCitys();
        for (int i=0; i<key_city.size(); i++){
            if (key_city.get(i).getId().equals(MyStickyListHeadersAdapter.LOCATION)){
                continue;
            } else if (key_city.get(i).getId().equals(MyStickyListHeadersAdapter.HOT)){
                continue;
            } else {
                mAllCitys.add(key_city.get(i).getCity());
            }
        }
    }

    private void updateCityData(Editable s){
        mShowCitys.clear();
        if (s.toString().isEmpty()){
            mStickyList.setVisibility(View.VISIBLE);
            mSideBar.setVisibility(View.VISIBLE);
            mXYEditView.clearFocus();
            mXYEditView.hideSoftKeyword();
        }else{
            mStickyList.setVisibility(View.INVISIBLE);
            mSideBar.setVisibility(View.INVISIBLE);
            for (int i=0; i<mAllCitys.size(); i++){
                if (mAllCitys.get(i).contains(s.toString())){
                    mShowCitys.add(mAllCitys.get(i));
                }
            }
        }
        ((ArrayAdapter)mShowListView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onItemTouchDown(int loc) {
        List<CityListData> key_city = mStickyAdapter.getCitys();
        for (int i=0; i<key_city.size(); i++){
            if (mDatas.get(loc).equals(key_city.get(i).getId())){
                mStickyList.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onItemTouchUp(int loc) {
        Toast.makeText(this, mDatas.get(loc) + " is released", Toast.LENGTH_SHORT).show();
    }
}
