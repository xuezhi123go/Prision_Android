package com.gkzxhn.prison.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.presenter.LoginPresenter;
import com.gkzxhn.prison.utils.Utils;
import com.gkzxhn.prison.view.ILoginView;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public class LoginActivity  extends SuperActivity implements ILoginView{
    private EditText etAccount,etPassword;
    private LoginPresenter mPresenter;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initControls();
        init();
        //清除下信息
        SharedPreferences sharedPreferences= getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    private void initControls(){
        etAccount= (EditText) findViewById(R.id.loign_layout_et_username);
        etPassword= (EditText) findViewById(R.id.loign_layout_et_password);
    }
    private void init(){
        //显示记住的密码
        SharedPreferences preferences= getSharedPreferences(Constants.USER_ACCOUNT_TABLE, Activity.MODE_PRIVATE);
        etAccount.setText(preferences.getString(Constants.USER_ACCOUNT,""));
        etPassword.setText(preferences.getString(Constants.USER_PASSWORD,""));
        mPresenter=new LoginPresenter(this,this);
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting));
        stopRefreshAnim();
    }
    public void onClickListener(View view){
        if(view.getId()==R.id.loign_layout_btn_login){
            String account=etAccount.getText().toString().trim();
            String password=etPassword.getText().toString().trim();
            if(account.length()==0){
                showToast(getString(R.string.please_input)+getString(R.string.account));
            }else if(password.length()==0){
                showToast(getString(R.string.please_input)+getString(R.string.password));
            }else{
                mPresenter.login(account,password);
            }
        }
    }

    @Override
    public void onSuccess() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void startRefreshAnim() {
        if(mProgress!=null&&!mProgress.isShowing())mProgress.show();
    }

    @Override
    public void stopRefreshAnim() {
        if(mProgress!=null&&mProgress.isShowing())mProgress.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        stopRefreshAnim();
        mPresenter.onDestory();
        super.onDestroy();
    }
}
