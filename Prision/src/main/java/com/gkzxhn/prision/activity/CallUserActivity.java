package com.gkzxhn.prision.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.customview.CustomDialog;
import com.gkzxhn.prision.customview.ShowTerminalDialog;
import com.gkzxhn.prision.entity.MeetingDetailEntity;
import com.gkzxhn.prision.keda.sky.app.GKStateMannager;
import com.gkzxhn.prision.keda.utils.NetWorkUtils;
import com.gkzxhn.prision.keda.vconf.manager.VConferenceManager;
import com.gkzxhn.prision.presenter.CallUserPresenter;
import com.gkzxhn.prision.view.ICallUserView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class CallUserActivity extends SuperActivity implements ICallUserView{
    private CallUserPresenter mPresenter;
    private DotsTextView tvLoading;
    private ImageView ivCard01,ivCard02;
    private CustomDialog mCustomDialog;
    private final long DOWN_TIME=20000;//倒计时 20秒
    private ShowTerminalDialog mShowTerminalDialog;
    private ProgressDialog mProgress;
    private SharedPreferences preferences;
    private String phone=null;
    private String nickName=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_user_layout);
        initControls();
        init();
        registerReceiver();
    }
    private void initControls(){
        tvLoading= (DotsTextView) findViewById(R.id.common_loading_layout_tv_load);
        ivCard01= (ImageView) findViewById(R.id.call_user_layout_iv_card_01);
        ivCard02= (ImageView) findViewById(R.id.call_user_layout_iv_card_02);
    }
    private void init(){
        String id=getIntent().getStringExtra(Constants.EXTRA);
        phone=getIntent().getStringExtra(Constants.EXTRAS);
        nickName=getIntent().getStringExtra(Constants.EXTRA_TAB);
        mProgress = ProgressDialog.show(this, null, getString(R.string.check_other_status));
        mProgress.setCanceledOnTouchOutside(true);
        stopProgress();
        mCustomDialog=new CustomDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.custom_dialog_layout_tv_confirm){
                    online();
                }
            }
        });
        mPresenter=new CallUserPresenter(this,this);
        preferences=mPresenter.getSharedPreferences();
        mPresenter.request(id);//请求详情
    }
    public void openVConfVideoUI(){
        if(mTimer!=null)mTimer.cancel();
        stopProgress();
        if (phone==null || phone.equals(GKStateMannager.mE164))  return;
        if (!VConferenceManager.isAvailableVCconf(true, true, true))   return;
        VConferenceManager.openVConfVideoUI(CallUserActivity.this, true, String.format("%s(%s)",nickName,phone), phone);
    }
    public void stopVConfVideo(){
        stopProgress();
        mCustomDialog.setTitle(getString(R.string.other_offline));
        mCustomDialog.show();
    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.call_user_layout_bt_call:
                online();
                break;
        }
    }
    private void online(){
        String account=preferences.getString(Constants.TERMINAL_ACCOUNT,"");
        if(account!=null&&account.length()>0) {
            if (NetWorkUtils.isAvailable(this)) {
                startProgress();
                //启动倒计时
                mTimer.start();
                //发送云信消息，检测家属端是否已经准备好可以呼叫
                CustomNotification notification = new CustomNotification();
                notification.setSessionId(mPresenter.getEntity().getAccid());
                notification.setSessionType( SessionTypeEnum.P2P);
                // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
                // 这里以类型 “1” 作为“正在输入”的状态通知。
                JSONObject json = new JSONObject();
                try {
                    json.put("code", -1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notification.setContent(json.toString());
                NIMClient.getService(MsgService.class).sendCustomNotification(notification);
            } else {
                showToast(R.string.network_error);
            }
        }else{
            if(mShowTerminalDialog==null){
                mShowTerminalDialog=new ShowTerminalDialog(this);
            }
            if(!mShowTerminalDialog.isShowing())mShowTerminalDialog.show();
        }
    }
    @Override
    public void onSuccess() {
        String[] img_urls = mPresenter.getEntity().getImageUrl().split("\\|");
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
    private CountDownTimer mTimer=new CountDownTimer(DOWN_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            long second = millisUntilFinished / 1000;
        }
        @Override
        public void onFinish() {
            stopVConfVideo();
        }
    };


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
    public void startProgress() {
        if(mProgress!=null&&!mProgress.isShowing())mProgress.show();
    }

    public void stopProgress() {
        if(mProgress!=null&&mProgress.isShowing())mProgress.dismiss();
    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopRefreshAnim();
            if(intent.getAction().equals(Constants.TERMINAL_FAILED_ACTION)){//GK注册失败
                showToast(R.string.GK_register_failed);
            }else if(intent.getAction().equals(Constants.TERMINAL_SUCCESS_ACTION)){// GK 注册成功
               openVConfVideoUI();
            }
        }
    };



    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.TERMINAL_FAILED_ACTION);
        intentFilter.addAction(Constants.TERMINAL_SUCCESS_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        super.onDestroy();
    }
}
