package com.gkzxhn.prison.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.model.iml.MainModel;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.starlight.mobile.android.lib.util.CommonHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public class CancelVideoDialog extends Dialog {
    private View contentView;
    private Context context;
    private Spinner mSpinner;
    private String[] rateArray;
    private String content;
    private View.OnClickListener onClickListener;
    private boolean isCancelVideo;

    private MainModel mModel;
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public CancelVideoDialog(Context context,boolean isCancelVideo) {
        super(context, R.style.update_dialog_style);
        this.context=context;
        this.isCancelVideo=isCancelVideo;
        mModel=new MainModel();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = LayoutInflater.from(getContext()).inflate(
                isCancelVideo?R.layout.cancel_video_dialog:R.layout.cancel_meeting_dialog_layout, null);
        setContentView(contentView);
        mSpinner= (Spinner) contentView.findViewById(R.id.spinner);
        init();
        measureWindow();
    }
    public String getContent(){
        return content;
    }
    private void init(){
        rateArray = context.getResources().getStringArray(isCancelVideo?R.array.cancel_video_reason:R.array.cancel_meeting_reason);
        content =rateArray[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),

                R.layout.spinner_item, rateArray);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                content =rateArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        contentView.findViewById(R.id.cancel_video_dialog_tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                CommonHelper.clapseSoftInputMethod((Activity) context);
            }
        });
        contentView.findViewById(R.id.cancel_video_dialog_tv_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                CommonHelper.clapseSoftInputMethod((Activity) context);
                if(onClickListener!=null)onClickListener.onClick(view);
                if(isCancelVideo)sendMessage();

            }
        });
    }
    private void sendMessage(){
        SharedPreferences sharedPreferences=mModel.getSharedPreferences();
        String otherAccount= sharedPreferences.getString(Constants.EXTRA,"");//对方云信帐号
        String meetingId= sharedPreferences.getString(Constants.EXTRAS,"");//记录ID
        if(otherAccount!=null&&otherAccount.length()>0) {
//// 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
//            NIMClient.getService(MsgService.class).sendMessage(message, false);
            // 构造自定义通知，指定接收者
            CustomNotification notification = new CustomNotification();
            notification.setSessionId(otherAccount);
            notification.setSessionType( SessionTypeEnum.P2P);

// 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
// 这里以类型 “1” 作为“正在输入”的状态通知。
            JSONObject json = new JSONObject();
            try {
                json.put("msg", content);
                json.put("code", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            notification.setContent(json.toString());

// 发送自定义通知
            NIMClient.getService(MsgService.class).sendCustomNotification(notification);
        }
        mModel.requestCancel(meetingId,content,null);
    }

    public void measureWindow(){
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        WindowManager m = dialogWindow.getWindowManager();

        Display d = m.getDefaultDisplay();
        params.width = d.getWidth();
        //	        params.height=d.getHeight();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(params);
    }

}
