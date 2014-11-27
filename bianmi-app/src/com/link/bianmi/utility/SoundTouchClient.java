package com.link.bianmi.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import lib.module.soundtouch.SoundTouch;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.link.bianmi.SysConfig;

public class SoundTouchClient {

	private static final String TAG = "SoundTouchClient";

	private AudioRecord mAudioRecorder = null;

	private AudioTrack mAudioTrack;

	private int minRecBuffSize = 0;

	private int minPlayBufferSize = 0;

	private byte[] recorderBuffer;

	private byte[] playerBuffer;

	private boolean recordingstart = false;

	private boolean playingstart = false;

	private Context mContext;

	private RecordThread recordThread = null;

	private PlayThread playThread = null;

	// 正在录音
	private final int MSG_RECORDING = 1;
	// 停止录音
	private final int MSG_STOP_PLAY = 2;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_RECORDING:
				if (mListener != null) {
					mListener
							.onRecording(Math.abs(((float) msg.arg1 - 10)) / 60);
				}

				break;
			case MSG_STOP_PLAY:
				if (mListener != null) {
					mListener.onStopPlay();
				}

				break;
			}

		}
	};

	class RecordThread extends Thread {

		private FileOutputStream recordOutputStream = null;

		private String fullFileName;

		RecordThread() {

			String fileName = UUID.randomUUID().toString();
			Log.d(TAG, "recordfile name:" + fileName);
			fullFileName = SysConfig.getInstance().getPathTemp()
					+ File.separator + fileName + ".amr";
			File tempFile = new File(fullFileName);
			if (!tempFile.exists()) {
				try {
					tempFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				recordOutputStream = new FileOutputStream(
						new File(fullFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		public void run() {

			int totalPCMSamples = 0;
			int totalSTSamples = 0;

			int receiveSTSamples = 0;

			while (recordingstart && recordOutputStream != null) {
				int len = mAudioRecorder.read(recorderBuffer, 0,
						recorderBuffer.length);
				int inputSamples = len / ((16 * 1) / 8);
				totalPCMSamples += inputSamples;
				double sum = 0;
				for (int i = 0; i < len; i++) {
					sum += recorderBuffer[i] * recorderBuffer[i];
				}
				if (len > 0) {
					final double amplitude = sum / len;
					Log.d("bianmi", "len = " + len);
					Log.d("bianmi", "amplitude = " + amplitude);
					Log.d("bianmi", "progress = " + Math.sqrt(amplitude));
					Message msg = new Message();
					msg.arg1 = (int) Math.sqrt(amplitude);
					msg.what = MSG_RECORDING;
					mHandler.sendMessage(msg);
				}
				try {

					Log.d(TAG, "input ST amr size :" + inputSamples);

					SoundTouch.getSoundTouch().shiftingPitch(
							recorderBuffer, 0, len);

					do {
						receiveSTSamples = SoundTouch.getSoundTouch()
								.receiveSamples(recorderBuffer,
										recorderBuffer.length);

						totalSTSamples += receiveSTSamples;
						Log.d(TAG, "receive ST amr samples :"
								+ receiveSTSamples);

						if (receiveSTSamples != 0) {
							recordOutputStream.write(recorderBuffer, 0,
									receiveSTSamples * ((16 * 1) / 8));
							recordOutputStream.flush();
						}
						// sava into file
					} while (receiveSTSamples != 0);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// receive the remainder of the speech data
			SoundTouch.getSoundTouch().soundTouchFlushLastSamples();

			do {
				receiveSTSamples = SoundTouch.getSoundTouch()
						.receiveSamples(recorderBuffer, recorderBuffer.length);

				Log.d(TAG, "receive remainder ST samples:" + receiveSTSamples);

				totalSTSamples += receiveSTSamples;

				if (receiveSTSamples != 0) {

					try {
						recordOutputStream.write(recorderBuffer, 0,
								receiveSTSamples * ((16 * 1) / 8));
						recordOutputStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			} while (receiveSTSamples != 0);

			Log.d(TAG, "Total input amr samples:" + totalPCMSamples);
			Log.d(TAG, "total receive ST samples:" + totalSTSamples);

			mAudioRecorder.stop();
			mAudioRecorder.release();
			recordingstart = false;

			try {
				recordOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		};
	};

	class PlayThread extends Thread {

		private FileInputStream playInputStream = null;

		PlayThread(String fileName) {

			if (fileName != null) {
				try {
					playInputStream = new FileInputStream(new File(fileName));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {

			while (playingstart && playInputStream != null) {
				int len = 0;
				try {
					len = playInputStream.read(playerBuffer, 0,
							playerBuffer.length);

				} catch (IOException e) {
					e.printStackTrace();
				}

				if (len == -1) {
					Log.d(TAG, "file read finish!");

					break;
				}

				if (len != 0) {
					mAudioTrack.write(playerBuffer, 0, len);
					Log.d(TAG, "write [" + len + "]amr data into player!");
				}

			}

			mAudioTrack.stop();
			mAudioTrack.release();
			playingstart = false;
			Message msg = new Message();
			msg.what = MSG_STOP_PLAY;
			mHandler.sendMessage(msg);

			if (playInputStream != null) {
				try {
					playInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public SoundTouchClient(Context context) {
		mContext = context;
	}

	public void startRecord() {

		if (playThread != null && playThread.isAlive()) {
			Log.w(TAG, "AudioPlayer is running, please stop Player!");
			return;
		}

		if (recordThread != null && recordThread.isAlive()) {
			Log.w(TAG, "AudioRecorder is already start!");
			return;
		}

		minRecBuffSize = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				minRecBuffSize * 3);
		Log.d("bianmi", "minRecBuffSize = " + minRecBuffSize * 3);
		mAudioRecorder.startRecording();
		if (minRecBuffSize != 0) {

			recorderBuffer = new byte[minRecBuffSize * 3];
			recordingstart = true;
			recordThread = new RecordThread();
			recordThread.start();
			if (mListener != null) {
				mListener.onStartRecord();
			}

		}

	}

	public String stopRecorder() {
		recordingstart = false;

		String fileName = null;

		if (recordThread != null) {
			fileName = recordThread.fullFileName;
			recordThread = null;
		}

		if (mListener != null) {
			mListener.onStopRecord();
		}
		return fileName;
	}

	public void startPlay(String fileName) {

		if (recordThread != null && recordThread.isAlive()) {
			Log.w(TAG, "Record is not complite!");
			return;
		}

		if (playThread != null && playThread.isAlive()) {
			Log.w(TAG, "AudioPlayer is already started!");
			return;
		}

		if (!new File(fileName).exists() || !new File(fileName).isFile()) {
			Toast.makeText(mContext, "无法读取录音文件！", Toast.LENGTH_SHORT).show();
			if (mListener != null) {
				mListener.onStopPlay();
			}
			return;
		}

		minPlayBufferSize = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				minPlayBufferSize * 3, AudioTrack.MODE_STREAM);

		if (minPlayBufferSize != 0) {

			playerBuffer = new byte[minPlayBufferSize * 3];
			mAudioTrack.play();
			playingstart = true;
			playThread = new PlayThread(fileName);
			playThread.start();
			if (mListener != null) {
				mListener.onStartPlay();
			}

		}
	}

	public void stopPlay() {
		playingstart = false;
		playThread = null;
		if (mListener != null) {
			mListener.onStopPlay();
		}

	}

	private OnListener mListener;

	public void setOnListener(OnListener l) {
		mListener = l;
	}

	public interface OnListener {
		/**
		 * 开始录音
		 */
		public void onStartRecord();

		/**
		 * 正在录音
		 * 
		 * @param power
		 */
		public void onRecording(float power);

		/**
		 * 停止录音
		 */
		public void onStopRecord();

		/**
		 * 开始播放
		 */
		public void onStartPlay();

		/**
		 * 正在播放
		 * 
		 * @param maxProgress
		 * @param progress
		 */
		public void onPlaying(int maxProgress, int progress);

		/**
		 * 停止播放
		 */
		public void onStopPlay();
	}

}