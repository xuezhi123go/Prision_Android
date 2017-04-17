package com.gkzxhn.prision.model;

import com.gkzxhn.prision.async.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

public interface ICallUserModel extends IBaseModel {
    public void request(String id, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
}
