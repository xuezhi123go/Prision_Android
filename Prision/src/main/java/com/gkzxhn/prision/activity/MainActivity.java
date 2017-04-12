package com.gkzxhn.prision.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.adapter.MainAdapter;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.customview.calendar.CalendarCard;
import com.gkzxhn.prision.customview.calendar.CalendarViewAdapter;
import com.gkzxhn.prision.customview.calendar.CustomDate;
import com.starlight.mobile.android.lib.view.CusSwipeRefreshLayout;
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView;

public class MainActivity extends SuperActivity implements CusSwipeRefreshLayout.OnRefreshListener {
    private TextView tvMonth;
    private ViewPager mViewPager;
    private CustomDate mDate;
    private MainAdapter adapter;
    private RecyclerView mRecylerView;
    private CusSwipeRefreshLayout mSwipeRefresh;
    private View ivNodata;
    private DotsTextView tvLoading;//加载动画
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initControls();
        init();
    }
    private void initControls(){
        tvMonth= (TextView) findViewById(R.id.main_layout_tv_month);
        mViewPager= (ViewPager) findViewById(R.id.main_layout_vp_calendar);
        mRecylerView = (RecyclerView) findViewById(R.id.common_list_layout_rv_list);
        tvLoading= (DotsTextView) findViewById(R.id.common_loading_layout_tv_load);
        ivNodata=findViewById(R.id.common_no_data_layout_iv_image);
        mSwipeRefresh= (CusSwipeRefreshLayout) findViewById(R.id.common_list_layout_swipeRefresh);

    }
    private void init(){
        initCalander();
        adapter=new MainAdapter(this);
        mSwipeRefresh.setColor(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        //设置加载模式，为只顶部上啦刷新
        mSwipeRefresh.setMode(CusSwipeRefreshLayout.Mode.PULL_FROM_START);
        mSwipeRefresh.setLoadNoFull(false);
        mSwipeRefresh.setOnRefreshListener(this);
        mRecylerView.setAdapter(adapter);

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
//            if ((date.getYear() + "年" + date.getMonth() + "月").equals(monthText.getText().toString())) {
//                // 点击的是当月的
////                scrollView.scrollTo(0, 0);// 刷新时滑到顶端
//                mPresenter.requestDataList(date.getYear() + "-" + getFomatterNumber(date.getMonth()) + "-" + getFomatterNumber(date.getDay()));// 请求数据
//            } else if (date.getMonth() < Integer.parseInt(monthText.getText().toString().split("年")[1].
//                    substring(0, monthText.getText().toString().split("年")[1].length() - 1))) {
//                showToast(getString(R.string.left_sliding));
//            } else if (date.getMonth() > Integer.parseInt(monthText.getText().toString().split("年")[1].
//                    substring(0, monthText.getText().toString().split("年")[1].length() - 1))) {
//                showToast(getString(R.string.right_sliding));
//            }
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

        }
    }

    @Override
    public void onRefresh() {

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
}
