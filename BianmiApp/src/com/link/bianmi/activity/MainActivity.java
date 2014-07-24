package com.link.bianmi.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.bean.Secret;
import com.link.bianmi.fragment.FriendFragment;
import com.link.bianmi.fragment.HotFragment;
import com.link.bianmi.fragment.ImageFragment;
import com.link.bianmi.fragment.NearbyFragment;
import com.link.bianmi.fragment.SecretFragment;
import com.link.bianmi.utility.ToastUtil;
import com.link.bianmi.widget.ViewPagerTabBar;

public class MainActivity extends BaseFragmentActivity {
	private ViewPager mViewPager;
	private ViewPagerTabBar mViewPagerTab;
	private ImageFragment mImageFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(UserConfig.getInstance().getSessionId() == null || TextUtils.isEmpty(UserConfig.getInstance().getSessionId())){
			launchActivity(WelcomeActivity.class);
			finishActivity();
			return;
		}
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(true);
		setContentView(R.layout.activity_main);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setOffscreenPageLimit(3);
		mViewPagerTab = (ViewPagerTabBar) findViewById(R.id.viewpagertab);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(new HotFragment());
		fragments.add(new FriendFragment());
		fragments.add(new NearbyFragment());
		mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				fragments, new String[] {
						this.getResources().getString(
								R.string.main_viewpagertab_title_hot),
						this.getResources().getString(
								R.string.main_viewpagertab_title_friend),
						this.getResources().getString(
								R.string.main_viewpagertab_title_nearby) }));
		mViewPagerTab.setViewPager(mViewPager);
		mImageFragment = (ImageFragment) getSupportFragmentManager()
				.findFragmentById(R.id.main_image_fragment);
		mViewPagerTab.post(new Runnable() {
			@Override
			public void run() {
				getSupportFragmentManager().beginTransaction()
						.hide(mImageFragment).commit();
			}
		});

	}

	public void showImageFragment(ImageView smallImageView, boolean show,
			Secret item) {
		// showActionbarWithTabs(!show);
		if (show) {
			getSupportFragmentManager().beginTransaction().show(mImageFragment)
					.commit();
			mImageFragment.startScaleAnimation(smallImageView, item);
		} else {
			getSupportFragmentManager().beginTransaction().hide(mImageFragment)
					.commit();
		}

	}

	public void showActionbarWithTabs(boolean show) {
		if (show) {
			getActionBar().show();
			mViewPagerTab.setVisibility(View.VISIBLE);
		} else {
			getActionBar().hide();
			mViewPagerTab.setVisibility(View.GONE);
		}
	}

	public View getViewPagerTab() {
		return mViewPagerTab;
	}

	private class ViewPagerAdapter extends
			android.support.v4.app.FragmentPagerAdapter {

		String[] titles;
		ArrayList<Fragment> fragments;

		public ViewPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments, String[] titles) {
			super(fm);
			this.fragments = fragments;
			this.titles = titles;
		}

		@Override
		public SecretFragment getItem(int position) {
			return (SecretFragment) fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mImageFragment != null) {
			menu.findItem(R.id.action_add)
					.setVisible(!mImageFragment.canBack());
			menu.findItem(R.id.action_more).setVisible(
					!mImageFragment.canBack());
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_about) {
			launchActivity(AboutActivity.class);
			return true;
		} else if (item.getItemId() == R.id.action_add) {
			launchActivity(AddActivity.class);
			return true;
		} else if (item.getItemId() == R.id.action_exit) {
			UserConfig.getInstance().setSessionId("");
			launchActivity(WelcomeActivity.class);
			finishActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	long mLastBackPressedTime = 0;

	@Override
	public void onBackPressed() {
		if (mImageFragment.canBack()) {
			mImageFragment.goBack();

		} else {
			long cur_time = System.currentTimeMillis();

			if ((cur_time - mLastBackPressedTime) < 1000) {
				super.onBackPressed();
			} else {
				mLastBackPressedTime = cur_time;
				ToastUtil.showToast(MainActivity.this, getResources()
						.getString(R.string.str_back_twice_exit));
			}

		}
	}

}
