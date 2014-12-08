package com.link.bianmi.utils;

import android.content.Context;

import com.link.bianmi.R;

/**
 * 时间工具类
 * 
 * @author pangfq
 * @date 2014年12月8日 下午4:45:09
 */
public class TimeUtil {

	private static final long ONE_MINUTE = 60;
	private static final long ONE_HOUR = 60 * ONE_MINUTE;
	private static final long ONE_DAY = 24 * ONE_HOUR;
	private static final long ONE_MONTH = 30 * ONE_DAY;
	private static final long ONE_YEAR = 12 * ONE_MONTH;

	public static String formatTimeAgo(Context context, long timePassed) {
		long timeIntoFormat;
		timePassed = timePassed / 1000;
		if (timePassed < ONE_MINUTE) {
			return timePassed
					+ context.getResources().getString(R.string.second);
		} else if (timePassed < ONE_HOUR) {
			timeIntoFormat = timePassed / ONE_MINUTE;
			return timeIntoFormat
					+ context.getResources().getString(R.string.minute);
		} else if (timePassed < ONE_DAY) {
			timeIntoFormat = timePassed / ONE_HOUR;
			return timeIntoFormat
					+ context.getResources().getString(R.string.hour);
		} else if (timePassed < ONE_MONTH) {
			timeIntoFormat = timePassed / ONE_DAY;
			return timeIntoFormat
					+ context.getResources().getString(R.string.day);
		} else if (timePassed < ONE_YEAR) {
			timeIntoFormat = timePassed / ONE_MONTH;
			return timeIntoFormat
					+ context.getResources().getString(R.string.month);
		} else {
			timeIntoFormat = timePassed / ONE_YEAR;
			return timeIntoFormat
					+ context.getResources().getString(R.string.year);
		}
	}

}