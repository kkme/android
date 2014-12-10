package com.link.bianmi.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.link.bianmi.R;
import com.link.bianmi.adapter.ViewPagerAdapter;
import com.link.bianmi.fragment.ReminderPersonFragment;
import com.link.bianmi.fragment.ReminderSystemFragment;
import com.link.bianmi.widget.ViewPagerTabBar;

/**
 * 提醒：我的、系统
 * 
 * @author pangfq
 * @date 2014-11-23 上午11:17:45
 */
public class ReminderActivity extends BaseFragmentActivity {

	public ViewPager mViewPager;
	private ViewPagerTabBar mViewPagerTab;
	private ArrayList<Fragment> mFragments;
	private boolean mFragmentsLoaded = false;

	private MenuItem mLoadingItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBar
		getActionBar().setTitle(
				getResources().getString(R.string.message_center));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_reminder);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPagerTab = (ViewPagerTabBar) findViewById(R.id.viewpagertab);
		mFragments = new ArrayList<Fragment>();
		String fragmentTitles[];

		mFragments.add(new ReminderPersonFragment());
		mFragments.add(new ReminderSystemFragment());
		fragmentTitles = new String[] { getString(R.string.person_reminder),
				getString(R.string.system_reminder) };
		mViewPager.setOffscreenPageLimit(mFragments.size());
		mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				mFragments, fragmentTitles));
		mViewPagerTab.setViewPager(mViewPager);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.loading, menu);
		mLoadingItem = menu.findItem(R.id.action_loading);
		if (!mFragmentsLoaded) {
			for (int i = 0; i < mFragments.size(); i++) {
				mFragments.get(i).onCreateOptionsMenu(menu, inflater);
			}
			mFragmentsLoaded = true;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// ------------------------------Public------------------------------
	public View getViewPagerTab() {
		return mViewPagerTab;
	}

	public void finishLoading() {
		if (mLoadingItem == null)
			return;
		mLoadingItem.setVisible(false);

	}

	/**
	 * 开始加载
	 */
	public void startLoading() {
		if (mLoadingItem == null)
			return;
		mLoadingItem.setVisible(true);

	}
}