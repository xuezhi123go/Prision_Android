package com.starlight.mobile.android.lib.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starlight.mobile.android.lib.R;


/**
 * @author raleighluo
 *自定义属性
 *   <attr name="chHead_title" format="reference" />
 *    <attr name="chHead_title_padding_left" format="dimension" />
<attr name="chHead_leftText" format="reference" />
<!-- 左文字的左图标 -->
<attr name="chHead_leftTextImg" format="reference" />
<attr name="chHead_rightText" format="reference" />
<!-- 右文字的右图标 -->
<attr name="chHead_rightTextImg" format="reference" />
<attr name="chHead_leftImg" format="reference" />
<attr name="chHead_rightImg" format="reference" />
<!--左右图标或文字的背景点击效果-->
<attr name="chHead_imgClickEffect" format="reference" />
<attr name="chHead_textClickEffect" format="reference" />
<!--标题颜色-->
<attr name="chHead_titleColor" format="reference" />
<!--左右两边文字颜色-->
<attr name="chHead_textColor" format="reference" />
All view's onClick event android:onClick="onClickListener" ，否则需要重新设置监听器，方法setBtnClickListener
 */
public class CusHeadView extends RelativeLayout {
	private Context context;
	private ImageView ivLeft, ivRight;
	private TextView tvLeft, tvRight;
	private TextView tvTitle;
	private int textDrawableSize;

	public CusHeadView(Context context) {
		super(context,null);
	}

	public CusHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			this.context = context;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.common_head_layout, this);
			textDrawableSize = getResources().getDimensionPixelSize(R.dimen.chHead_text_drawable_size);
			tvTitle = (TextView) findViewById(R.id.common_head_layout_tv_title);

			tvTitle = (TextView) findViewById(R.id.common_head_layout_tv_title);

			ivLeft = (ImageView) findViewById(R.id.common_head_layout_iv_left);
			ivRight = (ImageView) findViewById(R.id.common_head_layout_iv_right);

			tvLeft = (TextView) findViewById(R.id.common_head_layout_tv_left);
			tvRight = (TextView) findViewById(R.id.common_head_layout_tv_right);

			//获取自定义属性
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CusHeadView_Attrs);


			int imgClickEffect=a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_imgClickEffect, R.drawable.common_head_text_btn_selector);
			if(imgClickEffect!=R.drawable.common_head_text_btn_selector){//5.0以前取消左右两边的点击效果
				ivLeft.setBackgroundResource(imgClickEffect);
				ivRight.setBackgroundResource(imgClickEffect);
			}
			int textClickEffect=a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_textClickEffect, R.drawable.common_head_text_btn_selector);
			if(textClickEffect!=R.drawable.common_head_text_btn_selector){//5.0以前取消左右两边的点击效果
				tvLeft.setBackgroundResource(imgClickEffect);
				tvRight.setBackgroundResource(imgClickEffect);
			}
			//文字颜色
			int titleColor=a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_titleColor,android.R.color.white);
			int textColor=a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_textColor, android.R.color.white);

			int titleSize=a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_title_size, 0);
			if(titleSize>=0) tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
			int titleLeftPadding=a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_title_padding_left, 0);
			if(titleLeftPadding>=0)tvTitle.setPadding(titleLeftPadding,0,0,0);
			//获取title属性值,默认为：空字符串
			tvTitle.setText(a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_title, R.string.empty));
			tvTitle.setTextColor(getResources().getColor(titleColor));
			//获取图标属性值,默认为：无色
			int leftImgRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_leftImg, android.R.color.transparent);
			if (leftImgRsc != android.R.color.transparent) {
				int leftPadding = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_leftImgPadding, -1);
				ivLeft.setImageResource(leftImgRsc);
				if(leftPadding>=0)ivLeft.setPadding(leftPadding,leftPadding,leftPadding,leftPadding);
				ivLeft.setVisibility(View.VISIBLE);
			}

			int rightImgRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_rightImg, android.R.color.transparent);
			if (rightImgRsc != android.R.color.transparent) {
				int rightPadding = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_rightImgPadding, -1);
				if(rightPadding>=0)ivRight.setPadding(rightPadding,rightPadding,rightPadding,rightPadding);
				ivRight.setImageResource(rightImgRsc);
				ivRight.setVisibility(View.VISIBLE);
			}

			int textSize=a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_text_size, 0);
			if(textSize>=0){
				tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
				tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			}

			int leftTextRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_leftText, R.string.empty);
			if (leftTextRsc != R.string.empty) {
				tvLeft.setText(leftTextRsc);
				ColorStateList csl=getResources().getColorStateList(textColor);
				if(csl!=null)
					tvLeft.setTextColor(csl);
				else
					tvLeft.setTextColor(getResources().getColor(textColor));
				tvLeft.setVisibility(View.VISIBLE);

				int leftTextImgRcs = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_leftTextImg, android.R.color.transparent);
				if (leftTextImgRcs != android.R.color.transparent) {
					//左文字的左图标
					Drawable drawable = getResources().getDrawable(leftTextImgRcs);
					/// 这一步必须要做,否则不会显示.图片的大小
					drawable.setBounds(0, 0, textDrawableSize, textDrawableSize);
					tvLeft.setCompoundDrawables(drawable, null, null, null);
				}

			}

			int rightTextRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_rightText, R.string.empty);
			if (rightTextRsc != R.string.empty) {
				tvRight.setText(rightTextRsc);
				ColorStateList csl=getResources().getColorStateList(textColor);
				if(csl!=null)
					tvRight.setTextColor(csl);
				else
					tvRight.setTextColor(getResources().getColor(textColor));
				tvRight.setVisibility(View.VISIBLE);
				//右文字的右图标
				int rightTextImgRcs = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_rightTextImg, android.R.color.transparent);
				if (rightTextImgRcs != android.R.color.transparent) {
					Drawable drawable = getResources().getDrawable(rightTextImgRcs);
					/// 这一步必须要做,否则不会显示.图片的大小
					drawable.setBounds(0, 0, textDrawableSize, textDrawableSize);
					tvRight.setCompoundDrawables(null, null, drawable, null);
				}
			}
			a.recycle();
		}catch (Exception e){
		}
	}

	public ImageView getLeftBtn() {
		return ivLeft;
	}

	public ImageView getRightBtn() {
		return ivRight;
	}

	public TextView getTvTitle() {
		return tvTitle;
	}

	public TextView getLeftTv() {
		return tvLeft;
	}

	public TextView getRightTv() {
		return tvRight;
	}

	public void setRightBtnImage(int resId) {
		if(resId>0){
			this.ivRight.setVisibility(VISIBLE);
			Drawable drawable= getResources().getDrawable(resId);
			//drawable.setBounds(0, 0, textDrawableSize, textDrawableSize);
			this.ivRight.setImageDrawable(drawable);
		} else {
			this.ivRight.setVisibility(GONE);
		}
	}

	public void setRightBtnImageVisible(boolean visibility) {
		if (visibility) {
			this.ivRight.setVisibility(VISIBLE);
		} else {
			this.ivRight.setVisibility(GONE);
		}
	}


	/**设置左文字的左图标
	 * @param resId
	 */
	public void setLeftTextLeftDrawable(int resId){
		if(resId>0){
			Drawable drawable= getResources().getDrawable(resId);
			/// 这一步必须要做,否则不会显示.图片的大小
			drawable.setBounds(0, 0, textDrawableSize, textDrawableSize);
			tvLeft.setCompoundDrawables(drawable,null,null,null);
		}else{
			tvLeft.setCompoundDrawables(null,null,null,null);
		}
	}
	/**设置右文字的右图标
	 * @param resId
	 */
	public void setRightTextRightDrawable(int resId){
		if(resId>0){
			Drawable drawable= getResources().getDrawable(resId);
			/// 这一步必须要做,否则不会显示.图片的大小
			drawable.setBounds(0, 0, textDrawableSize, textDrawableSize);
			tvRight.setCompoundDrawables(null,null,drawable,null);
		}else{
			tvRight.setCompoundDrawables(null,null,null,null);
		}
	}

	public void setBtnClickListener(OnClickListener onClickListener) {
		if (onClickListener != null) {
			ivLeft.setOnClickListener(onClickListener);
			ivRight.setOnClickListener(onClickListener);
			tvLeft.setOnClickListener(onClickListener);
			tvRight.setOnClickListener(onClickListener);
		}
	}
	//	public void showNetWorkPanel(boolean isConnected){
	//		try{
	//			RelativeLayout netWorkPanel=(RelativeLayout) findViewById(R.id.common_head_layout_rl_not_avaliable_network);
	//			netWorkPanel.setVisibility(isConnected?View.GONE:View.VISIBLE);
	//		}catch(Exception e){
	//			e.printStackTrace();
	//		}
	//	}

	/**
	 * 切换按钮
	 *
	 * @param btnLeftVisib
	 *            值如View.gone
	 * @param btnRightVisib
	 * @param tvLeftVisib
	 * @param tvRightVisib
	 */
	private void switchBtn(int btnLeftVisib, int btnRightVisib,
						   int tvLeftVisib, int tvRightVisib) {
		ivLeft.setVisibility(btnLeftVisib);
		ivRight.setVisibility(btnRightVisib);
		tvLeft.setVisibility(tvLeftVisib);
		tvRight.setVisibility(tvRightVisib);
	}



}
