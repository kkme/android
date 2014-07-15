package com.link.bianmi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.link.bianmi.R;

/** 论坛输入套件 **/
public class InputSuit extends LinearLayout {

	public InputSuit(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InputSuit(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.input_suit, this, true);
	}
}
