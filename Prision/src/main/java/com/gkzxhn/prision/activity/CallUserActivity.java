package com.gkzxhn.prision.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.entity.MeetingDetailEntity;
import com.gkzxhn.prision.presenter.CallUserPresenter;
import com.gkzxhn.prision.view.ICallUserView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class CallUserActivity extends SuperActivity implements ICallUserView{
    private CallUserPresenter mPresenter;
    private DotsTextView tvLoading;
    private ImageView ivCard01,ivCard02;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_user_layout);
        initControls();
        init();
    }
    private void initControls(){
        tvLoading= (DotsTextView) findViewById(R.id.common_loading_layout_tv_load);
        ivCard01= (ImageView) findViewById(R.id.call_user_layout_iv_card_01);
        ivCard02= (ImageView) findViewById(R.id.call_user_layout_iv_card_02);
    }
    private void init(){
        String id=getIntent().getStringExtra(Constants.EXTRA);
        mPresenter=new CallUserPresenter(this,this);
        mPresenter.request(id);//请求详情
    }
    public void openVConfVideoUI(){

    }
    public void stopVConfVideo(){

    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.call_user_layout_bt_call:
                break;
        }
    }

    @Override
    public void onSuccess(MeetingDetailEntity entity) {
        String[] img_urls = entity.getImageUrl().split("\\|");
        ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[0],ivCard01);
        ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[1],ivCard02);
        findViewById(R.id.call_user_layout_bt_call).setEnabled(true);
    }

    @Override
    public void startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI);
    }

    @Override
    public void stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI);
    }


    /**
     * 加载动画
     */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== Constants.START_REFRESH_UI){//开始加载动画
                tvLoading.setVisibility(View.VISIBLE);
                if (!tvLoading.isPlaying()) {

                    tvLoading.showAndPlay();
                }
            }else if(msg.what==Constants.STOP_REFRESH_UI){//停止加载动画
                if (tvLoading.isPlaying() || tvLoading.isShown()) {
                    tvLoading.hideAndStop();
                    tvLoading.setVisibility(View.GONE);
                }
            }
        }
    };
}
