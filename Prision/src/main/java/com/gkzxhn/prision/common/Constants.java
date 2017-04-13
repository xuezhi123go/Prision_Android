package com.gkzxhn.prision.common;

import android.os.Environment;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public interface Constants {
    /*-------------------------------Config-------------------------------------------------*/

    final String SD_ROOT_PATH= Environment.getExternalStorageDirectory().getPath()+"/Prision";
    final String SD_FILE_CACHE_PATH = SD_ROOT_PATH+"/cache/";

    final String SD_IMAGE_CACHE_PATH = SD_ROOT_PATH+"/imageCache/";//图片下载的缓存
    final String SD_ROOT_PHOTO_PATH = SD_ROOT_PATH+"/photo/";//图片，不自动删除
    final String SD_PHOTO_PATH = SD_ROOT_PHOTO_PATH+"cutPhoto/";//拍照存储或压缩图片的图片路径,启动时自动删除
    final String SD_AUDIO_PATH = SD_ROOT_PATH+"/audio/";

    /*-------------------------------User Tab-------------------------------------------------*/
    final String USER_TABLE="user_table";
    final String USER_IS_UNAUTHORIZED="isUnauthorized";
    final String USER_ID="user_id";
    final String IS_FIRST_IN="is_first_in";
    final String USER_ACCOUNT="user_account";//云信帐号
    final String USER_PASSWORD="user_password";//云信密码
    final String TERMINAL_ACCOUNT="terminal_account";//终端帐号
    final String TERMINAL_RATE="terminal_rate";//终端码率
    final String LAST_IGNORE_VERSION="last_ignore_version";//上一个忽略的版本
    final String OTHER_CARD="other_card";//身份证信息
    /*-------------------------------request url-------------------------------------------------*/
    final String RELEASE_DOMAIN="https://www.fushuile.com";//发布正式环境
    final String DEMO_DOMAIN="";//开发环境
    final String DOMAIN_NAME_XLS = RELEASE_DOMAIN;

    final String REQUEST_MEETING_LIST_URL=DOMAIN_NAME_XLS+"/api/v1/terminals";//会见列表
    final String REQUEST_CANCEL_MEETING_URL=DOMAIN_NAME_XLS+"/api/v1/meetings";// 取消会见


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
    /*-------------------------------静态变量-------------------------------------------------*/
}
