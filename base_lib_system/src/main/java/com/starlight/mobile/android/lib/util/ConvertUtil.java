package com.starlight.mobile.android.lib.util;

import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**日期转换--工具类
 *
 * yyyy/MM/dd HH:mm:ss
 *
 *
 * @author raleigh
 *
 */
public class ConvertUtil {

	/**
	 * 获取当前系统时间
	 * @return  long型的毫秒数
	 */
	public static long getCurrentTime() {
		Date data=new Date();
		return data.getTime();
	}
	/**
	 *
	 * @return 当前系统时间：格式为yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTimeFormate() {
		SimpleDateFormat dataFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		return dataFormat.format(new Date());
	}

	/**毫秒数转为date,有指定格式
	 * @param dateTime 时间毫秒数
	 * @param formate，日期格式如yyyy/MM/dd，可为null，默认为yyyy/MM/dd HH:mm:ss
	 * @return
	 */
	public static String timeInMillisToDateFormate(long timeInMillis,String formate) {
		String result="";
		try {
			Date date = new Date(timeInMillis);
			SimpleDateFormat df = new SimpleDateFormat(formate);
			result=df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**获取当前系统格式 时分秒
	 * @param timeInMillis 时间毫秒数
	 * @return 格式 （下午）06:09:08
	 */
	public static String getSystemTimeFormat(long timeInMillis){
		String result="";
		try{
			Date date=new Date(timeInMillis);
			DateFormat df = DateFormat.getTimeInstance();
			result=df.format(date);//06:09:08
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	/**获取当前系统格式 时分
	 * @param timeInMillis 时间毫秒数
	 * @return 格式 （下午）06:09
	 */
	public static String getSystemShortTimeFormat(long timeInMillis){
		String result="";
		try{
			Date date=new Date(timeInMillis);
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
			result=df.format(date);//(上下午)06:09:08
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**获取系统日期格式  年月日时分秒
	 * @param timeInMillis 时间毫秒数
	 * @return 格式2014年09月08日 （下午）06:09:08
	 */
	public static String getSystemLongDateFormat(long timeInMillis){
		String result="";
		try{
			Date date=new Date(timeInMillis);
			DateFormat df = DateFormat.getDateTimeInstance();
			result=df.format(date);//格式2014年09月08日 （下午）06:09:08
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**获取系统日期格式 年月日时分
	 * @param timeInMillis 时间毫秒数
	 * @return 格式2014年09月08日 （下午）06:09，不是本年返回9月8日（下午）06:09
	 */
	public static String getSystemDateFormat(long timeInMillis){
		String result="";
		try{
			Date date=new Date(timeInMillis);
			DateFormat df =DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);

			result=df.format(date);//格式2014年09月08日 06:09:08
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}


	/**获取系统日期格式 年月日
	 * @param  timeInMillis 时间毫秒数
	 * @return 格式2014年9月8日,不是本年返回9月8日
	 */
	public static String getSystemShortDateFormat(long timeInMillis){
		String result="";
		try{
			Date date=new Date(timeInMillis);
			DateFormat df =DateFormat.getDateInstance();
			result= df.format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**与当前时间比较大小， 并返回相差的天数（不比较时分秒比较）
	 * @param timeInMillis 时间毫秒数
	 * @return 相差的天数，小于0：小于当前时间，0等于当前时间，大于0 大于当前时间，
	 */
	public static int compareWithCurDate(long timeInMillis){
		int result=-1;
		try{
			Calendar mCal=Calendar.getInstance();
			mCal.setTimeInMillis(timeInMillis);
			Calendar nowCal=Calendar.getInstance();
			if(mCal.get(Calendar.YEAR)==nowCal.get(Calendar.YEAR)){//同年
				result=mCal.get(Calendar.DAY_OF_YEAR)-nowCal.get(Calendar.DAY_OF_YEAR);
			}else {//上一年，小于今年
				long intervalMilli=mCal.getTimeInMillis()-nowCal.getTimeInMillis();
				long divider=1000*60*60*24;
				result= (int) Math.ceil(intervalMilli/divider);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	/**与当前时间比较大小， 并返回相差的分钟(不比较秒数)
	 * @param timeInMillis 时间毫秒数
	 * @return 小于0：小于当前时间，0等于当前时间，大于0 大于当前时间，
	 */
	public static long compareWithCurTime(long timeInMillis){
		long result=-1;
		try{
			Calendar mCal=Calendar.getInstance();
			mCal.setTimeInMillis(timeInMillis);
			Calendar nowCal=Calendar.getInstance();
			long intervalMilli=mCal.getTimeInMillis()-nowCal.getTimeInMillis();
			//向上取整
			result= (long) Math.ceil(intervalMilli / (60*1000));
			//一天毫秒数（24*60*60*1000）

		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	/**与当前时间比较大小， 并返回相差的小时(不比较秒数)
	 * @param timeInMillis 时间毫秒数
	 * @return 小于0：小于当前时间，0等于当前时间，大于0 大于当前时间，
	 */
	public static int compareWithCurTimeForHour(long timeInMillis){
		int result=-1;
		try{
			Calendar mCal=Calendar.getInstance();
			mCal.setTimeInMillis(timeInMillis);
			Calendar nowCal=Calendar.getInstance();
			long intervalMilli=mCal.getTimeInMillis()-nowCal.getTimeInMillis();
			//向上取整
			result= (int) Math.ceil(intervalMilli / (60*60*1000));
			//一天毫秒数（24*60*60*1000）

		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	/**时间比较大小， 并返回相差的分钟(不比较秒数)
	 * @param timeInMillis 时间毫秒数
	 * @return timeInMillis2 大于 timeInMillis1 返回正数，否则返回负数，等于返回0
	 */
	public static int compareTime(long timeInMillis1,long timeInMillis2){
		int result=-1;
		try{
			Calendar mCal1=Calendar.getInstance();
			mCal1.setTimeInMillis(timeInMillis1);
			Calendar mCal2=Calendar.getInstance();
			mCal2.setTimeInMillis(timeInMillis2);
			long intervalMilli=mCal2.getTimeInMillis()-mCal1.getTimeInMillis();
			//向上取整
			result= (int) Math.ceil(intervalMilli / (60*1000));
			//一天毫秒数（24*60*60*1000）

		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/**比较日期相差的天数
	 * @param timeInMillis1
	 * @param timeInMillis2
	 * @return 相差的天数，timeInMillis2 大于 timeInMillis1  返回正数，否则返回负数，等于返回0
	 */
	public static int compareDate(long timeInMillis1,long timeInMillis2){
		int result=-1;
		try{
			Calendar mCal1=Calendar.getInstance();
			mCal1.setTimeInMillis(timeInMillis1);
			Calendar mCal2=Calendar.getInstance();
			mCal2.setTimeInMillis(timeInMillis2);

			if(mCal2.get(Calendar.YEAR)==mCal1.get(Calendar.YEAR)){//同年
				result=mCal2.get(Calendar.DAY_OF_YEAR)-mCal1.get(Calendar.DAY_OF_YEAR);
			}else if(mCal2.get(Calendar.YEAR)>mCal1.get(Calendar.YEAR)){//下一年，大于今年
				//今年的最后一天
				int thisYearLastDay=mCal1.getActualMaximum(Calendar.DAY_OF_YEAR);
				//今年剩余的天数+下一年的年天数
				result=thisYearLastDay-mCal1.get(Calendar.DAY_OF_YEAR)+mCal2.get(Calendar.DAY_OF_YEAR);
			}else {//上一年，小于今年
				//上一年的最后一天
				int lastYearLastDay=mCal2.getActualMaximum(Calendar.DAY_OF_YEAR);
				//上一年剩余的天数+今年的年天数
				result=-(lastYearLastDay-mCal2.get(Calendar.DAY_OF_YEAR)+mCal1.get(Calendar.DAY_OF_YEAR));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	/**比较时分，时间格式HH:mm
	 * @param timeStr
	 * @return timeStr1 小于 timeStr2返回 true
	 */
	public static boolean compareShortTime(String timeStr1,String timeStr2){
		boolean result=false;
		try{
			DateFormat df = new SimpleDateFormat("HH:mm");
			Date time1 = df.parse(timeStr1);
			Date time2 = df.parse(timeStr2);
			result=time1.before(time2);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}


	/**比较月日，格式MM月dd日
	 * @param timeStr
	 * @return timeStr1 小于 timeStr2返回 true
	 */
	public static int compareShortDate(String dateStr1,String dateStr2){
		int result=0;
		try{
			DateFormat df = new SimpleDateFormat("MM月dd日");
			Date time1 = df.parse(dateStr1);
			Date time2 = df.parse(dateStr2);
			result=time1.compareTo(time2);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static String dbTimeDisplayTimeSwitch(String DateTimeStr) {
		String errorReturnString = DateTimeStr;
		Date date=Calendar.getInstance().getTime();
		if (DateTimeStr != null && !DateTimeStr.equals("-")) {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			try {
				date=sdf.parse(DateTimeStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			errorReturnString = String.valueOf(date.getTime());
		}
		return errorReturnString;
	}



	public static double getDistanceFromLatLng(double latFrom, double lngFrom,
											   double latTo, double lngTo) {
		double radLatFrom = getRad(latFrom);
		double radLatTo = getRad(latTo);
		double rad1 = radLatFrom - radLatTo;
		double rad2 = getRad(lngFrom) - getRad(lngTo);
		double dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(rad1 / 2), 2)
				+ Math.cos(radLatFrom) * Math.cos(radLatTo)
				* Math.pow(Math.sin(rad2 / 2), 2)));
		dis = dis * EARTHRADIUS;
		dis = Math.round(dis * 10000) / 10000;
		return dis / 1.609;
	}

	// radius of earth
	private static double EARTHRADIUS = 6378.137;

	/**
	 * Get radian
	 *
	 * @param d
	 * @return
	 */
	private static double getRad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * Get json time, the time of the incoming string format for display in the
	 * application of this format example："\/Date(1375427338000+0000)\/"
	 *
	 * @param date
	 * @return
	 */
	public static String getJsonTimeFromDateStr(String dateStr) {
		String result = "";
		try {
			Calendar cal = Calendar.getInstance();
			String[] dateArray = dateStr.substring(0,dateStr.indexOf(" ")).split("/");
			String timeStr=dateStr.substring(dateStr.indexOf(" ")+1);
			String[] timeArray = timeStr.substring(0, timeStr.indexOf(" ")).split(":");
			String quantumStr=timeStr.substring(timeStr.indexOf(" ")+1);
			int quantum = Calendar.AM;
			if (dateStr.indexOf("上午") > 0 || dateStr.indexOf("下午") > 0) {
				quantum = "上午".equals(quantumStr) ? Calendar.AM : Calendar.PM;
			} else {
				quantum = "AM".equals(quantumStr) ? Calendar.AM : Calendar.PM;
			}

			cal.set(Integer.valueOf(dateArray[2]),
					Integer.valueOf(dateArray[0]) - 1,
					Integer.valueOf(dateArray[1]),
					Integer.valueOf(timeArray[0]),
					Integer.valueOf(timeArray[1]));
			cal.set(Calendar.AM_PM, quantum);

			Date date = cal.getTime();
			long utcDate=0;
			TimeZone tz = TimeZone.getDefault();
			int tOffset = tz.getRawOffset();
			Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			utcCal.setTime(date);
			utcCal.add(Calendar.MILLISECOND, -tOffset);
			//			String zoneTime = utcCal.getTime() + "";
			//			int pos = zoneTime.indexOf("+");
			//			if(pos==-1){
			//				pos = zoneTime.indexOf("-");
			//			}
			//			String res = zoneTime.substring(pos, pos + 6);
			//			String lastRes = res.replace(":", "");
			Date utcTempDate=utcCal.getTime();
			utcDate=utcTempDate.getTime();
			result = "/Date(" + utcDate + ")/";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Get json time, the time of the incoming string format for display in the
	 * application of this format example："\/Date(1375427338000+0000)\/"
	 *
	 * @param dateTimeStr  "MM/dd/yyyy HH:mm"
	 * @return
	 */
	public static String getJsonTimeFromDateTimeStr(String dateTimeStr) {
		String result = "";
		try {
			Calendar cal = Calendar.getInstance();
			String[] dateArray = dateTimeStr.substring(0,dateTimeStr.indexOf(" ")).split("/");
			String timeStr=dateTimeStr.substring(dateTimeStr.indexOf(" ")+1);
			String[] timeArray = timeStr.split(":");

			cal.set(Integer.valueOf(dateArray[2]),
					Integer.valueOf(dateArray[0]) - 1,
					Integer.valueOf(dateArray[1]),
					Integer.valueOf(timeArray[0]),
					Integer.valueOf(timeArray[1]));

			Date date = cal.getTime();
			long utcDate=0;
			TimeZone tz = TimeZone.getDefault();
			int tOffset = tz.getRawOffset();
			Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			utcCal.setTime(date);
			utcCal.add(Calendar.MILLISECOND, -tOffset);
			Date utcTempDate=utcCal.getTime();
			utcDate=utcTempDate.getTime();
			result = "/Date(" + utcDate + ")/";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Convert String date to Date
	 * @param date date for String format
	 * @return Date object
	 */
	public static Date stringToDate(String date) {
		Date dateTime = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			dateTime = df.parse(date);
		} catch (Exception e) {
			try {
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				dateTime = df.parse(date);
			} catch (Exception e2) {
				try {
					DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
					dateTime = df.parse(date);
				} catch (Exception e3) {
					try {
						dateTime = new Date(date);
					} catch (Exception e4) {
						e.printStackTrace();
					}
				}
			}
		}
		return dateTime;
	}



	/**
	 * Return date format String MM/dd/yyyy a
	 * @param jsonStr json format string
	 * @return date format string
	 */
	public static long getTimeInMilliFormJSONString(String jsonStr){
		long timeInMilli=0;
		try{
			if(jsonStr!=null){
				String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
				Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
				Matcher matcher = pattern.matcher(jsonStr);
				String result = matcher.replaceAll("$2");
				if(result!=null&&!"null".equals(result)){
					timeInMilli=new Long(result);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return timeInMilli;
	}


	/**字符串转换为整数
	 * @param str
	 * @return
	 */
	public static int strToInt(String str){
		int result = 0;
		try {
			if (isNum(str)) {
				result = Integer.valueOf(str);
			}
		}catch (Exception e){}
		return result;
	}

	/**字符串转换为double
	 * @param str
	 * @return
	 */
	public static double strToDoubleValue(String str){
		double result=0.0;
		try {
			if(isNum(str))
				result=Double.valueOf(str);
		}catch (Exception e){}
		return result;
	}

	/**判断是否为数字 包括正数，负数，小数
	 * @param str
	 * @return 是纯数字则返回true
	 */
	public  static boolean isNum(String str){
		boolean result=false;
		try{
			if(str!=null){
				Pattern pattern = Pattern.compile("-?[0-9]+.*[0-9]*");
				Matcher isNum = pattern.matcher(str);
				result=isNum.matches();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}


	/**
	 * Get utc time from TimeInMillis
	 * @param timeInMillis TimeInMillis
	 * @return json format
	 */
	public static String getJSONDateStringFromTimeInMillis(long timeInMillis){
		Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		utcCal.setTimeInMillis(timeInMillis);
		return String.format("/Date(%d)/", utcCal.getTimeInMillis());
	}



	/**
	 * Set month name
	 * @param month month name
	 */
	public static String getMonthName(int month){
		String monthName="January";
		switch (month) {
			case Calendar.JANUARY:
				monthName="January";
				break;
			case Calendar.FEBRUARY:
				monthName="February";
				break;
			case Calendar.MARCH:
				monthName="March";
				break;
			case Calendar.APRIL:
				monthName="April";
				break;
			case Calendar.MAY:
				monthName="May";
				break;
			case Calendar.JUNE:
				monthName="June";
				break;
			case Calendar.JULY:
				monthName="July";
				break;
			case Calendar.AUGUST:
				monthName="August";
				break;
			case Calendar.SEPTEMBER:
				monthName="September";
				break;
			case Calendar.OCTOBER:
				monthName="October";
				break;
			case Calendar.NOVEMBER:
				monthName="November";
				break;
			case Calendar.DECEMBER:
				monthName="December";
				break;
			case Calendar.UNDECIMBER:
				monthName="Undecimber";
				break;
			default:
				break;
		}
		return monthName;
	}



	/**指定日期是否在当前日期的本周内
	 * @param date
	 * @return
	 */
	public static boolean isInNowDateWeek(long timeInMillis){
		boolean result=false;
		try{
			//特定日期
			Calendar mCal=Calendar.getInstance();
			mCal.setFirstDayOfWeek(Calendar.MONDAY);
			mCal.setTimeInMillis(timeInMillis);
			//当前的日期
			Calendar cal=Calendar.getInstance();
			cal.setFirstDayOfWeek(Calendar.MONDAY);

			int distance=compareWithCurDate(timeInMillis);//不比较时分，只比较年月日
			if(Math.abs(distance)<7&&cal.get(Calendar.WEDNESDAY)<=mCal.get(Calendar.WEDNESDAY))
				result=true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;

	}

	/**url转码
	 * @param urlStr
	 * @return
	 */
	public static String urlEncoder(String urlStr){
		URL url = null;
		try {
			url = new URL(urlStr);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url==null?"":url.toString();
	}
}

