package com.gkzxhn.prision.model;

import com.gkzxhn.prision.async.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public interface IMainModel extends IBaseModel {
    void requestVersion(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void requestCancel(String id,String reason, VolleyUtils.OnFinishedListener<String> onFinishedListener);
    void request(String date, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
}
