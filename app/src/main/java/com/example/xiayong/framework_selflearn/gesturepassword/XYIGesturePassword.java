package com.example.xiayong.framework_selflearn.gesturepassword;

/**
 * 应用前后台切换时，关于手势密码的回调
 * Created by cugyong on 2018/1/12.
 */

public interface XYIGesturePassword {

    void onEntryForeground(XYIGestureParent gestureParent);
    void onEntryBackground(XYIGestureParent gestureParent);
}
