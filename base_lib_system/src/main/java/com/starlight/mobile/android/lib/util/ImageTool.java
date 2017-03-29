package com.starlight.mobile.android.lib.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

import java.io.File;


/**图片处理
 * @author raleigh
 *
 */
public class ImageTool {
	private  static int MAX_NUM_PIXELS = 320 * 480;
	private  static int MIN_SIDE_LENGTH = 320;
	private final static int LARGER_IMAGE_MAX_SIDE_LENGTH = 720*1280;//大图的比例
	private final static int LARGER_IMAGE_MIN_SIDE_LENGTH = 720;

	/**生成图片的压缩图
	 * @param context
	 * @param filePath
	 * @param scale 小于1的倍数，默认值为0.5，如scale=0.3f，屏幕的0.3倍
	 * @return
	 */
	public static Bitmap createImageThumbnail(Context context,String filePath,float scale) {
		if (null == filePath || !new File(filePath).exists())
			return null;
		if(scale>1||scale<=0)scale=0.5f;
		Bitmap bitmap = null;
		int degree = ViewUtil.getExifOrientation(filePath);
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		//绝对宽度与高度
		int maxWidth=(int)(dm.widthPixels*scale);
		int maxHeight=(int)(dm.heightPixels*scale);
		MIN_SIDE_LENGTH=maxWidth>maxHeight?maxHeight:maxWidth;
		MAX_NUM_PIXELS=maxWidth*maxHeight;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, MAX_NUM_PIXELS,MIN_SIDE_LENGTH);
			opts.inJustDecodeBounds = false;
			//            if (opts.inSampleSize == 1) {
			//                bitmap = BitmapFactory.decodeFile(filePath, opts);
			//
			//            } else {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
			
			//            }
		} catch (Exception e) {
			return null;
		}
		Bitmap newBitmap = ViewUtil.rotaingImageView(degree, bitmap);
		return newBitmap;
	}
	/**
	 * 
	 * @Description 生成图片的大图
	 * @param filePath
	 * @return
	 */
	public static Bitmap createImageBmp(String filePath) {
		if (null == filePath || !new File(filePath).exists())
			return null;
		Bitmap bitmap = null;
		int degree = ViewUtil.getExifOrientation(filePath);
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, LARGER_IMAGE_MAX_SIDE_LENGTH,LARGER_IMAGE_MIN_SIDE_LENGTH);
			opts.inJustDecodeBounds = false;
			//            if (opts.inSampleSize == 1) {
			//                bitmap = BitmapFactory.decodeFile(filePath, opts);
			//
			//            } else {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
			//            }
		} catch (Exception e) {
			return null;
		}
		Bitmap newBitmap = ViewUtil.rotaingImageView(degree, bitmap);
		return newBitmap;
	}
	 
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels,int SystemMinSideLength) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels,SystemMinSideLength);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels,int SystemMinSideLength) {
		if(SystemMinSideLength<MIN_SIDE_LENGTH)SystemMinSideLength=MIN_SIDE_LENGTH;
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1)
				? 1
						: (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1)
				? SystemMinSideLength
						: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h
								/ minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap getBigBitmapForDisplay(String imagePath,
			Context context) {
		if (null == imagePath || !new File(imagePath).exists())
			return null;
		try {
			int degeree = ViewUtil.getExifOrientation(imagePath);
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			if (bitmap == null)
				return null;
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
			float scaleW = bitmap.getWidth() / (float) dm.widthPixels;
			float scaleH = bitmap.getHeight() / (float) dm.heightPixels;
			float scale=scaleW>scaleH?scaleW:scaleH;
			Bitmap newBitMap = null;
			if (scale > 1) {
				newBitMap = zoomBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale));
				bitmap.recycle();
				Bitmap resultBitmap = ViewUtil.rotaingImageView(degeree, newBitMap);
				return resultBitmap;
			}
			Bitmap resultBitmap = ViewUtil.rotaingImageView(degeree, bitmap);
			return resultBitmap;
		} catch (Exception e) {
			return null;
		}
	}

	private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (null == bitmap) {
			return null;
		}
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / w);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			return newbmp;
		} catch (Exception e) {
			return null;
		}
	}

}
