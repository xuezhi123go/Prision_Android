package com.gkzxhn.prision.customview.calendar;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * created by huangzhengneng on 2016/1/18
 * 日历控件适配器
 */
public class CalendarViewAdapter extends PagerAdapter {
    public static final String TAG = "CalendarViewAdapter";
    private CalendarCard[] views;
    private int mCurrentIndex = 498;
    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE
    }
    private SildeDirection mDirection = SildeDirection.NO_SILDE;

    public CalendarViewAdapter(CalendarCard[] views) {
        super();
        this.views = views;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (((ViewPager) container).getChildCount() == views.length) {
            ((ViewPager) container).removeView(views[position % views.length]);
        }

        ((ViewPager) container).addView(views[position % views.length], 0);
        return views[position % views.length];
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) container);
    }

    public CalendarCard[] getAllItems() {
        return views;
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            measureDirection(position);
            updateCalendarView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 计算方向
     *
     * @param arg0
     */
    private void measureDirection(int arg0) {
        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;

        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        CalendarCard[] mShowViews = getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

}