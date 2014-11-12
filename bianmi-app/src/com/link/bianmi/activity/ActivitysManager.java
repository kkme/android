package com.link.bianmi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * 管理所有Activity的全局类
 * 
 * @author pangfq
 * @date 2014-8-15 下午10:46:25
 */
public class ActivitysManager {
	private static List<Activity> sActivitysList = null;

	/**
	 * 添加Activity
	 */
	public static void onResume(Activity activity) {
		if (sActivitysList == null) {
			sActivitysList = new ArrayList<Activity>();
		}

		sActivitysList.add(activity);
	}

	/**
	 * 移除Activity
	 */
	public static void onDestory(Activity activity) {
		if (sActivitysList == null) {
			sActivitysList = new ArrayList<Activity>();
		}
		sActivitysList.remove(activity);
	}

	/**
	 * 移除全部Activity
	 */
	public static void removeAllActivity() {

		for (int i = 0; i < sActivitysList.size(); i++) {
			Activity ac = sActivitysList.get(i);
			if (null != ac) {
				ac.finish();
			}
		}
		sActivitysList.clear();
	}

	public static Activity getActivityByName(String name) {

		for (Activity ac : sActivitysList) {

			if (ac.getClass().getName()
					.substring(ac.getClass().getName().lastIndexOf(".") + 1)
					.equals(name)) {
				return ac;
			}
		}

		return null;
	}

}
