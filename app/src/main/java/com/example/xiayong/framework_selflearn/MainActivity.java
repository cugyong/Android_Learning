package com.example.xiayong.framework_selflearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xiayong.framework_selflearn.citylist.XYCityListActivity;
import com.example.xiayong.framework_selflearn.gesturepassword.XYGesturePasswordTestActivity;
import com.example.xiayong.framework_selflearn.opensourcelib.pulltorefresh.PullToRefreshBase;
import com.example.xiayong.framework_selflearn.opensourcelib.pulltorefresh.PullToRefreshListView;
import com.example.xiayong.framework_selflearn.rxjava.RxJavaTestActivity;
import com.example.xiayong.framework_selflearn.draggridview.DragGridViewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends XYActivityBase implements AdapterView.OnItemClickListener{

    private final static String[] ACTICLES = {"RXjava", "CityList", "GesturePassword",
            "DragGridView"};
    private static List<Class<?>> classes = new ArrayList<Class<?>>();

    private PullToRefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _addActivity();
        _init();
    }

    private void _addActivity(){
        classes.add(RxJavaTestActivity.class);
        classes.add(XYCityListActivity.class);
        classes.add(XYGesturePasswordTestActivity.class);
        classes.add(DragGridViewActivity.class);
    }

    private void _init(){
        listView = (PullToRefreshListView)findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                ACTICLES);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setShowIndicator(true);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                listView.onRefreshComplete();
                Toast.makeText(MainActivity.this, "刷新完成!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> cls = classes.get(position);
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }
}
