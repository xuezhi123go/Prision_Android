package com.gkzxhn.prison.keda.vconf;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.keda.utils.StringUtils;
import com.gkzxhn.prison.service.ScreenRecordService;
import com.gkzxhn.prison.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kedacom.kdv.mt.bean.TMtAddr;
import com.kedacom.kdv.mt.constant.EmNativeConfType;

import java.util.List;


/**
  * 视频会议
  * Created by 方 on 2017/6/21.
  */

public class VConfVideoUI extends ActionBarActivity {

	protected VConf mVConf;
	protected String mE164;
	protected String mConfTitle;
	protected boolean mIsP2PConf;
	protected boolean mIsJoinConf;

	// 当前的音视频面
	protected Fragment mCurrFragmentView;

	// 当前的ContentFrame
	protected Fragment mVConfContentFrame;
	private List<TMtAddr> mTMtList;// 邀请的视频会议终端

	private int mVConfQuality;// 会议质量 2M.1M.256,192
	private int mDuration;// 会议时长


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("VConfVideo", "VConfVideoUI-->onCreate");
		super.onCreate(savedInstanceState);
		//开启录屏
//		getScreenBaseInfo();
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			startScreenRecording();
//		}
		// 让音量键固定为媒体音量控制,其他的页面不要这样设置--只在音视频的界面加入这段代码
		this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		setContentView(R.layout.vconf_video_ui_layout);
		initExtras();
		onViewCreated();
		registerReceiver();
//		startRecord();// 先开启录屏权限请求  授权了才开启视频
	}

	protected void onViewCreated() {
		mVConf = new VConf();
		mVConf.setAchConfE164(mE164);
		mVConf.setAchConfName(mConfTitle);

		if (!StringUtils.isNull(VConferenceManager.mCallPeerE164Num)) {
			mVConf.setAchConfE164(VConferenceManager.mCallPeerE164Num);
		}
		switchVConfFragment();
	}

	public void initExtras() {
		Bundle extra = getIntent().getExtras();
		if (null == extra) return;
		mConfTitle = extra.getString(Constants.TERMINAL_VCONFNAME);
		mE164 = extra.getString(Constants.TERMINAL_E164NUM);
		mIsP2PConf = extra.getBoolean(Constants.TERMINAL_MACKCALL, false);
		mIsJoinConf = extra.getBoolean(Constants.TERMINAL_JOINCONF, false);

		if (null != VConferenceManager.mConfInfo) {
			mConfTitle = VConferenceManager.mConfInfo.achConfName;
		}
		if (null != VConferenceManager.currTMtCallLinkSate) {
			mIsP2PConf = VConferenceManager.currTMtCallLinkSate.isP2PVConf();
		}
		if (null != VConferenceManager.mCallPeerE164Num) {
			mE164 = VConferenceManager.mCallPeerE164Num;
		}
		mVConfQuality = extra.getInt(Constants.TERMINAL_VCONFQUALITY); //会议质量
		mDuration = extra.getInt(Constants.TERMINAL_VCONFDURATION);//会议时长
		try {
			mTMtList = new Gson().fromJson(extra.getString("tMtList"),
					new TypeToken<List<TMtAddr>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Fragment getCurrFragmentView() {
		return mCurrFragmentView;
	}

	public VConfVideoFrame getVConfContentFrame() {
		return (VConfVideoFrame) mVConfContentFrame;
	}

	/**
	 * 切换视音频界面
	 */
	public void switchVConfFragment() {
		// 视频会议
		if (VConferenceManager.isCSVConf() || VConferenceManager.nativeConfType == EmNativeConfType.VIDEO
				|| (null != VConferenceManager.currTMtCallLinkSate && !VConferenceManager.currTMtCallLinkSate.isCaller())) {// 被叫时直接进入视频界面
			if (null == mVConfContentFrame) {
				mVConfContentFrame = new VConfVideoFrame();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.vconf_video_ui_layout_fl_root, mVConfContentFrame);
				ft.commitAllowingStateLoss();
			}

			if (!(mCurrFragmentView instanceof VConfVideoPlayFrame)) {
				mCurrFragmentView = new VConfVideoPlayFrame();
				((VConfVideoFrame) mVConfContentFrame).replaceContentFrame(mCurrFragmentView);
			}
		}
		// 视频入会
		else {
			if (mCurrFragmentView instanceof VConfJoinVideoFrame) {
				return;
			}

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			mVConfContentFrame = null;
			mCurrFragmentView = new VConfJoinVideoFrame();
			ft.replace(R.id.vconf_video_ui_layout_fl_root, mCurrFragmentView);
			ft.commit();
		}
	}

	public void setScreenOrientationLandscape() {
		if (VConferenceManager.isCSMCC() && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) { // 横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	/**
	 * 旋转屏幕时
	 * @see android.support.v4.app.FragmentActivity#onConfigurationChanged(Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("VConfVideo", "VconfVideoFrame-->onConfigurationChanged 是否为横屏：false");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("VConfVideo", "VconfVideoFrame-->onConfigurationChanged 是否为横屏：true");

		}
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	/** @return the tMtList */
	public List<TMtAddr> gettMtList() {
		return mTMtList;
	}



	@Override
	protected void onDestroy() {
		Log.w("VConfVideo", "VConfVideoUI-->onDestroy");
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}



	/** @return the mE164 */
	public String getmE164() {
		return mE164;
	}

	/** @return the mVConf */
	public VConf getmVConf() {
		return mVConf;
	}

	/** @return the mConfTitle */
	public String getmConfTitle() {
		return mConfTitle;
	}

	/** @return the mIsP2PConf */
	public boolean ismIsP2PConf() {
		return mIsP2PConf;
	}

	/** @return the mIsJoinConf */
	public boolean ismIsJoinConf() {
		return mIsJoinConf;
	}

	/** @param mConfTitle the mConfTitle to set */
	public void setmConfTitle(String mConfTitle) {
		this.mConfTitle = mConfTitle;
	}

	/** @return the mVConfQuality */
	public int getVConfQuality() {
		return mVConfQuality;
	}

	/** @return the mDuration */
	public int getDuration() {
		return mDuration;
	}


    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()){
				case Constants.MEETING_FORCE_CLOSE_ACTION:
					finish();
					break;
				case Constants.MEETING_SWITCHVCONFVIEW_ACTION:
					switchVConfFragment();
					break;
				case Constants.MEETING_MUTEIMAGE_ACTION:
					if (null !=getVConfContentFrame()) {
						getVConfContentFrame().getBottomFunctionFragment().setMuteImageView(true);
					}
					break;
				case Constants.MEETING_NOT_MUTEIMAGE_ACTION:
					if (null !=getVConfContentFrame()) {
						getVConfContentFrame().getBottomFunctionFragment().setMuteImageView(false);
					}
					break;
				case Constants.MEETING_QUIETIMAGE_ACTION:
					if (null !=getVConfContentFrame()) {
						getVConfContentFrame().getBottomFunctionFragment().setQuietImageView(true);
					}
					break;
				case Constants.MEETING_NOT_QUIETIMAGE_ACTION:
					if (null !=getVConfContentFrame()) {
						getVConfContentFrame().getBottomFunctionFragment().setQuietImageView(false);
					}
					break;
				case Constants.MEETING_REMOVEREQCHAIRMANHANDLER_ACTION:
					if (null !=getVConfContentFrame()) {
						getVConfContentFrame().getBottomFunctionFragment().removeReqChairmanHandler();
						getVConfContentFrame().getBottomFunctionFragment().updateOperationView();
					}
					break;
				case Constants.MEETING_REMOVEREQSPEAKERHANDLER_ACTION:
					if (null !=getVConfContentFrame()) {
						getVConfContentFrame().getBottomFunctionFragment().removeReqSpeakerHandler();
						getVConfContentFrame().getBottomFunctionFragment().updateOperationView();
					}
					break;
				case Constants.MEETING_ASSSENDSREAMSTATUSNTF_ACTION:
					//视频播放界面 切换到双流
					if (getVConfContentFrame() == null) {
						return;
					}
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							getVConfContentFrame().switchDualStreamCtrl(VConferenceManager.isDualStream);
						}
					});

					break;
			}
		}
	};
	/**
	 * 注册广播监听器
	 */
	private void registerReceiver(){
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(Constants.MEETING_FORCE_CLOSE_ACTION);
		intentFilter.addAction(Constants.MEETING_ASSSENDSREAMSTATUSNTF_ACTION);
		intentFilter.addAction(Constants.MEETING_SWITCHVCONFVIEW_ACTION);
		intentFilter.addAction(Constants.MEETING_MUTEIMAGE_ACTION);
		intentFilter.addAction(Constants.MEETING_NOT_MUTEIMAGE_ACTION);
		intentFilter.addAction(Constants.MEETING_QUIETIMAGE_ACTION);
		intentFilter.addAction(Constants.MEETING_NOT_QUIETIMAGE_ACTION);
		intentFilter.addAction(Constants.MEETING_REMOVEREQCHAIRMANHANDLER_ACTION);
		intentFilter.addAction(Constants.MEETING_REMOVEREQSPEAKERHANDLER_ACTION);
		registerReceiver(mBroadcastReceiver,intentFilter);
	}

	//录屏方案3
	private static final int REQUEST_CODE = 1000;
	/**
	 * 获取屏幕录制的权限,开启录制
	 */
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void startScreenRecording() {
		// TODO Auto-generated method stub
		if((Utils.getTFPath()!=null&&Utils.getTFPath().length()>0)||Utils.hasSDFree()) {//有TF卡或者有足够SD存储容量
			MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) (this.getSystemService(Context.MEDIA_PROJECTION_SERVICE));
			Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
			startActivityForResult(permissionIntent, REQUEST_CODE);
		}
	}
	private int mScreenWidth;
	private int mScreenHeight;
	private int mScreenDensity;
	/** 是否为标清视频 */
	private boolean isVideoSd = true;
	/** 是否开启音频录制 */
	private boolean isAudio = true;


	/**
	 * 获取屏幕相关数据
	 */
	private void getScreenBaseInfo() {
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
		mScreenDensity = metrics.densityDpi;
		mScreenWidth=mScreenWidth-mScreenDensity/2;//解决有些平板满屏无法录制问题
	}

	//	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
				// 获得权限，启动Service开始录制
				Intent service = new Intent(this, ScreenRecordService.class);
				service.setAction(Constants.START_RECORDSCREEN_ACTION);
				service.putExtra("code", resultCode);
				service.putExtra("data", data);
				service.putExtra("audio", isAudio);
				service.putExtra("width", mScreenWidth);
				service.putExtra("height", mScreenHeight);
				service.putExtra("density", mScreenDensity);
				service.putExtra("quality", isVideoSd);
				this.startService(service);
			} else {
				Toast.makeText(this,R.string.cancel_record,Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void finish() {
		//停止录屏
//		GKApplication.getInstance().stopScreenRecording();
		super.finish();
	}
}
