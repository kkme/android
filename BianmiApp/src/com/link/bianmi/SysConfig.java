package com.link.bianmi;

import java.util.Properties;

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
		mProperties.setProperty("bianmi.base.url.debug",
				"http://infinigag-us.aws.af.cm");// base url
		mProperties.setProperty("bianmi.dbname.debug", "bianmi_d");

		// ---------------release
		mProperties.setProperty("bianmi.base.url.release",
				"http://infinigag-us.aws.af.cm");// base url
		mProperties.setProperty("bianmi.dbname.release", "bianmi_v1");
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
			return mProperties.getProperty("bianmi.base.url.debug");
		} else {
			return mProperties.getProperty("bianmi.base.url.release");
		}
	}

	/** 获取Url:热门 **/
	public String getHotUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.base.url.debug") + "/hot/";
		} else {
			return mProperties.getProperty("bianmi.base.url.release") + "/hot/";
		}
	}

	/** 获取Url:朋友 **/
	public String getFriendUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.base.url.debug") + "/fresh/";
		} else {
			return mProperties.getProperty("bianmi.base.url.release")
					+ "/fresh/";
		}
	}

	/** 获取Url:附近 **/
	public String getNearbyUrl() {
		if (isDebug()) {
			return mProperties.getProperty("bianmi.base.url.debug")
					+ "/trending/";
		} else {
			return mProperties.getProperty("bianmi.base.url.release")
					+ "/trending/";
		}
	}

}
