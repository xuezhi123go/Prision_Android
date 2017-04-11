package com.gkzxhn.prision.async;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gkzxhn.prision.common.GKApplication;


/**
 * Created by Raleigh on 15/8/27.
 */
public class SingleRequestQueue {
    private static SingleRequestQueue instance;
    private Context applicationContext;
    private RequestQueue mRequestQueue;
    public static synchronized SingleRequestQueue getInstance() {
        if (instance == null) {
            instance = new SingleRequestQueue();
        }
        return instance;
    }
    private SingleRequestQueue(){
        applicationContext= GKApplication.getInstance();
        mRequestQueue=getRequestQueue();
    }
    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(applicationContext);
        }
        return mRequestQueue;
    }

    /**Add Task to the task queue,And you can  cancel the task by the tag
     * @param req
     * @param tag
     * @param <T>
     */
    public <T> void add(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    /**cancel the task
     * @param tag
     */
    public void cancelAll(String tag){
        if(tag!=null)mRequestQueue.cancelAll(tag);
    }
}
