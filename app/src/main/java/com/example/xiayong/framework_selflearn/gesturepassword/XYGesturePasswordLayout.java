package com.example.xiayong.framework_selflearn.gesturepassword;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.xiayong.framework_selflearn.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、手势密码的布局；2、画手势轨迹；3、验证手势密码
 * 继承ViewGroup的自定义视图需要重载dispatchDraw画图
 * Created by cugyong on 2018/1/11.
 */

public class XYGesturePasswordLayout extends ViewGroup{

    private static final String TAG = "XYGesturePasswordLayout";

    private List<Integer> mCorrectPath; // 正确的手势密码
    private static final int mSize = 3; // 手势密码每一行圆的数目
    private static final int mLineWidth = 3;
    private Context mContext;
    private float mRate = 0.26f; //手势密码圆圈的间隔占整个宽度的比例
    private int mCircleRadius; // 手势密码圆圈的半径
    private int mCircleGen; // 手势密码圆圈的间隔
    private int mGestureWidth; // 手势密码区域所占宽度
    // 所有圆的圆心坐标
    private float[] mCircleX = new float[mSize * mSize];
    private float[] mCircleY = new float[mSize * mSize];
    // 手指滑动的圆的轨迹
    private List<Integer> mPath = new ArrayList<>();
    private Paint mCirclePaint;
    private float mSmallCircleRate = 0.5f; // 内圆占外圆半径的比例
    private Paint mLinePaint;
    // 当前触碰的点
    private float mCurrentX = -1;
    private float mCurrentY;
    // 画的线路径
    private List<Float> mLinePathX = new ArrayList<>();
    private List<Float> mLinePathY = new ArrayList<>();
    private boolean isUp = false; //是否释放手指
    private boolean mIsGestureRight = false;
    private GesturePasswordMatchOKCallBack mMatchOKCallBack; // 手势匹配成功回调

    public XYGesturePasswordLayout(Context context) {
        this(context, null);
    }

    public XYGesturePasswordLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYGesturePasswordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        _initVar();
        _initView();
    }

    private void _initVar(){
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mContext.getResources().getColor(R.color.colorBlack));
        mCirclePaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setColor(mContext.getResources().getColor(R.color.colorBlack));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mLineWidth);

        mCorrectPath = new ArrayList<>();
        mCorrectPath.add(0);
        mCorrectPath.add(3);
        mCorrectPath.add(6);
        mCorrectPath.add(7);
        mCorrectPath.add(8);
    }

    private void _initView(){
        for (int i=0; i<mSize * mSize; i++){
            XYGesturePasswordView xyGesturePasswordView = new XYGesturePasswordView(mContext);
            xyGesturePasswordView.setId(i+1);
            addView(xyGesturePasswordView);
        }
    }

    /**
     * 确定每个手势密码圆圈的半径和间隔
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        width = height > width ? width : height;
        int parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        mCircleGen = (int) ((width * mRate) / (mSize - 1));
        mCircleRadius = (width - mCircleGen * (mSize - 1)) / (mSize * 2);
        mGestureWidth = mCircleRadius * 2 * mSize + mCircleGen * (mSize - 1);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mCircleRadius * 2, MeasureSpec.EXACTLY);

        super.onMeasure(parentWidthMeasureSpec, parentWidthMeasureSpec);

        measureChildren(childWidthMeasureSpec, childWidthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        if (b){
            int width = i2 - i;
            int diff = (width - mGestureWidth) / 2;
            for (int j=0; j<getChildCount(); j++){
                View child = findViewById(j + 1);
                int row = j / mSize;
                int col = j % mSize;
                int left = col * (mCircleRadius * 2 + mCircleGen) + diff;
                int top = row * (mCircleRadius * 2 + mCircleGen) + diff;
                mCircleX[j] = left + mCircleRadius;
                mCircleY[j] = top + mCircleRadius;
                child.layout(left, top, left + mCircleRadius * 2, top + mCircleRadius * 2);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                _onActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                _onActionMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                _onActionUp(event);
                break;
        }
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mLinePathX.size() == 1){ // 刚刚点击了一个点时, 画内圆
            canvas.drawCircle(mCircleX[mPath.get(0)], mCircleY[mPath.get(0)],
                    mCircleRadius * mSmallCircleRate, mCirclePaint);
            if (isUp && !mIsGestureRight){ // 如果输入手势密码错误
                ((XYGesturePasswordView)findViewById(mPath.get(0) + 1)).setPaintColor(
                        XYGesturePasswordView.PAINT_COLOR.ERROR_COLOR);
            }
        }
        for (int i=0; i<mLinePathX.size() - 1; i++){ // 画连线和内圆
            canvas.drawCircle(mCircleX[mPath.get(i)], mCircleY[mPath.get(i)],
                    mCircleRadius * mSmallCircleRate, mCirclePaint);
            canvas.drawCircle(mCircleX[mPath.get(i+1)], mCircleY[mPath.get(i+1)],
                    mCircleRadius * mSmallCircleRate, mCirclePaint);
            canvas.drawLine(mLinePathX.get(i), mLinePathY.get(i),
                    mLinePathX.get(i + 1), mLinePathY.get(i + 1), mLinePaint);
            if (isUp && !mIsGestureRight){ // 如果输入手势密码错误
                ((XYGesturePasswordView)findViewById(mPath.get(i) + 1)).setPaintColor(
                        XYGesturePasswordView.PAINT_COLOR.ERROR_COLOR);
                ((XYGesturePasswordView)findViewById(mPath.get(i + 1) + 1)).setPaintColor(
                        XYGesturePasswordView.PAINT_COLOR.ERROR_COLOR);
            }
        }
        int size = mLinePathX.size();
        if (!isUp && mCurrentX > 0 && size > 0 && mCurrentX != mLinePathX.get(size - 1)){ // 当前点不是起始点且未包含在线路径列表中
            canvas.drawLine(mLinePathX.get(size - 1), mLinePathY.get(size - 1),
                    mCurrentX, mCurrentY, mLinePaint);
        }
        super.dispatchDraw(canvas);
    }

    private void _onActionDown(MotionEvent event){
        setDefaultVals();
        addPoint(event);
        invalidate();
    }

    private void _onActionMove(MotionEvent event){
        mCurrentX = event.getX();
        mCurrentY = event.getY();

        addPoint(event);
        invalidate();
    }

    private void _onActionUp(MotionEvent event){
        isUp = true;

        boolean isCorrent = true;
        if (mCorrectPath.size() == mPath.size()){
            for (int i=0; i<mCorrectPath.size(); i++){
                if (!mCorrectPath.get(i).equals(mPath.get(i))){
                    isCorrent = false;
                    break;
                }
            }
        } else{
            isCorrent = false;
        }
        setVals(isCorrent);
        mIsGestureRight = isCorrent;
        invalidate();
    }

    /**
     * 将参数设置为默认值
     */
    private void setDefaultVals(){
        isUp = false;
        mIsGestureRight = false;
        mCurrentX = -1;
        mLinePathX.clear();
        mLinePathY.clear();
        for (int i=0; i<mPath.size(); i++){
            ((XYGesturePasswordView)findViewById(mPath.get(i) + 1)).setPaintColor(
                    XYGesturePasswordView.PAINT_COLOR.NORMAL_COLOR);
        }
        mPath.clear();
        mLinePaint.setColor(mContext.getResources().getColor(R.color.colorBlack));
        mCirclePaint.setColor(mContext.getResources().getColor(R.color.colorBlack));
    }

    /**
     * 根据输入手势密码正确与否设置画笔的颜色
     * @param isCorrent
     */
    private void setVals(boolean isCorrent){
        if (isCorrent){
            Toast.makeText(mContext, "The GesturePassword is right!", Toast.LENGTH_SHORT).show();
            mMatchOKCallBack.gesturePasswordMatchOK();
        } else {
            mLinePaint.setColor(mContext.getResources().getColor(R.color.colorRed));
            mCirclePaint.setColor(mContext.getResources().getColor(R.color.colorRed));
            Toast.makeText(mContext, "The GesturePassword is error!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点在圆内则保存
     * @param event
     */
    private void addPoint(MotionEvent event){
        int loc = isBelongCircle(event.getX(), event.getY());
        if (-1 != loc && !isPathContain(loc)){
            mPath.add(loc);
            mLinePathX.add(mCircleX[loc]);
            mLinePathY.add(mCircleY[loc]);
        }
    }

    /**
     * 传入的坐标是否在圆内
     * @return 圆的ID-1
     */
    private int isBelongCircle(float x, float y){
        for (int i=0; i<mSize * mSize; i++){
            float val = mCircleRadius * mCircleRadius;
            float val1 = (mCircleX[i] - x) * (mCircleX[i] - x) + (mCircleY[i] - y) * (mCircleY[i] - y);
            if ( val >= val1){ // 在该圆内
                return i;
            }
        }
        return -1;
    }

    /**
     * 是否传入的圆已经包含在路径中
     * @param point
     * @return
     */
    private boolean isPathContain(int point){
        for (int i=0; i<mPath.size(); i++){
            if (mPath.get(i).equals(point)){
                return true;
            }
        }
        return false;
    }

    public void setMatchOKCallBack(GesturePasswordMatchOKCallBack callBack){
        mMatchOKCallBack = callBack;
    }

    interface GesturePasswordMatchOKCallBack {
         void gesturePasswordMatchOK();
    }
}
