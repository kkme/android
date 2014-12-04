package com.link.bianmi.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘工具类
 */
public class SoftInputUtils {

	/**
	 * 弹出软键盘
	 */
	public static void popupSoftInput(final EditText edittext) {
		if (edittext == null)
			return;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) edittext
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(edittext, 0);
			}
		}, 500);

	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInput(final Activity activity) {
		if (activity == null)
			return;
		InputMethodManager inputManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		View focusView = activity.getCurrentFocus();
		if (focusView == null)
			return;
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);

	}

}
