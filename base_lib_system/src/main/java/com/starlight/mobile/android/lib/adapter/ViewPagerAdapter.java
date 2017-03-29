package com.starlight.mobile.android.lib.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**主页ViewPager适配器
 * @author raleighluo
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragmentList;
	private Fragment currentFragment;

	private Context context; 
	public ViewPagerAdapter(Context context, FragmentManager fm, Fragment fragment) {
		super(fm);
		fragmentList=new ArrayList<Fragment>();
		fragmentList.add(fragment);
		this.context=context;
	}   
	public ViewPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragmentlist) {
		super(fm);
		this.fragmentList=fragmentlist;	 
		this.context=context;
	}




	public void addFragment(FragmentManager fm, Fragment fragment) {
		fragmentList.add(fragment);
		notifyDataSetChanged();
	}   
	@Override
	public Fragment getItem(int position) {
		
		return (fragmentList == null || fragmentList.size() == 0) ? null
				: fragmentList.get(position);

	}   

	@Override
	public int getCount() {
		return fragmentList == null ? 0 : fragmentList.size(); 
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		currentFragment= (Fragment) object;
		super.setPrimaryItem(container, position, object);
	}
	public Fragment getCurrentFragment(){
		return currentFragment;
	}

}

