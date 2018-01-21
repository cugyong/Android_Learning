package com.example.xiayong.framework_selflearn.utils;

import android.app.Activity;
import android.graphics.Rect;

/**
 * Created by cugyong on 2018/1/20.
 */

 public class XYUtils {

    /**
     * 获取状态栏的高度
     * 状态栏：显示手机信号的那一行
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity){
        int statusBarHeight = -1;
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        if (rect.top > 0){
            statusBarHeight = rect.top;
        } else { // 通过R类的反射
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                statusBarHeight = activity.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
