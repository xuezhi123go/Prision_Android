package com.gkzxhn.prision.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;


import com.gkzxhn.prision.common.Constants;
import com.starlight.mobile.android.lib.util.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * Created by Raleigh.Luo on 17/3/14.
 */

public class DownLoadHelper {
    private DownloadFinishListener listener;
    private DownloadAsyn downloadAsyn;
    private String mUrl;
    private String filePath=null;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                int currentSize=msg.arg1;
                int size=msg.arg2;
                if(listener!=null) listener.onProgress(currentSize,size);
            }
        }
    };


    public DownLoadHelper(){

    }


    public void setListener(DownloadFinishListener listener) {
        this.listener = listener;
    }

    public void download(String filePath){
        this.mUrl=filePath;
        startAsynTask();
    }

    public void onStop(){
        this.listener=null;
        if (downloadAsyn != null) {
            if (downloadAsyn.getStatus() == AsyncTask.Status.RUNNING) downloadAsyn.cancel(true);
            downloadAsyn = null;
        }
    }
    /**
     * 鍚姩寮傛浠诲姟
     *
     */
    private void startAsynTask() {
        try {
            if (downloadAsyn != null) {
                if (downloadAsyn.getStatus() == AsyncTask.Status.RUNNING) downloadAsyn.cancel(true);
                downloadAsyn = null;
            }
            downloadAsyn = new DownloadAsyn();
            downloadAsyn.setOnAsynFinishListener(onAsynFinishListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                downloadAsyn.executeOnExecutor(Executors.newCachedThreadPool());
            } else {
                downloadAsyn.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private OnAsynFinishListener onAsynFinishListener=new OnAsynFinishListener() {
        @Override
        public void onFinish(int code) {
            try {
                if(code== HttpStatus.SC_CREATED||code== HttpStatus.SC_OK)
                {
                    listener.onSuccess(filePath);
                }else if(code==HttpStatus.SC_REQUEST_TIMEOUT){
                    startAsynTask();
                }else{
                    listener.onFailed(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed(null);
            }
        }

        @Override
        public void onProgress(int currentSize,int totalSize) {
            listener.onProgress(currentSize, totalSize);
        }
    };

    public interface DownloadFinishListener {
        public void onSuccess(String filePath);
        public void onFailed(String error);
        public void onProgress(int currentSize, int totalSize);
    }
    class DownloadAsyn extends AsyncTask<Void, Integer, Integer> {

        private OnAsynFinishListener listener;
        public void setOnAsynFinishListener(OnAsynFinishListener listener){
            this.listener=listener;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            int responseCode=0;
            File rootFile = new File(Constants.SD_ROOT_PATH);
            if (!rootFile.exists()) {
                rootFile.mkdir();
            }
            File tmpFile = new File(Constants.SD_FILE_CACHE_PATH);
            if (!tmpFile.exists()) {
                tmpFile.mkdir();
            }
            final File file = new File(Constants.SD_FILE_CACHE_PATH + "/" + "app.apk");
            filePath=file.getAbsolutePath();
            try {
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                InputStream inputStream = connection.getInputStream();
                int size = connection.getContentLength();//文件大小
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                connection.connect();
                Long starttime = System.currentTimeMillis();
                while (true) {
                    if (inputStream != null) {
                        int numRead = inputStream.read(buffer);
                        if (numRead <= 0) {
                            break;
                        } else {
                            fos.write(buffer, 0, numRead);
                            int currentSize=(int) file.length();
                            Message message=new Message();
                            message.what=1;
                            message.arg1=currentSize;
                            message.arg2=size;
                            handler.sendMessage(message);
                        }
                    }
                }
                responseCode=connection.getResponseCode();
                connection.disconnect();
                fos.close();
                inputStream.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            try {
                if(listener!=null)listener.onFinish(responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public interface OnAsynFinishListener{
        public void onFinish(int responseCode);
        public void onProgress(int currentSize, int totalSize);
    };

}
