package com.gkzxhn.prison.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gkzxhn.prison.R;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.entity.MeetingDetailEntity;
import com.gkzxhn.prison.model.ICallUserModel;
import com.gkzxhn.prison.model.iml.CallUserModel;
import com.gkzxhn.prison.view.ICallUserView;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
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
        checkStatusCode();
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
    /**
     * 判断当前云信id状态
     */
    public StatusCode checkStatusCode() {
        ICallUserView view=mWeakView==null?null:mWeakView.get();
        StatusCode code = NIMClient.getStatus();
        if (code == StatusCode.KICKOUT) {// 被其他端挤掉
            Toast.makeText(GKApplication.getInstance(), R.string.kickout,Toast.LENGTH_SHORT).show();
            GKApplication.getInstance().loginOff();
            ((Activity)mWeakContext.get()).finish();
        } else if (code == StatusCode.CONNECTING) {// 正在连接
        } else if (code == StatusCode.LOGINING) {// 正在登录
        } else if (code == StatusCode.NET_BROKEN) { // 网络连接已断开
            if(view!=null)view.showToast(R.string.network_error);
        } else if (code == StatusCode.UNLOGIN) {// 未登录
            //系统自动登录云信
            String username=getSharedPreferences().getString(Constants.USER_ACCOUNT,"");
            String password= getSharedPreferences().getString(Constants.USER_PASSWORD,"");
            if(username!=null&username.length()>0) {
                LoginInfo info = new LoginInfo(username, password); // config...
                //登录云信
                NIMClient.getService(AuthService.class).login(info)
                        .setCallback(null);
            }else{//退出到登录界面
                GKApplication.getInstance().loginOff();
                ((Activity)mWeakContext.get()).finish();
            }
        }
        return code;
    }
}
