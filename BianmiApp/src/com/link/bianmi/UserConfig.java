package com.link.bianmi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 用户配置
 * 
 * @author pangfq
 * 
 */
public class UserConfig {

	private SharedPreferences mPref;
	private static UserConfig mInstance = null;

	public static UserConfig getInstance() {
		if (null == mInstance) {
			mInstance = new UserConfig(BianmiApplication.getInstance());
		}
		return mInstance;
	}

	private UserConfig(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/** 设置userId **/
	public void setUserId(String userId) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.userid", userId);
		editor.commit();
	}

	/** 获取userId **/
	public String getSessionId() {
		return mPref.getString("bianmi.user.userid", null);
	}

	/** 获取虚拟deviceid **/
	public String getVirtualDeviceId() {
		return mPref.getString("bianmi.prefer.virtualdeviceid", "");
	}

	/** 设置虚拟deviceid **/
	public void setVirtualDeviceId(String deviceId) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.prefer.virtualdeviceid", deviceId);
		editor.commit();
	}

	/** 设置游客登录 **/
	public void setIsGuest(boolean isGuest) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean("bianmi.user.isguest", isGuest);
		editor.commit();
	}

	/** 获取是否是游客登录 **/
	public boolean getIsGuest() {
		return mPref.getBoolean("bianmi.user.isguest", false);
	}

	/** 保存锁定密码 **/
	public void setLockPassKey(String key) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.lockpass.key", key);
		editor.commit();
	}

	/** 获取锁定密码 **/
	public String getLockPassKey() {
		return mPref.getString("bianmi.user.lockpass.key", "");
	}

	/** 保存密码输入成功 **/
	public void setLockPassSuccess(boolean success) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean("bianmi.user.lockpass.key.success", success);
		editor.commit();
	}

	/** 获取密码是否输入成功 **/
	public boolean getLockPassSuccess() {
		return mPref.getBoolean("bianmi.user.lockpass.key.success", false);
	}

}
