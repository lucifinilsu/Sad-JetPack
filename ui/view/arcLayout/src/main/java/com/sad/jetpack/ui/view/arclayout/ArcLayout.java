package com.sad.jetpack.ui.view.arclayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class ArcLayout extends RelativeLayout {
    private int width;
    private int height;
    private int arcHeight;
    private int mBgColor;

    private Paint paint;
    public static final int TOP=1;
    public static final int BOTTOM=2;
    private int posType=TOP;
    public ArcLayout(Context context) {
        this(context, null);
    }

    public ArcLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ArcLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcLayout);
        arcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcLayout_arcHeight, 0);//dipToPx(getContext(),typedArray.getDimensionPixelSize(R.styleable.ArcLayout_arcHeight, 0));
        mBgColor=typedArray.getColor(R.styleable.ArcLayout_arcColor, Color.parseColor("#ffffffff"));
        posType = typedArray.getInt(R.styleable.ArcLayout_arcPosType, TOP);
        if (paint==null){
            paint = new Paint();
        }

        typedArray.recycle();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setWillNotDraw(false);

    }
    public void setArcHeight(int arcHeight,boolean requestLayout){
        this.arcHeight=arcHeight;
        if (requestLayout){
            requestLayout();
        }
        else {
            invalidate();
        }

    }

    public void setPosType(int posType,boolean requestLayout) {
        this.posType = posType;
        if (requestLayout){
            requestLayout();
        }
        else {
            invalidate();
        }
    }

    public int getArcHeight() {
        return arcHeight;
    }

    public int getPosType() {
        return posType;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            height = getMeasuredHeight();
            width = getMeasuredWidth();
            if (height<arcHeight){
                arcHeight=height;
            }
            //Log.e("arc","--------->mHeight="+height+",mArcHeight="+arcHeight+",w="+width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path path = new Path();
        if (this.posType==TOP){
            path.moveTo(0,arcHeight);
            path.quadTo(width / 2,-arcHeight,width,arcHeight);
        }
        else if (posType==BOTTOM){
            path.moveTo(0,height);
            path.quadTo(width / 2,height-2*arcHeight,width,height);
        }
        path.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(mBgColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.drawPath(path, paint);
    }

}
