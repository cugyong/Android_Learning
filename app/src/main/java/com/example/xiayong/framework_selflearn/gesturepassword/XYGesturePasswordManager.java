package com.example.xiayong.framework_selflearn.gesturepassword;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.example.xiayong.framework_selflearn.XYApplication;
import com.example.xiayong.framework_selflearn.utils.XYConstant;

/**
 * 关于应用从后台进入前台，是否展示手势密码等逻辑
 * Created by cugyong on 2018/1/12.
 */

public class XYGesturePasswordManager implements XYIGesturePassword{

    public static final String TIME_OUT = "time_out";
    public static final int TIME = 1; // 超时时间

    public static XYGesturePasswordManager mInstance;

    private static Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int var = message.what;
            if (var == XYConstant.MESSAGE_GESTURE_TIMEOUT){ // 手势密码已经超时
                mInstance.onGesturePasswordTimeOut(true);
            }
            return false;
        }
    });

    private XYGesturePasswordManager(){
    }

    public static synchronized XYGesturePasswordManager getInstance(){
        if (mInstance == null){
            mInstance = new XYGesturePasswordManager();
        }
        return mInstance;
    }

    @Override
    public void onEntryForeground(XYIGestureParent gestureParent) {
        mHandler.removeMessages(XYConstant.MESSAGE_GESTURE_TIMEOUT);
        onShowGesturePassword(gestureParent); // 是否显示手势密码
        onGesturePasswordTimeOut(false);
    }

    @Override
    public void onEntryBackground(XYIGestureParent gestureParent) {
        mHandler.sendEmptyMessageDelayed(XYConstant.MESSAGE_GESTURE_TIMEOUT, TIME * 1000);
    }

    private void onGesturePasswordTimeOut(boolean isTimeOut){
        Intent intent = new Intent();
        intent.putExtra(XYConstant.KEY_GESTURE_TIMEOUT_OR_NOT, isTimeOut);
        intent.putExtra(XYConstant.KEY_GESTURE_TIMEOUT, TIME_OUT);
        intent.setAction(XYConstant.KEY_GESTURE_TIMEOUT);
        LocalBroadcastManager.getInstance(XYApplication.getInstance()).sendBroadcast(intent);
    }

    private void onShowGesturePassword(XYIGestureParent gestureParent){
        SharedPreferences preferences = XYApplication.getInstance().getSharedPreferences(
                XYConstant.KEY_GESTURE_TIMEOUT, Activity.MODE_PRIVATE
        );
        boolean isShowPassword = preferences.getBoolean(XYConstant.KEY_GESTURE_TIMEOUT, false);
        if (isShowPassword){
            Intent intent = new Intent(gestureParent.getActivity(), XYGesturePasswordTestActivity.class);
            gestureParent.getActivity().startActivity(intent);
        }
    }
}
