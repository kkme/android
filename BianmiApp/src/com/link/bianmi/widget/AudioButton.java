package com.link.bianmi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.link.bianmi.R;

/**
 * 根据传入时间自动调整宽度的组件*
 */
public class AudioButton extends FrameLayout {

	public AudioButton(Context context) {
		super(context);
		initView(null);
	}

	public AudioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		LayoutInflater.from(getContext()).inflate(R.layout.audio_button_layout,
				this);
	}
}
