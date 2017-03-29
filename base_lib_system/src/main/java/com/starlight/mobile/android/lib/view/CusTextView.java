package com.starlight.mobile.android.lib.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.starlight.mobile.android.lib.R;

/**自定义textview 图标样式
 * @author raleigh
 * @date 2015-02-09
 *
 */
public class CusTextView extends TextView{
	private int leftHeight = -1;
	private int leftWidth = -1;
	private int rightHeight = -1;
	private int rightWidth = -1;
	private int topHeight = -1;
	private int topWidth = -1;
	private int bottomHeight = -1;
	private int bottomWidth = -1;
	public CusTextView(Context context){
		this(context,null);
	}

	public CusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CusTextView_Attrs);  
		if (a != null) {
			int count = a.getIndexCount();
			int index = 0;
			for (int i = 0; i < count; i++) {
				index = a.getIndex(i);

				if(index==R.styleable.CusTextView_Attrs_ctv_bottomImg_height)
					bottomHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_bottomImg_width)
					bottomWidth = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_leftImg_height)
					leftHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_leftImg_width)
					leftWidth = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_rightImg_height)
					rightHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_rightImg_width)
					rightWidth = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_topImg_height)
					topHeight = a.getDimensionPixelSize(index, -1);
				if(index== R.styleable.CusTextView_Attrs_ctv_topImg_width)
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
		Drawable[] drawables = getCompoundDrawables();
		if(drawableTop!=null)
			drawableTop.setBounds(0, 0, topWidth, topHeight);
		setCompoundDrawables(drawables[0],drawableTop, drawables[2],drawables[3]);
	}
	public void setLeftDrawable(Drawable drawableLeft){
		Drawable[] drawables = getCompoundDrawables();
		if(drawableLeft!=null)
			drawableLeft.setBounds(0, 0, leftWidth, leftHeight);
		setCompoundDrawables(drawableLeft,drawables[1], drawables[2],drawables[3]);
	}
	public void setRightDrawable(Drawable drawableRight){
		Drawable[] drawables = getCompoundDrawables();
		if(drawableRight!=null)
			drawableRight.setBounds(0, 0, rightWidth, rightHeight);
		setCompoundDrawables(drawables[0],drawables[1], drawableRight,drawables[3]);
	}
	public void setBottomDrawable(Drawable drawableBottom){
		Drawable[] drawables = getCompoundDrawables();
		if(drawableBottom!=null)
			drawableBottom.setBounds(0, 0, bottomWidth, bottomHeight);
		setCompoundDrawables(drawables[0],drawables[1], drawables[2],drawableBottom);
	}

	public int getBottomHeight() {
		return bottomHeight;
	}

	public void setBottomHeight(int bottomHeight) {
		this.bottomHeight = bottomHeight;
	}

	public int getBottomWidth() {
		return bottomWidth;
	}

	public void setBottomWidth(int bottomWidth) {
		this.bottomWidth = bottomWidth;
	}

	public int getLeftHeight() {
		return leftHeight;
	}

	public void setLeftHeight(int leftHeight) {
		this.leftHeight = leftHeight;
	}

	public int getLeftWidth() {
		return leftWidth;
	}

	public void setLeftWidth(int leftWidth) {
		this.leftWidth = leftWidth;
	}

	public int getRightHeight() {
		return rightHeight;
	}

	public void setRightHeight(int rightHeight) {
		this.rightHeight = rightHeight;
	}

	public int getRightWidth() {
		return rightWidth;
	}

	public void setRightWidth(int rightWidth) {
		this.rightWidth = rightWidth;
	}

	public int getTopHeight() {
		return topHeight;
	}

	public void setTopHeight(int topHeight) {
		this.topHeight = topHeight;
	}

	public int getTopWidth() {
		return topWidth;
	}

	public void setTopWidth(int topWidth) {
		this.topWidth = topWidth;
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


}
