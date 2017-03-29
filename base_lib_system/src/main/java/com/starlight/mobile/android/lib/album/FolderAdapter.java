package com.starlight.mobile.android.lib.album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starlight.mobile.android.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raleigh on 15/7/10.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>{
    private Context mContext;
    private List<ImageFloder> mDatas=new ArrayList<ImageFloder>();
    private AlbumItemClickListener onClickListener;
    private int allPhotosCount=0;
    private int currentFloderPosition=0;
    private AlbumImageLoader mAlbumImageLoader;

    public FolderAdapter(Context context, List<ImageFloder> mDatas, int allPhotosCount){
        this.mContext=context;
        this.allPhotosCount=allPhotosCount;
        mAlbumImageLoader=new AlbumImageLoader(3, AlbumImageLoader.Type.LIFO);
        if(mDatas!=null)
            this.mDatas.addAll(mDatas);
    }
    public void setOnItemClickListener(AlbumItemClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.album_dir_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        try {
            int photoCount = 0;
            String floderName = mContext.getString(R.string.album_all_photos);
            if (position == 0) {
                photoCount = allPhotosCount;
                viewHolder.tvName.setText(R.string.album_all_photos);
                ImageFloder floder = mDatas.get(position);
                mAlbumImageLoader.loadImage(floder.getFirstImagePath(), viewHolder.ivImage);
                viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentFloderPosition = position;
                        notifyDataSetChanged();
                        if (onClickListener != null) onClickListener.onClick(v, null);
                    }
                });
            } else {
                final ImageFloder mFloder = mDatas.get(position - 1);
                floderName = mFloder.getName();
                viewHolder.tvName.setText(mFloder.getName().substring(1));
                mAlbumImageLoader.loadImage(mFloder.getFirstImagePath(), viewHolder.ivImage);
                photoCount = mFloder.getCount();

                viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentFloderPosition = position;
                        notifyDataSetChanged();
                        if (onClickListener != null) onClickListener.onClick(v, mFloder);
                    }
                });
            }
            if (currentFloderPosition == position) viewHolder.vSelect.setVisibility(View.VISIBLE);
            else viewHolder.vSelect.setVisibility(View.GONE);
            viewHolder.tvCount.setText(String.format("%s %s", photoCount, photoCount > 1 ? mContext.getString(R.string.album_pictures) : mContext.getString(R.string.album_picture)));
        }catch (Exception e){}

    }

    @Override
    public int getItemCount() {
        //多一个All Photos
        return mDatas.size()+1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage;
        private View convertView,vSelect;
        private TextView tvName,tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            this.convertView=itemView;
            ivImage=(ImageView)convertView.findViewById(R.id.album_dir_item_layout_iv_image);
            vSelect=convertView.findViewById(R.id.album_dir_item_layout_iv_select);
            tvName=(TextView)convertView.findViewById(R.id.album_dir_item_layout_tv_name);
            tvCount=(TextView)convertView.findViewById(R.id.album_dir_item_layout_tv_count);

        }
    }
    interface AlbumItemClickListener{
        public void onClick(View v, ImageFloder floder);
    }
    public void onDestory(){
        mAlbumImageLoader.onDestory();
    }
}
