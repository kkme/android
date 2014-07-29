package com.link.bianmi.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.utility.Tools;
import com.link.bianmi.widget.NotifyingScrollView;
import com.link.bianmi.widget.ViewPagerTabBar;

public class AboutActivity extends BaseFragmentActivity {

	private ViewPager mViewPager;
	private ViewPagerTabBar mTabStrip;
	private NotifyingScrollView view_gag, view_me;
	int last_t = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("关于");
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_about);
		mViewPager = (ViewPager) findViewById(R.id.about_viewpager);
		mViewPager.setOffscreenPageLimit(3);
		mTabStrip = (ViewPagerTabBar) findViewById(R.id.about_tab);
		ArrayList<View> views = new ArrayList<View>(2);
		view_gag = (NotifyingScrollView) LayoutInflater.from(this).inflate(
				R.layout.about_layout_author, null);
		view_me = (NotifyingScrollView) LayoutInflater.from(this).inflate(
				R.layout.about_layout_author, null);
		views.add(view_me);
		views.add(view_gag);
		mViewPager.setAdapter(new ViewpagerAdapter(views, new String[] {
				"author", "9gag" }));
		mTabStrip.setViewPager(mViewPager);
		// initInsetTop();

		final int max_tranY = Tools.dip2px(this, 48);

		NotifyingScrollView.OnScrollChangedListener onScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {

			@Override
			public void onScrollChanged(ScrollView who, int l, int t, int oldl,
					int oldt) {
				// TODO Auto-generated method stub
				if (t < 0) {
					return;
				}
				int deltaY = last_t - t;
				last_t = t;
				float tran_y = mTabStrip.getTranslationY() + deltaY;
				if (tran_y >= 0) {
					mTabStrip.setTranslationY(0);
				} else if (tran_y < -max_tranY) {
					mTabStrip.setTranslationY(-max_tranY);
				} else {
					mTabStrip.setTranslationY(tran_y);
				}
				// Log.e("TEMP",
				// String.format("l:%d t:%d oldl:%d oldt:%d dy:%d", l, t, oldl,
				// oldt, deltaY));
			}
		};
		view_gag.setOnScrollChangedListener(onScrollChangedListener);
		view_me.setOnScrollChangedListener(onScrollChangedListener);
		mTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				if (mTabStrip.getTranslationY() != 0) {
					mTabStrip.setTranslationY(0);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	// private void initInsetTop() {
	// final int top_margin = MxxUiUtil.dip2px(this, 48);
	// view_gag.setPadding(0, config.getPixelInsetTop(true) + top_margin,
	// config.getPixelInsetRight(), config.getPixelInsetBottom());
	// view_gag.requestLayout();
	// view_me.setPadding(0, config.getPixelInsetTop(true) + top_margin,
	// config.getPixelInsetRight(), config.getPixelInsetBottom());
	// view_me.requestLayout();
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ViewpagerAdapter extends PagerAdapter {

		private List<View> views;
		private String[] titles;

		public ViewpagerAdapter(List<View> views, String[] titles) {
			this.views = views;
			this.titles = titles;
		}

		public ViewpagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			if (titles == null) {
				return super.getPageTitle(position);
			} else {
				return titles[position];
			}

		}

	}

}
