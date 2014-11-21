package com.link.bianmi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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
	private RoundProgressBar mRoundBar;
	private OnListener mListener = null;

	private int mMax;
	private Handler mHandler = new Handler();

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mRoundBar.setProgress(mMax--);
			mHandler.postDelayed(mRunnable, 1000);
		}
	};

	public PlayerView(Context context) {
		this(context, null);
	}

	public PlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.player, this, true);
		mPlayBtn = (ImageButton) findViewById(R.id.player_btn);
		mRoundBar = (RoundProgressBar) findViewById(R.id.player_roundbar);

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		float paintWidth = array.getDimension(
				R.styleable.RoundProgressBar_paint_width, 5);
		array.recycle();

		array = context.obtainStyledAttributes(attrs, R.styleable.Play);
		array.recycle();
		mRoundBar.setPaintWidth(paintWidth);

		reset();

		mPlayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mStatus) {
				case INIT:
				case STOP:
					mStatus = PlayStatus.PLAYING;
					mPlayBtn.setBackgroundResource(R.drawable.btn_pause);
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
		mRoundBar.setProgress(0);
		mPlayBtn.setBackgroundResource(R.drawable.btn_play);
	}

	public void play(int max) {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_pause);
			mStatus = PlayStatus.PLAYING;
			mMax = max;
			mRoundBar.setMax(mMax);
			mHandler.post(mRunnable);
		}
	}

	public void stop() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_play);
			mStatus = PlayStatus.STOP;
			mHandler.removeCallbacks(mRunnable);
			mRoundBar.setProgress(0);
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