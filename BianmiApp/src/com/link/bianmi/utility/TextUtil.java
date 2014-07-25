package com.link.bianmi.utility;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.TextView;
import android.widget.Toast;

import com.link.bianmi.widget.TypefaceSpan;

public class TextUtil {

	public static SpannableString getTypefaceSpannableString(Context context,
			String string, String fontName) {
		SpannableString spannableString = new SpannableString(string);
		spannableString.setSpan(
				new TypefaceSpan(fontName, Typeface.createFromAsset(
						context.getAssets(), fontName), false), 0,
				spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}

	public static SpannableString getTypefaceSpannableString(Context context,
			String string, String fontName, boolean isBold) {
		SpannableString spannableString = new SpannableString(string);
		spannableString.setSpan(
				new TypefaceSpan(fontName, Typeface.createFromAsset(
						context.getAssets(), fontName), isBold), 0,
				spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}

	public static void copyTextViewString(TextView textView) {
		String content = textView.getText().toString();
		Context context = textView.getContext();
		copyString(context, content);
	}

	public static void copyString(Context context, String content) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("label", content);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(content);
		}
		Toast.makeText(context, "The text has been copied to clipboard.",
				Toast.LENGTH_SHORT).show();
	}

	public static String pasteString(Context context) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText().toString().trim();
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText().toString();
		}
	}

}
