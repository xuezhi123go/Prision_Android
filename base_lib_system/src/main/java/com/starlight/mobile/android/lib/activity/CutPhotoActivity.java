package com.starlight.mobile.android.lib.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;

import com.starlight.mobile.android.lib.R;
import com.starlight.mobile.android.lib.album.AlbumActivity;
import com.starlight.mobile.android.lib.util.ImageHelper;
import com.starlight.mobile.android.lib.view.CutPhotoView;

import java.io.File;

/**
 * Created by Raleigh on 15/8/19.
 * 剪切图片，并返回图片剪切后保存的路径
 * AlbumActivity.EXTRAS
 */
public class CutPhotoActivity extends Activity {
    private CutPhotoView cpCutPhoto;
    private String saveImageDir;
    private ProgressDialog mProgressDialog;
    private String imagePath,originPath;
    private final int LOADING_CODE=1,CUTTING_CODE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cut_photo_layout);
        init();
    }
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if(msg.what==CUTTING_CODE) {
                mProgressDialog.dismiss();
                if (imagePath != null) {
                    Intent intent = new Intent();
                    intent.putExtra(AlbumActivity.EXTRAS, imagePath);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }else if(msg.what==LOADING_CODE){
                mProgressDialog.dismiss();
                cpCutPhoto.setImagePath(originPath);
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        cpCutPhoto.invalidate();
    }
    private void init() {
        cpCutPhoto = (CutPhotoView) findViewById(R.id.cut_photo_layout_cp_cut);
        if(mProgressDialog!=null&&mProgressDialog.isShowing())mProgressDialog.dismiss();
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.album_loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveImageDir = Environment.getExternalStorageDirectory().getPath();
                Intent data = getIntent();
                final Uri imageUri = data.getData();
                if (data.hasExtra(AlbumActivity.EXTRAS))
                    saveImageDir = data.getStringExtra(AlbumActivity.EXTRAS);
                if (new File(imageUri.getPath()).exists()) {
                    originPath=imageUri.getPath();
                } else {
                    originPath = getRealPathFromURI(imageUri);
                }
                mHandler.sendEmptyMessage(LOADING_CODE);
            }
        }).start();
    }



    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void onClickListener(View v) {
        if (v.getId() == R.id.common_head_layout_iv_left) {
            this.finish();
        } else if (v.getId() == R.id.common_head_layout_tv_right) {//确定
            if(mProgressDialog!=null&&mProgressDialog.isShowing())mProgressDialog.dismiss();
            mProgressDialog = ProgressDialog.show(this, null, getString(R.string.album_cutting));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = cpCutPhoto.clip();
                        imagePath = new ImageHelper().compressImage(bitmap, saveImageDir);
                        mHandler.sendEmptyMessage(CUTTING_CODE);
                    }  catch (Exception e){
                        e.printStackTrace();
                        imagePath=null;
                        mHandler.sendEmptyMessage(CUTTING_CODE);
                    }
                }
            }).start();

        }
    }

    @Override
    protected void onDestroy() {
        cpCutPhoto.onDestory();
        super.onDestroy();
    }
}
