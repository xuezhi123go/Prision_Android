package com.starlight.mobile.android.lib.album;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.starlight.mobile.android.lib.R;
import com.starlight.mobile.android.lib.view.photoview.PhotoView;
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raleigh on 15/7/23.
 */
public class AlbumPreviewAdapter extends PagerAdapter {
    private Context context;

    private AlbumImageLoader mAlbumImageLoader;
    private List<File> list=new ArrayList<File>();
    private PhotoViewAttacher.OnShortTouchListener onShortTouchListener;
    public AlbumPreviewAdapter(Context context,List<File> list,PhotoViewAttacher.OnShortTouchListener onShortTouchListener){
        this.context = context;
        this.list.addAll(list);//不要用赋值，否则就是同一个变量了
        this.onShortTouchListener=onShortTouchListener;
        mAlbumImageLoader=new AlbumImageLoader(3,AlbumImageLoader.Type.LIFO);
    }

    @Override
    public int getCount() {
        return list.size();
    }
    public List<File> getList() {
        return list;
    }
    public File getItem(int position){
        return list.get(position);
    }

    public boolean remove(int position){
        boolean result=false;
        if(list!=null&&position<list.size()){
            getItemPosition(list.get(position));
            if(list!=null&&list.size()>position){
                list.remove(position);
                result=true;
            }
            //直接使用notifyDataSetChanged是无法更新，需要同时重写getItemPosition返回常量 POSITION_NONE (此常量为viewpager带的)。
            notifyDataSetChanged();
        }
        return result;
    }
    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view=View.inflate(context, R.layout.album_preview_item_layout, null);
        try{
            PhotoView photoView =(PhotoView) view.findViewById(R.id.preview_item_layout_pv_image);
            ProgressBar progress=(ProgressBar) view.findViewById(R.id.preview_item_layout_progress);
            File imageItem=list.get(position);
            mAlbumImageLoader.loadImage(imageItem.getAbsolutePath(), photoView);
            photoView.setOnShortTouchListener(onShortTouchListener);
            ((ViewPager) container).addView(view);
        }catch(Exception e){
            e.printStackTrace();
        }
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    public void onDestory(){
        mAlbumImageLoader.onDestory();
    }
}
