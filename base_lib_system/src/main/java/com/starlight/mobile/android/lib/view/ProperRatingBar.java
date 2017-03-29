package com.starlight.mobile.android.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.starlight.mobile.android.lib.R;

/**
 * Created by Raleigh.Luo on 16/5/29.
 */
public class ProperRatingBar extends LinearLayout {

    private static final int DF_TOTAL_TICKS = 5;
    private static final int DF_DEFAULT_TICKS = 3;
    private static final boolean DF_CLICKABLE = false;
    private static final int DF_SYMBOLIC_TICK_RES = R.string.prb_default_symbolic_string;
    private static final int DF_SYMBOLIC_TEXT_SIZE_RES = R.dimen.prb_symbolic_tick_default_text_size;
    private static final int DF_SYMBOLIC_TEXT_STYLE = Typeface.NORMAL;
    private static final int DF_SYMBOLIC_TEXT_NORMAL_COLOR = Color.BLACK;
    private static final int DF_SYMBOLIC_TEXT_SELECTED_COLOR = Color.GRAY;
    private static final int DF_TICK_SPACING_RES = R.dimen.prb_drawable_tick_default_spacing;

    private int totalTicks;
    private int lastSelectedTickIndex;
    private boolean isClickable;
    private String symbolicTick;
    private int customTextSize;
    private int customTextStyle;
    private int customTextNormalColor;
    private int customTextSelectedColor;
    private int tickNormalDrawable;
    private int tickSelectedDrawable;
    private int tickSpacing;
    private int tickDrawableWidth=0;
    private int tickDrawableHeigt=0;
    private boolean isHalfstepSize=true;//是否为0.5步长否则为1，默认为0.5

    private boolean useSymbolicTick = false;
    private float rating;
    private RatingListener listener = null;
    private Bitmap mHalfBitmap=null;

    public ProperRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProperRatingBar);
        //
        totalTicks = a.getInt(R.styleable.ProperRatingBar_prb_totalTicks, DF_TOTAL_TICKS);
        rating = a.getFloat(R.styleable.ProperRatingBar_prb_defaultRating, DF_DEFAULT_TICKS);
        //
        isClickable = a.getBoolean(R.styleable.ProperRatingBar_prb_clickable, DF_CLICKABLE);
        //
        symbolicTick = a.getString(R.styleable.ProperRatingBar_prb_symbolicTick);
        if (symbolicTick == null) symbolicTick = context.getString(DF_SYMBOLIC_TICK_RES);
        //
        customTextSize = a.getDimensionPixelSize(R.styleable.ProperRatingBar_android_textSize,
                context.getResources().getDimensionPixelOffset(DF_SYMBOLIC_TEXT_SIZE_RES));
        customTextStyle = a.getInt(R.styleable.ProperRatingBar_android_textStyle, DF_SYMBOLIC_TEXT_STYLE);
        customTextNormalColor = a.getColor(R.styleable.ProperRatingBar_prb_symbolicTickNormalColor,
                DF_SYMBOLIC_TEXT_NORMAL_COLOR);
        customTextSelectedColor = a.getColor(R.styleable.ProperRatingBar_prb_symbolicTickSelectedColor,
                DF_SYMBOLIC_TEXT_SELECTED_COLOR);
        tickDrawableWidth=a.getDimensionPixelSize(R.styleable.ProperRatingBar_prb_tickDrawable_width,0);
        tickDrawableHeigt=a.getDimensionPixelSize(R.styleable.ProperRatingBar_prb_tickDrawable_height,0);
        isHalfstepSize=a.getBoolean(R.styleable.ProperRatingBar_prb_isHalf_stepSize,true);
        //
        tickNormalDrawable = a.getResourceId(R.styleable.ProperRatingBar_prb_tickNormalDrawable,0);
        tickSelectedDrawable = a.getResourceId(R.styleable.ProperRatingBar_prb_tickSelectedDrawable,0);
        tickSpacing = a.getDimensionPixelOffset(R.styleable.ProperRatingBar_prb_tickSpacing,
                context.getResources().getDimensionPixelOffset(DF_TICK_SPACING_RES));
        //
        afterInit();
        //
        a.recycle();
    }

    private void afterInit() {
        if (rating > totalTicks) rating = totalTicks;
        int doubleNum=(int)(rating*2);
        lastSelectedTickIndex = (int) (doubleNum%2>0?doubleNum/2:rating - 1);
        //
        if (tickNormalDrawable ==0 || tickSelectedDrawable == 0) {
            useSymbolicTick = true;
        }else{
            initHalfImage();
        }
        //
        addChildren(this.getContext());
    }

    private void addChildren(Context context) {
        this.removeAllViews();
        for (int i = 0; i < totalTicks; i++) {
            addChild(context, i);
        }
        redrawChildren();
    }
    private void initHalfImage(){
        // 防止出现Immutable bitmap passed to Canvas constructor错误
        Bitmap normalBitmap = BitmapFactory.decodeResource(getResources(),
                tickNormalDrawable).copy(Bitmap.Config.ARGB_8888, true);
        Bitmap selectBitmap = ((BitmapDrawable) getResources().getDrawable(
                tickSelectedDrawable)).getBitmap();
        Bitmap newBitmap2 = Bitmap.createBitmap(selectBitmap,0,0,selectBitmap.getWidth()/2, selectBitmap.getHeight());
        mHalfBitmap = Bitmap.createBitmap(normalBitmap);

        Canvas canvas = new Canvas(mHalfBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(0);
        canvas.drawRect(0, 0, normalBitmap.getWidth()/2, normalBitmap.getHeight(), paint);
        paint = new Paint();
        canvas.drawBitmap(newBitmap2,0,0, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储新合成的图片
        canvas.restore();
    }

    private void addChild(Context context, int position) {
        if (useSymbolicTick) {
            addSymbolicChild(context, position);
        } else {
            addDrawableChild(context, position);
        }
    }

    private void addSymbolicChild(Context context, int position) {
        TextView tv = new TextView(context);
        tv.setText(symbolicTick);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, customTextSize);
        if (customTextStyle != 0) {
            tv.setTypeface(Typeface.DEFAULT, customTextStyle);
        }
        if (isClickable) {
            tv.setTag(R.id.prb_child_tag_id, position);
            tv.setOnClickListener(mTickClickedListener);
        }
        this.addView(tv);
    }

    private void addDrawableChild(Context context, int position) {
        ImageView iv = new ImageView(context);
        if(tickDrawableWidth>0){
            LayoutParams params=new LayoutParams(tickDrawableWidth,tickDrawableHeigt);
            iv.setLayoutParams(params);
        }
        iv.setPadding(tickSpacing, tickSpacing, tickSpacing, tickSpacing);
        if (isClickable) {
            iv.setTag(R.id.prb_child_tag_id, position);
            iv.setOnClickListener(mTickClickedListener);
        }
        this.addView(iv);
    }

    private OnClickListener mTickClickedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int temp= (int) v.getTag(R.id.prb_child_tag_id);
            lastSelectedTickIndex=temp==lastSelectedTickIndex?temp-1:temp;
            rating = lastSelectedTickIndex + 1;
            redrawChildren();
            if (listener != null) listener.onRatePicked(ProperRatingBar.this);
        }
    };

    private void redrawChildren() {
        for (int i = 0; i < totalTicks; i++) {
            if (useSymbolicTick) {
                redrawChildSelection((TextView) ProperRatingBar.this.getChildAt(i), i <= lastSelectedTickIndex);
            } else {
                int doubleNum=(int)(rating*2);
                redrawChildSelection((ImageView) ProperRatingBar.this.getChildAt(i), i <= lastSelectedTickIndex,i==lastSelectedTickIndex&&doubleNum%2>0);
            }
        }
    }

    private void redrawChildSelection(ImageView child, boolean isSelected,boolean hasHalf) {
        if (isSelected) {
            if(isHalfstepSize&&hasHalf){//半星
                child.setImageBitmap(mHalfBitmap);
            }else child.setImageResource(tickSelectedDrawable);

        } else {
            child.setImageResource(tickNormalDrawable);
        }


    }

    private void redrawChildSelection(TextView child, boolean isSelected) {
        if (isSelected) {
            child.setTextColor(customTextSelectedColor);
        } else {
            child.setTextColor(customTextNormalColor);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Saving and restoring state
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.rating = rating;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        setRating(savedState.rating);
    }

    static class SavedState extends BaseSavedState {

        float rating;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.rating = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.rating);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Them getter and setter methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the attached {@link RatingListener}
     * @return listener or null if none was set
     */
    @Nullable
    public RatingListener getListener() {
        return listener;
    }

    /**
     * Set the {@link RatingListener} to be called when user taps rating bar's ticks
     * @param listener listener to set
     *
     * @throws IllegalArgumentException if listener is <b>null</b>
     */
    public void setListener(RatingListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null!");

        this.listener = listener;
        this.isClickable = true;
    }

    /**
     * Remove listener
     */
    public void removeRatingListener() {
        this.listener = null;
    }

    /**
     * Get the current rating shown
     * @return rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * Set the rating to show
     * @param rating new rating value
     */
    public void setRating(float rating) {
        if (rating  > this.totalTicks) rating = totalTicks;
        this.rating = rating;
        int doubleNum=(int)(rating*2);
        lastSelectedTickIndex = doubleNum%2>0?doubleNum/2: (int) (rating - 1);
        redrawChildren();
    }

    public void setSymbolicTick(String tick) {
        this.symbolicTick = tick;
        afterInit();
    }

    public String getSymbolicTick() {
        return this.symbolicTick;
    }
    public interface RatingListener {

        void onRatePicked(ProperRatingBar ratingBar);
    }
}
