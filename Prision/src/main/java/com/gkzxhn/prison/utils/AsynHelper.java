package com.gkzxhn.prison.utils;

import android.os.AsyncTask;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.entity.MeetingEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starlight.mobile.android.lib.util.JSONUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raleigh on 15/8/20.
 */
public class AsynHelper extends AsyncTask<Object, Integer, Object> {
    /*  * Params 启动任务执行的输入参数，比如HTTP请求的URL。
	 * Progress 后台任务执行的百分比。
	 * Result 后台执行任务最终返回的结果，比如String,Integer等。
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
								asynHelper.executeOnExecutor(Executors.newCachedThreadPool(),args..);
							}else{
								asynHelper.execute(args...);
							}
	 * */
    private TaskFinishedListener taskFinishedListener;
    private AsynHelperTag target;
    public AsynHelper(AsynHelperTag tag) {
        super();
        this.target=tag;
    }
    public void setOnTaskFinishedListener(
            TaskFinishedListener taskFinishedListener) {
        this.taskFinishedListener = taskFinishedListener;
    }
    /** On load task finished listener */
    public interface TaskFinishedListener {
        public void back(Object object);
    }
    public enum AsynHelperTag{
        DEFUALT_TAG,

    }
    @Override
    protected Object doInBackground(Object... params) {
        Object result=null;
        try {
            if(target!=null) {
                if (target == AsynHelperTag.DEFUALT_TAG){
                    String response = (String) params[0];
                    List<MeetingEntity> lastData=new ArrayList<>();
                    List<MeetingEntity> datas = new Gson().fromJson(response,  new TypeToken<List<MeetingEntity>>() {}.getType());
                    for(MeetingEntity entity:datas){
                        if(!entity.getStatus().equals("FINISHED")){
                            if(entity.getStatus().equals("CANCELED")){
                                String[] reson=GKApplication.getInstance().getResources().getStringArray(R.array.cancel_video_reason);
                                if(entity.getRemarks().equals(reson[reson.length-1])||
                                        entity.getRemarks().equals(reson[reson.length-2])){
                                    lastData.add(entity);
                                }
                            }else{
                                lastData.add(entity);
                            }
                        }
                    }
                    result=lastData;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
        }
        return result;
    }


    @Override
    protected void onPostExecute(Object result) {
        try {
            if (target!=null&&taskFinishedListener != null) {
                taskFinishedListener.back(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
