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

	/** 设置sessionId **/
	public void setSessionId(String sessionId) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("bianmi.user.sessionid", sessionId);
		editor.commit();
	}

	/** 获取sessionId **/
	public String getSessionId() {
		return mPref.getString("bianmi.user.sessionid", null);
	}

	/** 获取虚拟deviceid **/
	public String getVirtualDeviceId() {
		return mPref.getString("engzo.prefer.virtualdeviceid", "");
	}

	/** 设置虚拟deviceid **/
	public void setVirtualDeviceId(String deviceId) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("engzo.prefer.virtualdeviceid", deviceId);
		editor.commit();
	}
}
