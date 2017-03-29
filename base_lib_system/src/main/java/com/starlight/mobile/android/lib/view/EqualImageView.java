package com.starlight.mobile.android.lib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Raleigh on 15/12/10.
 */
public class EqualImageView  extends ImageView {
    private OnChangeHeightListener listener;
    public void setOnChangeHeightListener(OnChangeHeightListener listener){
        this.listener=listener;
    }
    private int height=0;

    public EqualImageView(Context context) {
        super(context);
    }

    public EqualImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EqualImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth=getMeasuredWidth();
        setMeasuredDimension(measuredWidth, measuredWidth);

        if(measuredWidth>0&&height!=measuredWidth){
            height=measuredWidth;
            if(listener!=null)listener.onChange(height);
        }
    }
    public interface OnChangeHeightListener{
        public void onChange(int measuredHeight);
    }
}
