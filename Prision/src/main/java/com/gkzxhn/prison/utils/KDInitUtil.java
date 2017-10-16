package com.gkzxhn.prison.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.keda.callback.MyMtcCallback;
import com.gkzxhn.prison.keda.utils.GKStateMannager;
import com.gkzxhn.prison.keda.utils.LoginStateManager;
import com.gkzxhn.prison.keda.utils.TruetouchGlobal;
import com.gkzxhn.prison.keda.utils.DNSParseUtil;
import com.gkzxhn.prison.keda.utils.FormatTransfer;
import com.gkzxhn.prison.keda.utils.NetWorkUtils;
import com.gkzxhn.prison.keda.utils.StringUtils;
import com.google.gson.Gson;
import com.kedacom.kdv.mt.api.Base;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.bean.TagTNetUsedInfoApi;
import com.kedacom.kdv.mt.constant.EmConfProtocol;
import com.kedacom.kdv.mt.constant.EmMtModel;
import com.kedacom.kdv.mt.constant.EmNetAdapterWorkType;
import com.kedacom.truetouch.audio.AudioDeviceAndroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:科达视频SDK相关操作
 */

public class KDInitUtil {
    private static final String TAG = KDInitUtil.class.getName();
    public static boolean isH323;// h323代理
    
    public static void init(){
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Base.mtStart(EmMtModel.emSkyAndroidPhone, TruetouchGlobal.MTINFO_SKYWALKER, "5.0", getMediaLibDir()
                        + File.separator, MyMtcCallback.getInstance(), "kedacom"); // 启动业务终端，开始接受回调
                parseH323();
                // 设音视频上下文置
                AudioDeviceAndroid.initialize(GKApplication.getInstance());
                setUserdNetInfo();
                // 启动Service
                Base.initService();
                checkStaticPic(GKApplication.getInstance(), getTempDir() + File.separator);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        Log.i(TAG, "kd init complete");
                    }
                });
    }

    private static void parseH323() {
        // 从数据库获取当前 是否注册了代理
        StringBuffer H323PxyStringBuf = new StringBuffer();
        Configure.getH323PxyCfg(H323PxyStringBuf);
        String h323Pxy = H323PxyStringBuf.toString();
        TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
        // { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
        if (null != tmtH323Pxy) {
            isH323 = tmtH323Pxy.bEnable;
//            isH323 = true;
        }
    }

    private static String getMediaLibDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/mediaLib" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + File.separator;
    }

    private static String getTempDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/mediaLib/temp" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * getTmpDir
     * @return
     */
    public static String getTmpDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/.tmp" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    // 保存截图的路径(绝对路径)
    public static String getPictureDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/.picture" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    // 图片保存文件夹绝对路径
    public static String getSaveDir() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "kedacom/sky_Demo/save" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * 设置正在使用的网络信息
     */
    private static void setUserdNetInfo() {
        String ip = NetWorkUtils.getIpAddr(GKApplication.getInstance(), true);

        TagTNetUsedInfoApi netInfo = new TagTNetUsedInfoApi();
        netInfo.emUsedType = EmNetAdapterWorkType.emNetAdapterWorkType_Wifi_Api;
        // netInfo.dwIp = NetWorkUtils.getFirstWiFiIpAddres(TruetouchApplication.getContext());
        try {
            netInfo.dwIp = FormatTransfer.lBytesToLong(InetAddress.getByName(ip).getAddress());
        } catch (Exception e) {
            netInfo.dwIp = FormatTransfer.reverseInt((int) NetWorkUtils.ip2int(ip));
        }
        if (NetWorkUtils.isMobile(GKApplication.getInstance())) {
            netInfo.emUsedType = EmNetAdapterWorkType.emNetAdapterWorkType_MobileData_Api;
        }
        String dns = NetWorkUtils.getDns(GKApplication.getInstance());
        try {
            if (!StringUtils.isNull(dns)) {
                netInfo.dwDns = FormatTransfer.lBytesToLong(InetAddress.getByName(dns).getAddress());
            } else {
                netInfo.dwDns = 0;
            }
        } catch (UnknownHostException e) {
            Log.e("Test", "dwDns: " + dns + "--" + netInfo.dwDns);
        }

        Log.e("Test", "ip: " + ip + "--" + netInfo.dwIp);

        Configure.sendUsedNetInfoNtf(netInfo);
    }



    public static void login() {

        KDInitUtil.isH323 = true;
        if (!KDInitUtil.isH323) {
            Configure.setAudioPriorCfgCmd(false);
            if (isMtH323Local()) {
                // 取消代理，成功则 登陆aps
                setCancelH323PxyCfgCmd();
                return;
            }
            String terminalAccount = GKApplication.getInstance().getTerminalAccount();
            terminalAccount = "001001" + terminalAccount;
            LoginStateManager.loginAps(terminalAccount, GKApplication.getInstance().getTerminalPassword(), Constants.TERMINAL_ADDRESS);
        } else {
            Configure.setAudioPriorCfgCmd(KDInitUtil.isH323);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String ip = DNSParseUtil.dnsParse(Constants.TERMINAL_ADDRESS);
                    // 解析成功，注册代理
                    long dwIp = 0;
                    try {
                        dwIp = FormatTransfer.lBytesToLong(InetAddress.getByName(ip).getAddress());
                    } catch (Exception e) {
                        dwIp = FormatTransfer.reverseInt((int) NetWorkUtils.ip2int(ip));
                    }
                    long localH323Ip = getMtH323IpLocal();
                    // 没有注册代理，或者 注册代理的ip 改变了
                    if (localH323Ip == 0 || dwIp != localH323Ip) {
                        setH323PxyCfgCmd(dwIp);
                        return;
                    }
                    // 注册代理
                    String terminalAccount = GKApplication.getInstance().getTerminalAccount();
                    terminalAccount = "001001" + terminalAccount;
                    GKStateMannager.instance().registerGKFromH323(terminalAccount, GKApplication.getInstance().getTerminalPassword(), "");
                }
            }).start();
        }
    }

    /**
     * 检测本地 是否是代理 代理ip
     * @return
     */

    private static long getMtH323IpLocal() {
        // 从数据库获取当前 是否注册了代理
        StringBuffer H323PxyStringBuf = new StringBuffer();
        Configure.getH323PxyCfg(H323PxyStringBuf);
        String h323Pxy = H323PxyStringBuf.toString();
        TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
        // { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
        if (null != tmtH323Pxy && tmtH323Pxy.bEnable) {
            Log.i("Login", "tmtH323Pxy.dwSrvIp   " + tmtH323Pxy.dwSrvIp);
            return tmtH323Pxy.dwSrvIp;
        }
        return 0;
    }

    /**
     * 检测本地 是否是代理
     * @return
     */

    private static boolean isMtH323Local() {
        // 从数据库获取当前 是否注册了代理
        StringBuffer H323PxyStringBuf = new StringBuffer();
        Configure.getH323PxyCfg(H323PxyStringBuf);
        String h323Pxy = H323PxyStringBuf.toString();
        TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
        // { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
        if (null != tmtH323Pxy) {
            Log.i("Login", "是否h323代理   " + tmtH323Pxy.bEnable);
            return tmtH323Pxy.bEnable;
        }
        return false;
    }

    /**
     * 设置取消注册H323代理
     */
    private static void setCancelH323PxyCfgCmd() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // 取消代理
                Configure.setH323PxyCfgCmd(false, false, 0);
                // 关闭并重新开启协议栈
                Configure.stackOnOff((short) EmConfProtocol.em323.ordinal());
            }
        }).start();
    }

    /**
     * 设置代理模式成功/失败
     * @param isEnable true:设置代理可用
     */
    public static void setH323PxyCfgCmdResult(final boolean isEnable) {
        KDInitUtil.isH323 = isEnable;
        if (!isEnable) {
//            Log.i("Login", "取消代理 -- 登录APS " + Constants.mAccount + "-" + Constants.mPassword);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoginStateManager.loginAps(GKApplication.getInstance().getTerminalAccount(), GKApplication.getInstance().getTerminalPassword(),Constants.TERMINAL_ADDRESS);
                }
            }).start();
        } else {
//            Log.i("Login", " 注册代理 -- 登录gk ");
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 注册代理
                    GKStateMannager.instance().registerGKFromH323(GKApplication.getInstance().getTerminalAccount(), GKApplication.getInstance().getTerminalPassword(), "");
                }
            }).start();

            return;

        }
    }

    /**
     * 注册H323代理
     */
    private static void setH323PxyCfgCmd(final long dwIp) {
        Log.i("Login setting", "H323设置代理:" + dwIp);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Configure.setH323PxyCfgCmd(true, true, dwIp);
                // 关闭并重新开启协议栈
                Configure.stackOnOff((short) EmConfProtocol.em323.ordinal());
            }
        }).start();
    }

    /**
     * 视频会议静态图片
     */
    private static void checkStaticPic(Context context, String mediaLibTempDir) {
        if (null == context || StringUtils.isNull(mediaLibTempDir)) return;

        final String staticpicName = "staticpic.bmp";

        File file = new File(mediaLibTempDir, staticpicName);
        if (file.exists() && file.length() > 0) {
            return;
        }

        InputStream inStream = null;
        FileOutputStream foutStream = null;
        try {
            AssetManager am = context.getAssets();
            if (null == am) {
                return;
            }

            inStream = am.open(staticpicName);
            foutStream = new FileOutputStream(file);

            int byteread = 0;
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                foutStream.write(buffer, 0, byteread);
                foutStream.flush();
            }
        } catch (Exception e) {
            Log.e("Exception", "FileUtil checkStaticPic 1:", e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }

                if (foutStream != null) {
                    foutStream.close();
                }
            } catch (Exception e2) {
                Log.e("Exception", "FileUtil checkStaticPic 2:", e2);
            }
        }

    }
}
