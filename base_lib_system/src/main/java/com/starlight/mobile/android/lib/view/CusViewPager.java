
package com.starlight.mobile.android.lib.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class CusViewPager extends ViewPager {
	 
	public CusViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	boolean isCanScroll=true;
	 
	public void setCanScroll(boolean isCanScroll){  
		this.isCanScroll = isCanScroll;  
	}  


	@Override  
	public void scrollTo(int x, int y){  
		if (isCanScroll){  
			super.scrollTo(x, y);  
		}  
	}  
}
