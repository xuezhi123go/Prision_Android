package com.starlight.mobile.android.lib.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class JSONUtil {
	/**
	 * 根据不同集合对象返回JSON对象
	 * 
	 * @param obj
	 *            集合对象
	 * @return json对象
	 */
	@SuppressWarnings("rawtypes")
	public static Object getJsObject(Object obj) {
		if (obj instanceof Map) {
			return getJsMap((Map) obj);
		} else if (obj instanceof Collection) {
			return getJsCollection((Collection) obj);
		} else {
			return getJsValue(obj);
		}
	}

	/**
	 * 将Map对象转换为JSON对象的方法
	 * 
	 * @param map
	 *            Map对象
	 * @return JSON对象
	 */
	@SuppressWarnings("rawtypes")
	public static Object getJsMap(Map map) {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		Iterator iter = map.entrySet().iterator();
		if (iter.hasNext()) {
			Map.Entry ety = (Map.Entry) iter.next();
			buf.append(getJsValue(ety.getKey()));
			buf.append(":");
			buf.append(getJsObject(ety.getValue()));
		}
		while (iter.hasNext()) {
			Map.Entry ety = (Map.Entry) iter.next();
			buf.append(",");
			buf.append(getJsValue(ety.getKey()));
			buf.append(":");
			buf.append(getJsObject(ety.getValue()));
		}
		buf.append("}");
		return buf;
	}

	/**
	 * 根据集合对象获取JSON对象
	 * 
	 * @param list
	 *            集合对象
	 * @return JSON对象
	 */
	@SuppressWarnings("rawtypes")
	public static Object getJsCollection(Collection list) {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		Iterator iter = list.iterator();
		if (iter.hasNext()) {
			buf.append(getJsObject(iter.next()));
		}
		while (iter.hasNext()) {
			buf.append(",");
			buf.append(getJsObject(iter.next()));
		}
		buf.append("]");
		return buf;
	}

	public static String getJsString(Object obj) {
		if (obj == null) {
			obj = new String("");
		}
		return obj.toString().replaceAll("\\\\", "\\\\\\\\")
				.replaceAll("'", "\\\\\'");
	}

	public static Object getJsValue(Object objValue) {
		StringBuffer buf = new StringBuffer();
		buf.append("'");
		buf.append(getJsString(objValue));
		buf.append("'");
		return buf;
	}

	/**
	 * 从JSONObject中获取JSON数组对象
	 * 
	 * @param jsonObject
	 *            JSON对象
	 * @param name
	 *            数组的key
	 */
	public static JSONArray getJSONArray(JSONObject jsonObject, String name) {
		JSONArray array = null;
		Object obj = getJSONObjectValue(jsonObject, name);
		try {
			array = new JSONArray(obj.toString());
		} catch (JSONException e) {
			// TODO 错误日志处理
			e.printStackTrace();
		}
		return array;
	}

	/**
	 * 根据Map集合创建JSON对象
	 * 
	 * @param map
	 *            Map集合
	 * @return JSON对象
	 */
	@SuppressWarnings("rawtypes")
	public static JSONObject getJSONObject(Map map) {
		JSONObject object = new JSONObject(map);
		return object;
	}

	/**
	 * 根据Collection实现类创建JSON数组
	 * 
	 * @param collection
	 *            实现了Collection接口的集合类
	 * @return JSON数组
	 */
	@SuppressWarnings("rawtypes")
	public static JSONArray getJSONArray(Collection collection) {
		JSONArray array = new JSONArray(collection);
		return array;
	}

	/**
	 * 根据JSON格式的字符串构建JSON对象
	 * 
	 * @param jsonStr
	 *            JSON格式字符串
	 * @return JSON对象
	 */
	public static JSONObject getJSONObject(String jsonStr) {
		JSONObject object = new JSONObject();
		try {
			object = new JSONObject(jsonStr);
		} catch (JSONException e) {
			// TODO 错误日志处理
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 根据属性名称从JSON对象中取出值
	 * 
	 * @param jsonObject
	 *            JSON对象
	 * @param name
	 *            属性名称
	 * @return Object对象（有可能是单个值，有可能是JSONObject，也有可能是JSONArray）
	 */
	public static Object getJSONObjectValue(JSONObject jsonObject, String name) {
		Object object = new Object();
		try {
			object = jsonObject.get(name);

		} catch (JSONException e) {
			// TODO 错误日志处理
			e.printStackTrace();
		}
		return object;
	}
	public static String getJSONObjectStringValue(JSONObject jsonObject, String name) {
		String object = "";
		try {
			if(jsonObject.has(name))object = jsonObject.getString(name);
		} catch (JSONException e) {
		}
		return object;
	}
	/**
	 * 根据属性名称从JSON对象中取出值
	 * 
	 * @param jsonObject
	 *            JSON对象
	 * @param name
	 *            属性名称
	 * @return Object对象（有可能是单个值，有可能是JSONObject，也有可能是JSONArray）
	 */
	public static String getJSONValue(JSONObject jsonObject, String name) {
		String object=null;
		try {
			if(jsonObject!=null&&jsonObject.has(name)){
				object = jsonObject.get(name).toString();
			}
		} catch (Exception e) {
			// TODO 错误日志处理
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 根据JSON对象的属性名称从JSON中取出JSON对象
	 * 
	 * @param jsonObject
	 *            JSON对象
	 * @param name
	 *            属性名称
	 * @return JSON对象
	 */
	public static JSONObject getJSONObject(JSONObject jsonObject, String name) {
		JSONObject object = new JSONObject();
		try {
			object = new JSONObject(jsonObject.get(name).toString());
		} catch (JSONException e) {
			// TODO 错误日志处理
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 根据下标从JSONArray中取出JSON对象
	 * 
	 * @param array
	 *            JSON数组
	 * @param index
	 *            下载
	 * @return JSON对象
	 */
	public static JSONObject getJSONObjectByIndex(JSONArray array, int index) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = array.getJSONObject(index);
		} catch (JSONException e) {
			// TODO 错误日志处理
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 试图将String转换为 JSONArray
	 * 
	 * @param sString
	 *            符合json格式的string
	 * @return JSONArray
	 */
	public static JSONArray getJSONArray(String sString) {
		JSONArray mArray = null;
		try {
			if(sString!=null&&sString.trim().length()>0)
				mArray = new JSONArray(sString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mArray;
	}

	/**
	 * 获得惟一资源ID
	 * 
	 * @return 惟一资源ID
	 */
	public static String generateRandomBasedUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 对字符串进行编码
	 * 
	 * @param encodeString
	 * @return
	 */
	public static String urlEncode(String encodeString) {
		String result = "";
		try {
			result = URLEncoder.encode(encodeString, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 对字符串进行解码
	 * 
	 * @param encodeString
	 * @return
	 */
	public static String urlDecode(String decodeString) {
		String result = "";
		try {
			result = URLDecoder.decode(decodeString, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 格式化日期
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getDateString(Timestamp timestamp) {
		String str = "";
		if (timestamp != null) {
			SimpleDateFormat objSimpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			str = objSimpleDateFormat.format(timestamp);
		}
		return str;
	}

	/**
	 * 格式化日期
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getDateNormalString(Timestamp timestamp) {
		String str = "";
		if (timestamp != null) {
			SimpleDateFormat objSimpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			str = objSimpleDateFormat.format(timestamp);
		}
		return str;
	}

	/**
	 * 格式boolean
	 * 
	 * @param bool
	 * @return
	 */
	public static String getBooleanString(int bool) {
		String str = "否";
		if (bool == 1) {
			str = "是";
		}
		return str;
	}


	/**
	 * 从对象转换为JSONArray对象方法
	 * 
	 * @param object
	 *            对象
	 * @return JSONArray对象
	 */
	public static JSONArray fromObject(Object object) {
		JSONArray array = null;

		return array;
	}

	/**
	 * 从对象转换为JSONArray对象方法
	 * 
	 * @param object
	 *            对象
	 * @return JSONArray对象
	 */
	public static JSONObject fromObject(String object) {
		JSONObject jsonObject = null;

		return jsonObject;
	}


}
