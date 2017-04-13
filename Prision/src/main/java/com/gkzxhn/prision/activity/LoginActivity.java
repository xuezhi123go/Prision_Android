package com.gkzxhn.prision.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.presenter.LoginPresenter;
import com.gkzxhn.prision.view.ILoginView;

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
    }
    private void initControls(){
        etAccount= (EditText) findViewById(R.id.loign_layout_et_username);
        etPassword= (EditText) findViewById(R.id.loign_layout_et_password);
    }
    private void init(){
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

}
