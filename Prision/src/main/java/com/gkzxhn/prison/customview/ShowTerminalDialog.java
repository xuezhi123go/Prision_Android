package com.gkzxhn.prison.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.activity.ConfigActivity;

/**
 * Created by Raleigh.Luo on 17/3/27.
 */

public class ShowTerminalDialog extends Dialog{
    private View contentView;
    private Context context;
    public ShowTerminalDialog(Context context, int theme) {
        super(context, theme);
    }
    public ShowTerminalDialog(Context context) {
        super(context, R.style.update_dialog_style);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.show_terminal_dialog_layout, null);
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
        setCanceledOnTouchOutside(false);
        contentView.findViewById(R.id.show_terminal_dialog_layout_tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        contentView.findViewById(R.id.show_terminal_dialog_layout_tv_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                context.startActivity(new Intent(context, ConfigActivity.class));
            }
        });
    }
}
