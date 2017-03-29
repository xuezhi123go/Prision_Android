package com.starlight.mobile.android.lib.album;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.starlight.mobile.android.lib.R;
import com.starlight.mobile.android.lib.view.RadioButtonPlus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Raleigh on 15/7/10.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>
{
	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	private  List<String> mSelectedImage = new LinkedList<String>();

	private LayoutInflater mInflater;
	private Context mContext;
	private List<File> mDatas=new ArrayList<File>();
	private final int max_optional_count;
	private final boolean isSigleMode;
	private AlbumImageLoader mAlbumImageLoader;


	public AlbumAdapter(Context context,TextView tvPreView,int max_optional_count,boolean isSigleMode)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.max_optional_count=max_optional_count;
		this.isSigleMode=isSigleMode;
		mAlbumImageLoader=new AlbumImageLoader(3,AlbumImageLoader.Type.LIFO);
	}
	public void updateAll(List<File> mDatas){
		this.mDatas.clear();
		if(mDatas!=null){
			this.mDatas.addAll(mDatas);
		}
		notifyDataSetChanged();

	}
	public List<String>  getSelectedImage(){
		return mSelectedImage;
	}
	public List<File>  getItems(){
		return mDatas;
	}
	public File getItem(int position)
	{
		return mDatas.get(position);
	}
	public void setSelectedImage(List<String> mSelectedImage){
		this.mSelectedImage.clear();
		if(mSelectedImage!=null)this.mSelectedImage.addAll(mSelectedImage);
		notifyDataSetChanged();
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view= LayoutInflater.from(mContext).inflate(R.layout.album_grid_item_layout, viewGroup,
				false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
//		String imageName=mDatas.get(position).;
//		final String imagePath=mDirPath + "/" + imageName;
		viewHolder.ivImage.setImageResource(R.drawable.ic_picture_load);
//设置no_pic
		//设置图片
		final String imagePath=mDatas.get(position).getAbsolutePath();
		mAlbumImageLoader.loadImage(imagePath, viewHolder.ivImage);
		viewHolder.ivImage.setColorFilter(null);
		viewHolder.ivImage.setOnTouchListener(ShadowTouchListener);

		if(isSigleMode) {
			viewHolder.rbSelect.setVisibility(View.GONE);
		}else{
			viewHolder.rbSelect.setVisibility(View.VISIBLE);
			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedImage.contains(imagePath)) {
				viewHolder.rbSelect.setChecked(true);
				viewHolder.ivImage.setColorFilter(Color.parseColor("#77000000"));
			} else {
				//设置no_selected
				viewHolder.rbSelect.setChecked(false);
			}
			viewHolder.rbSelect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// 已经选择过该图片
					if (mSelectedImage.contains(imagePath)) {
						mSelectedImage.remove(imagePath);
						viewHolder.rbSelect.setChecked(false);
						viewHolder.ivImage.setColorFilter(null);
						if (listener != null) listener.checkedChange(mSelectedImage.size());
					} else {// 未选择该图片
						if(mSelectedImage.size()<max_optional_count) {
							mSelectedImage.add(imagePath);
							viewHolder.rbSelect.setChecked(true);
							viewHolder.ivImage.setColorFilter(Color.parseColor("#77000000"));
							if (listener != null) listener.checkedChange(mSelectedImage.size());
						}else{
							viewHolder.rbSelect.setChecked(false);
							String hint=String.format("%s %s %s",mContext.getString(R.string.album_up_to),max_optional_count ,max_optional_count>1?mContext.getString(R.string.album_pictures):mContext.getString(R.string.album_picture));
							Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();

						}
					}
				}
			});
		}

		viewHolder.ivImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener!=null)listener.itemClick(position);
			}
		});

	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		private View convertView;
		private ImageView ivImage;
		private RadioButtonPlus rbSelect;

		public ViewHolder(View itemView) {
			super(itemView);
			this.convertView=itemView;
			ivImage=(ImageView)convertView.findViewById(R.id.album_grid_item_layout_iv_image);
			rbSelect=(RadioButtonPlus)convertView.findViewById(R.id.album_grid_item_layout_rb_select);
		}
	}
	public interface AlbumClickListener{
		public void itemClick(int position);
		public void checkedChange(int selectedCount);

	}
	private AlbumClickListener listener;
	public void setAlbumClickListener(AlbumClickListener listener){
		this.listener=listener;
	}
	private View.OnTouchListener ShadowTouchListener=new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//点击阴影效果的实现 黑色背景色+透明度
			ImageView imgView=(ImageView )v;
			if(event.getAction()==MotionEvent.ACTION_DOWN) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) imgView.setImageAlpha(0x88);
				else imgView.setAlpha(0x88);

				imgView.invalidate();
			} else if(event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) imgView.setImageAlpha(0xFF);
				else imgView.setAlpha(0xFF);

				imgView.invalidate();
			}
			return false;
		}
	};
	public void onDestory(){
		mAlbumImageLoader.onDestory();
	}
}
