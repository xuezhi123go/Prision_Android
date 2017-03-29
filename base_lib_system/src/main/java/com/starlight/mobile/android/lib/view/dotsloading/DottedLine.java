package com.starlight.mobile.android.lib.view.dotsloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.starlight.mobile.android.lib.R;


/**
 * Created by Administrator on 2016/11/14 0014.
 */

public class DottedLine extends View {
    private Paint paint = null;
    private Path path = null;
    private PathEffect pe = null;
    public DottedLine(Context context) {
        super(context);
    }

    public DottedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DottedLine);
        int lineColor = a.getColor(R.styleable. DottedLine_lineColor, 0XFF000000);
        a.recycle();
        this. paint = new Paint();
        this. path = new Path();
        this. paint.setStyle(Paint.Style. STROKE);
        this. paint.setColor(lineColor);
        this. paint.setAntiAlias( true);
        this. paint.setStrokeWidth( dip2px(getContext(), 2.0F));
        float[] arrayOfFloat = new float[4];
        arrayOfFloat[0] =  dip2px(getContext(), 2.0F);
        arrayOfFloat[1] = dip2px(getContext(), 2.0F);
        arrayOfFloat[2] = dip2px(getContext(), 2.0F);
        arrayOfFloat[3] = dip2px(getContext(), 2.0F);
        this. pe = new DashPathEffect(arrayOfFloat,dip2px(getContext(), 1.0F));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this. path.moveTo(0.0F, 0.0F);
        this. path.lineTo(0.0F, getMeasuredHeight());
        this. paint.setPathEffect( this. pe);
        canvas.drawPath( this. path, this. paint);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public  int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public  int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public  int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public  int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
