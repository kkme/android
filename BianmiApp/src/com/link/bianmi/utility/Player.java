package com.link.bianmi.utility;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 *  网络播放器 
 * @author sunpf
 *
 */
public class Player implements IPlayer, OnBufferingUpdateListener,
		OnCompletionListener, MediaPlayer.OnPreparedListener{
	
	private final static  String TAG ="Player";
			
	public MediaPlayer mediaPlayer;
	/** 监听器**/
	protected OnListener mListener;
	//总长度
	private int mDuration=0;
	private Timer mTimer;
	//定时器任务
	 private MyTimerTask	mTimerTask;

	private boolean mNotifyPosition=false;
	

	public Player(){
		init();
	}
	
	
	
	private void init(){
		
		try {
			release();
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			
					
		} catch (Exception e) {
			Log.e(TAG, "init error", e);
		}			
	}
	
	
	private void cancelTimerTask(){
		
		try{
			if(mTimerTask!=null ){
				 synchronized (mTimerTask) {
					 mTimerTask.pause=false;
					 mTimerTask.notifyAll();
					 mTimerTask.cancel();
					 mTimerTask=null;
				 }
			}	
		}catch(Exception ex){}
		
		try{
			 if(mTimer!=null){
				 mTimer.cancel();
				 mTimer=null;
			 }
		}catch(Exception ex){}
	}
	

	class MyTimerTask extends  TimerTask {
		/**是否暂停 **/
		public volatile boolean pause=false;

		@Override
		public void run() {
			synchronized (this) {
	            while(pause) {
	                try {
	                    wait();
	                } catch (InterruptedException e) {
	                    Thread.interrupted();
	                }
	            }
	        }
			if(mediaPlayer==null )
				return;
			
			try{

				Message msg=new Message();
				int currentPostion=mediaPlayer.getCurrentPosition();
				msg.arg1=currentPostion;
				handleProgress.sendMessage(msg);
				if(currentPostion==mDuration || mListener==null){
					cancelTimerTask();
				}
			}catch(Exception ex){
				cancelTimerTask();
				return;
			}

	    }		
		
	};
	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {

			try{
				if(mTimerTask!=null && mListener!=null){
					mListener.OnCurrentPosition(msg.arg1);
				}
			}catch(Exception ex){
				Log.e(TAG, ex.getMessage());
			}
		};
	};


	
	
	public void playUrl(String fileUrl,boolean notifyPosition)
	{
		try {

			mNotifyPosition=notifyPosition;
			init();
			
			if(fileUrl.startsWith("http")){
				mediaPlayer.setDataSource(fileUrl);
				mediaPlayer.prepareAsync();	
			}else{
//				FileInputStream fs = new FileInputStream(fileUrl);
//				FileDescriptor fd= fs.getFD();
//				fs.close();
//				mediaPlayer.setDataSource(fd);				
//				mediaPlayer.prepare();
				
				
				FileInputStream fs = new FileInputStream(fileUrl);
				mediaPlayer.setDataSource(fs.getFD());
				mediaPlayer.prepare();
				fs.close();
			}
			
			
		}catch(Exception e){
			if(mListener!=null)
				mListener.OnError(e);
		}

	}	
	
	
	
	public void playUrl(String fileUrl)
	{
		playUrl(fileUrl,false);
	}
//
//	/** 设置附加标识 **/
//	public void setTag(int tag){
//		mTag=tag;
//	}
//	
	
	/**播放**/
	public void play(){
		if(mediaPlayer==null)
			return;
		
		try{
			mediaPlayer.start();
			if(mTimerTask!=null ){
				 synchronized (mTimerTask) {
					 mTimerTask.pause=false;
					 mTimerTask.notifyAll();
				 }
			}
		}catch(Exception ex){}


	}
	

	
	/**暂停**/
	public void pause()
	{
		if(mediaPlayer==null)
			return;
		
		try{
			mediaPlayer.pause();
			if(mTimerTask!=null ){
				 synchronized (mTimerTask) {
					 mTimerTask.pause=true;
				 }
			}
		}catch(Exception ex){}
	}
	/** 停止 **/
	public void stopPlay()
	{
		try{
			cancelTimerTask();
			if (mediaPlayer != null && mediaPlayer.isPlaying()) { 
				mediaPlayer.stop();
	        } 
		}catch(Exception ex){}

		
	}

	public void release(){
		
		try{
			if(mediaPlayer!=null){
				stopPlay();
				 mediaPlayer.release();
				 mediaPlayer = null; 
			}
		}catch(Exception ex){}
	}
	
	
	
	
	@Override
	/**
	 * 通过onPrepared播放
	 */
	public void onPrepared(MediaPlayer arg0) {
	//	arg0.start();
		mDuration=mediaPlayer.getDuration();
		if(mListener!=null){
			mListener.OnPrepared(mDuration);
		}
		
		
		if(mNotifyPosition){
			if(mTimerTask==null)
				mTimerTask = new MyTimerTask();
			if(mTimer==null)
				mTimer=new Timer();
			mTimer.schedule(mTimerTask, 0, 25);
		}
		
		play();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		try{
			if(mListener!=null){
				mListener.OnCompletion();
			}
			
		}catch(Exception ex){}
		
		try{
			release();
		}catch(Exception ex){}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		if(mListener!=null){
			mListener.OnBufferingupdate(bufferingProgress);
		}
	}
	
	
	/** 设置监听状态 **/
	public void SetOnListener(OnListener l){
		mListener=l;
	}
	
	/** 获取监听器 **/
	public OnListener getListener(){
		return mListener;
	}
	
	

	

	/** 是否正在播放 **/
	public boolean isPlaying(){
		boolean playing=false;
		if(mediaPlayer!=null && mediaPlayer.isPlaying())
			playing=true;
		return playing;
	}

}
