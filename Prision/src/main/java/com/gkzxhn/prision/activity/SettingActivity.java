package com.gkzxhn.prision.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gkzxhn.prision.R;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class SettingActivity extends SuperActivity {
    private int mResultCode=RESULT_CANCELED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.setting_layout_tv_end_setting:
                Intent intent=new Intent(this, ConfigActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.setting_layout_tv_update:
//                tvUpdateHint.setText(R.string.check_updating);
//                requestVersion();
                break;
            case R.id.setting_layout_tv_logout:
                break;
            case R.id.common_head_layout_iv_left:
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            mResultCode=RESULT_OK;
        }
    }


}
