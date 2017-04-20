package com.gkzxhn.prision.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.customview.UpdateDialog;
import com.gkzxhn.prision.entity.MeetingEntity;
import com.gkzxhn.prision.entity.VersionEntity;
import com.gkzxhn.prision.presenter.MainPresenter;
import com.gkzxhn.prision.view.IMainView;

import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class SettingActivity extends SuperActivity implements IMainView{
    private int mResultCode=RESULT_CANCELED;
    private TextView tvUpdateHint;
    private MainPresenter mPresenter;
    private UpdateDialog updateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        tvUpdateHint= (TextView) findViewById(R.id.setting_layout_tv_update_hint);
        mPresenter=new MainPresenter(this,this);
        mPresenter.requestVersion();
    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.setting_layout_tv_end_setting:
                Intent intent=new Intent(this, ConfigActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.setting_layout_tv_update:
                tvUpdateHint.setText(R.string.check_updating);

                break;
            case R.id.setting_layout_tv_logout:
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
}
