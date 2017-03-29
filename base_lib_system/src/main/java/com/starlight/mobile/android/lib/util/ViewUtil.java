package com.starlight.mobile.android.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**控件资源工具类
 * @author raleigh
 *
 */
public class ViewUtil {

	/**缩放图片大小
	 * @param path图片本地路径
	 * @return
	 * 
	 */
	public static Bitmap fitSizeImg(String path) {
		if(path == null || path.length()<1 ) return null;
		File file = new File(path);
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new   BitmapFactory.Options();
		// 数字越大读出的图片占用的heap越小 不然总是溢出
		if (file.length() < 20480) {       // 0-20k
			opts.inSampleSize = 1;
		} else if (file.length() < 51200) { // 20-50k
			opts.inSampleSize = 2;
		} else if (file.length() < 307200) { // 50-300k
			opts.inSampleSize = 4;
		} else if (file.length() < 819200) { // 300-800k
			opts.inSampleSize = 6;
		} else if (file.length() < 1048576) { // 800-1024k
			opts.inSampleSize = 8;
		} else {
			opts.inSampleSize = 10;
		}
		resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		return resizeBmp;
	}
	/**等比例缩放图片
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap scalingBmp(String path, int width, int height)
	{
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, op);

		// 编码后bitmap的宽高,bitmap除以屏幕宽度得到压缩比
		int widthRatio = (int) Math.ceil(op.outWidth / (float) width);
		int heightRatio = (int) Math.ceil(op.outHeight / (float) height);

		if (widthRatio > 1 && heightRatio > 1)
		{
			if (widthRatio > heightRatio)
			{
				// 压缩到原来的(1/widthRatios)
				op.inSampleSize = widthRatio;
			} else
			{
				op.inSampleSize = heightRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, op);
		return bmp;
	}

	/**等比例缩放图片
	 * @param res  getResources()
	 * @param id   图片的id,如:R.drawable.ic_lancher
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap scalingBmp(Resources res,int id, int width, int height)
	{
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeResource(res,id, op);

		// 编码后bitmap的宽高,bitmap除以屏幕宽度得到压缩比
		int widthRatio = (int) Math.ceil(op.outWidth / (float) width);
		int heightRatio = (int) Math.ceil(op.outHeight / (float) height);

		if (widthRatio > 1 && heightRatio > 1)
		{
			if (widthRatio > heightRatio)
			{
				// 压缩到原来的(1/widthRatios)
				op.inSampleSize = widthRatio;
			} else
			{
				op.inSampleSize = heightRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bmp=BitmapFactory.decodeResource(res, id, op);
		return bmp;
	}

	/**等比例缩放图片,通过Matrix缩放
	 * @param bm
	 * @param width
	 * @param height
	 * @return
	 */
	public  static Bitmap scalingBmp(Bitmap bm,int width,int height){
		// 获得图片的宽高
		int mWidth = bm.getWidth();
		int mHeight = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) width) / mWidth;
		float scaleHeight = ((float) height) / mHeight;
		float scale=scaleWidth>scaleHeight?scaleHeight:scaleWidth;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, mWidth, mHeight, matrix, true);
		return newbm;
	}
	public static void createMkdir(String mkdirPath){
		File folder = new File(mkdirPath);
		if (!folder.exists()) // 如果文件夹不存在则创建
		{
			folder.mkdirs();
		}	
	}
	public static int dipToPx(Context context, int dip) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return (int) px;
	}

	/**
	 * @param context
	 * @param view
	 * @param colorId
	 *            getResouce.getColor()
	 */
	public static void setStateLeftColor(Context context, TextView view,
			int colorId) {
		int r = dipToPx(context, 8);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); // 组合圆角矩形和ShapeDrawable
		shapeDrawable.getPaint().setColor(colorId); // 设置形状的颜色
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		shapeDrawable.setBounds(0, 0, dipToPx(context, 10),
				dipToPx(context, 10));
		view.setCompoundDrawablePadding(dipToPx(context, 5));// 边距
		view.setCompoundDrawables(shapeDrawable, null, null, null); // 设置左图标
	}

	/**
	 * --设置Textview的左图标
	 * 
	 * @param context
	 * @param view
	 * @param colorId
	 *            getResouce.getColor()
	 * @param size
	 *            大小
	 */
	public static void setStateLeftColor(Context context, TextView view,
			int colorId, int size) {
		int r = dipToPx(context, 8);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); // 组合圆角矩形和ShapeDrawable
		shapeDrawable.getPaint().setColor(colorId); // 设置形状的颜色
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		shapeDrawable.setBounds(0, 0, dipToPx(context, size),
				dipToPx(context, size));
		view.setCompoundDrawablePadding(dipToPx(context, 5));// 边距
		view.setCompoundDrawables(shapeDrawable, null, null, null); // 设置左图标
	}

	/**
	 * --设置Textview的右图标
	 * 
	 * @param context
	 * @param view
	 * @param colorId
	 *            getResouce.getColor()
	 */
	public static void setStateRightColor(Context context, TextView view,
			int colorId) {
		int r = dipToPx(context, 8);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); // 组合圆角矩形和ShapeDrawable
		shapeDrawable.getPaint().setColor(colorId); // 设置形状的颜色
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		shapeDrawable.setBounds(0, 0, dipToPx(context, 10),
				dipToPx(context, 10));
		view.setCompoundDrawablePadding(dipToPx(context, 5));// 边距
		view.setCompoundDrawables(null, null, shapeDrawable, null); // 设置左图标
	}

	/**
	 * 设置View圆形背景
	 */
	public static void setOvalBackGroundColor(Context context, View view,
			int colorId) {
		int r = dipToPx(context, 8);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape); // 组合圆角矩形和ShapeDrawable
		shapeDrawable.getPaint().setColor(colorId); // 设置形状的颜色
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		shapeDrawable.setBounds(0, 0, dipToPx(context, 15),
				dipToPx(context, 15));
		view.setBackgroundDrawable(shapeDrawable); // 设置左图标
	}

	public static String getAbsolutePathFromURI(Uri contentUri,
			Activity activity) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	// /**处理图片为圆角图片,适用于长宽相差不大的图片
	// * @param bitmap 以最长的一边为边长
	// * @return
	// */
	// public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	// bitmap.getHeight(), Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xff424242;
	// final Paint paint = new Paint();
	// int side=
	// bitmap.getWidth()>bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
	//
	// final Rect rect = new Rect(0, 0, side, side);
	// final RectF rectF = new RectF(rect);
	// paint.setAntiAlias(true);
	// canvas.drawARGB(0, 0, 0, 0);
	// paint.setColor(color);
	// canvas.drawOval(rectF, paint);
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	// canvas.drawBitmap(bitmap, rect, rect, paint);
	//
	// return output;
	// }
	// /**处理图片为圆角图片,适用于长宽相差不大的图片
	// * @param bitmap 以最短的一边为边长
	// * @return
	// */
	// public static Bitmap getRoundedCornerBitmap2(Bitmap bitmap) {
	// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	// bitmap.getHeight(), Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xff424242;
	// final Paint paint = new Paint();
	// int side=
	// bitmap.getWidth()>bitmap.getHeight()?bitmap.getHeight():bitmap.getWidth();
	// final Rect rect = new Rect(0, 0, side, side);
	// final RectF rectF = new RectF(rect);
	// paint.setAntiAlias(true);
	// canvas.drawARGB(0, 0, 0, 0);
	// paint.setColor(color);
	// canvas.drawOval(rectF, paint);
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	// canvas.drawBitmap(bitmap, rect, rect, paint);
	//
	// return output;
	// }
	/**
	 * 处理图片为圆角图片,适用于长宽相差交大的图片
	 * 
	 * @param bitmap
	 *            以最短的一边为边长
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap3(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 
	 */
	public static boolean isImage(String imagePath) {
		try {
			File imageFile = new File(imagePath);
			if (imageFile.length() == 0) {
				return false;
			} else {
				Bitmap bitmap;
				Drawable drawable = Drawable.createFromPath(imagePath);
				BitmapDrawable bd = (BitmapDrawable) drawable;
				bitmap = bd.getBitmap();
				// image = Image.From
				if (bitmap.getHeight() > 0 && bitmap.getWidth() > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return false;
	}

	public static Display getScreenDisplay(Context context) {

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display;
	}

	/**
	 * @param bitmap
	 * @param imagePath
	 *            ，如Constants.SD_PHOTO_IMG_PATH
	 */
	/**此方法已过期，请使用ImageHelper中的saveBitmap方法
	 * @param bitmap
	 * @param imageSaveDir 图片保存的路径，如/storage0/myDir/photo/
	 * @param quality 图片压缩的质量，值的范围为0-100，100表示不压缩
	 * @return 图片存储的完整路径
	 */
	@Deprecated
	public static String saveBitmap(Bitmap bitmap, String imageSaveDir,int quality) {

		createMkdir(imageSaveDir);
		String ramdom = UUID.randomUUID().toString().replace("-", "");

		String jpegPath = imageSaveDir + ramdom
				+ ".jpg";

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


	public static String imgToBase64(String imgPath) {

		Bitmap bitmap = null;
		if (imgPath != null && imgPath.length() > 0) {
			bitmap = readBitmap(imgPath);
		}
		if (bitmap == null) {
			// bitmap not found!!
			return null;
		}
		//旋转角度
		int degree=getExifOrientation(imgPath);
		if(degree == 90 || degree == 180 || degree == 270){
			//Roate preview icon according to exif orientation
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			bitmap = Bitmap.createBitmap(bitmap,
					0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		} 
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static Bitmap readBitmap(String imgPath) {
		try {
			return BitmapFactory.decodeFile(imgPath);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 设置ListView的高度（动态的）  
	 * */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		int count=listAdapter.getCount();
		params.height = totalHeight+ (listView.getDividerHeight() * count);
		listView.setLayoutParams(params);
	}



	public  static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;

		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			// MmsLog.e(ISMS_TAG, "getExifOrientation():", ex);
		}

		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				// We only recognize a subset of orientation tag values.
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				default:
					break;
				}
			}
		}

		return degree;
	}
	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 800, 600);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static Bitmap getSmallBitmap(String filePath, int width, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, width, width);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 加载图片，防止内存溢出
	 * 
	 * @param pathName
	 *            图片路径
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {
		// 获取尺寸
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(pathName, options);
	}

	public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = newWidth / width;
		float scaleHeight = newHeight / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 计算图片变化后于原图的比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 图像原始高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * 压缩图片质量
	 * 
	 * @param image
	 *            图片
	 * @return 压缩后的图片文件
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 获取图片
	 * 
	 * @param srcPath
	 *            图片路径
	 * @return 返回图片
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 压缩图片
	 * 
	 * @param image
	 *            图片文件
	 * @return 返回压缩后的图片文件
	 */
	public static Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/*
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		if (null == bitmap) {
			return null;
		}
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
}
