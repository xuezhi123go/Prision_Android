package com.gkzxhn.prison.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.customview.CustomDialog;
import com.gkzxhn.prison.customview.UpdateDialog;
import com.gkzxhn.prison.entity.MeetingEntity;
import com.gkzxhn.prison.entity.VersionEntity;
import com.gkzxhn.prison.presenter.MainPresenter;
import com.gkzxhn.prison.view.IMainView;

import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class SettingActivity extends SuperActivity implements IMainView{
    private int mResultCode=RESULT_CANCELED;
    private TextView tvUpdateHint;
    private MainPresenter mPresenter;
    private UpdateDialog updateDialog;
    private CustomDialog mCustomDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        tvUpdateHint= (TextView) findViewById(R.id.setting_layout_tv_update_hint);
        mPresenter=new MainPresenter(this,this);
        mCustomDialog = new CustomDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.custom_dialog_layout_tv_confirm) {
                    GKApplication.getInstance().loginOff();
                    finish();
                }
            }
        });
        mCustomDialog.setContent(getString(R.string.exit_account_hint),
                getString(R.string.cancel),getString(R.string.ok));
        registerReceiver();
    }
    public void onClickListener(View view){
        switch (view.getId()) {
            case R.id.setting_layout_tv_end_setting:
                Intent intent = new Intent(this, ConfigActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.setting_layout_tv_update:
                tvUpdateHint.setText(R.string.check_updating);
                mPresenter.requestVersion();
                break;
            case R.id.setting_layout_tv_logout:
                if (mCustomDialog != null&&!mCustomDialog.isShowing())
                    mCustomDialog.show();
                break;
            case R.id.common_head_layout_iv_left:
                setResult(mResultCode);
                finish();
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            mResultCode=RESULT_OK;
            showToast(R.string.alter_terminal_account_success);
        }
    }


    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void updateItems(List<MeetingEntity> datas) {

    }

    @Override
    public void onCanceled() {

    }

    @Override
    public void updateVersion(VersionEntity version) {
        //新版本
        int newVersion = version.getVersionCode();
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            int currentVersion=packageInfo.versionCode;//当前App版本
            if (newVersion > currentVersion) {//新版本大于当前版本
                //版本名
                String versionName =  version.getVersionName();
                // 下载地址
                String downloadUrl =  version.getDownloadUrl();
                //是否强制更新
                if(updateDialog==null)updateDialog=new UpdateDialog(this);
                updateDialog.setForceUpdate(version.isForce());
                updateDialog.setDownloadInfor(versionName,newVersion,downloadUrl);
                updateDialog.show();//显示对话框
                tvUpdateHint.setText(getString(R.string.new_version_colon)+versionName);
            }else{
                tvUpdateHint.setText(R.string.has_last_version);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startRefreshAnim() {

    }

    @Override
    public void stopRefreshAnim() {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        if (mCustomDialog != null&&mCustomDialog.isShowing())   mCustomDialog.dismiss();
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.dismiss();
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//点击返回键，返回到主页
            setResult(mResultCode);
            finish();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.measureWindow();
        if(mCustomDialog!=null&&mCustomDialog.isShowing())mCustomDialog.measureWindow();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.measureWindow();
        if(mCustomDialog!=null&&mCustomDialog.isShowing())mCustomDialog.measureWindow();

    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.NIM_KIT_OUT)){
                finish();
            }
        }
    };
    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.NIM_KIT_OUT);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }
}
