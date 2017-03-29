package com.starlight.mobile.android.lib.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class CommonHelper {
	/**
	 * Check the double value is equal 0
	 * @param arg1 double value
	 * @return  value
	 */
	public static boolean isDoubleEqualZero(double arg1){
		boolean result=false;
		if(Math.abs(arg1-0)<0.0005){
			result=true;
		}
		return result;
	}
	/**
	 * 创建文件
	 *
	 * 如果是/sdcard/download/123.doc则只需传入filePath=download/123.doc
	 *
	 * @param filePath
	 *          文件路径
	 * @return 创建文件的路径
	 * @throws IOException
	 */
	public static String creatFileToSDCard(String filePath) throws IOException {
		// 无论传入什么值 都是从根目录开始 即/sdcard/+filePath
		// 创建文件路径包含的文件夹
		String sdPath="";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			sdPath=Environment.getExternalStorageDirectory().toString() + "/";
		}
		String dir="";
		if (filePath.startsWith(sdPath)) {
			dir= filePath.replace(getFileName(filePath), "");
		}else{
			dir=sdPath + filePath.replace(getFileName(filePath), "");
		}
		String filedir = creatDirToSDCard(dir);
		String fileFinalPath = filedir + getFileName(filePath);
		File file = new File(fileFinalPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		return fileFinalPath;
	}
	/**
	 * 获取文件名
	 *
	 * @param filePath
	 * @return
	 */
	private static String getFileName(String filePath) {
		int index = 0;
		String tempName = "";
		if ((index = filePath.lastIndexOf("/")) != -1) {
			// 如果有后缀名才
			tempName = filePath.substring(index + 1);
		}
		return tempName.contains(".") ? tempName : "";
	}

	/**
	 * 创建文件夹
	 *
	 * @param dirPath
	 */
	public static String creatDirToSDCard(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dirPath;
	}


	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				/*
				BACKGROUND=400 EMPTY=500 FOREGROUND=100
				GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
				//				Log.i(context.getPackageName(), "此appimportace ="
				//						+ appProcess.importance
				//						+ ",context.getClass().getName()="
				//						+ context.getClass().getName());
				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					//					Log.i(context.getPackageName(), "处于后台"
					//							+ appProcess.processName);
					return true;
				} else {
					//					Log.i(context.getPackageName(), "处于前台"
					//							+ appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	/**关闭虚拟键盘
	 * @param  activity
	 */
	public static void clapseSoftInputMethod(Activity activity){

		try{//activity
			InputMethodManager inputMethodManager = (InputMethodManager)
					activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if(inputMethodManager.isActive())//键盘是打开的状态
				inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		}catch(Exception e){
		}
	}
	/**
	 * 显示软键盘
	 */
	public static void showSoftInputMethod(Context context, View v) {
		try{
			if (v != null) {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
			}
		}catch(Exception e){
		}
	}

	/**虚拟键盘是否已经打开
	 * @param activity
	 */
	public static boolean softInputIsOpened(Activity activity){
		InputMethodManager inputMethodManager = (InputMethodManager)
				activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		return inputMethodManager.isActive();//键盘是打开的状态
	}

	/**
	 * @Description 判断存储卡是否存在
	 * @return
	 */
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}

		return false;
	}




	/**
	 * 写入图片到sdcard
	 *
	 * @param b
	 * @param destDirStr
	 * @param file
	 */
	public static void writeFile(byte[] b, String destDirStr, String file) {
		// 获取扩展SD卡设备状态
		String sDStateString = Environment.getExternalStorageState();
		File myFile = null;
		// 拥有可读可写权限
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 获取扩展存储设备的文件目录
				File SDFile = Environment
						.getExternalStorageDirectory();
				File destDir = new File(SDFile.getAbsolutePath()
						+ File.separator + destDirStr);
				if (!destDir.exists())
					destDir.mkdirs();
				// 打开文件
				myFile = new File(destDir + File.separator + file);
				// 判断是否存在,不存在则创建
				if (!myFile.exists()) {
					myFile.createNewFile();
				} else {
					myFile.delete();
				}
				// 写数据
				FileOutputStream outputStream = new FileOutputStream(myFile);
				outputStream.write(b);
				outputStream.flush();
				outputStream.close();
				outputStream = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				b = null;
			}
		}
	}

	/**
	 * 直接将字符串文本写入到文件中
	 *
	 * @param content
	 *            文本文件
	 * @param destDirStr
	 *            目标目录
	 * @param file
	 */
	public static void writeFile(String content, String destDirStr, String file) {
		// 获取扩展SD卡设备状态
		String sDStateString = Environment.getExternalStorageState();
		File myFile = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;

		// 拥有可读可写权限
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 获取扩展存储设备的文件目录
				File SDFile = Environment
						.getExternalStorageDirectory();
				File destDir = new File(SDFile.getAbsolutePath()
						+ File.separator + destDirStr);
				if (!destDir.exists())
					destDir.mkdirs();
				// 打开文件
				myFile = new File(destDir + File.separator + file);
				// 判断是否存在,不存在则创建
				if (!myFile.exists()) {
					myFile.createNewFile();
				} else {
					myFile.delete();
				}
				bufferedReader = new BufferedReader(new StringReader(content));
				FileOutputStream writerStream = new FileOutputStream(
						myFile);
				bufferedWriter = new BufferedWriter(
						new java.io.OutputStreamWriter(writerStream, "utf-8"));
				char buf[] = new char[1024]; // 字符缓冲区
				int len;
				while ((len = bufferedReader.read(buf)) != -1) {
					bufferedWriter.write(buf, 0, len);
				}
				bufferedWriter.flush();
				bufferedReader.close();
				bufferedWriter.close();
				bufferedReader = null;
				bufferedWriter = null;
				// 写数据
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入图片到sdcard
	 *
	 * @param b
	 * @param destDirStr
	 * @param file
	 */
	public static void writeFileAppend(byte[] b, String fileFullPath,
									   boolean isAppend) {
		// 获取扩展SD卡设备状态
		String sDStateString = Environment.getExternalStorageState();
		File myFile = null;
		// 拥有可读可写权限
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 获取扩展存储设备的文件目录
				String fileDir = fileFullPath.substring(0,
						fileFullPath.lastIndexOf("/") + 1);
				File dir = new File(fileDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 打开文件
				myFile = new File(fileFullPath);
				// 判断是否存在,不存在则创建
				if (!myFile.exists()) {
					myFile.createNewFile();
				}
				// 写数据
				FileOutputStream outputStream = new FileOutputStream(myFile,
						isAppend);
				outputStream.write(b);
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				//				CommonHelper.log("写入文件" + fileFullPath + "出错");
				e.printStackTrace();
			}
		} else {
			//			CommonHelper.log("写入文件" + fileFullPath + "无权限");
		}
	}

	public static void writeSegmentFile(Context context, byte[] b,
										String fileFullPath, long fileFullLen) {
		// 获取扩展SD卡设备状态
		String sDStateString = Environment.getExternalStorageState();
		File myFile = null;
		// 拥有可读可写权限
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 获取扩展存储设备的文件目录
				String fileDir = fileFullPath.substring(0,
						fileFullPath.lastIndexOf("/") + 1);
				File dir = new File(fileDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				boolean isAppend = false;
				myFile = new File(fileFullPath);
				if (myFile.exists()) {
					isAppend = true;
				} else {
					myFile.createNewFile();
				}
				if (myFile != null) {
					FileOutputStream outputStream = new FileOutputStream(
							myFile, isAppend);
					outputStream.write(b);
					outputStream.flush();
					outputStream.close();
					// xml存储完整文件大小
					writeFileSize(context, fileFullPath, fileFullLen);
				}
			} catch (Exception e) {
				//				CommonHelper.log("写入文件" + fileFullPath + "出错");
				e.printStackTrace();
			}
		} else {
			//			CommonHelper.log("写入文件" + fileFullPath + "无权限");

		}
	}

	public static boolean writeFileSize(Context c, String key, long value) {
		SharedPreferences settings = c.getSharedPreferences("file",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	public static long readFileSize(Context c, String key) {
		SharedPreferences settings = c.getSharedPreferences("file",
				Context.MODE_PRIVATE);
		return settings.getLong(key, -1);
	}

	/**
	 * 删除所有文件，目录或者文件都适用，递归删除所有层级的文件或者文件夹
	 *
	 * @param file
	 * @throws Exception
	 */
	public static void deleteAllFile(File file) throws Exception {
		if (file.exists()) {
			if (file.isFile()) {
				// 判断是否是文件
				file.delete();
			} else if (file.isDirectory()) {
				// 否则如果它是一个目录
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					// 遍历目录下所有的文件
					deleteAllFile(files[i]);
				}
			}
			file.delete();
		} else {
			throw new Exception("要删除的文件" + file + "不存在！");
		}
	}

	/**
	 * 根据图片路径读取字节流
	 *
	 * @param filePath
	 *            文件路径
	 * @return
	 * @throws Exception
	 */
	public static byte[] getFileByte(String filePath) throws Exception {
		// 获取扩展SD卡设备状态
		String sDStateString = Environment.getExternalStorageState();
		File myFile = null;
		// 拥有可读可写权限
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 打开文件
				myFile = new File(filePath);
				// 判断是否存在,不存在则创建
				if (!myFile.exists()) {
					throw new Exception("image file not found!");
				}
				long len = myFile.length();
				byte[] bytes = new byte[(int) len];

				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						new FileInputStream(myFile));
				int r = bufferedInputStream.read(bytes);
				bufferedInputStream.close();
				if (r != len) {
					throw new IOException("error read image file");
				}
				return bytes;

			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("没有读写权限");
		}
	}

	/**
	 * 获取文件夹下面的所以文件
	 *
	 * @param directory
	 * @param lstFiles
	 */
	public static void getAllFiles(File directory, List<File> lstFiles) {
		File files[] = directory.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					lstFiles.add(f);
					getAllFiles(f, lstFiles);
				} else {
					lstFiles.add(f);
				}
			}
		}
	}






	/**
	 * voice to String
	 *
	 * @param filePath
	 *            file path
	 * @return BASE64 coded
	 */
	public static String voiceToString(String filePath) {
		File file = new File(filePath);
		byte[] buffer = new byte[(int) file.length()];
		try {
			FileInputStream inputFile = new FileInputStream(file);
			inputFile.read(buffer);
			inputFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeToString(buffer, Base64.DEFAULT);
	}
	private static int px2dip(float pxValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public static int getElementSzie(Context context) {
		if (context != null) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			int screenHeight = px2dip(dm.heightPixels, context);
			int screenWidth = px2dip(dm.widthPixels, context);
			int size = screenWidth / 6;
			if (screenWidth >= 800) {
				size = 60;
			} else if (screenWidth >= 650) {
				size = 55;
			} else if (screenWidth >= 600) {
				size = 50;
			} else if (screenHeight <= 400) {
				size = 20;
			} else if (screenHeight <= 480) {
				size = 25;
			} else if (screenHeight <= 520) {
				size = 30;
			} else if (screenHeight <= 570) {
				size = 35;
			} else if (screenHeight <= 640) {
				if (dm.heightPixels <= 960) {
					size = 35;
				} else if (dm.heightPixels <= 1000) {
					size = 45;
				}
			}
			return size;
		}
		return 40;
	}
	public static int getImageMessageItemMinWidth(Context context) {
		return getElementSzie(context) * 3;
	}
	public static int getImageMessageItemMinHeight(Context context) {
		return getElementSzie(context) * 3;
	}
	public static int getImageMessageItemDefaultWidth(Context context) {
		return getElementSzie(context) * 5;
	}

	public static int getImageMessageItemDefaultHeight(Context context) {
		return getElementSzie(context) * 7;
	}


}
