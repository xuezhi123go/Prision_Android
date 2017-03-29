package com.starlight.mobile.android.lib.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.starlight.mobile.android.lib.R;

/**选择图片来源对话框，相册/拍照
 * @author raleigh
 *
 */
public class CusPhotoFromDialog extends Dialog{
	private View root;
	private Button btnTakePhoto,btnAlbum,btnCancel;
	private PhotoFromClickListener listener;
	private String takePhoto="", album="", cancel="";
	public CusPhotoFromDialog(Context context) {
		super(context,R.style.CusPhotoFromDialog_style);
		root = LayoutInflater.from(getContext()).inflate(R.layout.cus_photo_from_dialog_layout, null);
	}

	public CusPhotoFromDialog(Context context, int theme) {
		super(context, theme);
		root = LayoutInflater.from(getContext()).inflate(R.layout.cus_photo_from_dialog_layout, null);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(root);
		btnTakePhoto=(Button)findViewById(R.id.cus_photo_from_dialog_layout_btn_take_photo);
		btnAlbum=(Button)findViewById(R.id.cus_photo_from_dialog_layout_btn_album);
		btnCancel=(Button)findViewById(R.id.cus_photo_from_dialog_layout_btn_cancel);
		btnTakePhoto.setOnClickListener(onClickListener);
		btnAlbum.setOnClickListener(onClickListener);
		btnCancel.setOnClickListener(onClickListener);
		btnTakePhoto.setText(takePhoto);
		btnAlbum.setText(album);
		btnCancel.setText(cancel);

		Window dialogWindow = this.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);

		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay();
		//填充页面
		root.setLayoutParams(new FrameLayout.LayoutParams(
				d.getWidth(), FrameLayout.LayoutParams.WRAP_CONTENT));
		params.width = d.getWidth();
		dialogWindow.setAttributes(params);

	}
	public void measureWindow(){
		Window dialogWindow = this.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);

		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay();
		//填充页面
		root.setLayoutParams(new FrameLayout.LayoutParams(
				d.getWidth(), FrameLayout.LayoutParams.WRAP_CONTENT));
		params.width = d.getWidth();
		dialogWindow.setAttributes(params);
	}

	private View.OnClickListener onClickListener=new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if(listener!=null)listener.back(v);
			dismiss();
		}
	};
	/**设置标题，在show()方法之后使用
	 * @param takePhoto 第一个按钮的text
	 * @param album 第二个按钮的text
	 * @param cancel 第三个按钮的text
	 */
	public void setBtnTitle(String takePhoto,String album,String cancel){
		this.takePhoto=takePhoto;
		this.album=album;
		this.cancel=cancel;
	}
	/**设置按钮监听器，
	 *  * @param onClickListener
	 */
	public void setBtnClickListener(PhotoFromClickListener photoFromClickListener){
		this.listener=photoFromClickListener;
	}

	public Button getBtnTakePhoto() {
		return btnTakePhoto;
	}

	public Button getBtnAlbum() {
		return btnAlbum;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}
	public interface PhotoFromClickListener{
		public void back(View v);
	}

}
