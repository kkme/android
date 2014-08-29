package com.link.bianmi;

import java.io.File;
import java.util.Properties;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 
 * @Description 系统配置
 * @author pangfq
 * @date 2014年7月10日 上午10:04:30
 */
public class SysConfig {
	private static SysConfig mInstance;

	public static SysConfig getInstance() {
		if (mInstance == null) {
			mInstance = new SysConfig();
		}
		return mInstance;
	}

	private Properties mProperties;

	private SysConfig() {

		mProperties = new Properties();

		mProperties.setProperty("bianmi.debug", String.valueOf(true));// 是否为debug模式

		// ---------------debug
		mProperties.setProperty("bianmi.url.base.debug", "http://infinigag-us.aws.af.cm");
		mProperties.setProperty("bianmi.dbname.debug", "bianmi_d");
		mProperties.setProperty("bianmi.url.signup.debug", "http://121.40.88.136/bianmi/index.php/Index/Login/reg");
		mProperties.setProperty("bianmi.url.signin.debug", "http://121.40.88.136/bianmi/index.php/Index/Login/index");
		mProperties.setProperty("bianmi.url.signout.debug", "http://121.40.88.136/bianmi/index.php/Index/Login/signout");

		// ---------------release
		mProperties.setProperty("bianmi.dbname.release", "bianmi_v1");
		mProperties.setProperty("bianmi.url.base.release", "http://infinigag-us.aws.af.cm"); // Base URL
		mProperties.setProperty("bianmi.url.signup.release", "/signup"); // 注册
		mProperties.setProperty("bianmi.url.signin.release", "/signin"); // 登录
		mProperties.setProperty("bianmi.url.signout.release", "/signout"); // 登出

	}

	public String getRootPath() {
		String bianmiPath = getSDPath();
		if (TextUtils.isEmpty(bianmiPath)) {
			bianmiPath = BianmiApplication.getInstance().getFilesDir()
					.getPath()
					+ File.separator + "BianMi";
		} else {
			bianmiPath += File.separator + "BianMi";
		}

		return bianmiPath;
	}

	public String getSecretPath() {
		String secretPath = getRootPath() + File.separator + "secret";
		File dir = new File(secretPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return secretPath;
	}

	public String getSDPath() {
		String path = "";
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			path = Environment.getExternalStorageDirectory().toString();// 获取跟目录
		}
		return path;
	}

	public String getPathTemp() {
		String tempPath = getRootPath() + File.separator + "temp";
		File dir = new File(tempPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return tempPath;
	}

	/** 是否为Debug模式 **/
	public boolean isDebug() {
		return Boolean.valueOf(mProperties.getProperty("bianmi.debug"));
	}

	/** 获取数据库名称 **/
	public String getDName() {
		if (isDebug())
			return mProperties.getProperty("bianmi.dbname.debug");
		else
			return mProperties.getProperty("bianmi.dbname.release");
	}

	/** 获取BaseUrl **/
	public String getBaseUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.base.debug");
		} else {
			return mProperties.getProperty("bianmi.url.base.release");
		}
	}

	/** 获取Url:热门 **/
	public String getHotUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.base.debug") + "/hot/";
		} else {
			return mProperties.getProperty("bianmi.url.base.release") + "/hot/";
		}
	}

	/** 获取Url:朋友 **/
	public String getFriendUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.base.debug") + "/fresh/";
		} else {
			return mProperties.getProperty("bianmi.url.base.release")
					+ "/fresh/";
		}
	}

	/** 获取Url:附近 **/
	public String getNearbyUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.base.debug")
					+ "/trending/";
		} else {
			return mProperties.getProperty("bianmi.url.base.release")
					+ "/trending/";
		}
	}

	/** 获取注册URL **/
	public String getSignUpUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.signup.debug");
		} else {
			return mProperties.getProperty("bianmi.url.signup.release");
		}
	}

	/** 获取登录URL **/
	public String getSignInUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.signin.debug");
		} else {
			return mProperties.getProperty("bianmi.url.signin.release");
		}
	}

	/** 获取登出URL **/
	public String getSignOutUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.url.signout.debug");
		} else {
			return mProperties.getProperty("bianmi.url.signout.release");
		}
	}

	public static class Constant {
		public static final String INTENT_BUNDLE_KEY_ISGUEST = "is_guest";
	}

}
