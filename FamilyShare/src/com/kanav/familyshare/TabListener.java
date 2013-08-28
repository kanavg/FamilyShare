package com.kanav.familyshare;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class TabListener<T extends Fragment> implements ActionBar.TabListener
{
	private Fragment mFragment;
	private Activity mActivity;
	private String mTab;
	private final Class<T> mClass;
	
	public TabListener(Activity act,String tag, Class<T> cls){
		mActivity = act;
		mTab = tag;
		mClass = cls;
	}
	

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(mFragment == null){
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			ft.add(android.R.id.content, mFragment, mTab);
		}
		else {
			ft.attach(mFragment);
		}
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if(mFragment != null){
			ft.detach(mFragment);
		}
		
	}

}
