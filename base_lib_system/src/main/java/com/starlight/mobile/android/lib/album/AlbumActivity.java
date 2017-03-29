package com.starlight.mobile.android.lib.album;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.starlight.mobile.android.lib.R;
import com.starlight.mobile.android.lib.album.ImageDirPopupWindow.OnImageDirSelected;
import com.starlight.mobile.android.lib.view.CusHeadView;
import com.starlight.mobile.android.lib.view.CusTextView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Raleigh on 15/7/10.
 *
 * Intent入参
 *     Intent action= AlbumActivity.EXTRAS_SIGLE_MODE  单张图片选择，不需要单选框和预览功能,isSigleMode=true
 *        无action isSigleMode＝false
 *     IntExtra  AlbumActivity.EXTRA_IMAGE_SELECT_COUNT  本次可选的数量,default=9
 * return  AlbumActivity.EXTRAS
 *     SIGLE MODE:String 图片路径
 *     Not SIGLE MODE:List<String> SelectedImages 已选择的图片路径集合
 */
public class AlbumActivity extends Activity implements OnImageDirSelected
{
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;


	private AlbumAdapter mAdapter;
	private RecyclerView mRecyclerView;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	private CusHeadView chHead;

	private CusTextView mAlbumName;
	int totalCount = 0;

	private int mScreenHeight;
	private  int max_optional_count;//本次可选的总数
	private boolean isSigleMode;

	private ImageDirPopupWindow mListImageDirPopupWindow;
	private int allPhotosCount=0;
	public  static final String EXTRAS_SIGLE_MODE="extras_sigle_mode";//Finish的结果集
	public  static final String EXTRA_IMAGE_SELECT_COUNT="extra_image_select_count";
	private final String EXTRA_IMAGE_LIST="extra_image_list";

	private final String EXTRA_CURRENT_POSITION="extra_current_position";
	private final String EXTRA_HAS_SELECTED_IMAGE="extra_has_selected_image";//已经选择的图片路径列表
	public  static String EXTRAS="extras";//Finish的结果集
	private final int MAX_OPTIONAL_COUNT=9;//最大的可选数量
	private TextView tvPreview;
	private final int REQUEST_PREVIEW_CODE=0x001;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		max_optional_count=getIntent().getIntExtra(EXTRA_IMAGE_SELECT_COUNT, MAX_OPTIONAL_COUNT);
		String action =getIntent().getAction();
		if(action!=null&&action.equals(EXTRAS_SIGLE_MODE))
			isSigleMode=true;
		else
			isSigleMode=false;
		initView();
		getImages();
	}

	/**
	 * 为View绑定数据
	 */
	private void loadAllPhotos()
	{
		allPhotosCount=0;
		if (mImageFloders == null)
		{
			Toast.makeText(getApplicationContext(), R.string.album_no_photo, Toast.LENGTH_SHORT).show();
			return;
		}
		List<File> mImgs=new ArrayList<File>();
		for(int i=0;i<mImageFloders.size();i++){
			ImageFloder folder=mImageFloders.get(i);
			File mDir=new File(folder.getDir());
			mImgs.addAll(Arrays.asList(mDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".jpeg"))
						return true;
					return false;
				}
			})));
			allPhotosCount+=folder.getCount();
		}
		Collections.sort(mImgs, new AlbumFileComparator());//通过重写Comparator的实现类
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter.updateAll(mImgs);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw()
	{
		try {
			mListImageDirPopupWindow = new ImageDirPopupWindow(
					LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
					mImageFloders, allPhotosCount, LayoutInflater.from(getApplicationContext())
					.inflate(R.layout.album_dir_layout, null));


			mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// 设置背景颜色变暗
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 1.0f;
					getWindow().setAttributes(lp);
				}
			});
			// 设置选择文件夹的回调
			mListImageDirPopupWindow.setOnImageDirSelected(this);
		}catch (Exception e){e.printStackTrace();}
	}


	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages()
	{
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, R.string.album_no_sd_card, Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, getString(R.string.album_loading));

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try {

					String firstImage = null;

					Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					ContentResolver mContentResolver = AlbumActivity.this
							.getContentResolver();

					// 只查询jpeg和png的图片
					Cursor mCursor = mContentResolver.query(mImageUri, null,
							MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[]{"image/jpeg", "image/png"},
							MediaStore.Images.Media.DATE_MODIFIED);

					while (mCursor.moveToNext()) {
						// 获取图片的路径
						try {
							String path = mCursor.getString(mCursor
									.getColumnIndex(MediaStore.Images.Media.DATA));

							// 拿到第一张图片的路径
							if (firstImage == null)
								firstImage = path;
							// 获取该图片的父路径名
							File parentFile = new File(path).getParentFile();
							if (parentFile == null)
								continue;
							String dirPath = parentFile.getAbsolutePath();
							ImageFloder imageFloder = null;
							// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
							if (mDirPaths.contains(dirPath)) {
								continue;
							} else {
								mDirPaths.add(dirPath);
								// 初始化imageFloder
								imageFloder = new ImageFloder();
								imageFloder.setDir(dirPath);
								imageFloder.setFirstImagePath(path);
							}

							int picSize = parentFile.list(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String filename) {
									if (filename.endsWith(".jpg")
											|| filename.endsWith(".png")
											|| filename.endsWith(".jpeg"))
										return true;
									return false;
								}
							}).length;
							totalCount += picSize;

							imageFloder.setCount(picSize);
							mImageFloders.add(imageFloder);

							if (picSize > mPicsSize) {
								mPicsSize = picSize;
								mImgDir = parentFile;
							}
						}catch (Exception e){}
					}
					mCursor.close();

					// 扫描完成，辅助的HashSet也就可以释放内存了
					mDirPaths = null;
					// 通知Handler扫描图片完成
					mHandler.sendEmptyMessage(0x110);
				}catch (Exception e){}
			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView()
	{
		chHead=(CusHeadView)findViewById(R.id.album_layout_ch_head);
		tvPreview=(TextView)findViewById(R.id.album_layout_tv_album_preview);
		mRecyclerView=(RecyclerView)findViewById(R.id.album_layout_rv_list);
		mAlbumName = (CusTextView) findViewById(R.id.album_layout_tv_album_name);
		mAdapter=new AlbumAdapter(this,(TextView)findViewById(R.id.album_layout_tv_album_preview),max_optional_count,isSigleMode);
		mAdapter.setAlbumClickListener(albumClickListener);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(mAdapter);
		chHead.getRightTv().setEnabled(false);

		if(isSigleMode){
			chHead.getRightTv().setVisibility(View.GONE);
			tvPreview.setVisibility(View.GONE);
		}
	}
	private AlbumAdapter.AlbumClickListener albumClickListener = new AlbumAdapter.AlbumClickListener() {
		@Override
		public void itemClick(int position) {
			if(isSigleMode){
				Intent data=new Intent();
				data.putExtra(EXTRAS,  mAdapter.getItem(position).getAbsolutePath());
				setResult(RESULT_OK, data);
				finish();
			}else {
				Intent intent = new Intent(AlbumActivity.this, AlbumPreviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(EXTRA_IMAGE_LIST, (Serializable) mAdapter.getItems());
				bundle.putSerializable(EXTRA_HAS_SELECTED_IMAGE, (Serializable) mAdapter.getSelectedImage());
				bundle.putInt(EXTRA_CURRENT_POSITION, position);
				bundle.putInt(EXTRA_IMAGE_SELECT_COUNT, max_optional_count);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_PREVIEW_CODE);
			}
		}

		@Override
		public void checkedChange(int selectedCount) {
			if(!isSigleMode) {
				chHead.getRightTv().setEnabled(selectedCount > 0);
				tvPreview.setEnabled(selectedCount > 0);
				if (selectedCount > 0) {
					chHead.getRightTv().setText(String.format("%s(%d/%d)", getString(R.string.album_finish), selectedCount, max_optional_count));
					tvPreview.setText(String.format("%s(%s)", getString(R.string.album_preview), selectedCount));
				} else {
					chHead.getRightTv().setText(R.string.album_finish);
					tvPreview.setText(R.string.album_preview);
				}
			}

		}
	};

	@Override
	public void selected(ImageFloder floder)
	{
		if(floder==null) {
			loadAllPhotos();
			mAlbumName.setText(R.string.album_all_photos);
			mListImageDirPopupWindow.dismiss();
		}else{
			mImgDir = new File(floder.getDir());
			List<File> mImgs = Arrays.asList(mImgDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".jpeg"))
						return true;
					return false;
				}
			}));
			Collections.sort(mImgs, new AlbumFileComparator());//通过重写Comparator的实现类
			/**
			 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
			 */

			mAdapter.updateAll(mImgs);
			mAlbumName.setText(floder.getName().substring(1));
			mListImageDirPopupWindow.dismiss();
		}

	}
	public void onClickListener(View v){
		if (v.getId() == R.id.album_layout_tv_album_name) {
			if(mListImageDirPopupWindow.getAllPhotoCount()>0) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(v, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		} else if (v.getId() == R.id.album_layout_tv_album_preview) {//预览
			List<String> selectedImages = mAdapter.getSelectedImage();
			List<File> files = new ArrayList<File>();
			for (int i = selectedImages.size() - 1; i >= 0; i--) {
				files.add(new File(selectedImages.get(i)));
			}
			Intent intent = new Intent(AlbumActivity.this, AlbumPreviewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(EXTRA_IMAGE_LIST, (Serializable) files);
			bundle.putSerializable(EXTRA_HAS_SELECTED_IMAGE, (Serializable) selectedImages);
			bundle.putInt(EXTRA_CURRENT_POSITION, 0);
			bundle.putInt(EXTRA_IMAGE_SELECT_COUNT, max_optional_count);
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_PREVIEW_CODE);
		} else if (v.getId() == R.id.common_head_layout_tv_right) {
			Intent data = new Intent();
			data.putExtra(EXTRAS, (Serializable) mAdapter.getSelectedImage());
			setResult(RESULT_OK, data);
			this.finish();
		} else if (v.getId() == R.id.common_head_layout_iv_left) {
			this.finish();
		}
	}

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();
			// 为View绑定数据
			loadAllPhotos();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_PREVIEW_CODE&&resultCode==RESULT_CANCELED){
			mAdapter.setSelectedImage((List<String>) data.getSerializableExtra(EXTRAS));
		}else if(requestCode==REQUEST_PREVIEW_CODE&&resultCode==RESULT_OK){
			Intent intent=new Intent();
			intent.putExtra(EXTRAS, (Serializable)(List<String>) data.getSerializableExtra(EXTRAS));
			setResult(RESULT_OK, data);
			this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		mAdapter.onDestory();
		if(mListImageDirPopupWindow!=null)mListImageDirPopupWindow.onDestory();
		super.onDestroy();
	}
}
