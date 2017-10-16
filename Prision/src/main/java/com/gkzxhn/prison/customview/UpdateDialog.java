package com.gkzxhn.prison.customview;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.utils.DownLoadHelper;

import java.io.File;


public class UpdateDialog extends Dialog {
	private View contentView;
	private Context context;
	private TextView tvVersion,tvProgress,tvDownload,tvCancel;
	private ProgressBar mProgress;
	private String versionName;
	private DownLoadHelper mHelper;
	private String downloadUrl;
	private boolean isForceUpdate=false;
	private View vMidLine;
	private int versionCode;
	public UpdateDialog(Context context) {
		super(context, R.style.update_dialog_style);
		this.context=context;
		mHelper=new DownLoadHelper();
	}
	public void setDownloadInfor(String versionName, int versionCode,String downloadUrl) {
		this.downloadUrl = downloadUrl;
		this.versionName=versionName;
		this.versionCode=versionCode;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(getContext()).inflate(R.layout.update_dialog_layout, null);
		setContentView(contentView);
		initControls();
		init();
		measureWindow();
	}
	public void measureWindow(){
		Window dialogWindow = this.getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		WindowManager m = dialogWindow.getWindowManager();

		Display d = m.getDefaultDisplay();
		params.width = d.getWidth();
		//	        params.height=d.getHeight();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(params);
	}
	private void initControls(){
		tvCancel= (TextView) findViewById(R.id.update_dialog_layout_tv_cancel);
		vMidLine=findViewById(R.id.update_dialog_layout_v_mid);
		tvCancel.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);
		vMidLine.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);
		setCanceledOnTouchOutside(!isForceUpdate);
		tvDownload= (TextView) findViewById(R.id.update_dialog_layout_tv_download);
		tvVersion=(TextView) findViewById(R.id.update_dialog_layoutt_tv_new_version);
		tvProgress=(TextView) findViewById(R.id.update_dialog_layout_tv_progress);
		mProgress=(ProgressBar) findViewById(R.id.update_dialog_layout_progress);
		tvVersion.setText(context.getString(R.string.new_version_colon)+versionName);

	}
	public void setForceUpdate(boolean isForceUpdate){
		this.isForceUpdate=isForceUpdate;
		if(tvDownload!=null) {
			setCanceledOnTouchOutside(!isForceUpdate);
			tvCancel.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);
			vMidLine.setVisibility(isForceUpdate ? View.GONE : View.VISIBLE);
			tvDownload.setEnabled(true);
			tvProgress.setVisibility(View.GONE);
			mProgress.setVisibility(View.GONE);
		}
	}


	private void init(){
		tvDownload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mHelper.setListener(downloadFinishListener);
				tvDownload.setEnabled(false);
				tvProgress.setVisibility(View.VISIBLE);
				mProgress.setVisibility(View.VISIBLE);
				mHelper.download(downloadUrl);
			}
		});
		findViewById(R.id.update_dialog_layout_tv_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				//报错忽略升级的版本
				SharedPreferences preferences=getContext().getSharedPreferences(Constants.USER_TABLE,Context.MODE_PRIVATE);
				preferences.edit().putInt(Constants.LAST_IGNORE_VERSION,versionCode).commit();
				if(mHelper!=null)mHelper.onStop();
			}
		});
	}
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		super.setOnDismissListener(listener);
		if(mHelper!=null)mHelper.onStop();
	}

	@Override
	public void show() {
		super.show();
		if(tvDownload!=null)tvDownload.setEnabled(true);
		if(tvProgress!=null)tvProgress.setVisibility(View.INVISIBLE);
	}
	private DownLoadHelper.DownloadFinishListener downloadFinishListener=new DownLoadHelper.DownloadFinishListener() {
		@Override
		public void onSuccess(String filePath) {

			//安装apk
			File apkfile = new File(filePath);
			if (!apkfile.exists()) {
				return;
			}
			// 通过Intent安装APK文件
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");
			context.startActivity(i);
			dismiss();
		}

		@Override
		public void onFailed(String error) {
			Toast.makeText(getContext(),R.string.download_failed, Toast.LENGTH_SHORT).show();
			dismiss();

		}

		@Override
		public void onProgress(int currentSize, int totalSize) {
			float total=(float)totalSize/1024 / 1024;
			float current= (float)currentSize / 1024 / 1024;
			mProgress.setProgress((int) (current / total * 100));
			tvProgress.setText(String.format("%.2fMB/%.2fMB", current> total ? total: current, total));

		}
	};
}
