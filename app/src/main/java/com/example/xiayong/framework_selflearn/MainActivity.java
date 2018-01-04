package com.example.xiayong.framework_selflearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xiayong.framework_selflearn.rxjava.RxJavaTestActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private final static String[] ACTICLES = {"RXjava"};
    private static List<Class<?>> classes = new ArrayList<Class<?>>();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _addActivity();
        _init();
    }

    private void _addActivity(){
        classes.add(RxJavaTestActivity.class);
    }

    private void _init(){
        listView = (ListView)findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                ACTICLES);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> cls = classes.get(position);
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
    }
}
