package com.example.xiayong.framework_selflearn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.xiayong.framework_selflearn.R;

import java.util.List;

/**
 * Created by xiayong on 2018/1/4.
 * 城市列表或者联系人列表右边字母列控件
 */

public class XYSideBar extends View {

    private static final String TAG = "XYSideBar";

    // 显示的字符串列表
    private List<String> mItems;
    private Context mContext;
    private Paint mPaint;
    //控件宽度
    private int mWidth;
    //控件高度
    private int mHeight;
    //字符串上下之间的空隙
    private int mFontSpace;
    private Rect mStringRect = new Rect(0,0,0,0);
    private OnItemTouchListener mOnItemTouchListener;

    public XYSideBar(Context context) {
        this(context, null);
    }

    public XYSideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint = new Paint();
        mPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.font_size_14));
        mPaint.setColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        mPaint.setAntiAlias(true); //抗锯齿
        mFontSpace = mContext.getResources().getDimensionPixelSize(R.dimen.padding_2);
    }

    public void setItems(List<String> items){
        mItems = items;
        invalidate();// 数据变化后，重绘
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 控件的高度
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        //超过控件最大高度时，缩小字体压缩显示
        mHeight = _getHeight(maxHeight);
        // 控件的宽度
        mWidth = _getMaxItemWidth();
        int widthSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthSpec, heightSpec);
    }

    /**
     * 获得要显示的字符串列表中最宽的字符串的宽度
     * @return
     */
    private int _getMaxItemWidth(){
        int maxWidth = 0;
        for (int i=0; i<mItems.size(); i++ ){
            // 获得字符串的宽度
            int width = (int)mPaint.measureText(mItems.get(i), 0, mItems.get(i).length());
            if (maxWidth < width){
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    /**
     * 根据字号设置控件高度
     * @return
     */
    private int _getHeight(int maxHeight){
        int height = (_getFontHeight(R.dimen.font_size_14) + mFontSpace) * mItems.size();
        if (height > maxHeight){
            height = (_getFontHeight(R.dimen.font_size_12) + mFontSpace) * mItems.size();
        }
        if (height > maxHeight){
            height = (_getFontHeight(R.dimen.font_size_10) + mFontSpace) * mItems.size();
        }
        if (height > maxHeight){
            height = maxHeight;
        }
        return height;
    }

    /**
     * 设置字体大小，返回字体高度
     * @param fontSizeID
     */
    private int _getFontHeight(int fontSizeID){
        mPaint.setTextSize(mContext.getResources().getDimensionPixelSize(
                fontSizeID));
        return (int)(mPaint.getFontMetrics().descent - mPaint.getFontMetrics().ascent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float singleHeight = (float) mHeight/mItems.size();
        for (int i=0; i<mItems.size(); i++){
            float xPos = (int)(mWidth - mPaint.measureText(mItems.get(i), 0, mItems.get(i).length()))/2;
            mStringRect.setEmpty();
            mPaint.getTextBounds(mItems.get(i), 0, mItems.get(i).length(), mStringRect); //获取的是没有空隙的宽高
            float yPos = singleHeight * i + (singleHeight + mStringRect.height()) / 2;
            canvas.drawText(mItems.get(i), xPos, yPos, mPaint); //如果是需要画在基准线以下的字符，会有偏差
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int yPos = (int)event.getY();
        int loc = yPos * mItems.size() / mHeight;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mOnItemTouchListener != null){
                    mOnItemTouchListener.onItemTouchDown(loc);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mOnItemTouchListener != null){
                    mOnItemTouchListener.onItemTouchUp(loc);
                }
                break;
        }

        return true;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener){
        mOnItemTouchListener = onItemTouchListener;
    }

    public interface OnItemTouchListener {
        public void onItemTouchDown(int loc);
        public void onItemTouchUp(int loc);
    }
}
