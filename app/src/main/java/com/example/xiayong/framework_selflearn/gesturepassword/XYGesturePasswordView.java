package com.example.xiayong.framework_selflearn.gesturepassword;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.xiayong.framework_selflearn.R;

/**
 * 手势密码视图的小圆圈视图
 * Created by cugyong on 2018/1/11.
 */

public class XYGesturePasswordView extends View{

    public static enum PAINT_COLOR{NORMAL_COLOR, SELECTED_COLOR, ERROR_COLOR}

    private Paint mPaint;
    // 圆圈不同状态的颜色
    private int mNormalColor;
    private int mSelectedColor;
    private int mErrorColor;

    public XYGesturePasswordView(Context context) {
        this(context, null);
    }

    public XYGesturePasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYGesturePasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNormalColor = context.getResources().getColor(R.color.colorBlack);
        mSelectedColor = context.getResources().getColor(R.color.colorPrimaryDark);
        mErrorColor = context.getResources().getColor(R.color.colorRed);
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setColor(mNormalColor);
        mPaint.setStyle(Paint.Style.STROKE);// 空心圆
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        width = width > height ? height : width;
        int widthMeasureSpec1 = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec1, widthMeasureSpec1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = getWidth();
        int centerX = radius / 2;
        canvas.drawCircle(centerX, centerX, (float) ((radius / 2)*0.9), mPaint);
        super.onDraw(canvas);
    }

    public void setPaintColor(PAINT_COLOR paintColor){
        switch (paintColor){
            case NORMAL_COLOR:
                mPaint.setColor(mNormalColor);
                break;
            case SELECTED_COLOR:
                mPaint.setColor(mSelectedColor);
                break;
            case ERROR_COLOR:
                mPaint.setColor(mErrorColor);
                break;
        }
        postInvalidate();
    }
}
