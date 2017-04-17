package com.gkzxhn.prision.presenter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.gkzxhn.prision.R;
import com.gkzxhn.prision.async.VolleyUtils;
import com.gkzxhn.prision.entity.MeetingEntity;
import com.gkzxhn.prision.model.IMainModel;
import com.gkzxhn.prision.model.iml.MainModel;
import com.gkzxhn.prision.view.IMainView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class MainPresenter extends BasePresenter<IMainModel,IMainView> {
    public MainPresenter(Context context,IMainView view) {
        super(context, new MainModel(), view);
    }
    public void request(String date){
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.startRefreshAnim();
        mModel.request(date, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                IMainView view=mWeakView==null?null:mWeakView.get();
                if(view!=null)view.stopRefreshAnim();
                try{
                    int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                    if(code== HttpStatus.SC_OK){
                        List<MeetingEntity> datas = new Gson().fromJson(JSONUtil.getJSONObjectStringValue(response,"meetings"),  new TypeToken<List<MeetingEntity>>() {}.getType());
                        if(view!=null){
                            view.updateItems(datas);
                        }
                    }
                }catch (Exception e){ }

            }
            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });
    }
    public void requestCancel(String id,String reason){
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.showProgress();
        mModel.requestCancel(id, reason,new VolleyUtils.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String response) {
                IMainView view=mWeakView==null?null:mWeakView.get();
                if(view!=null)view.dismissProgress();
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response),"code"));
                if(view!=null){
                    if(code== HttpStatus.SC_OK) {
                        view.showToast(R.string.canceled_meeting);
                        view.onCanceled();
                    }else{
                        view.showToast(R.string.operate_failed);
                    }
                }


            }

            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });

    }

    @Override
    protected void stopAnim() {
        super.stopAnim();
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.dismissProgress();
    }
}
