package com.starlight.mobile.android.lib.album;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.starlight.mobile.android.lib.R;

import java.util.List;

/**
 * Created by Raleigh on 15/7/10.
 */
public class ImageDirPopupWindow extends PopupWindow
{
	private RecyclerView mRecyclerView;
	/**
	 * 布局文件的最外层View
	 */
	protected View mContentView;
	protected Context context;
	private FolderAdapter adapter;
	private final int ALL_PHOTO_COUNT;
	/**
	 * ListView的数据集
	 */

	public ImageDirPopupWindow(int width, int height,
							   List<ImageFloder> datas, int allPhotoCount, View convertView)
	{
		super(convertView, width, height, true);
		this.ALL_PHOTO_COUNT=allPhotoCount;
		this.mContentView = convertView;
		context = convertView.getContext();

		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
		setOutsideTouchable(true);
		setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});
		mRecyclerView=(RecyclerView)findViewById(R.id.album_dir_layout_rv_list);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		adapter=new FolderAdapter(context,datas,allPhotoCount);
		adapter.setOnItemClickListener(albumItemClickListener);
		mRecyclerView.setAdapter(adapter);
	}
	public int getAllPhotoCount(){
		return ALL_PHOTO_COUNT;
	}


	public View findViewById(int id)
	{
		return mContentView.findViewById(id);
	}

	public interface OnImageDirSelected
	{
		void selected(ImageFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}


	private FolderAdapter.AlbumItemClickListener albumItemClickListener=new FolderAdapter.AlbumItemClickListener() {
		@Override
		public void onClick(View v, ImageFloder floder) {
			if(mImageDirSelected!=null){
				mImageDirSelected.selected(floder);
			}
		}
	};
	public void onDestory(){
		adapter.onDestory();
	}


}
