package com.link.bianmi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.link.bianmi.R;



/**圆角ImageView**/
public class RoundImageView extends ImageView{
	
	private final RectF mRoundRect = new RectF();
	private float mRadius = 6;
	private final Paint maskPaint = new Paint();
	private final Paint zonePaint = new Paint();
	/**是否圆形**/
	private boolean mIsCircle=false;
	
	public RoundImageView(Context context) {
		super(context);
		init();
	}
	
	
	public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
		if(attrs!=null){
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImage); 
			mRadius=a.getDimension(R.styleable.RoundImage_rv_radius,6);
			mIsCircle=a.getBoolean(R.styleable.RoundImage_rv_circle, false);
			a.recycle();			
		}
		
	}
	

	

	
	private void init() {
	        maskPaint.setAntiAlias(true);	//抗锯齿
	        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));	//两者相交时候显示
	        //
	        zonePaint.setAntiAlias(true);
	        zonePaint.setColor(Color.WHITE);
	        //
	        float density = getResources().getDisplayMetrics().density;
	        mRadius = mRadius * density;
	}
	
	public void setRectAdius(float adius) {
			mRadius = adius;
	        invalidate();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
	        super.onLayout(changed, left, top, right, bottom);
	        int w = getWidth();
	        int h = getHeight();
	        mRoundRect.set(0, 0, w, h);
	        if(mIsCircle)
	        	mRadius=w;
	}
	
	@Override
	public void draw(Canvas canvas) {
	        canvas.saveLayer(mRoundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
	        canvas.drawRoundRect(mRoundRect, mRadius, mRadius, zonePaint);

	        canvas.saveLayer(mRoundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
	        super.draw(canvas);
	        canvas.restore();
	}
}
