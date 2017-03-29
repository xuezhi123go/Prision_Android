package com.starlight.mobile.android.lib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Raleigh on 15/9/9.
 */
public class ImageHelper {
    final int MAX_IMAGE_SIZE=100;//最大图片大小200KB
    final float MAX_IMAGE_WIDTH=720f;//最大图片width
    final float MAX_IMAGE_HEIGHT=1280f;//最大图片height
    private String compress(Bitmap image,String photoDir) {
        String path=null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > MAX_IMAGE_SIZE) {    //循环判断如果压缩后图片是否大于MAX_IMAGE_SIZE kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
                if (options < 0) {
                    options = 1;
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    break;
                }
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
            path = saveBitmap(bitmap, photoDir, options);
            bitmap.recycle();
            image.recycle();
            System.gc();
        }catch (Exception e){}
        return path;
    }

    /**图片按比例大小压缩方法（根据路径获取图片并压缩）：
     * @param srcPath
     * @return
     */
    public  String compressImage(String srcPath,String desPhotoDir) {
        String path=null;
        try{
            int degree = ViewUtil.getExifOrientation(srcPath);
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
            try {
                if (degree == 90 || degree == 180 || degree == 270) {
                    // Roate preview icon according to exif
                    // orientation
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);
                    Bitmap tBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    bitmap=tBitmap;
                    tBitmap.recycle();
                }
            }catch (Exception e){}

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = MAX_IMAGE_HEIGHT;
            float ww = MAX_IMAGE_WIDTH;
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            //缩放倍数
            newOpts.inSampleSize = be;//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            boolean isFinished=false;
            while(!isFinished) {
                try {
                    bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
                    isFinished=true;
                } catch (OutOfMemoryError e) {
                    isFinished=false;
                    newOpts.inSampleSize++;
                }
            }
            if(bitmap.getWidth()>MAX_IMAGE_WIDTH||bitmap.getHeight()>MAX_IMAGE_HEIGHT)bitmap=resizeImage(bitmap);
            path=compress(bitmap,desPhotoDir);//压缩好比例大小后再进行质量压缩
        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }
    private Bitmap resizeImage(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float newWidth = MAX_IMAGE_WIDTH;
        float newHeight = MAX_IMAGE_HEIGHT;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale=Math.min(scaleWidth, scaleHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // if you want to rotate the Bitmap
        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
        }catch (Exception e){

        }
        return bitmap;

    }

    /**图片按比例大小压缩方法（根据Bitmap图片压缩）：
     * @param image
     * @return
     */
    public  String compressImage(Bitmap image,String desPhotoDir) {
        String path=null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        if( baos.toByteArray().length / 1024>MAX_IMAGE_SIZE) {//判断如果图片大于MAX_IMAGE_SIZE KB,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
//        }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = MAX_IMAGE_HEIGHT;
            float ww = MAX_IMAGE_WIDTH;
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            path=compress(bitmap,desPhotoDir);//压缩好比例大小后再进行质量压缩
        }catch (Exception e){}
        return path;
    }
    /** 保存图片
     * @param bitmap
     * @param imageSaveDir 图片保存的路径，如/storage0/myDir/photo/
     * @param quality 图片压缩的质量，值的范围为0-100，100表示不压缩
     * @return 图片存储的完整路径
     */
    public  String saveBitmap(Bitmap bitmap, String imageSaveDir,int quality) {

        ViewUtil.createMkdir(imageSaveDir);
        String ramdom = UUID.randomUUID().toString().replace("-", "");

//        String jpegPath = imageSaveDir + ramdom + ".jpg";
        String jpegPath = imageSaveDir + ramdom;
        try {
            FileOutputStream fout = new FileOutputStream(jpegPath);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            // .i(tag, "saveJpeg：存储完毕！");
        } catch (IOException e) {
            // Log.i(tag, "saveJpeg:存储失败！");
            e.printStackTrace();
        }
        return jpegPath;
    }

}
