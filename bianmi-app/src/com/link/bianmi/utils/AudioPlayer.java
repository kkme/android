package com.link.bianmi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.link.bianmi.R;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class AudioPlayer {

	private final String TAG = "AudioPlayer";

	private AudioTrack mAudioTrack;

	private int mMinBufferSize = 0;

	private byte[] mBuffer;

	private boolean mIsStart = false;

	private Context mContext;

	private PlayThread mThread = null;

	// 停止播放
	private final int MSG_STOP = 2;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_STOP:
				if (mListener != null) {
					mListener.onStop();
				}

				break;
			}

		}
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

			while (mIsStart && playInputStream != null) {
				int len = 0;
				try {
					len = playInputStream.read(mBuffer, 0, mBuffer.length);

				} catch (IOException e) {
					e.printStackTrace();
				}

				if (len == -1) {
					Log.d(TAG, "file read finish!");

					break;
				}

				if (len != 0) {
					mAudioTrack.write(mBuffer, 0, len);
					Log.d(TAG, "write [" + len + "]amr data into player!");
				}

			}

			mAudioTrack.stop();
			mAudioTrack.release();
			mIsStart = false;
			Message msg = new Message();
			msg.what = MSG_STOP;
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

	public AudioPlayer(Context context) {
		mContext = context;
	}

	public void start(String fileName) {

		if (mThread != null && mThread.isAlive()) {
			Log.w(TAG, "AudioPlayer is already started!");
			return;
		}

		if (!new File(fileName).exists() || !new File(fileName).isFile()) {
			Toast.makeText(mContext,
					mContext.getString(R.string.audio_file_error),
					Toast.LENGTH_SHORT).show();
			if (mListener != null) {
				mListener.onStop();
			}
			return;
		}

		mMinBufferSize = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				mMinBufferSize * 3, AudioTrack.MODE_STREAM);

		if (mMinBufferSize != 0) {

			mBuffer = new byte[mMinBufferSize * 3];
			mAudioTrack.play();
			mIsStart = true;
			mThread = new PlayThread(fileName);
			mThread.start();
			if (mListener != null) {
				mListener.onStart();
			}

		}
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		mIsStart = false;
		mThread = null;
		if (mAudioTrack != null) {
			mAudioTrack.stop();
		}
		if (mListener != null) {
			mListener.onStop();
		}
	}

	private OnListener mListener;

	public void setOnListener(OnListener l) {
		mListener = l;
	}

	public interface OnListener {
		/**
		 * 开始播放
		 */
		public void onStart();

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
		public void onStop();
	}

}