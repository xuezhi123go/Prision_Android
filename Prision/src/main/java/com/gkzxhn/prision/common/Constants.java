package com.gkzxhn.prision.common;

import android.os.Environment;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public interface Constants {
    /*-------------------------------Config-------------------------------------------------*/

    public final String SD_ROOT_PATH= Environment.getExternalStorageDirectory().getPath()+"/Prision";
    public final String SD_FILE_CACHE_PATH = SD_ROOT_PATH+"/cache/";

    public final String SD_IMAGE_CACHE_PATH = SD_ROOT_PATH+"/imageCache/";//图片下载的缓存
    public final String SD_ROOT_PHOTO_PATH = SD_ROOT_PATH+"/photo/";//图片，不自动删除
    public final String SD_PHOTO_PATH = SD_ROOT_PHOTO_PATH+"cutPhoto/";//拍照存储或压缩图片的图片路径,启动时自动删除
    public final String SD_AUDIO_PATH = SD_ROOT_PATH+"/audio/";

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
    /*-------------------------------msg what-------------------------------------------------*/
    public final int START_REFRESH_UI=1,STOP_REFRESH_UI=2;//msg what
    /*-------------------------------静态变量-------------------------------------------------*/
}
