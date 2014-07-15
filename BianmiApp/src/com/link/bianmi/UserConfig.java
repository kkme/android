package com.link.bianmi;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.link.bianmi.utility.SecurityUtils;

/**
 * 用户配置
 * 
 * @author pangfq
 * 
 */
public class UserConfig {
	public static String TAG = "UserConfig";

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

	public SharedPreferences getPref() {
		return mPref;
	}

	/** 获取用户加密的databaseKey **/
	private String mDataBaseKey = "";
	private final String SecuDataBaseKey = "sllzoeng";

	public String getSecuDataBaseKey() {

		if (!SysConfig.getInstance().isDebug()
				&& TextUtils.isEmpty(mDataBaseKey)) {
			try {
				String key = mPref.getString("engzo.user.sh", "");
				if (TextUtils.isEmpty(key)) {
					key = SecurityUtils.getMD5Str(UUID.randomUUID().toString());
					setSecuDataBaseKey(key);
				}
				mDataBaseKey = SecurityUtils.encryptDES(key, SecuDataBaseKey);

			} catch (Exception e) {
			}
		}

		return mDataBaseKey;
	}

	private void setSecuDataBaseKey(String key) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("engzo.user.sh", key);
		editor.commit();
	}

	/** 初次同步课程版本标记 **/
	private final static String FirstSyncVersion = ".V4";

	/** 初次同步课程标记 **/
	public boolean isFirstSyncDone() {
		return UserConfig
				.getInstance()
				.getPref()
				.getBoolean("engzo.user.first_sync_done" + FirstSyncVersion,
						false);
	}

	/** 更新初次同步课程标记 **/
	public void setFirstSyncDone(boolean firstSyncDone) {
		SharedPreferences.Editor editor = UserConfig.getInstance().getPref()
				.edit();
		editor.putBoolean("engzo.user.first_sync_done" + FirstSyncVersion,
				firstSyncDone);
		editor.commit();
	}

	/** 修复版本bug标记 **/
	private final static String FixVersionBugVersion = ".V5";

	/** 修复版本bug标记 **/
	public boolean isFixVersionBugDone() {
		return UserConfig
				.getInstance()
				.getPref()
				.getBoolean("engzo.user.fix_bug_done" + FixVersionBugVersion,
						false);
	}

	/** 修复版本bug标记 **/
	public void setFixVersioBugDone(boolean fixBugDone) {
		SharedPreferences.Editor editor = UserConfig.getInstance().getPref()
				.edit();
		editor.putBoolean("engzo.user.fix_bug_done" + FixVersionBugVersion,
				fixBugDone);
		editor.commit();
	}

	/** 获取得分颜色 ***/
	public int getPreferColor() {
		return mPref.getInt("engzo.prefer.scorecolor", 1);
	}

	/** 更新得分颜色 ***/
	public void setPreferColor(int colorgroup) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putInt("engzo.prefer.scorecolor", colorgroup);
		editor.commit();
	}

	/** 全屏标记 **/
	public boolean isFullScreen() {
		return UserConfig.getInstance().getPref()
				.getBoolean("engzo.user.fullscreen", false);
	}

	/** 全屏标记 **/
	public void setFullScreen(boolean fullscreen) {
		SharedPreferences.Editor editor = UserConfig.getInstance().getPref()
				.edit();
		editor.putBoolean("engzo.user.fullscreen", fullscreen);
		editor.commit();
	}

	/** 设置自动停止录音 **/
	public boolean isAutoStop() {
		return UserConfig.getInstance().getPref()
				.getBoolean("engzo.user.expert.autostop", true);
	}

	/** 设置自动停止录音 **/
	public void setAutoStop(boolean autoStop) {
		SharedPreferences.Editor editor = UserConfig.getInstance().getPref()
				.edit();
		editor.putBoolean("engzo.user.expert.autostop", autoStop);
		editor.commit();
	}

	/** 获取虚拟deviceid **/
	public String getVirtualDeviceId() {
		return mPref.getString("engzo.prefer.virtualdeviceid", "");
	}

	/** 设置虚拟deviceid **/
	public void setVirtualDeviceId(String deviceId) {
		SharedPreferences.Editor editor = UserConfig.getInstance().getPref()
				.edit();
		editor.putString("engzo.prefer.virtualdeviceid", deviceId);
		editor.commit();
	}

}
