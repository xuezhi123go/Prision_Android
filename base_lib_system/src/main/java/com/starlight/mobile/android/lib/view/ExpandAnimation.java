package com.starlight.mobile.android.lib.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author raleigh
 *
 */
public class ExpandAnimation extends Animation {
	private TextView mAnimatedView;
	private LayoutParams mViewLayoutParams;
	private int mMarginStart, mMarginEnd;
	private boolean mIsVisibleAfter = false;
	private boolean mWasEndedAlready = false;
	/**
	 * Initialize the animation
	 *
	 * @param view
	 *            The layout we want to animate
	 * @param duration
	 *            The duration of the animation, in ms
	 */
	public ExpandAnimation(TextView view,int duration,OnExpandClickListener onClickListener) {

		view.setSingleLine(false);
		setDuration(duration);
		mAnimatedView = view;
		this.onClickListener=onClickListener;
		mViewLayoutParams=(LayoutParams) view.getLayoutParams();
		// if the bottom margin is 0,
		// then after the animation will end it'll be negative, and invisible.
		mIsVisibleAfter = (mViewLayoutParams.bottomMargin == 0);
		mMarginStart = mViewLayoutParams.bottomMargin;
		mMarginEnd = (mMarginStart == 0 ? (0 - view.getHeight()) : 0);

		if(this.onClickListener!=null) 	{	
			this.onClickListener.up();
		}
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		if (interpolatedTime < 1.0f) {

			// Calculating the new bottom margin, and setting it
			mViewLayoutParams.bottomMargin = mMarginStart
					+ (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

			// Invalidating the layout, making us seeing the changes we made
			mAnimatedView.requestLayout();

			// Making sure we didn't run the ending before (it happens!)
		} else if (!mWasEndedAlready) {

			if (mIsVisibleAfter) {	
				mAnimatedView.setSingleLine(true);
				mViewLayoutParams.bottomMargin = 1;//这个值主要是为了mIsVisibleAfter = (mViewLayoutParams.bottomMargin == 0);为false
				mAnimatedView.requestLayout();
				if(onClickListener!=null){
					onClickListener.down();
				}
			}else{
				mViewLayoutParams.bottomMargin = 0;
				mAnimatedView.requestLayout();
			}

			mWasEndedAlready = true;
		}
	}
	private OnExpandClickListener onClickListener;

	public interface OnExpandClickListener{
		public void down();
		public void up();
	}
}
