package com.gkzxhn.prision.model;

import com.gkzxhn.prision.async.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public interface IMainModel extends IBaseModel {
    void requestCancel(String id, VolleyUtils.OnFinishedListener<String> onFinishedListener);
    public void request(String date, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
}
