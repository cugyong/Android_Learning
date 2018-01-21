package com.example.xiayong.framework_selflearn.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.xiayong.framework_selflearn.R;

import java.io.InputStream;

/**
 * Created by cugyong on 2018/1/17.
 */

public class XYShowGifView extends View {

    private Movie movie;
    private long startTime = 0;
    private float scale;

    public XYShowGifView(Context context) {
        this(context, null);
    }

    public XYShowGifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYShowGifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        opt.inPurgeable = true;
//        opt.inInputShareable = true;
//        //获取资源图片  
    //      InputStream is = context.getResources().openRawResource(R.raw.timg);
   //       movie = Movie.decodeStream(is);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int movieWidth = movie.width();
//        int movieHeight = movie.height();
//        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
//        float scaleW = (float) maxWidth / (float) movieWidth;
//        float scaleH = (float) maxHeight / (float) movieHeight;
//        scale = Math.min(scaleW, scaleH);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = getResources().getDrawable(R.mipmap.timg);
        Bitmap bitmap = drawable2Bitmap(drawable);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
//        long now = SystemClock.uptimeMillis();
//        if (startTime == 0){
//            startTime = now;
//        }
//        if (movie != null){
//            int duration  = movie.duration();
//            if (duration == 0){
//                duration = 1000;
//            }
//            int current = (int)((now - startTime)%duration);
//            movie.setTime(current);
//            canvas.scale(scale, scale);
//            movie.draw(canvas,0,0);
//            invalidate();
//        }
    }

    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片  
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);
    }

    Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 :
                            Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;}
        else {
            return null;
        }
    }

}
