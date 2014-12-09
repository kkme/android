package com.link.bianmi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.unit.ninelock.NineLockActivity;

public class MainActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		View contentView = new View(this);
		contentView.setBackgroundResource(R.drawable.bg_splash);
		setContentView(contentView);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 没有登录，跳转欢迎页面
				if ((UserConfig.getInstance().getToken() == null || TextUtils
						.isEmpty(UserConfig.getInstance().getToken()))
						&& !UserConfig.getInstance().getIsGuest()) {
					launchActivity(WelcomeActivity.class);
					finish();
					return;
				}
				// 设置锁屏密码，跳转锁屏页面
				if (!UserConfig.getInstance().getLockPassKey().isEmpty()
						&& UserConfig.getInstance().getLockPassStartStatus()) {
					launchActivity(NineLockActivity.class);
					finish();
					return;
				}
				// 跳转首页
				launchActivity(HomeActivity.class);
				finish();
			}
		}, 1000);

	}
}
