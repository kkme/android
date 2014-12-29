package com.link.bianmi.widget;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.utils.AudioPlayer;
import com.link.bianmi.utils.FileHelper;

/**
 * 播放控件
 * 
 * @author pangfq
 * @date 2014年11月20日 上午11:14:45
 */
public class AudioCircleButton extends FrameLayout {

	private ImageButton mPlayBtn;
	private ImageView mPlayingView;

	private String mAudioUrl;
	private int mAudioLen;

	// ------------------------------Constructor------------------------------

	public AudioCircleButton(Context context) {
		this(context, null);
	}

	public AudioCircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.circle_audio_button,
				this, true);
		mPlayBtn = (ImageButton) findViewById(R.id.circle_imagebtn);
		final View loadingView = findViewById(R.id.loading_progressbar);
		loadingView.setVisibility(View.GONE);
		mPlayingView = (ImageView) findViewById(R.id.playing_imageview);
		reset();

		mPlayBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mStatus) {
				case INIT:
				case STOP:
					mStatus = PlayStatus.PLAYING;
					loadingView.setVisibility(View.VISIBLE);
					mPlayBtn.setBackgroundResource(R.drawable.bg_record_b);

					final String audioPath = SysConfig.getInstance()
							.getRootPath()
							+ File.separator
							+ "temp"
							+ File.separator
							+ FileHelper.getFileName(mAudioUrl);
					File file = new File(audioPath);
					if (file.exists() && file.isFile()) {
						loadingView.setVisibility(View.GONE);
						mPlayBtn.setBackgroundResource(R.drawable.bg_record_b);
						start(audioPath, mAudioLen);
						return;
					}
					FinalHttp fh = new FinalHttp();
					fh.download(mAudioUrl, // 这里是下载的路径
							audioPath,// true:断点续传
							// false:不断点续传（全新下载）
							false, // 这是保存到本地的路径
							new AjaxCallBack<File>() {
								@Override
								public void onLoading(long count, long current) {
								}

								@Override
								public void onSuccess(File t) {
									loadingView.setVisibility(View.GONE);
									mPlayBtn.setBackgroundResource(R.drawable.bg_record_b);
									start(audioPath, mAudioLen);
								}

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									loadingView.setVisibility(View.GONE);
									stop();
								}

							});

					break;
				case PLAYING:
					AudioPlayerController.getInstance().stop();
					break;
				}
			}
		});

	}

	private static class AudioPlayerController {

		private static AudioPlayerController sInstance = null;
		private AudioPlayer mPlayer;
		private AudioCircleButton mAudioBtn;

		private AudioPlayerController() {
			mPlayer = new AudioPlayer();
			mPlayer.setOnListener(new AudioPlayer.OnListener() {
				@Override
				public void onStop() {
					if (mAudioBtn != null)
						mAudioBtn.stop();
				}

				@Override
				public void onStart() {

				}

			});
		}

		public static AudioPlayerController getInstance() {
			if (sInstance == null) {
				sInstance = new AudioPlayerController();
			}
			return sInstance;
		}

		public void start(String audioPath, AudioCircleButton audioBtn) {
			mAudioBtn = audioBtn;
			mPlayer.start(audioPath);
		}

		public void stop() {
			mPlayer.stop();
		}
	}

	// ------------------------------------------Private--------------------------------------
	private PlayStatus mStatus;

	/** 播放状态 **/
	private enum PlayStatus {
		INIT, // 初始
		PLAYING, // 播放中
		STOP, // 停止
	}

	/** 重置 **/
	private void reset() {
		mStatus = PlayStatus.INIT;
		mPlayBtn.setBackgroundResource(R.drawable.btn_play);
		mPlayingView.setVisibility(View.GONE);
	}

	private void start(final String audioPath, int max) {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.bg_record_b);
			mPlayingView.setVisibility(View.VISIBLE);
			mStatus = PlayStatus.PLAYING;

			AudioPlayerController.getInstance().stop();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					AudioPlayerController.getInstance().start(audioPath,
							AudioCircleButton.this);
				}
			}, 200);

		}
	}

	private void stop() {
		if (mPlayBtn != null) {
			mPlayBtn.setBackgroundResource(R.drawable.btn_play);
			mPlayingView.setVisibility(View.GONE);
			mStatus = PlayStatus.STOP;
		}
	}

	// ------------------------------Public------------------------------
	public void init(String url, int length) {
		mAudioUrl = url;
		mAudioLen = length;
	}

}