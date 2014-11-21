package com.link.bianmi.widget;

import java.io.File;
import java.util.UUID;

import lib.module.soundtouch.NativeSoundTouch;
import lib.widget.seekarc.SeekArc;
import lib.widget.seekarc.SeekArc.OnSeekArcChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.qiniu.QiniuClient;
import com.link.bianmi.utility.CameraCrop;
import com.link.bianmi.utility.ContextHelper;
import com.link.bianmi.utility.ConvertHelper;
import com.link.bianmi.utility.FileHelper;
import com.link.bianmi.utility.ImageHelper;
import com.link.bianmi.utility.SoundTouchClient;

/**
 * 
 * 输入套件
 * 
 * @author pangfq
 * @date 2014年7月29日 上午8:56:29
 */
public class InputSuit extends LinearLayout {

	private Handler mHandler;

	/** 图片地址 **/
	private String mPhotoPath = "";
	/** 图片URL **/
	private String mPhotoUrl = "";

	/** 录音地址 **/
	private String mRecordPath = "";
	/** 音频URL **/
	private String mRecordUrl = "";
	/** 录音长度 **/
	private int mRecordLen = 0;
	/** 回复用户姓名 **/
	private String mUserName = "";
	/** 回复用户id **/
	private String mUserId = "";

	private Listener mListener;

	private CameraCrop mCamera;

	private SoundTouchClient mSTRecorder;
	private RecorderView mRecorderView;
	private PlayerView mPlayerView;
	private TextView mRecordDurationText;
	private Button mRerecordingBtn;// 重录按钮
	private TextView mRecordTipText;// 录音提示
	private CountDownTimer mCDTime;
	private STListener mSTListener;

	/** 上下文 **/
	private BaseFragmentActivity mActivity;
	private BaseFragment mFragment;

	/** 提示：有录音文件 **/
	private View mTipRecord;
	/** 提示：有图片文件 **/
	private View mTipPhoto;
	/** 回复内容 **/
	private EditText mMessageEdit;
	/** 提交 **/
	private Button mSubmitBtn;
	/** 选择图片组视图 **/
	private View mPhotoGroup;
	/** 操作图片 **/
	private View mPhotoOperateGroup;
	/** 显示图片组视图 **/
	private View mPhotoShowGroup;
	/** 显示照片 **/
	private ImageView mPhotoImage;
	/** 删除图片 **/
	private View mPhotoDeleteView;

	/** 录音组 **/
	private View mRecordGroup;
	/** 附件区 **/
	private View mAttachView;

	private Context mContext;

	/** 禁止操作 **/
	private View mDissableTouchView;
	private SeekArc mPitchSeekArc;// 音调SeekArc
	private SeekArc mTempoSeekArc;// 音速SeekArc
	private TextView mPitchProgressText;
	private TextView mTempoProgressText;

	public InputSuit(Context context) {
		super(context, null);
	}

	public InputSuit(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.input_suit, this, true);

		findViewById(R.id.attach_record_view).setOnClickListener(
				attachRecordListener);
		mTipRecord = findViewById(R.id.tips_record_view);
		findViewById(R.id.attach_photo_view).setOnClickListener(
				attachPhotoListener);
		mTipPhoto = findViewById(R.id.tips_photo_view);
		mAttachView = findViewById(R.id.attach_group);
		mMessageEdit = (EditText) findViewById(R.id.message_edit);
		mMessageEdit.addTextChangedListener(textWatcher);
		mMessageEdit.setOnFocusChangeListener(textFocuseListener);
		mSubmitBtn = (Button) findViewById(R.id.submit_view);
		mSubmitBtn.setOnClickListener(submitListener);
		mPhotoGroup = findViewById(R.id.photo_group);
		mPhotoOperateGroup = findViewById(R.id.photo_operate_group);
		findViewById(R.id.photo_album_view).setOnClickListener(albumListener);
		findViewById(R.id.photo_camera_view).setOnClickListener(cameraListener);
		mPhotoShowGroup = findViewById(R.id.photo_show_group);
		mPhotoImage = (ImageView) findViewById(R.id.photo_image);
		mPhotoImage.setOnClickListener(photoListener);
		mPhotoDeleteView = findViewById(R.id.photo_delete_view);
		mPhotoDeleteView.setOnClickListener(deletePhotoListener);
		mRecordGroup = findViewById(R.id.voice_group);

		mDissableTouchView = findViewById(R.id.dissableClick_view);
		mDissableTouchView.setVisibility(View.GONE);
		mDissableTouchView.setOnTouchListener(dissableTouchListener);

		mRecordDurationText = (TextView) findViewById(R.id.record_duration_textview);

		mPitchProgressText = (TextView) findViewById(R.id.pitch_seekarc_progress_textview);
		mTempoProgressText = (TextView) findViewById(R.id.tempo_seekarc_progress_textview);
		mPitchSeekArc = (SeekArc) findViewById(R.id.pitch_seekarc);
		mPitchSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekArc seekArc) {
				float pitch = (seekArc.getProgress() - 1000) / 100.0f;
				NativeSoundTouch.getSoundTouch().setPitchSemiTones(pitch);
			}

			@Override
			public void onStartTrackingTouch(SeekArc seekArc) {
			}

			@Override
			public void onProgressChanged(SeekArc seekArc, int progress,
					boolean fromUser) {
				float pitch = (progress - 1000) / 100.0f;
				NativeSoundTouch.getSoundTouch().setPitchSemiTones(pitch);
				mPitchProgressText.setText(String.valueOf(pitch));
			}
		});
		mTempoSeekArc = (SeekArc) findViewById(R.id.tempo_seekarc);
		mTempoSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekArc seekArc) {
				float tempo = (seekArc.getProgress() - 5000) / 100.0f;
				NativeSoundTouch.getSoundTouch().setTempoChange(tempo);
			}

			@Override
			public void onStartTrackingTouch(SeekArc seekArc) {
			}

			@Override
			public void onProgressChanged(SeekArc seekArc, int progress,
					boolean fromUser) {
				float tempo = (progress - 5000) / 100.0f;
				NativeSoundTouch.getSoundTouch().setTempoChange(tempo);
				mTempoProgressText.setText(String.valueOf(tempo + "%"));
			}
		});
		final RadioButton tomRadioBtn = (RadioButton) findViewById(R.id.tom_radiobtn);
		final RadioButton wangRadioBtn = (RadioButton) findViewById(R.id.wang_radiobtn);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (arg1 == tomRadioBtn.getId()) {
					checkTomVoiceRadioBtn();
				} else if (arg1 == wangRadioBtn.getId()) {
					checkWangVoiceRadioBtn();
				}
			}
		});
		mSTRecorder = new SoundTouchClient(context);
		mSTListener = new STListener();
		mSTRecorder.setOnListener(mSTListener);

		final int MaxSecond = 120000;
		mCDTime = new CountDownTimer(MaxSecond, 1000) {
			public void onTick(long millisUntilFinished) {
				mRecordLen = (int) ((MaxSecond - millisUntilFinished) / 1000);
				mRecordDurationText.setText((MaxSecond - millisUntilFinished)
						/ 1000 + "″");
			}

			public void onFinish() {
			}
		};

		mRecorderView = (RecorderView) findViewById(R.id.recorder_suit);
		mRecorderView.setOnListener(new RecorderView.OnListener() {
			@Override
			public void onStopRecord() {
				// 停止录音
				stopRecord();
			}

			@Override
			public void onStartRecord() {
				// 开始录音
				startRecord();
			}
		});
		mPlayerView = (PlayerView) findViewById(R.id.player_suit);
		mPlayerView.setVisibility(View.GONE);
		mPlayerView.setOnListener(new PlayerView.OnListener() {
			@Override
			public void onStop() {
			}

			@Override
			public void onPlay() {
				// 开始播放
				startPlay();
			}
		});
		// 重录按钮
		mRerecordingBtn = (Button) findViewById(R.id.rerecording_btn);
		mRerecordingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showConfirmRerecordingDialog();
			}
		});

		mRecordTipText = (TextView) findViewById(R.id.record_tip_textview);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.InputSuit);
			boolean isAttah = a.getBoolean(R.styleable.InputSuit_is_attach,
					false);
			if (isAttah) {
				mMessageEdit.setVisibility(View.GONE);
				mSubmitBtn.setVisibility(View.GONE);
			}
			a.recycle();
		}
		reset();
	}

	// ----------------------------------------Private-------------------------------------------
	/**
	 * 单选“汤姆”
	 */
	private void checkTomVoiceRadioBtn() {
		mPitchSeekArc.setProgress(2000);
		mPitchProgressText.setText("10.00");
		mTempoSeekArc.setProgress(5000);
		mTempoProgressText.setText("0.00%");
	}

	/**
	 * 单选“老王”
	 */
	private void checkWangVoiceRadioBtn() {
		mPitchSeekArc.setProgress(500);
		mPitchProgressText.setText("-5.00");
		mTempoSeekArc.setProgress(2500);
		mTempoProgressText.setText("0.00%");
	}

	/**
	 * 开始录音
	 */
	private void startRecord() {
		mHandler.removeMessages(0, null);
		mSTRecorder.startRecord();
		mRecordTipText.setText(mActivity
				.getString(R.string.inputsuit_click_stop_record));
		mCDTime.start();
	}

	/**
	 * 结束录音
	 */
	private void stopRecord() {
		mPlayerView.setVisibility(View.VISIBLE);
		mRerecordingBtn.setVisibility(View.VISIBLE);
		mRecordTipText.setVisibility(View.GONE);
		if (mSTRecorder != null)
			mRecordPath = mSTRecorder.stopRecorder();
		mCDTime.cancel();
	}

	/**
	 * 开始播放
	 */
	private void startPlay() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mSTRecorder.startPlay(mRecordPath);
			}
		}, 200);
	}

	/**
	 * 重录
	 */
	private void rerecording() {
		FileHelper.delete(mRecordPath);
		setPhotoPath("");
		setRecordPath("");
		mRecordLen = 0;
		mUserName = "";
		mUserId = "";
		mRecordPath = "";

		mRecordDurationText.setText("0\"");
		mRecordTipText.setText(mContext
				.getString(R.string.inputsuit_click_start_record));
		mRecorderView.reset();
		mPlayerView.reset();
		mRecorderView.setVisibility(View.VISIBLE);
		mPlayerView.setVisibility(View.GONE);
		mRerecordingBtn.setVisibility(View.GONE);
		mTipRecord.setVisibility(View.GONE);

	}

	/**
	 * 结束播放
	 */
	private void stopPlay() {
		mPlayerView.stop();
	}

	private OnFocusChangeListener textFocuseListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				mPhotoGroup.setVisibility(View.GONE);
				mRecordGroup.setVisibility(View.GONE);
				mAttachView.setVisibility(View.GONE);

				mDissableTouchView.setVisibility(View.VISIBLE);
			} else {
				if (mPhotoGroup.getVisibility() != View.VISIBLE)
					mAttachView.setVisibility(View.GONE);
			}

		}
	};

	/** 输入框内容变化 **/
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			checkEnableSubmit();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	/** 是否允许提交内容 **/
	private void checkEnableSubmit() {

		boolean enable = false;

		// 文本内容长度大于0，允许发送
		if (!enable && mMessageEdit.getText().toString().trim().length() > 0) {
			enable = true;
		}
		// 有照片允许发送
		if (!enable && !TextUtils.isEmpty(mPhotoPath)) {
			enable = true;
		}
		// 有音频允许发送
		if (!enable && !TextUtils.isEmpty(mRecordPath)) {
			enable = true;
		}

		boolean currentStatus = mSubmitBtn.isEnabled();
		if (enable != currentStatus) {
			mSubmitBtn.setEnabled(enable);
		}
	}

	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mMessageEdit.clearFocus();
		imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);
	}

	/** 选择录音操作 **/
	private OnClickListener attachRecordListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mRecordGroup.getVisibility() == View.VISIBLE) {
				mRecordGroup.setVisibility(View.GONE);
				mPhotoGroup.setVisibility(View.GONE);
				mAttachView.setVisibility(View.GONE);
			} else {
				mRecordGroup.setVisibility(View.VISIBLE);
				mPhotoGroup.setVisibility(View.GONE);
				mAttachView.setVisibility(View.VISIBLE);
				hideSoftInput();
			}
		}
	};

	/** 选择图片操作 **/
	private OnClickListener attachPhotoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (mPhotoGroup.getVisibility() == View.VISIBLE) {
				mPhotoGroup.setVisibility(View.GONE);
				mRecordGroup.setVisibility(View.GONE);
				mAttachView.setVisibility(View.GONE);
			} else {
				mRecordGroup.setVisibility(View.GONE);
				mPhotoGroup.setVisibility(View.VISIBLE);
				mAttachView.setVisibility(View.VISIBLE);
				hideSoftInput();
			}

		}
	};

	/** 发表 **/
	private OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			hideSoftInput();
			mAttachView.setVisibility(View.GONE);
			mSubmitBtn.setEnabled(false);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						if (mListener != null)
							mListener.onSubmit(mPhotoPath, mRecordPath,
									mRecordLen, mMessageEdit.getText()
											.toString(), mUserName, mUserId);
						checkEnableSubmit();
					} catch (Exception ex) {
					}
				}
			}, 100);

		}
	};

	/** 选择照片 **/
	private OnClickListener albumListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCamera == null)
				mCamera = new CameraCrop(mActivity, mFragment);
			mCamera.startImagePick();
		}
	};

	/** 选择相机 **/
	private OnClickListener cameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCamera == null)
				mCamera = new CameraCrop(mActivity, mFragment);
			mCamera.startActionCamera();
		}
	};

	/** 点击图片 **/
	private OnClickListener photoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};

	/** 删除图片 **/
	private OnClickListener deletePhotoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPhotoPath.contains(SysConfig.getInstance().getRootPath()))
				FileHelper.delete(mPhotoPath);
			setPhotoPath("");
			mPhotoImage.setImageBitmap(null);
			mPhotoOperateGroup.setVisibility(View.VISIBLE);
			mPhotoShowGroup.setVisibility(View.GONE);
			mTipPhoto.setVisibility(View.GONE);
		}
	};

	private OnTouchListener dissableTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mDissableTouchView.setVisibility(View.GONE);
				hideSoftInput();
			}

			return false;
		}
	};

	/** 设置图片地址 **/
	private void setPhotoPath(String path) {
		mPhotoPath = path;
		mPhotoUrl = "";
		checkEnableSubmit();
	}

	/** 设置录音地址 **/
	private void setRecordPath(String path) {
		mRecordPath = path;
		mRecordUrl = "";
		checkEnableSubmit();
	}

	/** 上传图片标志 **/
	private final int UPLOAD_TYPE_PHOTO = 1;
	/** 上传录音标志 **/
	private final int UPLOAD_TYPE_RECORD = 2;

	/** 上传文件 **/
	private void uploadAttach() {

		boolean needUploadPhoto = false; // 是否需要上传照片
		boolean needUploadRecord = false; // 是否需要上传录音
		if (!TextUtils.isEmpty(mPhotoPath) && TextUtils.isEmpty(mPhotoUrl)) {
			needUploadPhoto = true;
		}
		if (!TextUtils.isEmpty(mRecordPath) && TextUtils.isEmpty(mRecordUrl)) {
			needUploadRecord = true;
		}

		// 是否已经上传结束
		if (!needUploadPhoto && !needUploadRecord) {
			if (mListener != null) {
				mListener.onUploadAttach(true, mPhotoUrl, mRecordUrl,
						mRecordLen);
			}
			return;
		}

		try {

			final int uploadType; // 是否上传图片
			String key = "";
			String filePathTemp = "";
			if (needUploadPhoto) {

				String fileName = String.valueOf(ContextHelper.getDeviceId()
						+ FileHelper.getFileName(mPhotoPath));
				filePathTemp = SysConfig.getInstance().getPathTemp()
						+ File.separator + fileName;
				if (!new File(filePathTemp).exists()) { // 压缩图片
					ImageHelper.createImageThumbnail(mActivity, mPhotoPath,
							filePathTemp, 640, 80);
				}

				key = String.format("secret/image/%s_%s.%s", UUID.randomUUID()
						.toString().replace("-", ""),
						System.currentTimeMillis(),
						FileHelper.getExtensionName(mPhotoPath));
				uploadType = UPLOAD_TYPE_PHOTO;
			} else {
				key = String.format("secret/audio/%s_%s.%s", UUID.randomUUID()
						.toString().replace("-", ""),
						System.currentTimeMillis(),
						FileHelper.getExtensionName(mRecordPath));
				uploadType = UPLOAD_TYPE_RECORD;
				filePathTemp = mRecordPath;
			}

			final String f_uploadFile = filePathTemp;

			QiniuClient qc = new QiniuClient();
			qc.setOnListener(new QiniuClient.OnListener() {
				@Override
				public void onFailure(Exception ex) { // 上传图片失败：失败时回传参数为空
					mListener.onUploadAttach(false, mPhotoUrl, mRecordUrl,
							mRecordLen);
				}

				@Override
				public void onComplete(String key) { // 上传成功
					if (uploadType == UPLOAD_TYPE_PHOTO) {
						mPhotoUrl = String.format("http://%s/%s", SysConfig
								.getInstance().getQiniuBucketDomainAttach(),
								key);
					} else {
						mRecordUrl = String.format("http://%s/%s", SysConfig
								.getInstance().getQiniuBucketDomainAttach(),
								key);
					}
					if (f_uploadFile.contains(SysConfig.getInstance()
							.getPathTemp())) // lls文件夹中的文件可以删除
						FileHelper.delete(f_uploadFile);
					uploadAttach();
				}
			});

			qc.doUpload(f_uploadFile, key, SysConfig.getInstance()
					.getQiniuBucketNameAttach(), false, false);

		} catch (Exception ex) {
			mListener.onUploadAttach(false, mPhotoUrl, mRecordUrl, mRecordLen);
		} catch (OutOfMemoryError ex) {
			mListener.onUploadAttach(false, mPhotoUrl, mRecordUrl, mRecordLen);
		}
	}

	/**
	 * 重录确认对话框
	 */
	private void showConfirmRerecordingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		final AlertDialog dialog = builder
				.setTitle(mActivity.getString(R.string.inputsuit_rerecord_tip))
				.setPositiveButton(mActivity.getString(R.string.rerecording),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 重录
								rerecording();
							}
						})
				.setNegativeButton(mActivity.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 取消
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	/**
	 * 监听SoundTouch事件
	 */
	private class STListener implements SoundTouchClient.OnListener {

		@Override
		public void onStopRecord() {
			mTipRecord.setVisibility(View.VISIBLE);
		}

		@Override
		public void onStopPlay() {
			stopPlay();
		}

		@Override
		public void onStartRecord() {

		}

		@Override
		public void onStartPlay() {
			mPlayerView.play(mRecordLen);
		}

		@Override
		public void onRecording(float power) {
			mRecorderView.setVolume(power);
		}

		@Override
		public void onPlaying(int maxProgress, int progress) {

		}
	};

	// --------------------------------------------Public-------------------------------------------

	public interface Listener {
		/** 提交 **/
		public void onSubmit(String photoPath, String recordPath,
				int recordLen, String message, String userName, String UserId);

		/**
		 * 上传附件结果
		 * 
		 * @param result
		 *            上传结果
		 * @param photoUrl
		 *            图片url
		 * @param recordUrl
		 *            音频url
		 * **/
		public void onUploadAttach(boolean result, String photoUrl,
				String recordUrl, int recordLength);
	}

	/** 内容是否为空 **/
	public boolean isEmpty() {

		boolean result = true;
		if (mMessageEdit.getText().toString().trim().length() > 0
				|| mPhotoPath.length() > 0 || mRecordPath.length() > 0) {
			result = false;
		}
		return result;
	}

	/** 获取用户id **/
	public String getmUserId() {
		return mUserId;
	}

	/** 清理数据 **/
	public void reset() {
		setPhotoPath("");
		setRecordPath("");
		mRecordLen = 0;
		mUserName = "";
		mUserId = "";

		mRecordGroup.setVisibility(View.GONE);
		mPhotoGroup.setVisibility(View.GONE);
		mTipRecord.setVisibility(View.GONE);
		mTipPhoto.setVisibility(View.GONE);
		mAttachView.setVisibility(View.GONE);
		mPhotoShowGroup.setVisibility(View.GONE);
		mRerecordingBtn.setVisibility(View.GONE);
		mPlayerView.setVisibility(View.GONE);

		mRecorderView.setVisibility(View.VISIBLE);
		mRecordTipText.setVisibility(View.VISIBLE);
		mPhotoOperateGroup.setVisibility(View.VISIBLE);
		mMessageEdit.setText("");
		mMessageEdit.setHint("");
		mRecordDurationText.setText("0\"");
		mRecordTipText.setText(mContext
				.getString(R.string.inputsuit_click_start_record));
		mRecorderView.reset();
		mPlayerView.reset();

		// 变声默认是汤姆
		checkTomVoiceRadioBtn();
	}

	/** 是否展开状态 **/
	public boolean isOpen() {
		return mAttachView.getVisibility() == View.VISIBLE ? true : false;
	}

	/** 关闭展开 **/
	public void close() {
		mAttachView.setVisibility(View.GONE);
	}

	/** 初始化数据 **/
	public void init(BaseFragmentActivity activity, BaseFragment fragment,
			Listener l) {
		mActivity = activity;
		mFragment = fragment;
		mListener = l;
		mHandler = new Handler();
	}

	/** 处理照片 **/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		try {
			switch (requestCode) {
			case CameraCrop.REQUEST_CALENDAR:
				mCamera.setCorpFile("");
			case CameraCrop.REQUEST_CAMERA:
				try {
					Uri uri = mCamera.getUri(data);

					String srcFilePath = ConvertHelper.uri2StrPath(mActivity,
							uri);
					Bitmap thumbnail = ImageHelper.getImageThumbnail(
							srcFilePath, 128, 128);
					mPhotoImage.setImageBitmap(thumbnail); // 设置图片
					setPhotoPath(srcFilePath);
					mPhotoOperateGroup.setVisibility(View.GONE);
					mPhotoShowGroup.setVisibility(View.VISIBLE);
					mTipPhoto.setVisibility(View.VISIBLE);
					data = null;
				} catch (Exception ex) {
				} catch (OutOfMemoryError ex) {
				}
				break;

			default:
				break;
			}
		} catch (Exception ex) {

		}
	}

	/**
	 * 设置mention
	 * 
	 * @param userid
	 *            用户id
	 * @param name
	 *            用户名称
	 * @param showSoftInput
	 *            是否显示键盘
	 * **/
	public void setMention(String name, String userId, boolean showSoftInput) {
		String mentionUserName = "";
		if (name != null && name.length() > 0) {
			if (name.length() > 10)
				name = name.substring(0, 10) + "...";
			mentionUserName = "@" + name;
		}
		mMessageEdit.setHint(mentionUserName);
		mUserName = name;
		mUserId = userId;

		if (showSoftInput) {
			mMessageEdit.requestFocus();
			InputMethodManager inputManager = (InputMethodManager) mMessageEdit
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(mMessageEdit, 0);
		}
	}

	/** 图片地址 **/
	public String getPhotoPath() {
		return mPhotoPath;
	}

	/** 录音地址 **/
	public String getRecordPath() {
		return mRecordPath;
	}

	/** 录音长度 **/
	public int getRecordLen() {
		return mRecordLen;
	}

	/** 回复内容 **/
	public String getMessage() {
		return mMessageEdit.getText().toString();
	}

	/** 回复用户姓名 **/
	public String getUserName() {
		return mUserName;
	}

	/** 回复用户id **/
	public String getUserId() {
		return mUserId;
	}

	/**
	 * 清理数据
	 */
	public void cleanup() {
		mSTRecorder.setOnListener(null);
		mSTRecorder.stopRecorder();
		mSTRecorder.stopPlay();
		FileHelper.delete(mRecordPath);
		FileHelper.delete(mPhotoPath);
	}

	/** 开始上传附件 **/
	public void startUpload() {

		if (!TextUtils.isEmpty(mPhotoPath) && !(new File(mPhotoPath).exists())) {
			mPhotoPath = "";
		}
		if (!TextUtils.isEmpty(mRecordPath)
				&& !(new File(mRecordPath).exists())) {
			mRecordPath = "";
		}

		/** 文件不存在上传成功 **/
		if (TextUtils.isEmpty(mPhotoPath) && TextUtils.isEmpty(mRecordPath)) {
			if (mListener != null) {
				mListener.onUploadAttach(true, "", "", 0);
			}
			return;
		}

		uploadAttach();
	}
}