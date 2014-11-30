package com.link.bianmi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;

import com.link.bianmi.R;
import com.link.bianmi.widget.SuperToast;

/**
 * 数据合法性验证
 * 
 * @author pangfq
 * @date 2014年8月1日 上午9:52:12
 */
public class DataCheckUtil {

	/**
	 * 是否是手机号码
	 * 
	 * @param mobilePhoneNum
	 * @return
	 */
	private static boolean isPhoneNum(String phone) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phone);
		return m.matches();

	}

	/**
	 * 校验登录数据合法性
	 * 
	 * @param context
	 * @param phone
	 * @param pwd
	 * @return
	 */
	public static boolean checkSignInUpData(Context context, String phone,
			String pwd) {
		boolean ok = true;
		if (TextUtils.isEmpty(phone)) {
			SuperToast.makeText(context,
					context.getString(R.string.phone_empty),
					SuperToast.LENGTH_SHORT).show();
			ok = false;
		} else if (!isPhoneNum(phone)) {
			SuperToast.makeText(context,
					context.getString(R.string.phone_error),
					SuperToast.LENGTH_SHORT).show();
			ok = false;
		} else if (TextUtils.isEmpty(pwd)) {
			SuperToast.makeText(context, context.getString(R.string.pwd_empty),
					SuperToast.LENGTH_SHORT).show();
			ok = false;
		}

		return ok;
	}
}
