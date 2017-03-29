package com.starlight.mobile.android.lib.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.starlight.mobile.android.lib.album.AlbumImageLoader;

/**
 * @author raleigh
 *
 */
public class CutPhotoView extends RelativeLayout
{

	private CutPhotoZoom mZoomImageView;
	private CutPhotoBorderView mClipImageView;
	private Context mContext;
	private AlbumImageLoader mAlbumImageLoader;
	/**
	 * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
	 */
	private int mHorizontalPadding = 20;

	public CutPhotoView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
        this.mContext=context;
		mAlbumImageLoader=new AlbumImageLoader(1, AlbumImageLoader.Type.LIFO);
		mZoomImageView = new CutPhotoZoom(context);
		mClipImageView = new CutPhotoBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		//		/**
		//		 * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
		//		 */
		//		mZoomImageView.setImageDrawable(getResources().getDrawable(
		//				R.drawable.a));

		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		int mScreenHeight= dm.heightPixels;// 获取屏幕分辨率宽度
		if(mScreenWidth>mScreenHeight){
			mHorizontalPadding=(mScreenWidth-(mScreenHeight-10*mHorizontalPadding))/2;
		}
		// 计算padding的px
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
				.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}
	public void setImageBitmap(Bitmap bitmap){
		mZoomImageView.setImageBitmap(bitmap);
	}
	public void setImagePath(String path){
		if(mZoomImageView!=null)mAlbumImageLoader.loadImage(path, mZoomImageView);
	}
	public void setImageUri(Uri imageUri){
		mZoomImageView.setImageURI(imageUri);
	}

	/**
	 * 对外公布设置边距的方法,单位为dp
	 * 
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip()
	{
		return mZoomImageView.clip();
	}
	public void onDestory(){
		mAlbumImageLoader.onDestory();
	}

}
