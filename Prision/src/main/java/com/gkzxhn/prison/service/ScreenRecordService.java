package com.gkzxhn.prison.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Raleigh.Luo on 17/5/16.
 */

public class ScreenRecordService extends Service {

    private static final String TAG = "ScreenRecordingService";
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    /** 是否为标清视频 */
    private boolean isVideoSd;
    /** 是否开启音频录制 */
    private boolean isAudio;

    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "Service onCreate() is called");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if(intent.getAction()!=null) {
                if (intent.getAction().equals(Constants.START_RECORDSCREEN_ACTION)) {//开始录屏
                    String rootPath = Utils.getTFPath();//TF卡路径 外置SD卡根目录
                    if (rootPath == null && Utils.hasSDFree()) {//内置SD卡根目录
                        rootPath = Environment.getExternalStorageDirectory().getPath();
                    }
                    if (rootPath != null) {
                        Log.i(TAG, "Service onStartCommand() is called");
                        mResultCode = intent.getIntExtra("code", -1);
                        mResultData = intent.getParcelableExtra("data");

                        mScreenWidth = intent.getIntExtra("width", 720);
                        mScreenHeight = intent.getIntExtra("height", 1280);
                        mScreenDensity = intent.getIntExtra("density", 1);
                        isVideoSd = intent.getBooleanExtra("quality", true);
                        isAudio = intent.getBooleanExtra("audio", true);

                        mMediaProjection = createMediaProjection();

                        mMediaRecorder = createMediaRecorder(rootPath);
                        mVirtualDisplay = createVirtualDisplay(); // 必须在mediaRecorder.prepare() 之后调用，否则报错"fail to get surface"

                        mMediaRecorder.start();
                    }
                } else if (intent.getAction().equals(Constants.STOP_RECORDSCREEN_ACTION)) {
                    if (mVirtualDisplay != null) {
                        mVirtualDisplay.release();
                        mVirtualDisplay = null;
                    }
                    if (mMediaRecorder != null) {
                        mMediaRecorder.setOnErrorListener(null);
                        mMediaProjection.stop();
                        mMediaRecorder.reset();
                    }
                    if (mMediaProjection != null) {
                        mMediaProjection.stop();
                        mMediaProjection = null;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return Service.START_NOT_STICKY;
    }
    public List<Camera.Size> getSupportedVideoSizes(Camera camera) {
        if (camera.getParameters().getSupportedVideoSizes() != null) {
            return camera.getParameters().getSupportedVideoSizes();
        } else {
            // Video sizes may be null, which indicates that all the supported
            // preview sizes are supported for video recording.
            return camera.getParameters().getSupportedPreviewSizes();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaProjection createMediaProjection() {
        Log.i(TAG, "Create MediaProjection");
        return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(mResultCode, mResultData);
    }

    private MediaRecorder createMediaRecorder(String rootPath) {
        MediaRecorder mediaRecorder=null;
        try {
            String videoQuality = "HD";
            if (isVideoSd) videoQuality = "SD";

            Log.i(TAG, "Create MediaRecorder");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            if (isAudio) {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO);
            }
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            SharedPreferences preferences= GKApplication.getInstance().
                    getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);

            String fileName=String.format("%s_%s", Utils.getDateFromTimeInMillis(System.currentTimeMillis(),new SimpleDateFormat("yyyyMMddHHmmss")),
                    preferences.getString(Constants.RECORD_VIDEO_NAME,""));

            mediaRecorder.setOutputFile(rootPath + "/" + fileName + ".mp4");
            mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);  //after setVideoSource(), setOutFormat()
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //after setOutputFormat()
            if (isAudio)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  //after setOutputFormat()
            int bitRate;
            if (isVideoSd) {
                mediaRecorder.setVideoEncodingBitRate(mScreenWidth * mScreenHeight);
                mediaRecorder.setVideoFrameRate(30);
                bitRate = mScreenWidth * mScreenHeight / 1000;
            } else {
                mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);
                mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
                bitRate = 5 * mScreenWidth * mScreenHeight / 1000;
            }
            try {
                mediaRecorder.prepare();
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.i(TAG, "Audio: " + isAudio + ", SD video: " + isVideoSd + ", BitRate: " + bitRate + "kbps");
        }catch (Exception e){
            e.printStackTrace();
        }
        return mediaRecorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        Log.i(TAG, "Create VirtualDisplay");
        return mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
        if(mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if(mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaProjection.stop();
            mMediaRecorder.reset();
        }
        if(mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
