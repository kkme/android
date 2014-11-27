package com.link.bianmi.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.link.bianmi.widget.BlurView;
import com.link.bianmi.widget.photoview.PhotoViewAttacher;

public class ScaleImageView extends TopCropImageView{
	PhotoViewAttacher mPhotoViewAttacher;
	ImageViewListener imageViewListener;
	ValueAnimator animator;
	public static final int anim_duration = 240;
	private BlurView blurView;
	
	
	
	public void setBlurView(BlurView blurView) {
		this.blurView = blurView;
	}

	public interface ImageViewListener{
		public void onSingleTap();
		public void onScaleEnd();
	}
	

	public void setImageViewListener(ImageViewListener imageViewListener) {
		this.imageViewListener = imageViewListener;
	}

	public ScaleImageView(Context context) {
		super(context);
//		init();
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init();
	}

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		init();
	}
	
	public void initAttacher(){
		
		mPhotoViewAttacher = new PhotoViewAttacher(this);
		mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
			
			@Override
			public void onViewTap(View arg0, float arg1, float arg2) {
				// TODO Auto-generated method stub
//				((View)getParent()).setBackgroundColor(Color.argb(0, 0, 0, 0));
				if(imageViewListener!=null) imageViewListener.onSingleTap();
			}
		});
		mPhotoViewAttacher.setScaleType(ScaleType.FIT_CENTER);
		mPhotoViewAttacher.update();
	}
	
	public void startScaleAnimation(final ImageView smallImageView){
		if(animator!=null &&animator.isRunning()){
			return;
		}
		this.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startScale( smallImageView);
			}
		});
	}
	
	public void startCloseScaleAnimation(){
		if(animator!=null &&animator.isRunning()){
			return;
		}
		this.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startCloseScale();
			}
		});
	}
	
	public void resetScale(){
		mPhotoViewAttacher.zoomTo(0, 0, 0);
	}
	
	private void startCloseScale(){
		animator.setFloatValues(1.0f,0);
		animator.start();
	}
	
	private void startScale(ImageView smallImageView){
		if(smallImageView==null) return;
		if(mPhotoViewAttacher != null){
//			mPhotoViewAttacher.cleanup();
			mPhotoViewAttacher = null;
		}
		ScaleImageView.this.setTopCrop(true);
		this.setImageDrawable(smallImageView.getDrawable());
		this.postInvalidate();
		final View parentView = (View)getParent();
		int[] location = getViewLocationInWindow(smallImageView);
		int[] parent_location = getViewLocationInWindow(parentView);
		location[1] = location[1] - parent_location[1]; 
		
		final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
		final int margin_top = location[1] - parentView.getPaddingTop();
		final int margin_left = location[0];
		final int small_width = smallImageView.getWidth();
		final int small_height = smallImageView.getHeight();
		final int big_width = parentView.getWidth();
		final int big_height = parentView.getHeight() - parentView.getPaddingTop();
		layoutParams.topMargin = margin_top;
		layoutParams.leftMargin = margin_left;
		layoutParams.width = small_width;
		layoutParams.height = small_height;
		this.requestLayout();
		this.setVisibility(View.VISIBLE);
		final int drawable_width = this.getDrawable().getIntrinsicWidth();
		final int drawable_height = this.getDrawable().getIntrinsicHeight();
		final int target_width_by_drawable = parentView.getWidth();
		final int target_height_by_drawable = Math.min( big_height,  Math.round((float)target_width_by_drawable / (float)drawable_width * (float)drawable_height));
		final int target_margin_top_by_drawable = Math.max(0,  (big_height - target_height_by_drawable)/2 );
		animator = ValueAnimator.ofFloat(0, 1.0f);
//		animator.setCurrentPlayTime(10);
		animator.setDuration(anim_duration);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				float value = (Float) animation.getAnimatedValue();
				int black_alpha = (int) (200 * value);
				parentView.setBackgroundColor(Color.argb(black_alpha, 0, 0, 0));
				blurView.setAlpha(value);
//				layoutParams.topMargin = (int) (margin_top * (1.0f - value));
//				layoutParams.leftMargin = (int) (margin_left * (1.0f - value));
//				layoutParams.width = (int) (small_width + (big_width - small_width) * value);
//				layoutParams.height = (int) (small_height + (big_height - small_height) * value);
//				MxxScaleImageView.this.requestLayout();
				
				layoutParams.topMargin = (int) ( margin_top + ( target_margin_top_by_drawable - margin_top) * value);
				layoutParams.leftMargin = (int) (margin_left * (1.0f - value));
				layoutParams.width = (int) (small_width + (big_width - small_width) * value);
				layoutParams.height = (int) (small_height + (target_height_by_drawable - small_height) * value);
				ScaleImageView.this.requestLayout();
				
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				if(layoutParams.height < big_height){//��ʾ�ǷŴ�ʱ������
					layoutParams.topMargin = 0;
					layoutParams.width = big_width;
					layoutParams.height = big_height;
					ScaleImageView.this.requestLayout();
				}
				
				if(imageViewListener!=null) imageViewListener.onScaleEnd();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		animator.start();
	}
	
	
	
	private int[] getViewLocationInWindow(View view){
		int[] location = new int[2];
		view.getLocationInWindow(location);
		return location;
	}
	
	private int getWindowHeight(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

}
