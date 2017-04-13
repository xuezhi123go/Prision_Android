package com.gkzxhn.prision.presenter;

import android.content.Context;

import com.android.volley.VolleyError;
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
                try{
                    IMainView view=mWeakView==null?null:mWeakView.get();
                    int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                    if(code== HttpStatus.SC_OK){
                        List<MeetingEntity> datas = new Gson().fromJson(JSONUtil.getJSONObjectStringValue(response,"meetings"),  new TypeToken<List<MeetingEntity>>() {}.getType());
                        if(view!=null)view.updateItems(datas);
                    }else{
                    }
                }catch (Exception e){ }

            }
            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });
    }
    public void requestCancel(String id){
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.showProgress();

    }
}
