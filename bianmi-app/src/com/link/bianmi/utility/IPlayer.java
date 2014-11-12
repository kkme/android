package com.link.bianmi.utility;

public interface IPlayer {

	public void play();
	public void playUrl(String fileUrl);
	public void playUrl(String fileUrl,boolean notifyPosition);
	public void pause();
	public void stopPlay();
	public void release();
	
	/** 获取监听器 **/
	public OnListener getListener();
	public void SetOnListener(OnListener l);
	
	
	public interface OnListener{
		/** 播放结束 **/
		void OnCompletion();
		/** 准备播放 **/
		void OnPrepared(int maxPosition);
		/** 更新缓存 **/
		void OnBufferingupdate(int bufferingProgress);
		/** 异常 **/
		void OnError(Exception e);
		/** 当前播放位置通知 **/
		void OnCurrentPosition(int currentPosition);
	}	
}
