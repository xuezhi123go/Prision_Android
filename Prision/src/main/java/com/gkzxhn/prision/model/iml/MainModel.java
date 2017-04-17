package com.gkzxhn.prision.model.iml;


import com.android.volley.AuthFailureError;
import com.gkzxhn.prision.async.VolleyUtils;
import com.gkzxhn.prision.model.IMainModel;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.utils.KDInitUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class MainModel extends BaseModel implements IMainModel {
    @Override
    public void requestCancel(String id, String reason,VolleyUtils.OnFinishedListener<String> onFinishedListener) {
        String url= String.format("%s/%s",Constants.REQUEST_CANCEL_MEETING_URL, id);
        try {
            Map<String,String> params=new HashMap<String,String>();
            params.put("remarks",reason);
            volleyUtils.patch(url,params,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void request(String date,VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        String terminalAccount=preferences.getString(Constants.TERMINAL_ACCOUNT,"");
        if(terminalAccount.length()==0)terminalAccount= KDInitUtil.mAccount;
        String url= String.format("%s/%s/meetings?application_date=%s",Constants.REQUEST_MEETING_LIST_URL,terminalAccount,date);
        try {
            volleyUtils.get(JSONObject.class,url,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
}
