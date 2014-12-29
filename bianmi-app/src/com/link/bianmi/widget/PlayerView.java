package com.link.bianmi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.link.bianmi.R;

/**
 * 播放控件
 * 
 * @author pangfq
 * @date 2014年11月20日 上午11:14:45
 */
public class PlayerView extends RelativeLayout {

	private ImageButton mPlayBtn;
	private ImageView mPlayingView;
	private OnListener mListener = null;

	public PlayerView(Context context) {
		this(context, null);
	}

	public PlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.player_view, this, true);
		mPlayBtn = (ImageButton) findViewById(R.id.player_btn);
		mPlayingView = (ImageView) findViewById(R.id.playing_imageview);

		reset();

		mPlayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mStatus) {
				case INIT:
				case STOP:
					mStatus = PlayStatus.PLAYING;
					mPlayBtn.setBackgroundResource(R.drawable.bg_record_b);
					if (mListener != null) {
						mListener.onPlay();
					}
					break;
				case PLAYING:
					mStatus = PlayStatus.STOP;
					mPlayBtn.setBackgroundResource(R.drawable.btn_play);
					if (mListener != null) {
						mListener.onStop();
					}
					break;
				}
			}
		});

	}

	private PlayStatus mStatus;

	/** 播放状态 **/
	private enum PlayStatus {
		INIT, // 初始
		PLAYING, // 播放中
		STOP, // 停止
	}

	// ------------------------------------------Public--------------------------------------

	/** 重置 **/
	public void reset() {
		mStatus = PlayStatus.INIT;
		mPlayBtn.setBackgroundResource(R.drawable.btn_play);
		mPlayingView.setVisibility(View.GONE);
	}

	public void play() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.bg_record_b);
			mPlayingView.setVisibility(View.VISIBLE);
			mStatus = PlayStatus.PLAYING;
		}
	}

	public void stop() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_play);
			mStatus = PlayStatus.STOP;
			mPlayingView.setVisibility(View.GONE);
		}
	}

	public void setOnListener(OnListener listener) {
		mListener = listener;
	}

	public interface OnListener {
		public void onPlay();

		public void onStop();
	}

}