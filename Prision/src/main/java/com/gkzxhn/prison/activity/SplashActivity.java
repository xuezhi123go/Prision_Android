package com.gkzxhn.prison.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;

/**
 * Created by Raleigh.Luo on 17/4/5.
 */

public class SplashActivity extends Activity {
    private   final long SPLASH_DELAY_MILLIS = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        init();
    }
    private void init(){
        TextView tvVersionName= (TextView) findViewById(R.id.splash_layout_tv_version);
        String versionName="";
        // 包管理器
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName=packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersionName.setText( getString(R.string.app_v)+versionName);

        SharedPreferences preferences= getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
//        if(preferences.getBoolean(Constants.IS_FIRST_IN,true)) {
//            mHandler.sendEmptyMessageDelayed(0, SPLASH_DELAY_MILLIS);
//        }else
        if(preferences.getString(Constants.USER_ACCOUNT,"").length()==0){//未登录 未认证
            mHandler.sendEmptyMessageDelayed(1, SPLASH_DELAY_MILLIS);
        }else{//已登录
            mHandler.sendEmptyMessageDelayed(2, SPLASH_DELAY_MILLIS);
        }
    }
    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://启动动画
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1://跳转登录界面
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2://跳转主页
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }

            super.handleMessage(msg);

        }
    };

}
