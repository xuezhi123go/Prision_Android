package com.gkzxhn.prision.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.activity.CallUserActivity;
import com.gkzxhn.prision.activity.MainActivity;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.common.GKApplication;
import com.gkzxhn.prision.keda.sky.app.PcAppStackManager;
import com.gkzxhn.prision.keda.sky.app.TruetouchGlobal;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import org.json.JSONObject;

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:云信sdk相关
 *              sdk初始化、
 *              UI初始化、
 *              监听云信系统通知及后续操作
 */

public class NimInitUtil {

    private static final String TAG = NimInitUtil.class.getName();

    /**
     * 初始化云信sdk相关
     */
    public static void initNim(){
        NIMClient.init(GKApplication.getInstance(), loginInfo(), options()); // 初始化
        if (inMainProcess()) {
            observeCustomNotification();
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                    getObserver(), true);
        }
    }

    /**
     * 观察者
     * @return
     */
    @NonNull
    private static Observer<StatusCode> getObserver() {
        return new Observer<StatusCode>() {
            public void onEvent(StatusCode status) {
                switch (status) {
                    case KICKOUT:
                        Toast.makeText(GKApplication.getInstance(), R.string.kickout,Toast.LENGTH_SHORT).show();
//                        被踢下线进入主页
                        GKApplication.getInstance().loginOff();
                        break;
                    case NET_BROKEN:
                        Toast.makeText(GKApplication.getInstance(), R.string.network_error,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }


    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private static LoginInfo loginInfo() {
        return getLoginInfo();
    }

    /**
     * // 从本地读取上次登录成功时保存的用户登录信息
     * @return
     */
    private static LoginInfo getLoginInfo() {
        SharedPreferences preferences=GKApplication.getInstance().getSharedPreferences(Constants.USER_TABLE,Context.MODE_PRIVATE);
        String token =  preferences.getString(Constants.USER_ACCOUNT,"");
        String password =  preferences.getString(Constants.USER_PASSWORD,"");
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(token, password);
        } else {
            return null;
        }
    }

    /**
     * 主进程
     * @return
     */
    private static boolean inMainProcess() {
        String packageName = GKApplication.getInstance().getPackageName();
        String processName = getProcessName();
        return packageName.equals(processName);
    }

    /**
     * 获取当前进程名
     * @return 进程名
     */
    public static  String getProcessName() {
        String processName = null;
        // ActivityManager
        ActivityManager am = ((ActivityManager) GKApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE));
        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }
            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    private static SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = Constants.SD_ROOT_PATH+ "/nim";

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = Utils.getScreenWidthHeight()[0] / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.mipmap.avatar_def;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType) {
                return null;
            }
        };
        return options;
    }


    /**
     * 监听系统通知
     */
    private static void observeCustomNotification() {
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(new Observer<CustomNotification>() {
            @Override
            public void onEvent(CustomNotification customNotification) {
                Log.i(TAG, "custom notification ApnsText : " + customNotification.getApnsText());
                Log.i(TAG, "custom notification Content : " + customNotification.getContent());
                Log.i(TAG, "custom notification FromAccount : " + customNotification.getFromAccount());
                Log.i(TAG, "custom notification SessionId : " + customNotification.getSessionId());
                Log.i(TAG, "custom notification Time : " + customNotification.getTime());
                Log.i(TAG, "custom notification SessionType : " + customNotification.getSessionType());
                // 第三方 APP 在此处理自定义通知：存储，处理，展示给用户等
                Log.i(TAG, "receive custom notification: " + customNotification.getContent()
                        + " from :" + customNotification.getSessionId() + "/" + customNotification.getSessionType());
                String content = customNotification.getContent();
                try {
                    JSONObject json=new JSONObject(content);
                    if(json.has("code")){
                        int code=Integer.valueOf(json.getString("code"));
                        if(code==-1){//呼叫 连线成功
                            GKApplication.getInstance().sendBroadcast(new Intent(Constants.ONLINE_SUCCESS_ACTION));
                        }else if(code==-2){//挂断 联系失败
                            GKApplication.getInstance().sendBroadcast(new Intent(Constants.ONLINE_FAILED_ACTION));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, true);
    }

}
