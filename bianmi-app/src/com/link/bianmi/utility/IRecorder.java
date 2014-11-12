package com.link.bianmi.utility;

import java.util.Date;


/** 录音器接口 **/
public interface IRecorder {
	/** 获取持续时间**/
	long getDuration();
	/** 获取录音开始时间 **/
	Date getStart();
	/** 开始录音 **/
	void startRecord();
	/**停止录音 **/
	void stopRecord();
	/**取消录音**/
	void cancelRecord();

	/** 设置监听状态 **/
	public void SetOnListener(OnListener l);
	
	public interface OnListener{
		/** 音量 **/
		void OnVolumnPower(float power);
		/** 停止录音 **/
		void OnStop();
		/**取消录音**/
		void OnCancel();
	}	
}
