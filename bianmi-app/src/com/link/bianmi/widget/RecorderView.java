package com.link.bianmi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.link.bianmi.R;

/**
 * 录音控件
 * 
 * @author pangfq
 * @date 2014年11月20日 上午11:12:07
 */
public class RecorderView extends RelativeLayout {

	/** 开始录音背景 **/
	private View mStartRecordBgView;
	/** 正在录音背景 **/
	private View mRecordingBgView;
	/** 音量背景 **/
	private View mVolumeBgView;
	/** 麦克风背景 **/
	private View mMicBgView;
	/** 进度条:显示录音音量大小 **/
	private RoundProgressBar mVolumeBar;

	private View mView;

	private boolean mIsRecord = true;

	private float mLastVolumeSize = 0.0f;

	private OnListener mListener;

	public RecorderView(Context context) {
		this(context, null);
	}

	public RecorderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.recorder, this, true);

		mStartRecordBgView = findViewById(R.id.start_record_bg_view);
		mRecordingBgView = findViewById(R.id.recording_bg_view);
		mVolumeBgView = findViewById(R.id.volume_bg_view);
		mMicBgView = findViewById(R.id.record_mic_view);
		mVolumeBar = (RoundProgressBar) findViewById(R.id.volume_roundbar);

		reset();

		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (mStatus) {
				case INIT:
				case STOP:
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
			mVolumeBgView.setVisibility(View.VISIBLE);
			mMicBgView.setVisibility(View.VISIBLE);
			mVolumeBar.setVisibility(View.VISIBLE);

			mLastVolumeSize = 0.0f;
			setVolume(0);
			mIsRecord = true;

		} else if (!flag && mIsRecord) {
			mStartRecordBgView.setVisibility(View.VISIBLE);
			mRecordingBgView.setVisibility(View.GONE);
			mVolumeBgView.setVisibility(View.GONE);
			mMicBgView.setVisibility(View.GONE);
			mVolumeBar.setVisibility(View.GONE);
			mIsRecord = false;
		}

	}

	// -------------------------------Public----------------------------------
	/**
	 * 重置
	 */
	public void reset() {
		mView = this;
		mVolumeBar.setMax(360);
		mStartRecordBgView.setVisibility(View.VISIBLE);
		showVolume(false);
	}

	/** 设置录音音量 **/
	public void setVolume(float volumeSize) {
		if (mLastVolumeSize <= 0 && volumeSize > 0.3)
			volumeSize = 0.3f;// 初次显示音量最大只显示0.3f
		mLastVolumeSize = Math.max(volumeSize, mLastVolumeSize - 0.05f);
		int num = (int) (mLastVolumeSize * 360);
		mVolumeBar.setProgress(num);
	}

	public interface OnListener {
		public void onStartRecord();// 开始录音

		public void onStopRecord();// 停止录音
	}

	public void setOnListener(OnListener listener) {
		mListener = listener;
	}
}