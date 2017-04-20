package com.gkzxhn.prision.presenter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.gkzxhn.prision.async.VolleyUtils;
import com.gkzxhn.prision.entity.MeetingDetailEntity;
import com.gkzxhn.prision.model.ICallUserModel;
import com.gkzxhn.prision.model.iml.CallUserModel;
import com.gkzxhn.prision.view.ICallUserView;
import com.google.gson.Gson;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

public class CallUserPresenter extends BasePresenter<ICallUserModel,ICallUserView> {
    private MeetingDetailEntity entity;

    public MeetingDetailEntity getEntity() {
        return entity;
    }
    public CallUserPresenter(Context context, ICallUserView view) {
        super(context, new CallUserModel(), view);
    }
    public void request(String id){
        ICallUserView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.startRefreshAnim();
        mModel.request(id, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                ICallUserView view=mWeakView==null?null:mWeakView.get();
                if(view!=null)view.stopRefreshAnim();
                int code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"));
                if (code == HttpStatus.SC_OK) {
                    entity=new Gson().fromJson(JSONUtil.getJSONObjectStringValue(response, "family"), MeetingDetailEntity.class);
                    view.onSuccess();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });
    }
}
