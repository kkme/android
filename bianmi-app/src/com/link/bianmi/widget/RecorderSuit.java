package com.link.bianmi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.link.bianmi.R;

/**
 * 录音器
 */
public class RecorderSuit extends RelativeLayout {

	/** 默认状态背景 **/
	private View mStartRecordBgView;
	/** 正在录音背景 **/
	private View mRecordingBgView;
	/** 伞状背景 **/
	private View mUmbrellaView;
	/** 麦克风背景 **/
	private View mMicView;
	/** 进度条 **/
	private RoundProgressBar mRoundBar;

	private View mView;

	private boolean mIsRecord = true;

	private float mLastPower = 0.0f;

	public RecorderSuit(Context context) {
		this(context, null);
	}

	public RecorderSuit(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.recorder, this, true);

		mStartRecordBgView = findViewById(R.id.start_record_bg_view);
		mRecordingBgView = findViewById(R.id.recording_bg_view);
		mUmbrellaView = findViewById(R.id.record_umbrella_view);
		mMicView = findViewById(R.id.record_mic_view);
		mRoundBar = (RoundProgressBar) findViewById(R.id.record_roundBar);

		reset();

		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (mStatus) {
				case INIT:
					showVolume(true);
					mListener.onStartRecord();
					mStatus = RecordStatus.RECORDING;
					mView.setVisibility(View.VISIBLE);
					break;
				case RECORDING:
					showVolume(false);
					mListener.onStopRecord();
					mStatus = RecordStatus.STOP;
					mView.setVisibility(View.GONE);
					break;
				case STOP:
					showVolume(true);
					mListener.onStartRecord();
					mStatus = RecordStatus.RECORDING;
					mView.setVisibility(View.VISIBLE);
					break;
				}
			}
		});

	}

	private RecordStatus mStatus = RecordStatus.INIT;

	private enum RecordStatus {
		INIT, // 初始
		RECORDING, // 录音中
		STOP// 停止
	}

	private void showVolume(boolean flag) {

		if (flag && !mIsRecord) {
			mStartRecordBgView.setVisibility(View.GONE);
			mRecordingBgView.setVisibility(View.VISIBLE);
			mUmbrellaView.setVisibility(View.VISIBLE);
			mMicView.setVisibility(View.VISIBLE);
			mRoundBar.setVisibility(View.VISIBLE);

			mLastPower = 0.0f;
			setVolumeNumByPower(0);
			mIsRecord = true;

		} else if (!flag && mIsRecord) {
			mStartRecordBgView.setVisibility(View.VISIBLE);
			mRecordingBgView.setVisibility(View.GONE);
			mUmbrellaView.setVisibility(View.GONE);
			mMicView.setVisibility(View.GONE);
			mRoundBar.setVisibility(View.GONE);
			mIsRecord = false;
		}

	}

	// -------------------------------Public----------------------------------
	public void reset() {
		mView = this;
		mRoundBar.setMax(360);
		mStartRecordBgView.setVisibility(View.VISIBLE);
		showVolume(false);
	}

	/** 根据音量登记设置音量 **/
	public void setVolumeNumByPower(float power) {
		if (mLastPower <= 0 && power > 0.3)
			power = 0.3f;// 初次显示音量最大只显示0.3f

		mLastPower = Math.max(power, mLastPower - 0.05f);
		int num = (int) (mLastPower * 360);
		mRoundBar.setProgress(num);
	}

	public interface OnListener {
		public void onStartRecord();// 开始录音

		public void onStopRecord();// 停止录音
	}

	public OnListener mListener;

	public void setOnListener(OnListener listener) {
		mListener = listener;
	}
}