package com.starlight.mobile.android.lib.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.starlight.mobile.android.lib.R;
import com.starlight.mobile.android.lib.view.CusHeadView;
import com.starlight.mobile.android.lib.view.RadioButtonPlus;
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**图片预览
 * Created by Raleigh on 15/7/23.
 * return
 *    List<String> SelectedImages 已选择的图片路径集合
 */
public class AlbumPreviewActivity  extends Activity {
    private ViewPager viewPager;
    private int currentPosition=0;
    private AlbumPreviewAdapter adapter;
    private RadioButtonPlus rbSelect;
    private List<File> listData=new ArrayList<File>();
    private int listSize,//listData的大小，图片的总数量
            max_optional_count;//本次可选的总数
    private boolean isScrolling=false;//viewpager 是否正在滑动
    private LinearLayout llBottom;
    private CusHeadView chHead;
    private  List<String> mSelectedImage = new LinkedList<String>();
    private final String EXTRA_IMAGE_LIST="extra_image_list";
    private final String EXTRA_IMAGE_SELECT_COUNT="extra_image_select_count";
    private final String EXTRA_CURRENT_POSITION="extra_current_position";
    private final String EXTRA_HAS_SELECTED_IMAGE="extra_has_selected_image";//已经选择的图片路径列表
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_preview_layout);
        initControls();
        init();
    }
    private void initControls(){
        viewPager=(ViewPager) findViewById(R.id.preview_layout_viewpager);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        rbSelect=(RadioButtonPlus) findViewById(R.id.preview_layout_rb_select);
        llBottom=(LinearLayout) findViewById(R.id.preview_layout_ll_bottom);
        chHead=(CusHeadView)findViewById(R.id.preview_layout_ch_head);
    }
    private void init(){
        try{
            Intent data=getIntent();
            Bundle bundle=data.getExtras();
            if(data!=null&&bundle!=null){
                listData= (List<File>) bundle.getSerializable(EXTRA_IMAGE_LIST);
                if(bundle.containsKey(EXTRA_CURRENT_POSITION))
                    currentPosition=bundle.getInt(EXTRA_CURRENT_POSITION);
                listSize=listData.size();
                if(bundle.containsKey(EXTRA_IMAGE_SELECT_COUNT))//本次可选的总数
                    max_optional_count=bundle.getInt(EXTRA_IMAGE_SELECT_COUNT);
                if(bundle.containsKey(EXTRA_HAS_SELECTED_IMAGE)){
                    mSelectedImage = (List<String>) bundle.getSerializable(EXTRA_HAS_SELECTED_IMAGE);
                }
                adapter=new AlbumPreviewAdapter(this, listData,onShortTouchListener);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(currentPosition);

                chHead.getTvTitle().setText(String.format("%d/%d", currentPosition+1,listSize));
            }
            if(mSelectedImage.contains(listData.get(currentPosition).getAbsolutePath()))rbSelect.setChecked(true);//当前图片被选中
            if(mSelectedImage.size() ==0)
                chHead.getRightTv().setText(R.string.album_finish);
            else
                chHead.getRightTv().setText(String.format("%s(%d/%d)", getString(R.string.album_finish), mSelectedImage.size(), max_optional_count));
            rbSelect.setOnClickListener(onCheckedChangeListener);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void showOrHidePanel(boolean isShown){
        if(isShown){
            Animation headHideAnim= AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_top);
            Animation bottomHideAnim=AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_bottom);
            //设置动画时间
            headHideAnim.setDuration(400);
            bottomHideAnim.setDuration(400);
            chHead.startAnimation(headHideAnim);
            chHead.setVisibility(View.GONE);
            llBottom.startAnimation(bottomHideAnim);
            llBottom.setVisibility(View.GONE);
        }else{
            Animation headShowAnim=AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_from_top);
            Animation bottomShowAnim=AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_from_bottom);
            //设置动画时间
            headShowAnim.setDuration(400);
            bottomShowAnim.setDuration(400);
            chHead.startAnimation(headShowAnim);
            chHead.setVisibility(View.VISIBLE);
            llBottom.startAnimation(bottomShowAnim);
            llBottom.setVisibility(View.VISIBLE);
        }


    }
    public void onClickListener(View v){
        if(v.getId()==R.id.common_head_layout_iv_left){
            Intent data=new Intent();
            data.putExtra(AlbumActivity.EXTRAS, (Serializable) mSelectedImage);
            setResult(RESULT_CANCELED, data);
            this.finish();
        }else if(v.getId()==R.id.common_head_layout_tv_right){
            Intent data=new Intent();
            data.putExtra(AlbumActivity.EXTRAS, (Serializable) mSelectedImage);
            setResult(RESULT_OK, data);
            this.finish();
        }


    }
    private PhotoViewAttacher.OnShortTouchListener onShortTouchListener=new PhotoViewAttacher.OnShortTouchListener() {
        @SuppressLint("NewApi")
        @Override
        public void back(float upX,float upY) {
            if(chHead.getMeasuredHeight()<upY&&llBottom.getY()>upY)
                showOrHidePanel(chHead.isShown());
        }

        @Override
        public void doubleTab() {//双击
            showOrHidePanel(true);
        }
    };
    private View.OnClickListener onCheckedChangeListener=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            boolean isChecked=!mSelectedImage.contains(listData.get(currentPosition).getAbsolutePath());
            if(isChecked){
                if(!isScrolling){//没有在滑动页面
                    if(mSelectedImage.size() <max_optional_count){
                        mSelectedImage.add(listData.get(currentPosition).getAbsolutePath());
                    }else{
                        rbSelect.setChecked(false);
                        String hint=String.format("%s %s %s",getString(R.string.album_up_to),max_optional_count ,max_optional_count>1?getString(R.string.album_pictures):getString(R.string.album_picture));
                        Toast.makeText(AlbumPreviewActivity.this, hint,Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                if(!isScrolling){//没有在滑动页面
                    rbSelect.setChecked(false);
                    mSelectedImage.remove(listData.get(currentPosition).getAbsolutePath());
                }
            }
            if(mSelectedImage.size() ==0)
                chHead.getRightTv().setText(R.string.album_finish);
            else
                chHead.getRightTv().setText(String.format("%s(%d/%d)", getString(R.string.album_finish), mSelectedImage.size(), max_optional_count));

        }
    };


    private ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            isScrolling=true;
            if(listData!=null){
                currentPosition=position;
                chHead.getTvTitle().setText(String.format("%d/%d", currentPosition+1,listSize));
                rbSelect.setChecked(mSelectedImage.contains(listData.get(position).getAbsolutePath()));
                isScrolling=false;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            data.putExtra(AlbumActivity.EXTRAS, (Serializable) mSelectedImage);
            setResult(RESULT_CANCELED, data);
            this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        adapter.onDestory();
        super.onDestroy();
    }
}
