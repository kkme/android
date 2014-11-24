package com.link.bianmi.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.link.bianmi.fragment.SecretFragment;

/**
 * ViewPager适配器
 * 
 * @author pangfq
 * @date 2014年11月24日 下午4:32:43
 */
public class ViewPagerAdapter extends
		android.support.v4.app.FragmentPagerAdapter {
	String[] titles;
	ArrayList<Fragment> mFragments;

	// boolean[] initPages;// 页面是否已经初始化

	public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> mFragments,
			String[] titles) {
		super(fm);
		this.mFragments = mFragments;
		this.titles = titles;
		// if (mFragments != null) {
		// initPages = new boolean[mFragments.size()];
		// }
	}

	@Override
	public SecretFragment getItem(int position) {
		return (SecretFragment) mFragments.get(position);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {

		// if (position > 0 && !initPages[position]) {
		// ((BaseFragment) mFragments.get(position)).onFirstLoad();
		// initPages[position] = true;
		// }

	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
}