package com.link.bianmi.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.fragment.FriendFragment;
import com.link.bianmi.fragment.HotFragment;
import com.link.bianmi.fragment.ImageFragment;
import com.link.bianmi.fragment.NearbyFragment;
import com.link.bianmi.fragment.SecretFragment;
import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.widget.ViewPagerTabBar;

public class HomeActivity extends BaseFragmentActivity {
	public ViewPager mViewPager;
	private ViewPagerTabBar mViewPagerTab;
	private ImageFragment mImageFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(true);
		setContentView(R.layout.activity_main);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPagerTab = (ViewPagerTabBar) findViewById(R.id.viewpagertab);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		String fragmentTitles[];

		if (UserConfig.getInstance().getIsGuest()) {
			fragments.add(new HotFragment());
			fragments.add(new NearbyFragment());
			fragmentTitles = new String[] {
					this.getResources().getString(
							R.string.main_viewpagertab_title_hot),
					this.getResources().getString(
							R.string.main_viewpagertab_title_nearby) };
		} else {
			fragments.add(new HotFragment());
			fragments.add(new FriendFragment());
			fragments.add(new NearbyFragment());
			fragmentTitles = new String[] {
					this.getResources().getString(
							R.string.main_viewpagertab_title_hot),
					this.getResources().getString(
							R.string.main_viewpagertab_title_friend),
					this.getResources().getString(
							R.string.main_viewpagertab_title_nearby) };
		}
		mViewPager.setOffscreenPageLimit(fragments.size());
		mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				fragments, fragmentTitles));

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

	@Override
	public void onDestroy() {
		super.onDestroy();
		// UserConfig.getInstance().setLockPassSuccess(false);
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// if (mImageFragment != null) {
	// menu.findItem(R.id.action_add)
	// .setVisible(!mImageFragment.canBack());
	// menu.findItem(R.id.action_more).setVisible(
	// !mImageFragment.canBack());
	// }
	// return super.onPrepareOptionsMenu(menu);
	// }

	private MenuItem mMoreItem;
	private MenuItem mLoadingItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		mMoreItem = menu.findItem(R.id.action_more);
		mLoadingItem = menu.findItem(R.id.action_loading);
		mMoreItem.setVisible(false);
		mLoadingItem.setVisible(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			launchActivity(AddActivity.class);
		} else if (item.getItemId() == R.id.action_more_addfriend) {
		} else if (item.getItemId() == R.id.action_more_recommend) {
		} else if (item.getItemId() == R.id.action_more_settings) {
			launchActivityForResult(SettingsActivity.class, 6666);
		}
		return true;
	}

	long mLastBackPressedTime = 0;

	@Override
	public void onBackPressed() {
		// 如果正在加载，则取消加载
		if (mLoadingItem.isVisible()) {
			finishLoaded(true);
			return;
		}

		if (mImageFragment.canBack()) {
			mImageFragment.goBack();

		} else {
			long cur_time = System.currentTimeMillis();

			if ((cur_time - mLastBackPressedTime) < 1000) {
				super.onBackPressed();
			} else {
				mLastBackPressedTime = cur_time;
				Toast.makeText(
						HomeActivity.this,
						getResources().getString(
								R.string.press_back_again_to_exit),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	// ----------------------------------自定义方法
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
		boolean[] initPages;// 页面是否已经初始化

		public ViewPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments, String[] titles) {
			super(fm);
			this.fragments = fragments;
			this.titles = titles;
			if (fragments != null) {
				initPages = new boolean[fragments.size()];
			}
		}

		@Override
		public SecretFragment getItem(int position) {
			return (SecretFragment) fragments.get(position);
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {

			if (position > 0 && !initPages[position]) {
				((BaseFragment) fragments.get(position)).onFirstLoad();
				initPages[position] = true;
			}

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

	// ---------------------------------外部接口-------------------------------
	public void finishLoaded(boolean isStopAtOnce) {
		if (isStopAtOnce) {
			mLoadingItem.getActionView().clearAnimation();
			mLoadingItem.setVisible(false);
			mMoreItem.setVisible(true);
			return;
		}
		AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setDuration(1500);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mLoadingItem.setVisible(false);
				mMoreItem.setVisible(true);
			}
		});
		mLoadingItem.getActionView().setAnimation(anim);

	}

	public void startLoading() {
		mLoadingItem.setVisible(true);
		mMoreItem.setVisible(false);
	}

}
