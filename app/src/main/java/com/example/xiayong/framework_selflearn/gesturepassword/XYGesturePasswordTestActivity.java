package com.example.xiayong.framework_selflearn.gesturepassword;

import android.os.Bundle;

import com.example.xiayong.framework_selflearn.R;
import com.example.xiayong.framework_selflearn.XYActivityBase;

/**
 * Created by cugyong on 2018/1/11.
 */

public class XYGesturePasswordTestActivity extends XYActivityBase implements XYGesturePasswordLayout.GesturePasswordMatchOKCallBack{

    public static final String TAG = XYGesturePasswordTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        ((XYGesturePasswordLayout) findViewById(R.id.gesture_layout)).setMatchOKCallBack(this);
    }

    @Override
    public void gesturePasswordMatchOK() {
        finish();
    }
}
