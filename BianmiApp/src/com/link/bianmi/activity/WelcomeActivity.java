package com.link.bianmi.activity;

import java.util.UUID;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// 点击注册
		mSignUpBtn = findViewById(R.id.signup_button);
		mSignUpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				launchActivity(SignUpActivity.class);
			}
		});

		// 点击登录
		mSignInBtn = findViewById(R.id.signin_button);
		mSignInBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				launchActivityForResult(SignInActivity.class, 8888);
			}
		});

		// 点击游客
		mGuestBtn = findViewById(R.id.guest_button);
		mGuestBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserConfig.getInstance().setIsGuest(true);
				UserConfig.getInstance().setSessionId(UUID.randomUUID().toString());
				launchActivity(MainActivity.class);
				finishActivity();
			}
		});

		// ViewPager
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(mPageAdapter);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setPageMargin(30);
		viewPager.setOnPageChangeListener(mPageChangeListener);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 登录成功
		if (resultCode == 8888) {
			finishActivity();
		}
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
			imageView.setBackgroundColor(Color.GREEN);
			((ViewPager) container).addView(imageView, position);
			return imageView;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	};

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				mSignUpBtn.setVisibility(View.VISIBLE);
				mSignInBtn.setVisibility(View.VISIBLE);
				mGuestBtn.setVisibility(View.GONE);
			} else if (arg0 == 1) {
				if (mSignUpBtn.getVisibility() == View.VISIBLE)
					return;
				// 注册登录按钮消失，游客按钮出现
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
						mSignUpBtn.setVisibility(View.VISIBLE);
						mSignUpBtn.startAnimation(alphaAnim);
						mSignInBtn.setVisibility(View.VISIBLE);
						mSignInBtn.startAnimation(alphaAnim);
					}
				});
				mGuestBtn.setVisibility(View.GONE);
				mGuestBtn.startAnimation(alphaAnim);

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
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
}
