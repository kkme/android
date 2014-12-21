package com.link.bianmi.widget;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

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
public class AudioCircleButton extends RelativeLayout implements
		AudioPlayer.OnListener {

	private ImageButton mPlayBtn;
	private RoundProgressBar mRoundBar;

	private Context mContext;

	private String mAudioUrl;
	private int mMax;
	private Handler mHandler = new Handler();

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mRoundBar.setProgress(mMax--);
			mHandler.postDelayed(mRunnable, 1000);
		}
	};

	public AudioCircleButton(Context context) {
		this(context, null);
	}

	public AudioCircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.player, this, true);
		mPlayBtn = (ImageButton) findViewById(R.id.player_btn);
		mRoundBar = (RoundProgressBar) findViewById(R.id.player_roundbar);
		AudioPlayer.getInstance(context).setOnListener(this);
		final View loadingView = findViewById(R.id.loading_pb);
		loadingView.setVisibility(View.GONE);

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
					loadingView.setVisibility(View.VISIBLE);

					final String audioPath = SysConfig.getInstance()
							.getRootPath()
							+ File.separator
							+ "temp"
							+ File.separator
							+ FileHelper.getFileName(mAudioUrl);
					File file = new File(audioPath);
					if (file.exists() && file.isFile()) {
						loadingView.setVisibility(View.GONE);
						mPlayBtn.setBackgroundResource(R.drawable.btn_pause);
						startPlay(audioPath);
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

								public void onSuccess(File t) {
									loadingView.setVisibility(View.GONE);
									mPlayBtn.setBackgroundResource(R.drawable.btn_pause);
									startPlay(audioPath);
								}

							});

					break;
				case PLAYING:
					mStatus = PlayStatus.STOP;
					mPlayBtn.setBackgroundResource(R.drawable.btn_play);
					AudioPlayer.getInstance(mContext).stop();
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

	/**
	 * 设置音频路径
	 */
	public void setAudioUrl(String url) {
		mAudioUrl = url;
	}

	/**
	 * 开始播放
	 */
	private void startPlay(final String audioPath) {
		AudioPlayer.getInstance(mContext).stop();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				AudioPlayer.getInstance(mContext).start(audioPath);
			}
		}, 200);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onPlaying(int maxProgress, int progress) {
	}

	@Override
	public void onStop() {
		stop();
	}

}