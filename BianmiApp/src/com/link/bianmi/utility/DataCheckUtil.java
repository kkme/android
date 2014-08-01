package com.link.bianmi.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 数据合法性验证
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
	 * 校验注册数据合法性
	 * 
	 * @param context
	 * @param phonenum
	 * @param password
	 * @param passwordConfirm
	 * @return
	 */
	public static boolean checkSignUpData(Context context, String phonenum,
			String password, String passwordConfirm) {
		boolean ok = true;
		if (TextUtils.isEmpty(phonenum)) {
			Toast.makeText(context, "phonenum为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		} else if (!isPhoneNum(phonenum)) {
			Toast.makeText(context, "WrongPhonenumber!", Toast.LENGTH_SHORT)
					.show();
			ok = false;
		} else if (TextUtils.isEmpty(password)) {
			Toast.makeText(context, "password为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		} else if (TextUtils.isEmpty(passwordConfirm)) {
			Toast.makeText(context, "passwordConfirm为空！", Toast.LENGTH_SHORT)
					.show();
			ok = false;
		} else if (!password.equals(passwordConfirm)) {
			Toast.makeText(context, "两次输入密码不一致!", Toast.LENGTH_SHORT).show();
			ok = false;
		}

		return ok;
	}

	/**
	 * 校验登录数据合法性
	 * 
	 * @param context
	 * @param phonenum
	 * @param password
	 * @return
	 */
	public static boolean checkSignInData(Context context, String phonenum,
			String password) {
		boolean ok = true;
		if (TextUtils.isEmpty(phonenum)) {
			Toast.makeText(context, "phonenum为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		} else if (!isPhoneNum(phonenum)) {
			Toast.makeText(context, "WrongPhonenumber!", Toast.LENGTH_SHORT)
					.show();
			ok = false;
		} else if (TextUtils.isEmpty(password)) {
			Toast.makeText(context, "password为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		}

		return ok;
	}
}
