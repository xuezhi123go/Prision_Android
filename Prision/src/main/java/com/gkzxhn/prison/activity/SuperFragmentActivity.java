package com.gkzxhn.prison.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by Raleigh.Luo on 16/4/17.
 */
public class SuperFragmentActivity extends FragmentActivity {
    private Toast mToast;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mToast=Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    }
    public void showToast(int testResId) {
        if(mToast!=null) {
            mToast.setText(testResId);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    public void showToast(String showText) {
        if(mToast!=null) {
            mToast.setText(showText);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.show();
        }
    }
    public void cancelToast(){
        if(mToast!=null)mToast.cancel();
    }

    @Override
    protected void onPause() {
        cancelToast();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        cancelToast();
        super.onDestroy();
    }
    @Override
    public void finish() {
        cancelToast();
        super.finish();

    }
}
