package com.gkzxhn.prison.model.iml;

import com.android.volley.AuthFailureError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.model.ICallUserModel;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

public class CallUserModel extends BaseModel implements ICallUserModel{
    @Override
    public void request(String id, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        String url=String.format("%s/%s",Constants.REQUEST_MEETING_DETAIL_URL,id);
        try {
            volleyUtils.get(JSONObject.class,url,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
}
