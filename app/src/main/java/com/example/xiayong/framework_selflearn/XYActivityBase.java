package com.example.xiayong.framework_selflearn;

import android.app.Activity;
import android.os.Bundle;

import com.example.xiayong.framework_selflearn.gesturepassword.XYGesturePasswordManager;
import com.example.xiayong.framework_selflearn.gesturepassword.XYIGestureParent;

/**
 * Created by cugyong on 2018/1/12.
 */

public class XYActivityBase extends Activity implements XYIGestureParent{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        XYGesturePasswordManager.getInstance().onEntryForeground(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        XYGesturePasswordManager.getInstance().onEntryBackground(this);
        super.onPause();
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
