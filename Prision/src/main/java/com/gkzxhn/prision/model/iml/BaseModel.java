package com.gkzxhn.prision.model.iml;

import android.app.Activity;
import android.content.SharedPreferences;


import com.gkzxhn.prision.async.SingleRequestQueue;
import com.gkzxhn.prision.async.VolleyUtils;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.common.GKApplication;
import com.gkzxhn.prision.model.IBaseModel;

import java.util.UUID;

/**
 * Created by Administrator on 2016/6/12.
 */
public class BaseModel implements IBaseModel {
    protected final String REQUEST_TAG= UUID.randomUUID().toString().replace("-", "");
    protected VolleyUtils volleyUtils;
    protected SharedPreferences preferences;
    public BaseModel(){
        volleyUtils = new VolleyUtils();
        preferences= GKApplication.getInstance().
                getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public void stopAllReuqst() {
        SingleRequestQueue.getInstance().cancelAll(REQUEST_TAG);
    }
}
