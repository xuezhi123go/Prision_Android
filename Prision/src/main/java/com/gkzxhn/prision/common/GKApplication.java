package com.gkzxhn.prision.common;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.activity.LoginActivity;
import com.gkzxhn.prision.keda.sky.app.TruetouchGlobal;
import com.gkzxhn.prision.utils.KDInitUtil;
import com.gkzxhn.prision.utils.NimInitUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

public class GKApplication extends Application{
    private static GKApplication application;
    public static GKApplication getInstance() {
        return application;
    }
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoaderConfiguration config = null;
    private BaseDiskCache imageLoadCache;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        NimInitUtil.initNim();// 云信SDK相关初始化及后续操作
        KDInitUtil.init();// 科达SDK相关初始化及后续操作
        initImageLoader();
    }
    private void initImageLoader(){
        File pictureCacheDirFile = new File(Constants.SD_IMAGE_CACHE_PATH);
        if (!pictureCacheDirFile.exists()) {
            pictureCacheDirFile.mkdirs();
        }
        imageLoadCache=new BaseDiskCache(pictureCacheDirFile) {};
        config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(600, 800)// max width, max height，即保存的每个缓存文件的最大长宽
                // max width, max height
                // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75) //
                // Can slow ImageLoader, use it carefully (Better don't use it)
                .threadPoolSize(3).////线程池内加载的数量
                threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(16 * 1024 * 1024) // 50 Mb
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                ////任务线程的执行方式  后进先出法
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // You can pass your own memory cache implementation
                //自定义缓存路径
                .diskCache(imageLoadCache)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                //		  .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密 ,.new HashCodeFileNameGenerator()//使用HASHCODE对UIL进行加密命名
                // .imageDownloader(new Httpclie(5 * 1000, 20 * 1000)) //
                // connectTimeout (5 s), readTimeout (20 s)
                .defaultDisplayImageOptions(options).build();
        imageLoader.init(config);
        //	清除所有图片imageLoader.clearDiskCache();

        //下载图片ImageLoader.getInstance().displayImage(loadUri,imageView);
        //		如果经常出现OOM
        //	   ①减少配置之中线程池的大小，(.threadPoolSize).推荐1-5；
        //	   ②使用.bitmapConfig(Bitmap.config.RGB_565)代替ARGB_8888;
        //	   ③使用.imageScaleType(ImageScaleType.IN_SAMPLE_INT)或者        try.imageScaleType(ImageScaleType.EXACTLY)；
        //	   ④避免使用RoundedBitmapDisplayer.他会创建新的ARGB_8888格式的Bitmap对象；
        //	   ⑤使用.memoryCache(new WeakMemoryCache())，不要使用.cacheInMemory();
    }
    /**获取文件的缓存工具类
     * 通过url地址获取本地图片文件，通过文件就可以得到文件的路径 imageLoadCache.get(imageUri)
     * @return
     */
    public BaseDiskCache getImageLoadCache(){
        return imageLoadCache;
    }
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_imageloading)//默认加载的图片
            .showImageForEmptyUri(R.mipmap.ic_imageload_failed)//下载地址不存在

            .showImageOnFail(R.mipmap.ic_imageload_failed).cacheInMemory(false).cacheOnDisk(true)//加载失败的图
            //	.displayer(new RoundedBitmapDisplayer(0))  设置圆角，设置后不能使用loadimage方法，项目并不需要圆角
            .bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)    //设置图片的缩放类型，该方法可以有效减少内存的占用
            .build();


    public void loginOff(){
        NIMClient.getService(AuthService.class).logout();
//        TruetouchGlobal.logOff();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
