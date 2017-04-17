package com.gkzxhn.prision.activity;

import android.os.Bundle;
import android.view.View;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.keda.utils.StringUtils;
import com.gkzxhn.prision.keda.vconf.manager.VConferenceManager;
import com.gkzxhn.prision.utils.KDInitUtil;
import com.gkzxhn.prision.utils.LoginKedaUtil;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class ConfigActivity extends SuperActivity {
    private LoginKedaUtil mLoginKedaUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);
    }
    /* 登录成功
 * @param isSuccess
 * @param failedMsg
 */
    public void loginSuccess(final boolean isSuccess, final String failedMsg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dismissProgress();
                if (isSuccess) {
                    showToast("修改终端信息成功");
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
        }
    }
}
