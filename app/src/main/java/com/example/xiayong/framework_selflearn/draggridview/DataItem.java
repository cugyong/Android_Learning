package com.example.xiayong.framework_selflearn.draggridview;

import android.graphics.drawable.Drawable;

/**
 * Created by cugyong on 2018/1/19.
 */

public class DataItem {

    private Drawable res;
    private String text;
    private Drawable del;
    private boolean isDelShow;

    public DataItem(){}

    public Drawable getRes() {
        return res;
    }

    public void setRes(Drawable res) {
        this.res = res;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Drawable getDel() {
        return del;
    }

    public void setDel(Drawable del) {
        this.del = del;
    }

    public boolean isDelShow() {
        return isDelShow;
    }

    public void setDelShow(boolean delShow) {
        isDelShow = delShow;
    }
}
