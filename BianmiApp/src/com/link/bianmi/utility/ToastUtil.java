package com.link.bianmi.utility;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.link.bianmi.R;

public class ToastUtil {

	public static void showToast(Context context, CharSequence text) {
		TextView textView = new TextView(context);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundResource(R.drawable.mxx_toast_frame);
		textView.setTextColor(0xffffffff);
		textView.setTextSize(14);
		textView.setText(text);
		Toast toast = new Toast(context);
		toast.setView(textView);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

}
