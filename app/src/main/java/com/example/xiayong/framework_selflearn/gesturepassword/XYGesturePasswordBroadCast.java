package com.example.xiayong.framework_selflearn.gesturepassword;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.xiayong.framework_selflearn.utils.XYConstant;

/**
 * Created by cugyong on 2018/1/12.
 */

public class XYGesturePasswordBroadCast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra(XYConstant.KEY_GESTURE_TIMEOUT);
        boolean isTimeOut = intent.getBooleanExtra(XYConstant.KEY_GESTURE_TIMEOUT_OR_NOT, false);
        switch (time){
            case XYGesturePasswordManager.TIME_OUT:
                setTimeOut(context, isTimeOut);
                break;
        }
    }

    private void setTimeOut(Context context, boolean isTimeOut){
        SharedPreferences preferences = context.getSharedPreferences(XYConstant.KEY_GESTURE_TIMEOUT,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(XYConstant.KEY_GESTURE_TIMEOUT, isTimeOut);
        editor.commit();
    }
}
