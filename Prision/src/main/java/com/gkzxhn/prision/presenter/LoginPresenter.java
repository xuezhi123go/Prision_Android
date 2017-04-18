package com.gkzxhn.prision.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.model.IBaseModel;
import com.gkzxhn.prision.model.iml.BaseModel;
import com.gkzxhn.prision.utils.LoginKedaUtil;
import com.gkzxhn.prision.view.ILoginView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

public class LoginPresenter extends BasePresenter<IBaseModel,ILoginView>{
    private LoginKedaUtil mLoginKedaUtil;
    public LoginPresenter(Context context, ILoginView view) {
        super(context, new BaseModel(), view);
    }

    /**登录云信
     * @param account
     * @param password
     */
    public void login(final String account, final String password){
        ILoginView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.startRefreshAnim();
        LoginInfo info = new LoginInfo(account, password);
        //登录云信
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(new RequestCallback() {
                    @Override
                    public void onSuccess(Object param) {
                        ILoginView view=mWeakView==null?null:mWeakView.get();
                        if(view!=null) {
                            //登录科达GK
                            if(mLoginKedaUtil==null)mLoginKedaUtil=new LoginKedaUtil();
                            mLoginKedaUtil.login();
                            //保存登录信息
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Constants.USER_ACCOUNT,account);
                            editor.putString(Constants.USER_PASSWORD,password);
                            editor.commit();
                            //关闭加载条
                            view.startRefreshAnim();
                            view.onSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        ILoginView view=mWeakView==null?null:mWeakView.get();
                        if(view!=null) {
                            view.stopRefreshAnim();
                            switch (code) {
                                case 302:
                                    view.showToast(R.string.account_pwd_error);
                                    break;
                                case 503:
                                    view.showToast(R.string.server_busy);
                                    break;
                                case 415:
                                    view.showToast(R.string.network_error);
                                    break;
                                case 408:
                                    view.showToast(R.string.time_out);
                                    break;
                                case 403:
                                    view.showToast(R.string.illegal_control);
                                    break;
                                case 422:
                                    view.showToast(R.string.account_disable);
                                    break;
                                case 500:
                                    view.showToast(R.string.service_not_available);
                                    break;
                                default:
                                    view.showToast(R.string.login_failed);
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onException(Throwable exception) {
                        ILoginView view=mWeakView==null?null:mWeakView.get();
                        if(view!=null) {
                            view.stopRefreshAnim();
                            view.showToast(R.string.login_exception_retry);
                        }
                    }
                });
    }
}
