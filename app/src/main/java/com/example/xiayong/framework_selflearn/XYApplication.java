package com.example.xiayong.framework_selflearn;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.example.xiayong.framework_selflearn.gesturepassword.XYGesturePasswordBroadCast;
import com.example.xiayong.framework_selflearn.gesturepassword.XYGesturePasswordManager;
import com.example.xiayong.framework_selflearn.utils.XYConstant;

/**
 * Created by cugyong on 2018/1/12.
 */

public class XYApplication extends Application{

    public static XYApplication mInstance;

    @Override
    public void onCreate() {
        mInstance = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(XYConstant.KEY_GESTURE_TIMEOUT);
        LocalBroadcastManager.getInstance(this).registerReceiver(new XYGesturePasswordBroadCast(),
                filter);
        super.onCreate();
    }

    public static XYApplication getInstance(){
        return mInstance;
    }
}
