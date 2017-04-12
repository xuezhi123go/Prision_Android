package com.gkzxhn.prision.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gkzxhn.prision.R;
import com.gkzxhn.prision.activity.LoginActivity;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.common.GKApplication;
import com.gkzxhn.prision.model.IBaseModel;
import com.gkzxhn.prision.utils.Utils;
import com.gkzxhn.prision.view.IBaseView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.starlight.mobile.android.lib.util.CommonHelper;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;


/**
 * Created by Raleigh on 15/11/13.
 */
public class BasePresenter<M extends IBaseModel,V extends IBaseView> {
    protected WeakReference<Context> mWeakContext;
    protected final String UNAUTHCODE="401";
    protected final int PAGE_SIZE=20;
    protected final int FIRST_PAGE=1;
    protected int currentPage=FIRST_PAGE;
    protected M mModel;
    protected WeakReference<V> mWeakView;
    protected final int SUCCESS_CODE=0,FAILD_CODE=-1;

    public BasePresenter(Context context, M mModel, V view){
        this.mModel=mModel;
        if(context!=null)mWeakContext=new WeakReference<Context>(context);
        if(view!=null)mWeakView=new WeakReference<V>(view);
    }
    public SharedPreferences getSharedPreferences(){
        return mModel.getSharedPreferences();
    }
    protected SharedPreferences sharedPreferences= GKApplication.getInstance().getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
    protected void unauthorized(){//login has expired
        Context mContext=mWeakContext==null?null:mWeakContext.get();
        if(mContext!=null&&!sharedPreferences.getBoolean(Constants.USER_IS_UNAUTHORIZED,false)) {
            NIMClient.getService(AuthService.class).logout();//退出云信
            //清除别名
//            Toast.makeText(GKApplication.getInstance(), R.string.user_not_authorized, Toast.LENGTH_SHORT).show();
//            GKApplication.getInstance().clearAccount();
//            Intent intent = new Intent(mContext, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            mContext.startActivity(intent);
//            ((Activity) mContext).finish();
        }
    }


    public  void onDestory(){

        if(mModel!=null)mModel.stopAllReuqst();
    }



    protected void showErrors(VolleyError error){
        IBaseView view=mWeakView==null?null:mWeakView.get();
        int code = -1;
        try {
            code = error.networkResponse.statusCode;
            if (code == HttpStatus.SC_REQUEST_TIMEOUT) {//try agin
                if (view != null){
                    stopAnim();
                    view.stopRefreshAnim();
                    view.showToast(Utils.isConnected() ? R.string.request_timeout_with_try : R.string.not_available_network_hint);
                }

            } else if (code == HttpStatus.SC_BAD_REQUEST ||code == HttpStatus.SC_BAD_GATEWAY ||
                    code == HttpStatus.SC_SERVICE_UNAVAILABLE || code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {//service_not_available
                if (view != null) {
                    stopAnim();
                    view.stopRefreshAnim();
                    view.showToast(R.string.service_not_available);
                }
            } else if ( code == HttpStatus.SC_UNAUTHORIZED || code == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {//error password or username
                if (view != null){
                    stopAnim();
                    view.stopRefreshAnim();
                }
                unauthorized();
            } else {
                if (view != null) {
                    stopAnim();
                    view.stopRefreshAnim();
                    view.showToast(R.string.unexpected_errors);
                }
            }
        } catch (Exception e) {
            if (view != null) {
                stopAnim();
                view.stopRefreshAnim();
                view.showToast(Utils.isConnected() ? R.string.request_timeout_with_try : R.string.not_available_network_hint);
            }
        }
    }
    protected void stopAnim(){}

}
