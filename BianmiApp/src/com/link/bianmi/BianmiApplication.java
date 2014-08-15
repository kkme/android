package com.link.bianmi;

import android.app.Application;

public class BianmiApplication extends Application {

	private static BianmiApplication instance;

	public static BianmiApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	/**
	 * 退出登录
	 */
	public void signOut() {
		UserConfig.getInstance().setSessionId("");
		UserConfig.getInstance().setIsGuest(false);
	}

}
