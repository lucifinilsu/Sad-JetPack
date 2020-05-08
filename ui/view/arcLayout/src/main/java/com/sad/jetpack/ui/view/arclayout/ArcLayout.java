package com.sad.jetpack.ui.view.arclayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class ArcLayout extends RelativeLayout {
    private int width;
    private int height;
    private int arcHeight;
    private int mBgColor;

    private Path clipPath;
    private Paint paint;
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
        
        typedArray.recycle();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (paint==null){
            paint = new Paint();
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }
    public void setArcHeight(int arcHeight){
        this.arcHeight=arcHeight;
        invalidate();
    }
    private Path createClipPath() {
        final Path path = new Path();

        /*path.moveTo(0, 0);
        path.lineTo(0, height);
        path.quadTo(width / 2, height - 2 * arcHeight, width, height);
        path.lineTo(width, 0);
        path.close();*/

        path.moveTo(0,arcHeight);
        path.quadTo(width / 2,-arcHeight,width,arcHeight);
        path.close();


        return path;
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            calculateLayout();
            //LogUtils.e("--------->mHeight="+height+",mArcHeight="+arcHeight);
        }
    }
    private void calculateLayout() {
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        if (width > 0 && height > 0) {
            clipPath = createClipPath();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(mBgColor);
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.drawPath(clipPath, paint);
    }
}
