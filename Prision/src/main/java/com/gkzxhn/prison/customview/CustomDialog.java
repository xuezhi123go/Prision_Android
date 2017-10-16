package com.gkzxhn.prison.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gkzxhn.prison.R;


/**
 * Created by Raleigh.Luo on 17/4/10.
 */

public class CustomDialog extends Dialog {
    private Context context;
    private TextView tvTitle,tvCancel,tvConfirm;
    private String title="",leftText="",rightText="";
    private View.OnClickListener onClickListener;
    public CustomDialog(Context context,View.OnClickListener onClickListener) {
        super(context, R.style.update_dialog_style);
        this.context=context;
        this.onClickListener=onClickListener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null);
        setContentView(contentView);
        init();
        measureWindow();
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
    private void init(){
        tvTitle= (TextView) findViewById(R.id.custom_dialog_layout_tv_title);
        tvCancel= (TextView) findViewById(R.id.custom_dialog_layout_tv_cancel);
        tvConfirm= (TextView) findViewById(R.id.custom_dialog_layout_tv_confirm);
        tvTitle.setText(title);
        if(leftText!=null&&leftText.length()>0)tvCancel.setText(leftText);
        if(rightText!=null&&rightText.length()>0)tvConfirm.setText(rightText);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(onClickListener!=null)onClickListener.onClick(view);
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(onClickListener!=null)onClickListener.onClick(view);
            }
        });
    }
    public void setTitle(String title){
        this.title=title;
        if(tvTitle!=null)
            tvTitle.setText(title);
    }
    public void setContent(String title,String leftText,String rightText){
        this.title=title;
        this.leftText=leftText;
        this.rightText=rightText;
        if(tvTitle!=null) {
            tvTitle.setText(title);
            tvCancel.setText(leftText);
            tvConfirm.setText(rightText);
        }
    }
}
