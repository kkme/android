package com.link.bianmi;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.link.bianmi.utility.NetWorkHelper;
import com.link.bianmi.utility.NetWorkHelper.NetWorkType;

public class BianmiApplication extends Application {

	private static BianmiApplication instance;

	private NetWorkType mNetwork = NetWorkType.NET_INVALID; // 网络情况

	private boolean mInited = false;

	public static BianmiApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		mNetwork = NetWorkHelper.getNetWorkType(this);// 检测当前网络

		if (!mInited) {

			mNetwork = NetWorkHelper.getNetWorkType(this);// 检测当前网络
			initNetReceiver();
			// 初始化环境数据
			mInited = true;
		}
	}

	// 初始化监听网络
	private void initNetReceiver() {
		IntentFilter intenFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		BroadcastReceiver netReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				mNetwork = NetWorkHelper.getNetWorkType(BianmiApplication
						.getInstance());// 检测当前网络
				if (mNetwork != NetWorkType.NET_INVALID) {
					// SyncManager.syncData();
					// SyncManager.syncDataBackGround();
				}

			}
		};
		registerReceiver(netReceiver, intenFilter);// 注册监听网络变化
	}

	/**
	 * 获取当前网络状态
	 * 
	 * @return
	 */
	public NetWorkType getNetwork() {
		return mNetwork;
	}

	public boolean isNetworkDown() {
		return mNetwork == NetWorkType.NET_INVALID;
	}

	/**
	 * 退出登录
	 */
	public void signOut() {
		UserConfig.getInstance().setUserId("");
		UserConfig.getInstance().setIsGuest(false);
	}

}
