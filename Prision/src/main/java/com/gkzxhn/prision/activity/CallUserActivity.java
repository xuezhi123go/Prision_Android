package com.gkzxhn.prision.activity;

import android.os.Bundle;
import android.view.View;

import com.gkzxhn.prision.R;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class CallUserActivity extends SuperActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_user_layout);
    }
    public void openVConfVideoUI(){

    }
    public void stopVConfVideo(){

    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.bt_call:
                break;
        }
    }
}
