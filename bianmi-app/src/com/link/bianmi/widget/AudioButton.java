package com.link.bianmi.widget;

import java.util.UUID;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.utility.IPlayer;
import com.link.bianmi.utility.Player;

/**
 * 根据传入时间自动调整宽度的组件*
 */
public class AudioButton extends FrameLayout {

	private String mId = "";
	private String mFilePath = "";
	private int mDuration = 0;
	private float mMinWidth = 0;
	private float mMaxWidth = 0;
	private boolean mIsScalable = true;
	private View mLoadingView;
	private View mStoppedView;
	private View mPlayingView;

	private TextView mDuationText;

	public AudioButton(Context context) {
		super(context);
		initView(null);
	}

	public AudioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;

		if (mIsScalable) {
			float halfMaxWidth = mMaxWidth / 2;
			if (mDuration <= 3) {
				width = (int) mMinWidth;
			} else if (mDuration > 3 && mDuration <= 15) {
				// (3,15]时间段对应的长度(minWidth, halfMaxWidth]
				// 以minWidth 为基准,时间的偏移量线性对应extraWidth的偏移量
				width = (int) mMinWidth;
				float extraWidth = (mDuration - 3.0f) / (15.0f - 3.0f)
						* (halfMaxWidth - mMinWidth);
				width += extraWidth;
			} else if (mDuration > 15 && mDuration <= 120) {
				// (15,120]时间段对应的长度(halfMaxWidth, maxWidth]
				// 以halfMaxWidth 为基准,时间的偏移量线性对应extraWidth的偏移量
				width = (int) halfMaxWidth;
				float extraWidth = ((mDuration - 15.0f) / (120.0f - 15.0f))
						* (halfMaxWidth);
				width += extraWidth;
			} else {
				width = (int) mMaxWidth;
			}
		} else {
			width = (int) mMinWidth;
		}

		widthMeasureSpec = MeasureSpec.makeMeasureSpec(
				MeasureSpec.getSize(width), MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, getMeasuredHeight());
	}

	private void initView(AttributeSet attrs) {
		LayoutInflater.from(getContext()).inflate(R.layout.audio_button_layout,
				this);
		mMinWidth = getContext().getResources().getDimension(R.dimen.dp_72);
		mMaxWidth = getContext().getResources().getDimension(R.dimen.dp_204);
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.AudioButton);
			mIsScalable = a.getBoolean(R.styleable.AudioButton_ab_scalable,
					true);
			a.recycle();
		}
		mLoadingView = findViewById(R.id.preparing_view);
		mStoppedView = findViewById(R.id.stopped_view);
		mPlayingView = findViewById(R.id.playing_view);

		mDuationText = (TextView) findViewById(R.id.duration_text);
		showStoppedView();
		setAudioFile("", 0);
		mId = UUID.randomUUID().toString();

		this.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isPlaying = ForumAudioController.getInstance()
						.isPlaying(AudioButton.this);
				ForumAudioController.getInstance().stop(); // 停止播放
				if (!isPlaying) {
					ForumAudioController.getInstance().play(AudioButton.this);
				}

			}
		});

	}

	/** 获取标志 **/
	private String getTagId() {
		return mId + mFilePath;
	}

	private void showPreparingView() {
		mLoadingView.setVisibility(VISIBLE);
		mPlayingView.setVisibility(GONE);
		mStoppedView.setVisibility(GONE);
	}

	private void showPlayingView() {
		mPlayingView.setVisibility(VISIBLE);
		mStoppedView.setVisibility(GONE);
		mLoadingView.setVisibility(GONE);
	}

	private void showStoppedView() {
		mStoppedView.setVisibility(VISIBLE);
		mPlayingView.setVisibility(GONE);
		mLoadingView.setVisibility(GONE);
	}

	private void show(PlayStatus playStatus) {
		switch (playStatus) {
		case Playing:
			showPlayingView();
			break;
		case Stopped:
			showStoppedView();
			break;
		case Preparing:
			showPreparingView();
			break;
		}
	}

	enum PlayStatus {
		Playing, Stopped, Preparing
	}

	private static class ForumAudioController {
		private static ForumAudioController sInstance;
		private Player mPlayer; // 播放器
		private String mTagId = ""; // 最后一次播放的tagid
		private AudioButton mAudioButton; // 最后已从播放的audiobutton
		private PlayStatus mPlayStatus;

		/** 是否匹配 **/
		private boolean isMatch(AudioButton audioButton) {
			boolean result = false;
			if (audioButton != null && !TextUtils.isEmpty(mTagId)
					&& mTagId.compareTo(audioButton.getTagId()) == 0) {
				result = true;
			}
			return result;
		}

		private ForumAudioController() {
			mPlayer = new Player();
			mPlayer.setOnListener(new IPlayer.OnListener() {

				@Override
				public void OnCompletion() {
					mPlayStatus = PlayStatus.Stopped;
					if (isMatch(mAudioButton)) {
						mAudioButton.showStoppedView();
					}

				}

				@Override
				public void OnPrepared(int i) {
					mPlayStatus = PlayStatus.Playing;
					if (isMatch(mAudioButton)) {
						mAudioButton.showPlayingView();
					}

				}

				@Override
				public void OnBufferingupdate(int i) {

				}

				@Override
				public void OnError(Exception e) {
					mPlayStatus = PlayStatus.Stopped;
					if (isMatch(mAudioButton)) {
						mAudioButton.showStoppedView();
					}
				}

				@Override
				public void OnCurrentPosition(int i) {

				}
			});

		}

		public static ForumAudioController getInstance() {
			if (sInstance == null) {
				sInstance = new ForumAudioController();
			}
			return sInstance;
		}

		/** 停止当前播放 **/
		public void stop() {
			mPlayer.release();
			if (isMatch(mAudioButton)) {
				mAudioButton.showStoppedView();
			}
			mPlayStatus = PlayStatus.Stopped;
			mAudioButton = null;
		}

		/** 播放 **/
		public void play(AudioButton audioButton) {
			mTagId = audioButton.getTagId();// 当前播放第tag
			if (mPlayer.isPlaying()) {
				stop();
			}
			mAudioButton = audioButton;
			mAudioButton.showPreparingView();
			mPlayStatus = PlayStatus.Preparing;
			mPlayer.playUrl(audioButton.mFilePath);

		}

		/** 按钮是否正在播放 **/
		public boolean isPlaying(AudioButton audioButton) {
			boolean result = false;
			if (mPlayStatus != PlayStatus.Stopped && isMatch(audioButton))
				result = true;
			return result;
		}

		/** 播放状态 **/
		public PlayStatus getPlayStatus() {
			return mPlayStatus;
		}

	}

	// -----------------外部调用方法-----------------

	/** 恢复播放状态 **/
	public void restoreStatus() {
		boolean isPlaying = ForumAudioController.getInstance().isPlaying(
				AudioButton.this);
		if (isPlaying) {
			show(ForumAudioController.getInstance().getPlayStatus());
		} else {
			show(PlayStatus.Stopped);
		}
	}

	/** 设置音频 **/
	public void setAudioFile(String filePath, int duration) {

		if (TextUtils.isEmpty(filePath)) {
			setVisibility(View.GONE);
		} else {
			setVisibility(View.VISIBLE);
		}

		mDuration = duration;
		mFilePath = filePath;

		if (mDuration != 0) {
			mDuationText.setText(mDuration + "s\"");
		} else {
			// 音频时长为0时，则定位unknow状态，在此状态下不显示长度信息
			mDuationText.setText("");
		}

	}

	/** 停止播放 **/
	public static void stop() {
		ForumAudioController.getInstance().stop();
	}

	/** 设置组件是否可伸展 **/
	public void setScalable(boolean isScalable) {
		if (isScalable != mIsScalable) {
			mIsScalable = isScalable;
			invalidate();
		}
	}
}
