package com.gkzxhn.prision.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.WindowManager;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.common.GKApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.view.CusPhotoFromDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2016/4/7.
 */
public  class Utils {
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
}
