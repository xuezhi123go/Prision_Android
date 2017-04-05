package com.gkzxhn.prision.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gkzxhn.prision.R;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public class LoginActivity  extends SuperActivity{
    private EditText etAccount,etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControls();
        init();
    }
    private void initControls(){
        etAccount= (EditText) findViewById(R.id.loign_layout_et_username);
        etPassword= (EditText) findViewById(R.id.loign_layout_et_password);
    }
    private void init(){

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

            }
        }
    }
}
