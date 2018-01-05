package com.example.xiayong.framework_selflearn.citylist;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.xiayong.framework_selflearn.R;
import com.example.xiayong.framework_selflearn.opensourcelib.stickylistheaders.StickyListHeadersListView;
import com.example.xiayong.framework_selflearn.widget.XYSideBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiayong on 2018/1/5.
 */

public class XYCityListActivity extends Activity implements XYSideBar.OnItemTouchListener{

    private XYSideBar mSideBar;
    private StickyListHeadersListView mStickyList;
    private List<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        _init();
    }

    private void _init(){
        mSideBar = (XYSideBar) findViewById(R.id.side_bar);
        mSideBar.setOnItemTouchListener(this);
        _initData();
        mSideBar.setItems(mDatas);

        mStickyList = (StickyListHeadersListView) findViewById(R.id.sticky_list);
        MyStickyListHeadersAdapter adapter = new MyStickyListHeadersAdapter(this);
        mStickyList.setAdapter(adapter);
    }

    private void _initData(){
        mDatas = new ArrayList<String>();
        mDatas.add("A");
        mDatas.add("B");
        mDatas.add("C");
        mDatas.add("T");
        mDatas.add("J");
        mDatas.add("H");
    }

    @Override
    public void onItemTouchDown(int loc) {

    }

    @Override
    public void onItemTouchUp(int loc) {
        Toast.makeText(this, mDatas.get(loc) + " is released", Toast.LENGTH_SHORT).show();
    }
}
