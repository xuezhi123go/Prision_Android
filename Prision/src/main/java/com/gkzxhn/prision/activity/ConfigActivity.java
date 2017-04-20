package com.gkzxhn.prision.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.keda.sky.app.GKStateMannager;
import com.gkzxhn.prision.keda.utils.StringUtils;
import com.gkzxhn.prision.keda.vconf.manager.VConferenceManager;
import com.gkzxhn.prision.utils.KDInitUtil;
import com.gkzxhn.prision.utils.LoginKedaUtil;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class ConfigActivity extends SuperActivity {
    private LoginKedaUtil mLoginKedaUtil;
    private EditText etAccount;
    private Spinner mSpinner;
    private String[] mRateArray;
    private String mRate=null;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);
        initControls();
        init();
    }
    private void initControls(){
        etAccount= (EditText) findViewById(R.id.config_layout_et_account);
        mSpinner= (Spinner) findViewById(R.id.config_layout_sp_rate);
    }
    private void init(){
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting));
        stopRefreshAnim();
        mLoginKedaUtil=new LoginKedaUtil();
        mRateArray = getResources().getStringArray(R.array.rate_array);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mRate = mRateArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        int index=1;
        SharedPreferences preferences=getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
        String account=preferences.getString(Constants.TERMINAL_ACCOUNT,"");
        if(account!=null&&account.length()>0) {
            etAccount.setText(account);
            for(int i = 0; i< mRateArray.length; i++){
                String mRate= mRateArray[i];
                if(mRate.equals(String.valueOf(VConferenceManager.callRate))){
                    index=i;
                    break;
                }
            }
        }
        mRate = mRateArray[index];
        mSpinner.setSelection(index);
    }
    /* 登录成功
 * @param isSuccess
 * @param failedMsg
 */
    public void loginSuccess(final boolean isSuccess, final String failedMsg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopRefreshAnim();
                if (isSuccess) {
                    showToast(R.string.alter_terminal_account_success);
                    setResult(RESULT_OK);
                    finish();
                    return;
                }else {
                    KDInitUtil.mAccount = "";
                    VConferenceManager.callRate = 512;
                    if (StringUtils.isNull(failedMsg)) {
                        showToast(getString(R.string.login_failed));
                    } else {
                        showToast(failedMsg);
                    }
                }
            }
        });
    }
    public void setH323PxyCfgCmdResult(final boolean isEnable){
        mLoginKedaUtil.setH323PxyCfgCmdResult(isEnable);
    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.config_layout_btn_save:
                String acc = etAccount.getText().toString().trim();
                if (TextUtils.isEmpty(acc) ) {
                    showToast(R.string.please_input_terminal_account);
                }else {
                    startRefreshAnim();
                    //退出终端平台，如果已经注册了终端平台
                    GKStateMannager.restoreLoginState();
                    KDInitUtil.mAccount = acc;
                    VConferenceManager.callRate =Integer.valueOf(mRate);
                    if(mLoginKedaUtil!=null)mLoginKedaUtil.login();
                }
                break;
        }
    }
    public void startRefreshAnim() {
        if(mProgress!=null&&!mProgress.isShowing())mProgress.show();
    }

    public void stopRefreshAnim() {
        if(mProgress!=null&&mProgress.isShowing())mProgress.dismiss();
    }
}
