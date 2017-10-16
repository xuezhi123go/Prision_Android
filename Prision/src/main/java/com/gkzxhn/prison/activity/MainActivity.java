package com.gkzxhn.prison.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.adapter.MainAdapter;
import com.gkzxhn.prison.adapter.OnItemClickListener;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.customview.CancelVideoDialog;
import com.gkzxhn.prison.customview.ShowTerminalDialog;
import com.gkzxhn.prison.customview.UpdateDialog;
import com.gkzxhn.prison.customview.calendar.CalendarCard;
import com.gkzxhn.prison.customview.calendar.CalendarViewAdapter;
import com.gkzxhn.prison.customview.calendar.CustomDate;
import com.gkzxhn.prison.entity.MeetingEntity;
import com.gkzxhn.prison.entity.VersionEntity;
import com.gkzxhn.prison.presenter.MainPresenter;
import com.gkzxhn.prison.view.IMainView;
import com.netease.nimlib.sdk.StatusCode;
import com.starlight.mobile.android.lib.view.CusSwipeRefreshLayout;
import com.starlight.mobile.android.lib.view.RecycleViewDivider;
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView;

import java.util.List;

public class MainActivity extends SuperActivity implements IMainView,CusSwipeRefreshLayout.OnRefreshListener {
    private TextView tvMonth;
    private ViewPager mViewPager;
    private CustomDate mDate;
    private MainAdapter adapter;
    private RecyclerView mRecylerView;
    private CusSwipeRefreshLayout mSwipeRefresh;
    private View ivNodata;
    private DotsTextView tvLoading;//加载动画
    private MainPresenter mPresenter;
    private ProgressDialog mProgress;
    private CancelVideoDialog mCancelVideoDialog;
    private UpdateDialog updateDialog;
    private ShowTerminalDialog mShowTerminalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initControls();
        init();
        registerReceiver();
    }
    private void initControls(){
        tvMonth= (TextView) findViewById(R.id.main_layout_tv_month);
        mViewPager= (ViewPager) findViewById(R.id.main_layout_vp_calendar);
        mRecylerView = (RecyclerView) findViewById(R.id.common_list_layout_rv_list);
        tvLoading= (DotsTextView) findViewById(R.id.common_loading_layout_tv_load);
        ivNodata=findViewById(R.id.common_no_data_layout_iv_image);
        mSwipeRefresh= (CusSwipeRefreshLayout) findViewById(R.id.common_list_layout_swipeRefresh);
        ((TextView)findViewById(R.id.common_no_data_layout_iv_hint)).setText(R.string.no_meeting_data);
    }
    private void init(){
        initCalander();
        adapter=new MainAdapter(this);
        adapter.setOnItemClickListener(onItemClickListener);
        mSwipeRefresh.setColor(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        //设置加载模式，为只顶部上啦刷新
        mSwipeRefresh.setMode(CusSwipeRefreshLayout.Mode.PULL_FROM_START);
        mSwipeRefresh.setLoadNoFull(false);
        mSwipeRefresh.setOnRefreshListener(this);
        mRecylerView.setAdapter(adapter);
        int sizeMenu=getResources().getDimensionPixelSize(R.dimen.recycler_view_line_height);
        mRecylerView.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, sizeMenu, getResources().getColor(R.color.common_hint_text_color)));

        //初始化进度条
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting));
        dismissProgress();
        mCancelVideoDialog=new CancelVideoDialog(this,false);
        mCancelVideoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason=mCancelVideoDialog.getContent();
                mCancelVideoDialog.dismiss();
                mPresenter.requestCancel(adapter.getCurrentItem().getId(),reason);
            }
        });
        //请求数据
        mPresenter=new MainPresenter(this,this);

        mPresenter.requestVersion();


    }
    private void initCalander(){
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, onCellClickListener);
        }
        CalendarViewAdapter  adapter = new CalendarViewAdapter(views);
        mDate = CalendarCard.mShowDate;
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(adapter.getCurrentIndex());
        mViewPager.addOnPageChangeListener(adapter.getOnPageChangeListener());
    }

    private CalendarCard.OnCellClickListener onCellClickListener=new CalendarCard.OnCellClickListener() {
        @Override
        public void clickDate(CustomDate date) {
            mDate = date;
            onRefresh();
        }

        @Override
        public void changeDate(CustomDate date) {
            tvMonth.setText(date.getYear() + getString(R.string.year) + date.getMonth() + getString(R.string.month));
        }
    };
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.main_layout_btn_last://上一个月
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);

                break;
            case R.id.main_layout_btn_next://下一个月
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            case R.id.common_head_layout_iv_left:
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.common_head_layout_iv_right:
                onRefresh();
                break;

        }
    }

    @Override
    public void onRefresh() {
        mPresenter.checkStatusCode();
        if(mPresenter.checkStatusCode()== StatusCode.LOGINED) {
            //没有设置终端，则提示用户设置终端
            if (mPresenter.getSharedPreferences().getString(Constants.TERMINAL_ACCOUNT, "").length() == 0) {
                stopRefreshAnim();
                if (mShowTerminalDialog == null) {
                    mShowTerminalDialog = new ShowTerminalDialog(this);
                }
                if (!mShowTerminalDialog.isShowing()) mShowTerminalDialog.show();
            } else {
                mPresenter.request(mDate.toString());
            }
        }else{
            stopRefreshAnim();
        }
    }
    private OnItemClickListener onItemClickListener=new OnItemClickListener() {
        @Override
        public void onClickListener(View convertView, int position) {
            switch (convertView.getId()){
                case R.id.main_item_layout_tv_cancel:
                    if(mCancelVideoDialog!=null&&!mCancelVideoDialog.isShowing())mCancelVideoDialog.show();
                    break;
                default:
                    Intent intent=new Intent(MainActivity.this,CallUserActivity.class);
                    intent.putExtra(Constants.EXTRA,adapter.getCurrentItem().getId());
                    intent.putExtra(Constants.EXTRAS,adapter.getCurrentItem().getYxAccount());
                    intent.putExtra(Constants.EXTRA_TAB,adapter.getCurrentItem().getName());
                    startActivityForResult(intent,Constants.EXTRA_CODE);
                    break;
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.EXTRA_CODE&&resultCode==RESULT_OK){
            onRefresh();
        }
    }

    /**
     * 刷新动画加载
     */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== Constants.START_REFRESH_UI){//开始动画
                if (adapter == null || adapter.getItemCount() == 0) {
                    if (ivNodata.isShown()) {
                        ivNodata.setVisibility(View.GONE);
                    }
                    tvLoading.setVisibility(View.VISIBLE);
                    if (!tvLoading.isPlaying()) {

                        tvLoading.showAndPlay();
                    }
                    if (mSwipeRefresh.isRefreshing()) mSwipeRefresh.setRefreshing(false);
                } else {
                    if (!mSwipeRefresh.isRefreshing()) mSwipeRefresh.setRefreshing(true);
                }
            }else if(msg.what== Constants.STOP_REFRESH_UI){//停止动画
                if (tvLoading.isPlaying() || tvLoading.isShown()) {
                    tvLoading.hideAndStop();
                    tvLoading.setVisibility(View.GONE);
                }
                if (mSwipeRefresh.isRefreshing()) mSwipeRefresh.setRefreshing(false);
                if (mSwipeRefresh.isLoading()) mSwipeRefresh.setLoading(false);
                if (adapter == null || adapter.getItemCount() == 0) {

                    if (!ivNodata.isShown()) ivNodata.setVisibility(View.VISIBLE);
                } else {
                    if (ivNodata.isShown()) ivNodata.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public void showProgress() {
        if(mProgress!=null&&!mProgress.isShowing())mProgress.show();
    }

    @Override
    public void dismissProgress() {
        if(mProgress!=null&&mProgress.isShowing())mProgress.dismiss();
    }

    @Override
    public void updateItems(List<MeetingEntity> datas) {
        adapter.updateItems(datas);

    }

    @Override
    public void onCanceled() {
        adapter.removeCurrentItem();
    }

    @Override
    public void updateVersion(VersionEntity version) {
        //新版本
        int newVersion = version.getVersionCode();
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            int currentVersion=packageInfo.versionCode;//当前App版本
            int lastIgnoreVersion= mPresenter.getSharedPreferences().getInt(Constants.LAST_IGNORE_VERSION,0);
            boolean isIgoreVersion=lastIgnoreVersion==newVersion;//若是已忽略的版本，则不弹出升级对话框
            if(version.isForce())isIgoreVersion=false;
            if (newVersion > currentVersion&&!isIgoreVersion) {//新版本大于当前版本，则弹出更新下载到对话框
                //版本名
                String versionName =  version.getVersionName();
                // 下载地址
                String downloadUrl =  version.getDownloadUrl();
                //是否强制更新
                boolean isForceUpdate= version.isForce();
                if(updateDialog==null)updateDialog=new UpdateDialog(this);
                updateDialog.setForceUpdate(isForceUpdate);
                updateDialog.setDownloadInfor(versionName,newVersion,downloadUrl);
                updateDialog.show();//显示对话框
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.measureWindow();
        if(mShowTerminalDialog!=null&&mShowTerminalDialog.isShowing())mShowTerminalDialog.measureWindow();
        if(mCancelVideoDialog!=null&&mCancelVideoDialog.isShowing())mCancelVideoDialog.measureWindow();
        onRefresh();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.measureWindow();
        if(mShowTerminalDialog!=null&&mShowTerminalDialog.isShowing())mShowTerminalDialog.measureWindow();
        if(mCancelVideoDialog!=null&&mCancelVideoDialog.isShowing())mCancelVideoDialog.measureWindow();

    }

    @Override
    public void stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        if(mShowTerminalDialog!=null&&mShowTerminalDialog.isShowing())mShowTerminalDialog.dismiss();
        if(mCancelVideoDialog!=null&&mCancelVideoDialog.isShowing())mCancelVideoDialog.dismiss();
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.dismiss();
        super.onDestroy();
    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.NIM_KIT_OUT)){
                finish();
            }
        }
    };
    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.NIM_KIT_OUT);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}
