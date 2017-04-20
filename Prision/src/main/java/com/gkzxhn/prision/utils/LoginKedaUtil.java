package com.gkzxhn.prision.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.common.GKApplication;
import com.gkzxhn.prision.keda.sky.app.GKStateMannager;
import com.gkzxhn.prision.keda.sky.app.LoginStateManager;
import com.gkzxhn.prision.keda.utils.DNSParseUtil;
import com.gkzxhn.prision.keda.utils.FormatTransfer;
import com.gkzxhn.prision.keda.utils.NetWorkUtils;
import com.google.gson.Gson;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.constant.EmConfProtocol;

import java.net.InetAddress;



/**
 * Created by Raleigh.Luo on 17/3/28.
 */

public class LoginKedaUtil {
    public  void login() {

        KDInitUtil.isH323 = true;
        if (!KDInitUtil.isH323) {
            Configure.setAudioPriorCfgCmd(false);
            if (isMtH323Local()) {
                // 取消代理，成功则 登陆aps
                setCancelH323PxyCfgCmd();
                return;
            }
            LoginStateManager.loginAps(GKApplication.getInstance().getTerminalAccount(), GKApplication.getInstance().getTerminalPassword(), Constants.TERMINAL_ADDRESS);
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
                    GKStateMannager.instance().registerGKFromH323(GKApplication.getInstance().getTerminalAccount(), GKApplication.getInstance().getTerminalPassword(), "");
                }
            }).start();
        }
    }

    /**
     * 检测本地 是否是代理 代理ip
     * @return
     */

    private long getMtH323IpLocal() {
        // 从数据库获取当前 是否注册了代理
        StringBuffer H323PxyStringBuf = new StringBuffer();
        Configure.getH323PxyCfg(H323PxyStringBuf);
        String h323Pxy = H323PxyStringBuf.toString();
        TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
        // { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
        if (null != tmtH323Pxy && tmtH323Pxy.bEnable) {
            android.util.Log.i("Login", "tmtH323Pxy.dwSrvIp   " + tmtH323Pxy.dwSrvIp);
            return tmtH323Pxy.dwSrvIp;
        }
        return 0;
    }

    /**
     * 检测本地 是否是代理
     * @return
     */

    private boolean isMtH323Local() {
        // 从数据库获取当前 是否注册了代理
        StringBuffer H323PxyStringBuf = new StringBuffer();
        Configure.getH323PxyCfg(H323PxyStringBuf);
        String h323Pxy = H323PxyStringBuf.toString();
        TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
        // { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
        if (null != tmtH323Pxy) {
            android.util.Log.i("Login", "是否h323代理   " + tmtH323Pxy.bEnable);
            return tmtH323Pxy.bEnable;
        }
        return false;
    }

    /**
     * 设置取消注册H323代理
     */
    private void setCancelH323PxyCfgCmd() {

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
    private void setH323PxyCfgCmd(final long dwIp) {
        android.util.Log.i("Login setting", "H323设置代理:" + dwIp);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Configure.setH323PxyCfgCmd(true, false, dwIp);
                // 关闭并重新开启协议栈
                Configure.stackOnOff((short) EmConfProtocol.em323.ordinal());
            }
        }).start();
    }
}
