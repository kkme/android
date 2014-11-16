package com.link.bianmi.widget;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.link.bianmi.R;


/** 环形进度条 **/
public class RoundProgressBar extends TextView{

	
	 private Paint mFramePaint;      
	 
	 
	 //--------------------
     private Paint  mRoundPaints;		// 主进度条画笔
     private RectF  mRoundOval;			// 矩形区域
     private float    mPaintWidth;		// 画笔宽度
     private int    mPaintColor;		// 画笔颜色
     private int    mButtomColor;			//背景颜色
    
     
     private int mStartProgress;	    // 进度条起始位置
     private int mCurProgress;    		// 主进度条当前位置
	 private int mMaxProgress;			// 进度条最终位置
	 
	 private boolean mBRoundPaintsFill;	// 是否填充区域
	 //---------------------
	 private int   mSidePaintInterval;	// 圆环向里缩进的距离
	 
	 private Paint mSecondaryPaint;     // 辅助进度条画笔
	 
	 private int   mSecondaryCurProgress;	// 辅助进度条当前位置
	 
	 private Paint mBottomPaint;		// 进度条背景图画笔
	 
	 private boolean mBShowBottom;		// 是否显示进度条背景色
	 
	 

	 
	 private int mDefaultProgress=0;
	 
	 
	 
	public RoundProgressBar(Context context) {
		super(context);
		
		initParam();
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initParam();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);      
        
        mButtomColor=array.getColor(R.styleable.RoundProgressBar_bottom_color, Color.GRAY);
        mBottomPaint.setColor(mButtomColor);
        mMaxProgress = array.getInt(R.styleable.RoundProgressBar_maxProgress, 100); 	
//        mSaveMax = mMaxProgress;
        mBRoundPaintsFill = array.getBoolean(R.styleable.RoundProgressBar_fill, true);	// 获得是否是填充模式
        if (mBRoundPaintsFill == false)
        {
        	mRoundPaints.setStyle(Paint.Style.STROKE);
        	mSecondaryPaint.setStyle(Paint.Style.STROKE);
        	mBottomPaint.setStyle(Paint.Style.STROKE);
        }
        
        
        mSidePaintInterval = array.getInt(R.styleable.RoundProgressBar_inside_interval, 0);// 圆环缩进距离
        
        
        mBShowBottom = array.getBoolean(R.styleable.RoundProgressBar_show_bottom, true);
        
        mPaintWidth = array.getDimension(R.styleable.RoundProgressBar_paint_width, 5);
        if (mBRoundPaintsFill)						// 填充模式则画笔长度改为0
        {
        	mPaintWidth = 0;
        }
        
        mRoundPaints.setStrokeWidth(mPaintWidth);
        mSecondaryPaint.setStrokeWidth(mPaintWidth);
        mBottomPaint.setStrokeWidth(mPaintWidth);

        mPaintColor = array.getColor(R.styleable.RoundProgressBar_paint_color, 0xffffcc00);
        mRoundPaints.setColor(mPaintColor);
        int color = mPaintColor & 0x00ffffff | 0x66000000;
        mSecondaryPaint.setColor(color);
        
        mDefaultProgress=array.getInt(R.styleable.RoundProgressBar_rb_defaultvalue,0);
        
        
        
      
        
        array.recycle(); //一定要调用，否则会有问题
     
      
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(mDefaultProgress>0)
			setProgress(mDefaultProgress);
	}
	
	/**设置画笔宽度**/
	public void setPaintWidth(float width){
		mPaintWidth=width;
        mRoundPaints.setStrokeWidth(mPaintWidth);
        mSecondaryPaint.setStrokeWidth(mPaintWidth);
        mBottomPaint.setStrokeWidth(mPaintWidth);		
		
	}

	
	 
    private void initParam()
    {
    	mFramePaint = new Paint();
        mFramePaint.setAntiAlias(true);
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setStrokeWidth(0);
               
        mPaintWidth = 0;
        mPaintColor = 0xffffcc00;
          
        
        mRoundPaints = new Paint();
        mRoundPaints.setAntiAlias(true);
        mRoundPaints.setStyle(Paint.Style.FILL);

        mRoundPaints.setStrokeWidth(mPaintWidth);
        mRoundPaints.setColor(mPaintColor);
        
        mSecondaryPaint = new Paint();
        mSecondaryPaint.setAntiAlias(true);
        mSecondaryPaint.setStyle(Paint.Style.FILL);
        mSecondaryPaint.setStrokeWidth(mPaintWidth);
        
        int color = mPaintColor & 0x00ffffff | 0x66000000;
        mSecondaryPaint.setColor(color);
        
        
        mBottomPaint = new Paint();
        mBottomPaint.setAntiAlias(true);
        mBottomPaint.setStyle(Paint.Style.FILL);
        mBottomPaint.setStrokeWidth(mPaintWidth);
        mBottomPaint.setColor(Color.GRAY);
           
        
        mStartProgress = -90;
        mCurProgress = 0;
        mMaxProgress = 100;
//        mSaveMax = 100;
        
        mBRoundPaintsFill = true;
        mBShowBottom = true;
        
        mSidePaintInterval = 0;
        
        mSecondaryCurProgress = 0;           
        
        mRoundOval = new RectF(0, 0,  0, 0);
        

    }
	
    
	
    public synchronized void setProgress (int progress)
    {
    	mCurProgress = progress;
    	if (mCurProgress < 0)
    	{
    		mCurProgress = 0;
    	}
    	
    	if (mCurProgress > mMaxProgress)
    	{
    		mCurProgress = mMaxProgress;
    	}
    	
    	invalidate();
    }
    
    public synchronized int getProgress()
    {
    	return mCurProgress;
    }
    
    public synchronized void setSecondaryProgress (int progress)
    {
    	mSecondaryCurProgress = progress;
    	if (mSecondaryCurProgress < 0)
    	{
    		mSecondaryCurProgress = 0;
    	}
    	
    	if (mSecondaryCurProgress > mMaxProgress)
    	{
    		mSecondaryCurProgress = mMaxProgress;
    	}
    	
    	invalidate();
    }
    
    public synchronized int getSecondaryProgress()
    {
    	return mSecondaryCurProgress;
    }
	
    public synchronized void setMax(int max)
    {
    	max=Math.max(1, max);
    	
    	mMaxProgress = max;
    	if (mCurProgress > max)
    	{
    		mCurProgress = max;
    	}
    	
    	if (mSecondaryCurProgress > max)
    	{
    		mSecondaryCurProgress = max;
    	}
    	
 //   	mSaveMax = mMaxProgress;
    	
    	invalidate();
    }
    
    public synchronized int getMax()
    {
    	return mMaxProgress;
    }
    
    
    
    
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		super.onSizeChanged(w, h, oldw, oldh);

		Log.i("", "W = " + w + ", H = " + h);
		
		
		if (mSidePaintInterval != 0)
		{
			mRoundOval.set(mPaintWidth/2 + mSidePaintInterval, mPaintWidth/2 + mSidePaintInterval,
			w - mPaintWidth/2 - mSidePaintInterval, h - mPaintWidth/2 - mSidePaintInterval);	
		}else{

			int sl = getPaddingLeft();
			int sr = getPaddingRight();
			int st = getPaddingTop();
			int sb = getPaddingBottom();
		
					
			mRoundOval.set(sl + mPaintWidth/2, st + mPaintWidth/2, w - sr - mPaintWidth/2, h - sb - mPaintWidth/2);	
		}
		
	}
	
	

	public void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		       	

		if (mBShowBottom)
		{
			canvas.drawArc(mRoundOval, 0, 360, mBRoundPaintsFill, mBottomPaint);	
		}
		
		float secondRate = (float)mSecondaryCurProgress / mMaxProgress;
		float secondSweep = 360 * secondRate;
		canvas.drawArc(mRoundOval, mStartProgress, secondSweep, mBRoundPaintsFill, mSecondaryPaint);
		
		float rate = (float)mCurProgress / mMaxProgress;
		float sweep = 360 * rate;
		canvas.drawArc(mRoundOval, mStartProgress, sweep, mBRoundPaintsFill, mRoundPaints);
	
	
	}
	

	
	

	
	
	//-----------动画----------
	
	
	int mProcessRInterval;
	int mAnimProgress;
	int mAnimDuration;
	TimerTask mTimerTask;
	Timer mTimer;
	Handler mHandler;
	
	private static final int mTimerInterval=25;
	/**开始动画**/
	public void startAnim(int progress,int duration){
		progress=1000*progress;
		mAnimProgress=Math.min(mMaxProgress,progress);
		mAnimDuration=duration;
		mProcessRInterval =Math.max(1,mTimerInterval *mAnimProgress  / duration);	

		mHandler = new Handler(){ 
	        public void handleMessage(Message msg) {  
	            switch (msg.what) {      
		            case 1: 
		            	int progress=mCurProgress+mProcessRInterval;
		            	if(progress>=mAnimProgress){
		            		progress=mAnimProgress;
		            		stopAnim();
		            	}
		            	setProgress(progress);
		            break;      
	            }      
	            super.handleMessage(msg);  
	        }  
		};
		
		
		stopAnim();
		mTimerTask = new TimerTask(){  
		      public void run() {  
		    	  
		    	  if(mCurProgress>=mMaxProgress){
		    		  stopAnim();
		    		  return;
		    	  }
		    	  
			      Message message = new Message();      
			      message.what = 1;    
			      mHandler.sendMessage(message);    
		   }  
		};
		mTimer=new Timer();
		mTimer.schedule(mTimerTask,mTimerInterval, mTimerInterval);
		
	}
	
    public synchronized void setAnimMax(int max){
    	setMax(Math.max(1,max)*1000);
    }
    
    
	private void stopAnim(){
		try{
			if(mTimerTask!=null){
				mTimerTask.cancel();
				mTimerTask=null;
			}
			if(mTimer!=null){
				mTimer.cancel();
				mTimer=null;
			}
		}catch(Exception ex){}
	}
	
}

