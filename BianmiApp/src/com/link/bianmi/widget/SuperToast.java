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
	private static TextView mText;
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
			mText = (TextView) rootView.findViewById(R.id.textview);
			mToast.setView(rootView);
			mToast.setMargin(0, 0);
			mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
		}

		mToast.setDuration(duration);
		mText.setText(text);

		return mSuperToast;
	}

	public void show() {
		if (mToast != null) {
			mToast.show();
		}
	}

}
