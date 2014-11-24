package com.link.bianmi.activity;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.utility.Tools;

/**
 * 首次进入的欢迎界面
 * 
 * @author pangfq
 * @date 2014年7月24日 下午4:23:55
 */
public class WelcomeActivity extends BaseFragmentActivity {

	private View mSignUpBtn;
	private View mSignInBtn;
	private View mGuestBtn;
	private View mTipBtn;

	private ViewPager mViewPager;
	private View mViewPagerContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// 提示：滑动有惊喜
		mTipBtn = findViewById(R.id.tip_button);
		mTipBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mViewPager.setCurrentItem(1);
			}
		});

		// 点击注册
		mSignUpBtn = findViewById(R.id.signup_button);
		mSignUpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SysConfig.getInstance().smsAccess()) {
					launchActivity(SignUpBySmsActivity.class);
				} else {
					launchActivity(SignUpActivity.class);
				}
			}
		});

		// 点击登录
		mSignInBtn = findViewById(R.id.signin_button);
		mSignInBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				launchActivity(SignInActivity.class);
			}
		});

		// 点击游客
		mGuestBtn = findViewById(R.id.guest_button);
		mGuestBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserConfig.getInstance().setIsGuest(true);
				UserConfig.getInstance()
						.setUserId(UUID.randomUUID().toString());
				UserConfig.getInstance().setToken(UUID.randomUUID().toString());
				launchActivity(HomeActivity.class);
				finish();
			}
		});

		// 手机屏幕宽高
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int wScreen = dm.widthPixels;

		// ViewPager
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		LayoutParams params = mViewPager.getLayoutParams();
		params.width = wScreen - Tools.dip2px(this, 32);
		mViewPager.setLayoutParams(params);
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setPageMargin(Tools.dip2px(this, 8));
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPagerContainer = findViewById(R.id.viewpager_group);
		mViewPagerContainer.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mViewPager.dispatchTouchEvent(event);
			}
		});
	}

	private PagerAdapter mPageAdapter = new PagerAdapter() {

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(WelcomeActivity.this);
			switch (position) {
			case 0:
				imageView.setImageResource(R.drawable.bg_guider_0);
				break;
			case 1:
				imageView.setImageResource(R.drawable.bg_guider_1);
				break;
			case 2:
				imageView.setImageResource(R.drawable.bg_guider_2);
				break;
			}
			((ViewPager) container).addView(imageView, position);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	};

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		private int mLastPage = 0;

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				mSignUpBtn.clearAnimation();
				mSignInBtn.clearAnimation();
				mTipBtn.clearAnimation();
				AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
				alphaAnim.setDuration(200);
				alphaAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
						alphaAnim.setDuration(400);
						mTipBtn.setVisibility(View.VISIBLE);
						mTipBtn.startAnimation(alphaAnim);
					}
				});
				mSignInBtn.setVisibility(View.GONE);
				mSignInBtn.startAnimation(alphaAnim);
				mSignUpBtn.setVisibility(View.GONE);
				mSignUpBtn.startAnimation(alphaAnim);
			} else if (arg0 == 1) {
				if (mSignUpBtn.getVisibility() == View.VISIBLE)
					return;
				// 注册登录按钮消失，游客按钮出现
				mSignUpBtn.clearAnimation();
				mSignInBtn.clearAnimation();
				mGuestBtn.clearAnimation();
				mTipBtn.clearAnimation();
				AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
				alphaAnim.setDuration(200);
				alphaAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
						alphaAnim.setDuration(400);
						mSignUpBtn.setVisibility(View.VISIBLE);
						mSignUpBtn.startAnimation(alphaAnim);
						mSignInBtn.setVisibility(View.VISIBLE);
						mSignInBtn.startAnimation(alphaAnim);
					}
				});
				if (mLastPage == 0) {
					mTipBtn.setVisibility(View.GONE);
					mTipBtn.startAnimation(alphaAnim);
				} else if (mLastPage == 2) {
					mGuestBtn.setVisibility(View.GONE);
					mGuestBtn.startAnimation(alphaAnim);
				}

			} else if (arg0 == 2) {
				// 游客按钮消失，注册登录按钮出现
				mSignUpBtn.clearAnimation();
				mSignInBtn.clearAnimation();
				mGuestBtn.clearAnimation();
				AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
				alphaAnim.setDuration(200);
				alphaAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
						alphaAnim.setDuration(400);
						mGuestBtn.setVisibility(View.VISIBLE);
						mGuestBtn.startAnimation(alphaAnim);
					}
				});
				mSignUpBtn.setVisibility(View.GONE);
				mSignInBtn.setVisibility(View.GONE);
				mSignUpBtn.startAnimation(alphaAnim);
				mSignInBtn.startAnimation(alphaAnim);
			}

			mLastPage = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (mViewPagerContainer != null) {
				mViewPagerContainer.invalidate();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
}
