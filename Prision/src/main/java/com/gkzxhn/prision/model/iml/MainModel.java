package com.gkzxhn.prision.model.iml;


import com.android.volley.AuthFailureError;
import com.gkzxhn.prision.async.VolleyUtils;
import com.gkzxhn.prision.model.IMainModel;
import com.gkzxhn.prision.common.Constants;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class MainModel extends BaseModel implements IMainModel {
    @Override
    public void requestCancel(String id, VolleyUtils.OnFinishedListener<String> onFinishedListener) {
        if(volleyUtils==null)volleyUtils=new VolleyUtils();
        String url= String.format("%s/%s",Constants.REQUEST_CANCEL_MEETING_URL, id);
        try {
            volleyUtils.patch(url,new HashMap<String,String>(),REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void request(String date,VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        if(volleyUtils==null)volleyUtils=new VolleyUtils();
        String url= String.format("%s/%s/meetings",Constants.REQUEST_MEETING_LIST_URL,preferences.getString(Constants.TERMINAL_ACCOUNT,""));
        try {
            volleyUtils.get(JSONObject.class,url,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
}
