/**
 * @(#)MyMtcCallback.java   2014-10-21
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.gkzxhn.prison.keda.callback;

import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.keda.utils.LoginStateManager;
import com.gkzxhn.prison.keda.utils.StringUtils;
import com.gkzxhn.prison.keda.vconf.VConferenceManager;
import com.google.gson.Gson;
import com.kedacom.kdv.mt.api.IM;
import com.kedacom.kdv.mt.bean.MtApiHead;
import com.kedacom.kdv.mt.bean.TImSn;
import com.kedacom.kdv.mt.constant.EmMtCallDisReason;
import com.kedacom.kdv.mt.constant.EmMtEntity;
import com.kedacom.kdv.mt.mtapi.MtcCallback;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mtc Callback
 * 
 * @author chenj
 * @date 2014-10-21
 */

public class MyMtcCallback extends MtcCallback {

	public static final int HTTPCODE_SUCCESSID = 200;
	public static final int PLATFORMAPI_SUCCESSID = 1000;

	public static final String KEY_MTAPI = "mtapi";
	public static final String KEY_HEAD = "head";
	public static final String KEY_HEAD_EVENTID = "eventid";
	public static final String KEY_HEAD_EVENTNAME = "eventname";
	public static final String KEY_BODY = "body";
	public static final String KEY_basetype = "basetype";

	public static final String KEY_dwHandle = "dwHandle";
	public static final String KEY_dwErrID = "dwErrID";
	public static final String KEY_dwErrorID = "dwErrorID";
	public static final String KEY_AssParam = "AssParam";
	public static final String KEY_MainParam = "MainParam";
	public final static String KEY_TERRINFO = "tErrInfo";
	public static final String KEY_Reserved = "dwReserved";

	public String JNIHEADER = "com.kedacom.truetouch.mtc.jni.";

	// 停止接收Callback消息
	public boolean stopHanldeJni = false;

	private static MyMtcCallback mMyMtcCallback;

	static {
		mMyMtcCallback = new MyMtcCallback();
	}

	public synchronized static MyMtcCallback getInstance() {
		if (null == mMyMtcCallback) {
			mMyMtcCallback = new MyMtcCallback();
		}

		return mMyMtcCallback;
	}

	public static void releaseInstance() {
		mMyMtcCallback = null;
	}

	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	/**
	 */
	private MyMtcCallback() {
		super();
		stopHanldeJni = false;
	}

	public void Callback(String strMsg) {
		callback(strMsg);
	}

	// private JSONObject jsonBodyObj;

	/**
	 * 
	 * { "mtapi":{ "head":{ "eventid":2147, eventname:"RegResultNtf",
	 * "SessionID": "1" }, "body":{ "basetype" : true } } }
	 * 
	 */
	public void callback(String result) {
		if (StringUtils.isNull(result)) {
			return;
		}

//		System.out.println("-------------callback 1-----------------");
//		System.out.println(result);
//		System.out.println("-------------callback 2-----------------");

		String body = "";
		JSONObject jsonBodyObj = null;
		MtApiHead mtApiHead = null;
		try {
			JSONObject jsonObj = new JSONObject(result);
			jsonObj = jsonObj.getJSONObject(KEY_MTAPI);

			body = jsonObj.getString(KEY_BODY);
			jsonBodyObj = jsonObj.getJSONObject(KEY_BODY);

			mtApiHead = new Gson().fromJson(jsonObj.getString(KEY_HEAD), MtApiHead.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String eventType = getMessageChildFirstTag2(result);
		// if (StringUtils.isNull(eventType)) {
		// return;
		// }
		try {
			String eventname = mtApiHead.eventname;
			// 设置H323代理模式
			if (EmMtEntity.SetH323PxyCfgNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseSetH323PxyCfgNtf(mtApiHead.sessionId, jsonBodyObj);
			}
			else if (EmMtEntity.ApsLoginResultNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseApsLoginResultNtf(mtApiHead.sessionId, jsonBodyObj);
			}// Im登录
			else if (EmMtEntity.ImLoginRsp.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseImLoginRsp(jsonBodyObj);
			}
			// Im准备好获取数据
			else if (EmMtEntity.ImMembersDataReadyNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has("dwHandle")) {
					jsonBodyObj.getString("dwHandle");
				}
				// 查询联系人组
				IM.imQuerySubGroupInfoByGroupSnReq(new TImSn());
			}
			// 平台Token响应
			else if (EmMtEntity.RestGetPlatformAccountTokenRsp.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseRestGetPlatformAccountTokenRsp(body);
			}

			// 会议
			// ---------------------------------------------------------------------------
			// P2P结束
			else if (EmMtEntity.P2PEndedNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					jsonBodyObj.getInt(KEY_basetype);
				}
				VConferenceManager.quitConfAction(true, false);
			}
			// 结束会议(多点)通知
			else if (EmMtEntity.MulConfEndedNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					jsonBodyObj.getInt(KEY_basetype);
				}
				VConferenceManager.quitConfAction(true, false);
			}
			// 取消呼叫 BODY(EmMtCallDisReason_Api, N/A)
			else if (EmMtEntity.ConfCanceledNtf.toString().equalsIgnoreCase(eventname)) {

				int reson = -1;
				if (jsonBodyObj.has(KEY_basetype)) {
					reson = jsonBodyObj.getInt(KEY_basetype);
					new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(GKApplication.getInstance(), R.string.check_failed, Toast.LENGTH_SHORT)
									.show();
						}
					});
				}

				// 修改短会原因
				if (reson != -1 && null != VConferenceManager.currTMtCallLinkSate) {
					VConferenceManager.currTMtCallLinkSate.emCallDisReason =
							EmMtCallDisReason.toEmMtCallDisReason(reson,
									VConferenceManager.currTMtCallLinkSate.emCallDisReason);
				}

				if (null != VConferenceManager.currTMtCallLinkSate) {
					VConferenceManager.quitConfAction(VConferenceManager.currTMtCallLinkSate.isVConf(), false);
				} else {
					VConferenceManager.quitConfAction(false, false);
				}
			}
			// 被叫
			else if (EmMtEntity.ConfInComingNtf.toString().equals(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
			}
			// 状态通知 呼叫连接中
			else if (EmMtEntity.ConfCallingNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
			}
			// callMissed
			else if (EmMtEntity.CallMissedNtf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseCallMissed(body);
			}
			// 关闭第一路通道
			else if (EmMtEntity.PrimoVideoOff_Ntf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parsePrimoVideoOff(body);
			}
			// 开始点对点会议
			else if (EmMtEntity.P2PStartedNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
				// 修改自己的状态
				LoginStateManager.imModifySelfStateReq();
			}
			// 开始多点会议
			else if (EmMtEntity.MulConfStartedNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
				// 修改自己的状态
				LoginStateManager.imModifySelfStateReq();
				//
				// if (PcLog.isPrint) {
				// Log.d("Test", "入会成功，查询会议详情:" + VConferenceManager.mCallPeerE164Num);
				// }
				//
				// if (TruetouchApplication.getApplication().currLoginModle() != EmModle.customH323) {
				// ConfLibCtrl.confGetConfDetailCmd(VConferenceManager.mCallPeerE164Num);
				// }
			}
			// 获取会议信息
			else if (EmMtEntity.ConfInfoNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfInfo(body);
			}
			// 多点会议，设置本地终端信息
			else if (EmMtEntity.TerLabelNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.setTerLable(body);
			}
			// 当前会议所有在线终端通知
			else if (EmMtEntity.OnLineTerListNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseOnLineTerList(jsonBodyObj);
			}
			// 主席位置
			else if (EmMtEntity.ChairPosNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.setChairPos(body);
			}
			// 向本终端申请主席（本端为主席）
			else if (EmMtEntity.ApplyChairNtf.toString().equalsIgnoreCase(eventname)) {
//				VconfMtcCallback.applyChair(body);
			}
			// 向本终端申请发言（本端为主席）
			else if (EmMtEntity.ApplySpeakNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.applySpeakPos(body);
			}

			// 当前看的视频源通知
			else if (EmMtEntity.YouAreSeeingNtf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseYouAreSing(body);
			}
			// 会议中 有人加入会议通知
			else if (EmMtEntity.TerJoin_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.pareseTerJoinVconf(body);
			}
			// 会议中 有人退出会议通知
			else if (EmMtEntity.TerLeft_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.pareseTerLeftVconf(body);
			}
			// 申请主席 返回通知 获得主席令牌
			else if (EmMtEntity.ChairTokenGetNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) { // true表示申请成功，false表示申请失败
					// VconfMtcCallback.parseChairToken(jsonBodyObj.getBoolean(basetype));//
					// 设置本终端为主席终端
				}
			}
			// 申请主讲 返回通知
			else if (EmMtEntity.SeenByAllNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					// VconfMtcCallback.parseSeenByAll();// 设置本终端为主讲终端
				}
			}
			// 轮询状态上报
			else if (EmMtEntity.PollState_Ntf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseTMtPollInfo(body);
			}
			// 通知当前会议类型
			else if (EmMtEntity.NotifyConfTypeNtf.toString().equalsIgnoreCase(eventname)) {

			}
			// 画面合成参数
			else if (EmMtEntity.GetConfVMPResultNtf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseTMtVmpParam(body);
			}
			// simple 会议消息
			else if (EmMtEntity.SimpleConfInfo_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseSimpleConfInfo(body);
			}
			// 当前会议是否加密通知
			else if (EmMtEntity.CallEncryptNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					VConferenceManager.isCallEncryptNtf = jsonBodyObj.getBoolean(KEY_basetype);
				}
			}
			// 辅流发送状态通知
			else if (EmMtEntity.AssSndSreamStatusNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseAssSreamStatus(true, jsonBodyObj);
			}
			// 辅流接收状态通知
			else if (EmMtEntity.AssRcvSreamStatusNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseAssSreamStatus(false, jsonBodyObj);
			}
			// 多点会议中 双流发送者
			else if (EmMtEntity.AssStreamSender_Ntf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseAssSreamSender(jsonBodyObj);
			}
			// 注册GK RegResultNtf --- EmRegFailedReason
			else if (EmMtEntity.RegResultNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseGKRegResultNtf(jsonBodyObj);
			}
			// 注册授权认证
			else if (EmMtEntity.RegAuthResultNtf.toString().equalsIgnoreCase(eventname)) {
				// LoginMtcCallback.parseRegAuthResult(jsonBodyObj);
			}
			// CSU服务器配置
			else if (EmMtEntity.SetCSUCfgNtf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseCSUCfg(body);
			}
			// 静音
			else if (EmMtEntity.CodecQuietNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					GKApplication.getInstance().sendBroadcast(new Intent(jsonBodyObj.getBoolean(KEY_basetype)?Constants.MEETING_QUIETIMAGE_ACTION:Constants.MEETING_NOT_QUIETIMAGE_ACTION));
				}
			}
			// 哑音
			else if (EmMtEntity.CodecMuteNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {// 设置哑音
					GKApplication.getInstance().sendBroadcast(new Intent(jsonBodyObj.getBoolean(KEY_basetype)?Constants.MEETING_MUTEIMAGE_ACTION:Constants.MEETING_NOT_MUTEIMAGE_ACTION));
				}
			}
			// 会议列表信息
			else if (EmMtEntity.ConfListRsp.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfList(jsonBodyObj);
			}
			// 会议详情解析
			else if (EmMtEntity.GetConfDetailInfoNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfDetailInfo(jsonBodyObj);
			}
			// H323在线终端列表
			else if (EmMtEntity.OnLineTerListRsp.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseOnLineTerListRsp(jsonBodyObj);
			}
			// 创建会议是否成功(预加入会议结果通知)
			else if (EmMtEntity.PreCreateConfResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.paresJoinCreateConfResult(jsonBodyObj);
			}
			// 加入会议是否成功
			else if (EmMtEntity.PreJoinConfResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.paresJoinCreateConfResult(jsonBodyObj);
			}
			// 会议加密 弹框
			else if (EmMtEntity.McReqTerPwdNtf.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.paresReqTerPwdNtf(jsonBodyObj);
			}
			// 音视频编解码信息
			else if (EmMtEntity.DiagnoseCodecGetStatistic_Rsp.toString().equalsIgnoreCase(eventname)) {
				// VconfMtcCallback.parseCodecStatic(body);
			} // 按姓名模糊查找本企业联系人请求
			else if (EmMtEntity.RestUserListByStr_Rsp.toString().equals(eventname)) {
				//				VconfMtcCallback.parseRestUserListByStrRsp(jsonBodyObj);
			}// 延长会议时间结果(主席延长会议)
			else if (EmMtEntity.ProlongResultNtf.toString().equalsIgnoreCase(eventname)) {
				if (VConferenceManager.isChairMan()) {
					if (jsonBodyObj.has(KEY_basetype) && jsonBodyObj.getBoolean(KEY_basetype)) {
						new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(GKApplication.getInstance(), R.string.delay_meeting_success, Toast.LENGTH_SHORT)
										.show();
							}
						});
					} else {
						new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(GKApplication.getInstance(), R.string.delay_meeting_failed, Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
