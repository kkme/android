package com.link.bianmi.utility;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** 录音器 **/
public class AudioRecorder implements IRecorder{
	
	private final String TAG = "AudioRecorder";
	/** 振幅通知 **/
	private static final int MSG_MAXAMPLITUDE = 1;
	 /** 停止通知 **/
	private static final int MSG_STOP=2;
	/**取消通知**/
	private static final int MSG_CANCEL=3; 
	
	 
	private MediaRecorder mRecorder;
	
	private final static int BASE =200;	//噪声基准
	private static final int MAX_LENGTH = 1000 * 60 * 2;// 最大录音时长2分钟
	  
	private final static int BIT_RATE=32000;	//比特率
	private static final int SAMPLE_RATE=8000;	//采样率
	private final static int CHANNEL=1; 
	
	 /**相对最大音量值（过滤噪音）**/
    private static final float BASE_MAX_POWER=(float) (10 * Math.log10(32768));
    
	private String  mFilePath;  	//文件地址
    private Handler   mHandler;		
    
    private Date mStartDate;	//录音开始时间
    private Date mEndDate;		//录音结束时间
    private OnListener mListener; 
    
	private Timer mTimer=new Timer();
	//定时器任务
	private RecoderTask	mRecorderTask;

    
    public AudioRecorder(String filepath) {  
    	mFilePath = filepath; 
    	
    }  
    
    public Date getStart(){
    	return mStartDate;
    }
    /**获取文件地址**/
    public String getRecorderPath(){
    	return mFilePath;
    }
    
    /** 
     * 开始录音 使用aac格式 
     * @param mRecAudioFile 
     *            录音文件 
     * @return 
     */  
    public void startRecord() {
    	mEndDate=null;
        // 开始录音  
        /* ①Initial：实例化MediaRecorder对象 */  
        if (mRecorder == null)  
            mRecorder = new MediaRecorder();  
        try {  
        	
			File dir = new File(FileHelper.getFileDir(mFilePath));
			if (!dir.exists()) {
				dir.mkdirs();
			}
            /* ②setAudioSource/setVedioSource */  
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风  
            /* 
             * ③设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式 
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB) 
             */  
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB); 
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            mRecorder.setAudioChannels(CHANNEL);
            mRecorder.setAudioEncodingBitRate(BIT_RATE);
            mRecorder.setAudioSamplingRate(SAMPLE_RATE);
            /* 准备 */  
            mRecorder.setOutputFile(mFilePath);  
            mRecorder.setMaxDuration(MAX_LENGTH);  
            mRecorder.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					Log.i("test", "err");
				}
			});
            mRecorder.prepare();  
            /* 开始 */  
            mStartDate=new Date();
            mRecorder.start();  
            // AudioRecord audioRecord.  
            /* 获取开始时间* */  
           // startTime = System.currentTimeMillis();  
            // pre=mRecorder.getMaxAmplitude();  
           // updateMicStatus();    
            
            if(mHandler!=null){
            	mRecorderTask=new RecoderTask();
            	mTimer.schedule(mRecorderTask, 0, 50);
            }
        } catch (Exception e) {  
            Log.i(TAG, e.getMessage());  
        }
  
    }  
  
    
    
    public long getDuration(){
    	if(mStartDate!=null && mEndDate!=null){
    		return Math.min(mEndDate.getTime() - mStartDate.getTime(),MAX_LENGTH); 
    	}else{
    		return 0;
    	}
    }
    /** 
     * 停止录音 
     *  
     * @param mRecorder 
     */  
    @Override
    public void stopRecord() {  

    	try{
        	if(mEndDate==null)
        		mEndDate=new Date();   		
	    	if(mRecorderTask!=null){
	    		mRecorderTask.cancel();
	    		mRecorderTask=null;
	    	}
	    	if (mRecorder != null) {
		        mRecorder.stop();  
		        mRecorder.reset();  
		        mRecorder.release();  
		        mRecorder = null;  
		        obtainMessage(MSG_STOP,null);
	    	}

    	}catch(Exception ex){}
    	
    }  
    
    /**取消录音**/
    @Override
    public void cancelRecord(){
    	
    	try{
        	if(mEndDate==null)
        		mEndDate=new Date();
	    	if(mRecorderTask!=null){
	    		mRecorderTask.cancel();
	    		mRecorderTask=null;
	    	}
	    	if (mRecorder != null) {
		        mRecorder.stop();  
		        mRecorder.reset();  
		        mRecorder.release();  
		        mRecorder = null;  
		        FileHelper.delete(mFilePath);
		        obtainMessage(MSG_CANCEL,null);
	    	}

    	}catch(Exception ex){} 	
    	
    }
    
    
    
	/** 设置监听状态 **/
	public void SetOnListener(OnListener l){
		mListener=l;
		if(mHandler!=null)
			mHandler=null;
		mHandler = new Handler() {
			public void handleMessage(Message m) {

		        switch (m.what) {
		          case MSG_MAXAMPLITUDE: {
		        	 if(mListener!=null)
		        		 mListener.OnVolumnPower((Float)m.obj);
		          }break;
		          
		          case MSG_STOP:{
		        	  if(mListener!=null){
		        		  mListener.OnStop();
		        	  }
		        	  mListener=null;
		        	  mHandler=null;
		          }break;
		          
		          case MSG_CANCEL:{
		        	  if(mListener!=null){
		        		  mListener.OnCancel();
		        	  }
		        	  mListener=null;
		        	  mHandler=null;		        	  
		          }break;
		          
		          default:
		            break;
		        }
			};
		};
	}
    
	
    private void obtainMessage(int what, Object obj){
    	if(mHandler!=null){
    		
    		if(what==MSG_MAXAMPLITUDE){
    			float power = 0;// 音量大小  
	        	//	BASE的值由自己测试获得。 开启麦克风，不对麦克风说话，而由周围噪声获取的值  大概在300~600
    			float ratio=(Integer)obj/ BASE;
	            if (ratio > 1){  
	            	power=(float)(20 * Math.log10(ratio))/BASE_MAX_POWER; 
	            	if(power<1){
	            		mHandler.obtainMessage(what, power).sendToTarget();
	            	}
		        }
	    		
    		}else if(what==MSG_STOP){
    			mHandler.obtainMessage(what).sendToTarget();
    		}else if(what==MSG_CANCEL){
    			mHandler.obtainMessage(what).sendToTarget();
    		}
    	}
    }
    
	class RecoderTask extends  TimerTask{
		@Override
		public void run() {
			if(mRecorder!=null)
				obtainMessage(MSG_MAXAMPLITUDE,mRecorder.getMaxAmplitude());

			if(new Date().getTime() - mStartDate.getTime()>=MAX_LENGTH){
				stopRecord();
			}
	    }
	};

}
