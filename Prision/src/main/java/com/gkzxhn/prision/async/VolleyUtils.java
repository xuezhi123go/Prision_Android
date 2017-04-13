package com.gkzxhn.prision.async;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.gkzxhn.prision.common.GKApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Raleigh on 15/6/23.
 */
public class VolleyUtils<T>
{
    private Context applicationContext;

    public VolleyUtils()
    {
        applicationContext = GKApplication.getInstance();
    }

    /**
     * GET request
     *
     * @param obj                返回请求类型的实例，不能为null,如，String.class,JSONObject.class,JSONArray.class
     * @param url
     * @param onFinishedListener
     */
    public void get(Class obj, String url, final String tag, final OnFinishedListener<T> onFinishedListener) throws AuthFailureError
    {
        try
        {
            if (obj != null && obj.equals(String.class))
            {
                StringRequest request = new StringRequest(url, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (obj != null && obj.equals(JSONObject.class))
            {
                JsonObjectRequest request = new JsonObjectRequest(url, null, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (obj != null && obj.equals(JSONArray.class))
            {
                JsonArrayRequest request = new JsonArrayRequest(url, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };

                SingleRequestQueue.getInstance().add(request, tag);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * POST request
     *
     * @param url
     * @param params             参数，String:Map<String,String> ,JSONObject:JSONObject,JSONArray:JSONArray
     * @param onFinishedListener
     */
    public void post(String url, final Object params, final String tag, final OnFinishedListener<T> onFinishedListener) throws AuthFailureError
    {
        try
        {
            if (params instanceof JSONObject)
            {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, (JSONObject) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }

                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof JSONArray)
            {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, (JSONArray) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof Map)
            {
                StringRequest request = new StringRequest(Request.Method.POST, url, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        return (Map<String, String>) params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * PUT request
     *
     * @param url
     * @param params
     * @param onFinishedListener
     */
    public void put(String url, final T params, final String tag, final OnFinishedListener<T> onFinishedListener) throws AuthFailureError
    {
        try
        {
            if (params instanceof JSONObject)
            {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, (JSONObject) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof JSONArray)
            {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.PUT, url, (JSONArray) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof Map)
            {
                StringRequest request = new StringRequest(Request.Method.PUT, url, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        return (Map<String, String>) params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * PUT request
     *
     * @param url
     * @param params
     * @param onFinishedListener
     */
    public void patch(String url, final T params, final String tag, final OnFinishedListener<T> onFinishedListener) throws AuthFailureError
    {
        try
        {
            if (params instanceof JSONObject)
            {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, (JSONObject) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof JSONArray)
            {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.PATCH, url, (JSONArray) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof Map)
            {
                StringRequest request = new StringRequest(Request.Method.PATCH, url, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        return (Map<String, String>) params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * DELETE request
     *
     * @param url
     * @param params
     * @param onFinishedListener
     */
    public void delete(String url, final T params, final String tag, final OnFinishedListener<T> onFinishedListener) throws AuthFailureError
    {
        try
        {
            if (params instanceof JSONObject)
            {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, (JSONObject) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
//                //超时时间10s,最大重连次数2次
//                request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof JSONArray)
            {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.DELETE, url, (JSONArray) params, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
//                        headers.put("Accept", "application/json");
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            } else if (params instanceof Map)
            {
                StringRequest request = new StringRequest(Request.Method.DELETE, url, getListener(onFinishedListener), getErrorListener(onFinishedListener))
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        return (Map<String, String>) params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        return tag == null ? new HashMap<String, String>() : getCusHeaders();
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0f));
                SingleRequestQueue.getInstance().add(request, tag);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Response.Listener getListener(final OnFinishedListener<T> onFinishedListener)
    {
        Response.Listener listener = new Response.Listener<T>()
        {
            @Override
            public void onResponse(T response)
            {
                if (onFinishedListener != null) onFinishedListener.onSuccess(response);
            }
        };
        return listener;
    }

    private Response.ErrorListener getErrorListener(final OnFinishedListener<T> onFinishedListener)
    {
        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                if (onFinishedListener != null) onFinishedListener.onFailed(error);
            }
        };
        return errorListener;
    }

    public interface OnFinishedListener<T>
    {
        void onSuccess(T response);

        void onFailed(VolleyError error);
    }


    public Map<String, String> getCusHeaders()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        try
        {//TODO
//            headers.put(HTTP.CONTENT_TYPE, "charset=utf-8");
//            headers.put("Accept-Language", "zh-cn,zh");
//            headers.put("MobileType", "Android");
//            headers.put("DeviceModel", android.os.Build.MODEL);
//            headers.put("OSVersion", android.os.Build.VERSION.RELEASE);
//            headers.put("Longitude", String.valueOf(WitsParkApplication.getInstance().getLongitude()));
//            headers.put("Latitude", String.valueOf(WitsParkApplication.getInstance().getLatitude()));
//            try {
//                headers.put("DeviceToken", android.os.Build.SERIAL);
//            } catch (Exception e) {
//            }
//
//            try {
//                headers.put("AppVersion", String.valueOf(applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionCode));
//            } catch (Exception e) {
//                headers.put("AppVersion", "-1");
//            }
//            SharedPreferences preferences=SAASApplication.getInstance().getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
//            String sessionId = preferences.getString(Constants.USER_SESSION_ID, null);
//            if (sessionId != null && sessionId.length() > 0) {
//                headers.put("sessionId", sessionId);
//            }
//            String authorizationValue =preferences.getString(Constants.USER_ACCESS_TOKEN, null);
//            String tokenType= preferences.getString(Constants.USER_TOKEN_TYPE, null);
//            if (authorizationValue != null && authorizationValue.length() > 0 && tokenType!=null) {
//                headers.put("Authorization", String.format("%s %s", tokenType, authorizationValue));
//            }
//
//            headers.put("AppName", "SAAS");

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return headers;
    }


}
