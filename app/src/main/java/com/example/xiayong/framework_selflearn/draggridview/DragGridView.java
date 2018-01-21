package com.example.xiayong.framework_selflearn.draggridview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xiayong.framework_selflearn.R;
import com.example.xiayong.framework_selflearn.utils.XYUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 可动态增加、删除方块，可移动顺序
 * 1、拖动分两步：1.1 可视视图做移动动画； 1.2 动画都完成后做适配器的数据交换操作
 * 2、适配器中不复用view，似乎可以消除数据显示抖动问题
 * Created by cugyong on 2018/1/18.
 */

public class DragGridView extends GridView implements AdapterView.OnItemClickListener{

    private final static String TAG = "DragGridView";

    // 长按触发有效时间
    private final static int LONG_PRESS_VALID_TIME = 1000;
    // 长按 消息标识
    private final static int MESSAGE_LONG_PRESS = 1;
    // 长按触发后的震动时间
    private static final int VIBRATOR_TIME = 50;
    // 长按所在位置不属于任何一个子项目
    private static final int LOCATION_INVALID = -1;
    // 动画持续时间
    private static final int ANIMATION_DURATION = 300;

    private Context mContext;
    private GridViewAdapter mGridViewAdapter;

    // 处于可拖拉状态，则拦截后续move、up事件,
    private boolean mIsDrag = false;
    // 震动器
    private Vibrator mVibrator;
    // 窗口管理器
    private WindowManager mWindowManager;
    // 状态栏高度
    private int mStatusHeight;
    // 长按时选择的子项目
    private View mStartItemView;
    // 长按时的位置
    private int mStartX;
    private int mStartY;
    // 拖动的镜像
    private ImageView mDragIV;
    // 拖动的镜像的布局参数
    private WindowManager.LayoutParams mDragIVLayoutParams;
    // 手指所在点离所在子项目左边界的距离
    private int mPoint2ItemLeft;
    // 手指所在点离所在子项目上边界的距离
    private int mPoint2ItemTop;
    // DragGridView离屏幕左边界的距离
    private int mOffset2Left;
    // DragGridView离屏幕上边界的距离
    private int mOffset2Top;
    // 手指所在点所属子项目在适配器中的位置
    private int mOriginPointLoc;
    private int mCurrentPointLoc;
    // 动画是否完成
    private boolean mIsMove = false;
    // 是否是最后一个动画
    private boolean mIsLastAnimation = false;
    // 是否手指抬起
    private boolean mIsUP = false;
    // 是否数据交换完成
    private boolean mIsSwapDataDone = true;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int key = message.what;
            switch (key){
                case MESSAGE_LONG_PRESS: // 长按事件触发
                    mIsDrag = true;
                    mVibrator.vibrate(VIBRATOR_TIME);
                    // 创建镜像
                    createDragImage(mStartItemView, mStartX, mStartY);
                    // 隐藏选择的子项目
                    getChildAt(mOriginPointLoc - getFirstVisiblePosition()).setVisibility(INVISIBLE);
                    break;
            }
            return false;
        }
    });

    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mIsMove = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mIsLastAnimation){
                mIsLastAnimation = false;
                swapItemData();
                mIsMove = false;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init(){
        List<DataItem> dataItems = new ArrayList<>();
        DataItem dataItem = new DataItem();
        dataItem.setRes(mContext.getResources().getDrawable(R.mipmap.add));
        dataItem.setDel(mContext.getResources().getDrawable(R.mipmap.delete_grid));
        dataItem.setText(mContext.getResources().getString(R.string.add));
        dataItem.setDelShow(false);
        dataItems.add(dataItem);

        mGridViewAdapter = new GridViewAdapter(mContext, dataItems);
        setAdapter(mGridViewAdapter);
        setOnItemClickListener(this);

        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = XYUtils.getStatusBarHeight((Activity) mContext);
    }

    // i是当前item在gridview中适配器里的位置, l是当前item在gridview里的位置
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int size = mGridViewAdapter.getCount();
        if (size > 2 && i == size -1){ // 当有其他子项目时，适配器最后一个删除按钮
            mGridViewAdapter.showOrHideDelView();
        }else if (size < 2 || i == size - 2){ // 添加按钮: 适配器倒数第二个或者只有添加按钮时
            addItem();
        } else {
            Toast.makeText(mContext, "you have clicked: " + (i + 1), Toast.LENGTH_SHORT).show();
        }
    }

    // 分发touch事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int downX = (int)ev.getX();
        int downY = (int)ev.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mIsUP = false;
                int size = mGridViewAdapter.getCount();
                Pair<View, Integer> pair = getPointBelongItemViewLoc(downX, downY);
                mOriginPointLoc = pair.second;
                mStartItemView = pair.first;
                // 点在除添加、删除按钮以外的其他按钮上
                if (mOriginPointLoc != LOCATION_INVALID && mOriginPointLoc < size - 2){
                    mHandler.sendEmptyMessageDelayed(MESSAGE_LONG_PRESS, LONG_PRESS_VALID_TIME);
                    // getRawX()相对于屏幕左上角，getX()相对于所在控件左上角
                    mOffset2Left = (int)(ev.getRawX() - downX);
                    mOffset2Top = (int)(ev.getRawY() - downY);
                    mPoint2ItemLeft = downX - mStartItemView.getLeft();
                    mPoint2ItemTop = downY - mStartItemView.getTop();
                    mStartX = downX;
                    mStartY = downY;
                    Log.d(TAG, "发送了长按消息");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsDrag){ // 可以拖拉Item了，移动消息
                 //   Log.d(TAG, "可以拖拉Item了，移动消息");
                } else {
                    Pair<View, Integer> pair1 = getPointBelongItemViewLoc(downX, downY);
                    if (pair1.second != mOriginPointLoc){ // 手指移出了起始Item, 清空长按消息
                        mHandler.removeMessages(MESSAGE_LONG_PRESS);
                        Log.d(TAG, "手指移出了起始Item, 清空长按消息");
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDrag){
                    Log.d(TAG, "正在拖拽，手指抬起消息-dispatchTouchEvent");
                } else {
                    mHandler.removeMessages(MESSAGE_LONG_PRESS);
                    Log.d(TAG, "按的时间太短，清空长按消息");
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // 处理touch事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsDrag){
            int action = ev.getAction();
            int downX = (int) ev.getX();
            int downY = (int) ev.getY();
            switch (action){
                case MotionEvent.ACTION_MOVE:
                    if (mIsDrag){
                        // 移动镜像
                        moveDragImage(downX, downY);
                        if (!mIsMove){ // 可以拖动同时没有在进行动画时
                            swapItem(downX, downY);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mIsDrag){
                        mIsDrag = false;
                        mIsUP = true;
                        destoryDragImage();
                        int loc = getFirstVisiblePosition();
                        if (mIsSwapDataDone){
                            getChildAt(mOriginPointLoc - loc).setVisibility(VISIBLE);
                        }
                        Log.d(TAG, "正在拖拽，手指抬起消息-onTouchEvent");
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void addItem(){
        int size = mGridViewAdapter.getCount();
        if (size <= 1){ // 除了添加按钮，没有其他按钮时，添加新的按钮需要同时添加删除按钮
            DataItem dataItem_del = new DataItem();
            dataItem_del.setRes(mContext.getResources().getDrawable(R.mipmap.delete));
            dataItem_del.setDel(mContext.getResources().getDrawable(R.mipmap.delete_grid));
            dataItem_del.setText(mContext.getResources().getString(R.string.delete));
            dataItem_del.setDelShow(false);
            mGridViewAdapter.addData(dataItem_del, true);
        }
        DataItem dataItem_first = (DataItem) mGridViewAdapter.getItem(0);
        boolean isShowDel = dataItem_first.isDelShow();
        DataItem dataItem = new DataItem();
        dataItem.setRes(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
        dataItem.setDel(mContext.getResources().getDrawable(R.mipmap.delete_grid));
        if (size > 2 && isShowDel){ // 如果当前子项目右上角删除按钮显示，则新增加的项目也显示
            dataItem.setDelShow(true);
        } else {
            dataItem.setDelShow(false);
        }
        dataItem.setText("第" + (mGridViewAdapter.getCount() - 1) + "个");

        mGridViewAdapter.addData(dataItem, false);
    }

    private void swapItem(int downX, int downY){
        Pair<View, Integer> pair = getPointBelongItemViewLoc(downX, downY);
        moveItem(pair);
    }

    private void moveItem(Pair<View, Integer> pair){
        if (pair.second != LOCATION_INVALID && pair.second < mGridViewAdapter.getCount() - 2){
            // 加上mIsSwapDataDone能保证上次数据交换完成才进行下次动画，
            // 避免因为mOriginPointLoc没有及时更新而出现重复动画
            if (pair.second != mOriginPointLoc && mIsSwapDataDone) { // 手指滑动到另外一个子项目中，交换数据更换位置
                mIsSwapDataDone = false;
                mCurrentPointLoc = pair.second;
                int firstVis = getFirstVisiblePosition();
                int preX = getChildAt(mOriginPointLoc - firstVis).getLeft();
                int preY = getChildAt(mOriginPointLoc - firstVis).getTop();
                if (mOriginPointLoc < mCurrentPointLoc){
                    for (int i=mOriginPointLoc; i < mCurrentPointLoc; i++){
                        View view = getChildAt(i + 1 - firstVis);
                        if (i == mCurrentPointLoc - 1){
                            mIsLastAnimation = true;
                        }
                        Pair<Integer, Integer> pair1 = startAnimation(view, preX, preY);
                        Log.d(TAG, "startAnimation: " + (i+1) + " to " + i);
                        preX = pair1.first;
                        preY = pair1.second;
                    }
                } else {
                    for (int i=mOriginPointLoc; i > mCurrentPointLoc; i--){
                        View view = getChildAt(i - 1 - firstVis);
                        if (i == mCurrentPointLoc + 1){
                            mIsLastAnimation = true;
                        }
                        Pair<Integer, Integer> pair1 = startAnimation(view, preX, preY);
                        Log.d(TAG, "startAnimation: " + (i+1) + " to " + i);
                        preX = pair1.first;
                        preY = pair1.second;
                    }
                }
            }
        }
    }

    private Pair<Integer, Integer> startAnimation(View view, int preX, int preY){
        int curX = view.getLeft();
        int curY = view.getTop();
        TranslateAnimation animation = new TranslateAnimation(0, preX - curX,
                0, preY - curY);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(mAnimationListener);
        view.startAnimation(animation);
        return new Pair<>(curX,curY);
    }

    private void swapItemData(){
        DataItem curDataItem = (DataItem) mGridViewAdapter.getItem(mOriginPointLoc);
        DataItem curDataItem_copy = new DataItem();
        curDataItem_copy.setRes(curDataItem.getRes());
        curDataItem_copy.setText(curDataItem.getText());
        curDataItem_copy.setDelShow(curDataItem.isDelShow());
        curDataItem_copy.setDel(curDataItem.getDel());
        if (mCurrentPointLoc != LOCATION_INVALID && mCurrentPointLoc < mGridViewAdapter.getCount() - 2){
            if (mCurrentPointLoc != mOriginPointLoc){ // 手指滑动到另外一个子项目中，交换数据更换位置
                if (mOriginPointLoc < mCurrentPointLoc){
                    for (int i=mOriginPointLoc; i < mCurrentPointLoc; i++){
                        mGridViewAdapter.changeData(i, i + 1, false);
                    }
                } else {
                    for (int i=mOriginPointLoc; i > mCurrentPointLoc; i--){
                        mGridViewAdapter.changeData(i, i - 1, false);
                    }
                }
                mGridViewAdapter.changeData(mCurrentPointLoc, curDataItem_copy, false);
                int loc = getFirstVisiblePosition();
                getChildAt(mOriginPointLoc - loc).setVisibility(VISIBLE);
                if (!mIsUP){
                    getChildAt(mCurrentPointLoc - loc).setVisibility(INVISIBLE);
                }
                mOriginPointLoc = mCurrentPointLoc;
                mIsSwapDataDone = true;
                Log.d(TAG, "swap data done");
                mGridViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 创建拖动的镜像
     * @param view 长按点击的子项目
     * @param downX 相对父控件的点击位置
     * @param downY 相对父控件的点击位置
     */
    private void createDragImage(View view, int downX, int downY){
        // 开启该视图绘图缓存功能
        view.setDrawingCacheEnabled(true);
        // 获取该视图缓存的位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        // 释放绘图缓存，避免出现重复的镜像      
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        mDragIVLayoutParams = new WindowManager.LayoutParams();
        mDragIVLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mDragIVLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mDragIVLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mDragIVLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mDragIVLayoutParams.alpha = 0.55f; //透明度
        mDragIVLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mDragIVLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDragIVLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mDragIV = new ImageView(mContext);
        mDragIV.setImageBitmap(bitmap);
        mWindowManager.addView(mDragIV, mDragIVLayoutParams);
    }

    /**
     * 移动镜像
     * @param downX
     * @param downY
     */
    private void moveDragImage(int downX, int downY){
        mDragIVLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mDragIVLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowManager.updateViewLayout(mDragIV, mDragIVLayoutParams);
    }

    private void destoryDragImage(){
        if (mDragIV != null){
            mWindowManager.removeView(mDragIV);
            mDragIV = null;
        }
    }

    /**
     * 获得手指所在点所属子项目以及其在适配器中的位置
     * @param downX
     * @param downY
     * @return
     */
    private Pair<View, Integer> getPointBelongItemViewLoc(int downX, int downY){
        for (int i=0; i<getChildCount(); i++){ //getChildCount()返回的是当前在窗口中可见的子项目数
            View view = getChildAt(i);
            int width = view.getWidth();
            int height = view.getHeight();
            if (downX >= view.getLeft() && downX <= view.getLeft() + width && downY >=
                    view.getTop() && downY <= view.getTop() + height){
                return new Pair<>(view, getFirstVisiblePosition() + i);
            }
        }
        return new Pair<>(null, LOCATION_INVALID);
    }
}
