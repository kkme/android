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
			mInstance = new UserConfig(MyApplication.getInstance());
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
	public String getUserId() {
		return mPref.getString("bianmi.user.userid", null);
	}

	/** 设置token **/
	public void setToken(String token) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.token", token);
		editor.commit();
	}

	/** 获取token **/
	public String getToken() {
		return mPref.getString("bianmi.user.token", null);
	}

	/** 设置phone **/
	public void setPhone(String phone) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.phone", phone);
		editor.commit();
	}

	/** 获取phone **/
	public String getPhone() {
		return mPref.getString("bianmi.user.phone", null);
	}

	/** 设置加密后的登录密码 **/
	public void setPwd(String pwd) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.pwd", pwd);
		editor.commit();
	}

	/** 获取加密后的登录密码 **/
	public String getPwd() {
		return mPref.getString("bianmi.user.pwd", null);
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

	/** 保存手势密码 **/
	public void setLockPassKey(String key) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.lockpass.key", key);
		editor.commit();
	}

	/** 获取手势密码 **/
	public String getLockPassKey() {
		return mPref.getString("bianmi.user.lockpass.key", "");
	}

	/** 保存手势密码开启状态 **/
	public void setLockPassStartStatus(boolean success) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putBoolean("bianmi.user.lockpass.key.status", success);
		editor.commit();
	}

	/** 获取手势密码开启状态 **/
	public boolean getLockPassStartStatus() {
		return mPref.getBoolean("bianmi.user.lockpass.key.status", false);
	}

	/** 保存经度 **/
	public void setLongitude(float longitude) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putFloat("bianmi.user.location.longitude", longitude);
		editor.commit();
	}

	/** 获取经度 **/
	public float getLongitude() {
		return mPref.getFloat("bianmi.user.location.longitude", 22.57967f);
	}

	/** 保存纬度 **/
	public void setLatitude(float latitude) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putFloat("bianmi.user.location.latitude", latitude);
		editor.commit();
	}

	/** 获取纬度 **/
	public float getLatitude() {
		return mPref.getFloat("bianmi.user.location.latitude", 113.85987f);
	}

}
