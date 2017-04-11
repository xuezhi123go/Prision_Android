package com.gkzxhn.prision.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.customview.calendar.CalendarCard;
import com.gkzxhn.prision.customview.calendar.CalendarViewAdapter;
import com.gkzxhn.prision.customview.calendar.CustomDate;

public class MainActivity extends AppCompatActivity {
    private TextView tvMonth;
    private ViewPager mViewPager;
    private CalendarViewAdapter adapter;
    private CustomDate mDate;
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
    }
    private void init(){
        initCalander();

    }
    private void initCalander(){
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, onCellClickListener);
        }
        adapter = new CalendarViewAdapter(views);
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
}
