package com.starlight.mobile.android.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.starlight.mobile.android.lib.R;


/**底部菜单按钮
 * @author raleighluo
 *
 */
public class RadioButtonPlus extends RadioButton {
	private int leftHeight = -1;
	private int leftWidth = -1;
	private int rightHeight = -1;
	private int rightWidth = -1;
	private int topHeight = -1;
	private int topWidth = -1;
	private int bottomHeight = -1;
	private int bottomWidth = -1;
	private boolean isShow=false;
	private Rect rect;


	public RadioButtonPlus(Context context) {
		super(context);
	}

	public RadioButtonPlus(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public RadioButtonPlus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.TRANSPARENT);
		int size=getResources().getDimensionPixelSize(R.dimen.radio_button_plus_size);
		Rect rect=new Rect(this.getWidth() - size, 0, this.getWidth(), this.getHeight() - size);
		this.rect=rect;
		canvas.drawRect(rect, paint);
		if(isShow){
			Bitmap icon=BitmapFactory.decodeResource(getResources(), R.drawable.tab_notification_bg);
			Matrix matrix=new Matrix();
			matrix.postScale(0.5f, 0.5f);
			Bitmap newIcon=Bitmap.createBitmap(icon,0,0,icon.getWidth(),icon.getHeight(),matrix,true);
			canvas.drawBitmap(newIcon, this.getWidth()-45, 10, null);
		}else{
			canvas.save();
			canvas.restore();
		}
	}

	/**
	 * Get Rect
	 * @return Rect
	 */
	public Rect getRect(){
		return rect;
	}


	/**
	 * Set the notification String
	 * @param isShow set is show
	 */
	public void setNotification(boolean isShow){
		this.isShow=isShow;
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RadioButtonPlus, defStyle, 0);
		if (a != null) {
			int count = a.getIndexCount();
			int index = 0;
			for (int i = 0; i < count; i++) {
				index = a.getIndex(i);

				if(index==R.styleable.RadioButtonPlus_radio_bottom_height)
					bottomHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_bottom_width)
					bottomWidth = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_left_height)
					leftHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_left_width)
					leftWidth = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_right_height)
					rightHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_right_width)
					rightWidth = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_top_height)
					topHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.RadioButtonPlus_radio_top_width)
					topWidth = a.getDimensionPixelSize(index, -1);
			}

			Drawable[] drawables = getCompoundDrawables();
			int dir = 0;
			// 0-left; 1-top; 2-right; 3-bottom;
			for (Drawable drawable : drawables) {
				setImageSize(drawable, dir++);
			}
			setCompoundDrawables(drawables[0], drawables[1], drawables[2],drawables[3]);
		}
	}
	public void setTopDrawable(Drawable drawableTop){
		drawableTop.setBounds(0, 0, topWidth, topHeight);
		setCompoundDrawables(null,drawableTop, null,null);
	}
	public void setLeftDrawable(Drawable drawableLeft){
		drawableLeft.setBounds(0, 0, leftWidth, leftHeight);
		setCompoundDrawables(drawableLeft,null, null,null);
	}
	public void setRightDrawable(Drawable drawableRight){
		drawableRight.setBounds(0, 0, rightWidth, rightHeight);
		setCompoundDrawables(null,null, drawableRight,null);
	}
	public void setBottomDrawable(Drawable drawableBottom){
		drawableBottom.setBounds(0, 0, bottomWidth, bottomHeight);
		setCompoundDrawables(null,null, null,drawableBottom);
	}

	private void setImageSize(Drawable d, int dir) {
		if (d == null) {
			return;
		}

		int height = -1;
		int width = -1;
		switch (dir) {
		case 0:
			// left
			height = leftHeight;
			width = leftWidth;
			break;
		case 1:
			// top
			height = topHeight;
			width = topWidth;
			break;
		case 2:
			// right
			height = rightHeight;
			width = rightWidth;
			break;
		case 3:
			// bottom
			height = bottomHeight;
			width = bottomWidth;
			break;
		}
		if (width != -1 && height != -1) {
			d.setBounds(0, 0, width, height);
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth=getMeasuredWidth();
		if(measuredWidth>0&&width!=measuredWidth){
			width=measuredWidth;
			if(listener!=null)listener.onChange(width);
		}
	}
	private int width=0;
	public interface OnChangeWidthListener{
		public void onChange(int measuredWidth);
	}
	private OnChangeWidthListener listener;
	public void setOnChangeWidthListener(OnChangeWidthListener listener){
		this.listener=listener;
	}
}