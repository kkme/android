package com.link.bianmi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.link.bianmi.R;

/**
 * 播放器
 */
public class PlayerSuit extends RelativeLayout {

	private ImageButton mPlayBtn;
	private RoundProgressBar mRoundBar;

	public PlayerSuit(Context context) {
		this(context, null);
	}

	public PlayerSuit(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.player, this, true);
		mPlayBtn = (ImageButton) findViewById(R.id.player_btn);
		mRoundBar = (RoundProgressBar) findViewById(R.id.player_roundBar);

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		float paintWidth = array.getDimension(
				R.styleable.RoundProgressBar_paint_width, 5);
		array.recycle();

		array = context.obtainStyledAttributes(attrs, R.styleable.Play);
		array.recycle();
		mRoundBar.setPaintWidth(paintWidth);

		resetStatus();

		mPlayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mStatus) {
				case INIT:
				case PAUSE:
				case STOP:
					mListener.onPlay();
					mStatus = PlayStatus.PLAYING;
					mRoundBar.setProgress(0);
					mPlayBtn.setBackgroundResource(R.drawable.btn_pause);
					break;
				case PLAYING:
					mListener.onPause();
					mStatus = PlayStatus.PAUSE;
					mPlayBtn.setBackgroundResource(R.drawable.btn_playing);
					break;
				}
			}
		});

	}

	private PlayStatus mStatus;

	/** 播放状态 **/
	private enum PlayStatus {
		INIT, // 初始
		PLAYING, // 正在播放
		PAUSE, // 暂停播放
		STOP, // 停止播放
	}

	// ------------------------------------------Public--------------------------------------

	/** 重置状态 **/
	public void resetStatus() {
		mStatus = PlayStatus.INIT;
		mRoundBar.setProgress(0);
		mPlayBtn.setBackgroundResource(R.drawable.btn_play);
	}

	private OnListener mListener = null;

	public void setOnListener(OnListener listener) {
		mListener = listener;
	}

	public void play() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_pause);
		}
	}

	public void stop() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_play);
		}
	}

	public void pause() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_playing);
		}
	}

	public interface OnListener {
		public void onPlay();

		public void onPause();

		public void onStop();
	}

}