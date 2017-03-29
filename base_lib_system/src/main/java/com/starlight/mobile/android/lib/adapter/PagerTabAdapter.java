package com.starlight.mobile.android.lib.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Raleigh on 15/7/2.
 */
public class PagerTabAdapter extends FragmentPagerAdapter {

    private Fragment currentFragment;
    private List<Fragment> fragmentList;
    private List<String>   titleList;
    public OnPageSelected onPageSelected;
    public void setOnPageSelected(OnPageSelected onPageSelected){
        this.onPageSelected=onPageSelected;

    }

    public PagerTabAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList){
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment= (Fragment) object;
        if(onPageSelected!=null)onPageSelected.OnPageSelected();
        super.setPrimaryItem(container, position, object);
    }
    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    /**
     * 得到每个页面
     */
    @Override
    public Fragment getItem(int arg0) {
        return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
    }

    /**
     * 每个页面的title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return (titleList.size() > position) ? titleList.get(position) : "";
    }
    public interface OnPageSelected{
        public void OnPageSelected();
    }

    /**
     * 页面的总个数
     */
    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

}
