package com.gkzxhn.prison.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.WindowManager;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.GKApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.view.CusPhotoFromDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2016/4/7.
 */
public  class Utils {
    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    /**
     * 显示图片来源对话框，相册/拍照
     *
     * @param context
     * @param photoFromClickListener
     */
    public static CusPhotoFromDialog buildPhotoDialog(Context context,
                                                      CusPhotoFromDialog.PhotoFromClickListener photoFromClickListener) {
        CusPhotoFromDialog dialog = new CusPhotoFromDialog(context);
        dialog.setBtnClickListener(photoFromClickListener);
        dialog.setBtnTitle(context.getResources()
                .getString(R.string.take_photo), context.getResources()
                .getString(R.string.album), context.getResources()
                .getString(R.string.cancel));
        return dialog;
    }

    public static boolean isConnected() {
        try {
            //获得网络连接服务
            ConnectivityManager connManager = (ConnectivityManager) GKApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            // State state = connManager.getActiveNetworkInfo().getState();
            // 获取WIFI网络连接状态
            NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            // 判断是否正在使用WIFI网络
            if (NetworkInfo.State.CONNECTED == state) {
                return true;
            } else {
                state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                // 判断是否正在使用GPRS网络
                return NetworkInfo.State.CONNECTED == state;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 验证手机号码的合法性
     * @param mobiles 手机号码
     * @return 返回是否为手机号码
     */
    public static boolean isPhoneNumber(String mobiles){
//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//        Matcher m = p.matcher(mobiles);
//        return m.matches();
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
    /**
     * 验证邮箱地址是否正确
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){
        boolean flag = false;
        try{
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }

        return flag;
    }
    /**
     * 处理时间
     *
     * @param timeInMillis
     */
    public static String dealTime(long timeInMillis) {
        Context mContext=GKApplication.getInstance();
        String result = "";
        if (isToday(timeInMillis)) {//今天
            result = ConvertUtil.getSystemShortTimeFormat(timeInMillis);
        } else {//非今天
            int distance = ConvertUtil.compareWithCurDate(timeInMillis);//不比较时分，只比较年月日
            result = getDateFromTimeInMillis(timeInMillis,new SimpleDateFormat("yyyy-MM-dd HH:mm"));
        }
        return result;
    }
    public static boolean isToday(long timeInMillis){
        Calendar todayCalendar=Calendar.getInstance();
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return todayCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)&&
                todayCalendar.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)&&
                todayCalendar.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH);
    }
    public static String getDateFromTimeInMillis(long timeInMillis) {
        String result="";
        if(timeInMillis>0) {
            try {
                Date date = new Date(timeInMillis);
                //英文格式时间格式化
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                result = df.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static String getDateFromTimeInMillis(long timeInMillis,SimpleDateFormat df) {
        String result="";
        if(timeInMillis>0) {
            try {
                Date date = new Date(timeInMillis);
                //英文格式时间格式化
                result = df.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static DisplayImageOptions getOptions(int defualtImgRes) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defualtImgRes)//默认加载的图片
                .showImageForEmptyUri(defualtImgRes)//下载地址不存在

                .showImageOnFail(defualtImgRes).cacheInMemory(false).cacheOnDisk(true)//加载失败的图
                //	.displayer(new RoundedBitmapDisplayer(0))  设置圆角，设置后不能使用loadimage方法，项目并不需要圆角
                .bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)    //设置图片的缩放类型，该方法可以有效减少内存的占用
                .build();
    }
    /**
     * 获取屏幕宽高
     * @return
     */
    public static int[] getScreenWidthHeight(){
        WindowManager wm = (WindowManager) GKApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[]{width, height};
    }
    //当前应用是否处于前台
    public static boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo: processes) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**获取TF卡路径
     * @return
     */
    public static String getTFPath(){
        String path=null;
        List<String> paths=getExtSDCardPathList();
        if (paths != null) {
            if (paths.size() > 1) {
                try {
                    path = paths.get(1);
                    File tempFile=new File(path+"/prisionTemp.txt");
                    boolean result;
                    if(!tempFile.exists()){
                        result=tempFile.mkdir();
                    }else{
                        result=tempFile.delete();
                    }
                    if(!result)path="";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }
    public static boolean hasSDFree(){
        return getSDFreeSize()>100;//大于100M
    }
    /**获取SD卡剩余空间
     * @return
     */
    public static long getSDFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocksLong();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }
    /**获取SD卡剩余空间
     * @return
     */
    public static boolean hasTFFree(){
        //取得SD卡文件路径
        StatFs sf = new StatFs(getTFPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocksLong();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize)/1024 /1024>100; //单位MB   磁盘大于100M
    }
    /**
     * 获取外置SD卡路径以及TF卡的路径
     * <p>
     * 返回的数据：paths.get(0)肯定是外置SD卡的位置，因为它是primary external storage.
     *
     * @return 所有可用于存储的不同的卡的位置，用一个List来保存
     */
    public static List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                String mountPath = null;
                if (line.contains("sdcard")) {
                    if ((!line.contains("fat") && !line.contains("fuse") && !line
                            .contains("storage"))
                            || line.contains("secure")
                            || line.contains("asec")
                            || line.contains("firmware")
                            || line.contains("shell")
                            || line.contains("obb")
                            || line.contains("legacy") || line.contains("data")) {
                        continue;
                    }
                    String[] parts = line.split(" ");
                    int length = parts.length;
                    if (mountPathIndex >= length) {
                        continue;
                    }
                    mountPath = parts[mountPathIndex];
                    if (!mountPath.contains("/") || mountPath.contains("data")
                            || mountPath.contains("Data")) {
                        continue;
                    }
                    File mountRoot = new File(mountPath);
                    if (!mountRoot.exists() || !mountRoot.isDirectory()
                            || !mountRoot.canWrite()) {
                        continue;
                    }
                    boolean equalsToPrimarySD = mountPath.equals(extFile
                            .getAbsolutePath());
                    if (equalsToPrimarySD) {
                        continue;
                    }
                    //扩展存储卡即TF卡或者SD卡路径
                    if (null != mountPath) {
                        paths.add(mountPath);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
