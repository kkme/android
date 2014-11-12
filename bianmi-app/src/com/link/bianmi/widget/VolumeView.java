package com.link.bianmi.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.link.bianmi.R;

public class VolumeView extends View  {
	
	public final static int MAX_PERCENT=100;

	BitmapDrawable mBg;
	BitmapDrawable mFg;
	
	Paint mPaint; 
	/**显示音量百分比**/
	private int mPercent=0;
	
	public VolumeView(Context context) {  
         super(context,null);  

     }  
	
	public VolumeView(Context context, AttributeSet attrs) {
		super(context, attrs);
        mFg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_voice_hit);
        mBg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_voice);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
	}

	
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
	        int measureWidth = measureWidth(widthMeasureSpec);  
	        int measureHeight = measureHeight(heightMeasureSpec);  
	        // 计算自定义的ViewGroup中所有子控件的大小  
	        //measureChildren(widthMeasureSpec, heightMeasureSpec);  
	        // 设置自定义的控件MyViewGroup的大小  
	        setMeasuredDimension(measureWidth, measureHeight);  
	        
	        mBg.setBounds(0,0,measureWidth,measureHeight);
	        mFg.setBounds(0,0,measureWidth,measureHeight);
	    }  
	 
	 
	    private int measureWidth(int pWidthMeasureSpec) {  
	        int result = 0;  
	        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式  
	        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸  
	  
	        switch (widthMode) {  
 
	        case MeasureSpec.AT_MOST:  
	        case MeasureSpec.EXACTLY:  
	            result = widthSize;  
	            break;  
	        }  
	        return result;  
	    }  
	  
	    private int measureHeight(int pHeightMeasureSpec) {  
	        int result = 0;  
	  
	        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);  
	        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);  
	  
	        switch (heightMode) {  
	        case MeasureSpec.AT_MOST:  
	        case MeasureSpec.EXACTLY:  
	            result = heightSize;  
	            break;  
	        }  
	        return result;  
	    } 	 
 
	    
	  
	    
	@Override
	protected void onDraw(Canvas canvas) { 
		 super.onDraw(canvas); 
		 
		 mBg.draw(canvas); 
		 canvas.save();
		 
		 int height=this.getHeight();
		 canvas.clipRect(0, height*(MAX_PERCENT-mPercent)/MAX_PERCENT , this.getWidth(),height);
		 mFg.draw(canvas);
		 
		 canvas.restore();
		 
	 }
	
	
	public void setVolume(int percent){
		mPercent=Math.min(MAX_PERCENT, percent);
		this.invalidate();
	}
}
