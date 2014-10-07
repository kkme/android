package com.link.bianmi.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.link.bianmi.R;

public class SuperToast {

	private static Toast mToast;
	private static SuperToast mSuperToast = new SuperToast();

	public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
	public static final int LENGTH_LONG = Toast.LENGTH_LONG;

	public static SuperToast makeText(Context context, int resId, int duration) {

		return makeText(context, context.getString(resId), duration);
	}

	public static SuperToast makeText(Context context, String text, int duration) {

		if (mToast == null) {
			mToast = new Toast(context);
			View rootView = LayoutInflater.from(context).inflate(
					R.layout.supertoast, null);
			TextView textView = (TextView) rootView.findViewById(R.id.textview);
			textView.setText(text);
			mToast.setView(rootView);
			mToast.setDuration(duration);
			mToast.setMargin(0, 0);
			mToast.setGravity(Gravity.BOTTOM, 0, 0);
		}

		return mSuperToast;
	}

	public void show() {
		if (mToast != null) {
			mToast.show();
		}
	}

}
