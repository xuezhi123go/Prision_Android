package com.gkzxhn.prison.common;

import android.os.Environment;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public interface Constants {
    /*-------------------------------Config-------------------------------------------------*/

    final String SD_ROOT_PATH= Environment.getExternalStorageDirectory().getPath()+"/YWTPrision";
    final String SD_FILE_CACHE_PATH = SD_ROOT_PATH+"/cache/";

    final String SD_IMAGE_CACHE_PATH = SD_ROOT_PATH+"/imageCache/";//图片下载的缓存
    final String SD_ROOT_PHOTO_PATH = SD_ROOT_PATH+"/photo/";//图片，不自动删除
    final String SD_PHOTO_PATH = SD_ROOT_PHOTO_PATH+"cutPhoto/";//拍照存储或压缩图片的图片路径,启动时自动删除
    final String SD_VIDEO_PATH = SD_ROOT_PATH+"/video/";
    final boolean IS_DEBUG_MODEL=false;//debug模式打印日志到控制台,发布版本不打印
    final int REQUEST_TIMEOUT=60000;//超时时间1分钟
    /*-------------------------------User Tab-------------------------------------------------*/
    final String USER_TABLE="user_table";
    final String USER_ACCOUNT_TABLE="user_account_table";//记住账号密码
    final String USER_IS_UNAUTHORIZED="isUnauthorized";
    final String USER_ID="user_id";
    final String IS_FIRST_IN="is_first_in";
    final String USER_ACCOUNT="user_account";//云信帐号
    final String USER_PASSWORD="user_password";//云信密码
    final String TERMINAL_ACCOUNT="terminal_account";//终端帐号
    final String TERMINAL_RATE="terminal_rate";//终端码率
    final String TERMINAL_PASSWORD="terminal_password";//终端密码，空
    final String LAST_IGNORE_VERSION="last_ignore_version";//上一个忽略的版本
    final String OTHER_CARD="other_card";//身份证信息
    //每次会见视频名称记录,囚犯名字_会见记录ID 后期添加 时间戳年月日时分秒，如20170908102033_张三_144
    final String RECORD_VIDEO_NAME="record_video_name";
//    final String TERMINAL_ADDRESS= "222.244.146.206";//科达服务器终端
    public static String TERMINAL_ADDRESS = "106.14.18.98";    //阿里云的服务器,开启会商

    /*-------------------------------Video Config-------------------------------------------------*/
    final  String TERMINAL_E164NUM = "e164Num";
    final  String TERMINAL_VCONFNAME = "VconfName";
    final  String TERMINAL_MACKCALL = "MackCall";
    final  String TERMINAL_JOINCONF = "JoinConf";
    final  String TERMINAL_VCONFQUALITY = "VconfQuality";
    final  String TERMINAL_VCONFDURATION = "VconfDuration";


    /*-------------------------------request url-------------------------------------------------*/
    final String UPLOAD_VIDEO_DOMAIN="";//上传视频的ip
    final int UPLOAD_VIDEO_PORT=9999;//上传视频的端口
    final String RELEASE_DOMAIN="https://www.fushuile.com";//发布正式环境
    final String DEMO_DOMAIN="";//开发环境
    final String DOMAIN_NAME_XLS = RELEASE_DOMAIN;

    final String REQUEST_MEETING_LIST_URL=DOMAIN_NAME_XLS+"/api/v1/terminals";//会见列表
    final String REQUEST_CANCEL_MEETING_URL=DOMAIN_NAME_XLS+"/api/v1/meetings";// 取消会见
    final String REQUEST_MEETING_DETAIL_URL=DOMAIN_NAME_XLS+"/api/v1/families";// 会见详情
    final String REQUEST_VERSION_URL=DOMAIN_NAME_XLS+"/api/v1/versions/2";//版本更新
    final String REQUEST_CRASH_LOG_URL=DOMAIN_NAME_XLS+"/api/v1/loggers";//版本更新

    /*-------------------------------msg what-------------------------------------------------*/
    final int START_REFRESH_UI=1,STOP_REFRESH_UI=2;//msg what
    /*-------------------------------Request Code-------------------------------------------------*/
    final String EXTRA="extra";
    final String EXTRA_TAB="extra_tab";
    final String EXTRAS="extras";
    final String EXTRA_ENTITY="extra_entity";
    final String EXTRA_POSITION="extra_position";
    public final int EXTRA_CODE=0x001;
    final int PREVIEW_PHOTO_CODE=0x102;
    final int SELECT_PHOTO_CODE=0x103;
    final int TAKE_PHOTO_CODE=0x104;
    final int RESIZE_REQUEST_CODE=0x105;
    final int EXTRAS_CODE=0x106;
    /*-------------------------------action-------------------------------------------------*/
    final String _ACTION="com.gkzxhn.prision.TERMINAL_SUCCESS";//注册终端成功

    final String TERMINAL_SUCCESS_ACTION="com.gkzxhn.prision.TERMINAL_SUCCESS";//注册终端成功
    final String TERMINAL_FAILED_ACTION="com.gkzxhn.prision.TERMINAL_FAILED";//注册终端失败
    final String ONLINE_FAILED_ACTION="com.gkzxhn.prision.ONLINE_FAILED_ACTION";//连线失败
    final String ONLINE_SUCCESS_ACTION="com.gkzxhn.prision.ONLINE_SUCCESS_ACTION";//连线成功
    //云信被踢下线
    final String NIM_KIT_OUT="com.gkzxhn.prision.NIM_KIT_OUT";

    //退出视频会议
    final String MEETING_FORCE_CLOSE_ACTION="com.gkzxhn.prision.MEETING_FORCE_CLOSE_ACTION";
    //接收双流
    final String MEETING_ASSSENDSREAMSTATUSNTF_ACTION="com.gkzxhn.prision.MEETING_ASSSENDSREAMSTATUSNTF_ACTION";
    //根据呼叫状态选择是否切换界面
    final String MEETING_SWITCHVCONFVIEW_ACTION="com.gkzxhn.prision.MEETING_SWITCHVCONFVIEW_ACTION";
    //设置哑音图标
    final String MEETING_MUTEIMAGE_ACTION="com.gkzxhn.prision.MEETING_MUTEIMAGE_ACTION";
    //不设置哑音图标
    final String MEETING_NOT_MUTEIMAGE_ACTION="com.gkzxhn.prision.MEETING_NOT_MUTEIMAGE_ACTION";
    // 设置静音
    final String MEETING_QUIETIMAGE_ACTION="com.gkzxhn.prision.MEETING_QUIETIMAGE_ACTION";
    // 不设置静音
    final String MEETING_NOT_QUIETIMAGE_ACTION="com.gkzxhn.prision.MEETING_NOT_QUIETIMAGE_ACTION";
    // 刷新音视频下面的工具栏
    final String MEETING_REMOVEREQCHAIRMANHANDLER_ACTION="com.gkzxhn.prision.MEETING_REMOVEREQCHAIRMANHANDLER_ACTION";
    // 刷新音视频下面的工具栏
    final String MEETING_REMOVEREQSPEAKERHANDLER_ACTION="com.gkzxhn.prision.MEETING_REMOVEREQSPEAKERHANDLER_ACTION";

    //开始录屏
    final String START_RECORDSCREEN_ACTION="com.gkzxhn.prision.START_RECORDSCREEN_ACTION";
    //结束录屏
    final String STOP_RECORDSCREEN_ACTION="com.gkzxhn.prision.STOP_RECORDSCREEN_ACTION";
}
