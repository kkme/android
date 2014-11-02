package com.link.bianmi.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;

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
	private static boolean isPhoneNum(String phoneNum) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phoneNum);
		return m.matches();

	}

	/**
	 * 校验登录数据合法性
	 * 
	 * @param context
	 * @param phonenum
	 * @param password
	 * @return
	 */
	public static boolean checkSignInUpData(Context context, String phonenum,
			String password) {
		boolean ok = true;
		if (TextUtils.isEmpty(phonenum)) {
			SuperToast
					.makeText(context, "phonenum为空！", SuperToast.LENGTH_SHORT)
					.show();
			ok = false;
		} else if (!isPhoneNum(phonenum)) {
			SuperToast.makeText(context, "WrongPhonenumber!",
					SuperToast.LENGTH_SHORT).show();
			ok = false;
		} else if (TextUtils.isEmpty(password)) {
			SuperToast
					.makeText(context, "password为空！", SuperToast.LENGTH_SHORT)
					.show();
			ok = false;
		}

		return ok;
	}
}
