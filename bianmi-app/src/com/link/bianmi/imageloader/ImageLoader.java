package com.link.bianmi.imageloader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.link.bianmi.MyApplication;
import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.utils.ImageHelper;

/** 头像加载器 **/
public class ImageLoader{

	private static ImageLoader mInstance;
	/**最大图片宽度**/
	private static final int MAX_WIDTH_IMAGE=640;	
	/**最大头像宽度**/
	private static final int MAX_WIDTH_AVATAR=120;
	
	public static final int DEFAULT_IMAGE_RESID=R.drawable.ic_launcher;
	private SimpleImageLoader mLoader;
	private SimpleImageLoader mLoader_temp;

	/**默认图片**/
	private Bitmap mDefaultImage;

	

	/**清理所有Cache**/
	public static void cleanAllCache(){
		getInstance().mLoader.clear();
		getInstance().mLoader_temp.clear();
	}

	private static ImageLoader getInstance(){
		if(mInstance==null){
			mInstance=new ImageLoader();
			mInstance.mDefaultImage=ImageManager.drawableToBitmap(MyApplication.getInstance().getResources().getDrawable(DEFAULT_IMAGE_RESID));
			
			
			mInstance.mLoader=new SimpleImageLoader(MyApplication.getInstance(),"",mInstance.mDefaultImage);
			mInstance.mLoader_temp=new SimpleImageLoader(MyApplication.getInstance(),
					SysConfig.getInstance().getSecretPath(), mInstance.mDefaultImage);
		}
		return mInstance;
	}
	

	
	
	private  ImageLoaderCallback createCenterImageViewCallback(
			final ImageView iv, String url,final int width) {
		return new ImageLoaderCallback() {
			@Override
			public void refresh(String url, final Bitmap bitmap,boolean httperror) {
				if(iv!=null && url.compareTo((String)iv.getTag())==0)
					displayCenterImage(iv,bitmap,width);
			}
		};
	}
	
	/*显示居中图片**/
	private  void displayCenterImage(ImageView coverImage,Bitmap bitmap,int width){
		try{
			Bitmap bmap=null;
			if(bitmap!=null)
				bmap=ImageHelper.getScaleImg(bitmap,width);
			coverImage.setImageBitmap(bmap);
		}
		catch(OutOfMemoryError e){}
		catch(Exception ex){}
	}
	
	


	
	/**显示居中图片
	 * 
	 * **/
	private  void displayCenterImage(SimpleImageLoader loader,ImageView coverImage,String url,Bitmap defaultBitmap,int width){

		if(TextUtils.isEmpty(url)	|| 
				!loader.dispaly(coverImage,url,createCenterImageViewCallback(coverImage,url,width))){

			displayCenterImage(coverImage,defaultBitmap,width);
		}
	}
	
	
	
	/** 获取图像加载器
	     * @param isTempFile
	     * 	是否临时文件
	 */
	public static SimpleImageLoader getLoader(boolean isTempFile){
		if(isTempFile)
			return getInstance().mLoader_temp;
		else
			return getInstance().mLoader;
	}
	

	
	
	private static String getURL(String url,int width){
		return url + "?imageView2/2/w/" + width;
	}
	
    /**
     * 图像加载
     * 
     * @param defaultResId
     * 	默认图片
     * @param dynamic
     * 	动态调整view高度
     * @param temp
     *  temp file
     * **/
    public static void displayImage(final ImageView imageView, final String url,final int defaultResId,final boolean isTempFile) {
        int viewWidth = imageView.getWidth();
        int width = Math.min(viewWidth, MAX_WIDTH_IMAGE);
        getLoader(isTempFile).display(imageView, getURL(url,width),defaultResId);

    }
    
    
    
    
    /**
     * 
    * @param width
    * 	期望宽度:  如果大于MAX_WIDTH_IMAGE 将被强制替换
     */
    public static void displayImage(final ImageView imageView, final String url,final int defaultResId,int width,final boolean isTempFile) {

    	if(width<=0 || width>MAX_WIDTH_IMAGE)
    		width=MAX_WIDTH_IMAGE;
        getLoader(isTempFile).display(imageView, getURL(url,width),defaultResId);

    }
    
    public static void displayImage_DynamicView(final ImageView imageView, final String url,final boolean isTempFile) {
    	
        int viewWidth = imageView.getWidth();
        if(viewWidth>0){
            int width = Math.min(viewWidth, MAX_WIDTH_IMAGE);
            getInstance().displayCenterImage(getLoader(isTempFile),
            			imageView, getURL(url,width),
            			getInstance().mDefaultImage, viewWidth);
        }else{
	        final ViewTreeObserver observer = imageView.getViewTreeObserver();
	        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                // 根据返回的图片宽度，动态请求合适的图片大小
	                // 图片最大宽度为 640px
	                int viewWidth = imageView.getWidth();
	                int width = Math.min(viewWidth, MAX_WIDTH_IMAGE);
	                getInstance().displayCenterImage(getLoader(isTempFile),
	                			imageView, getURL(url,width),
	                			getInstance().mDefaultImage, viewWidth);
	            }
	        });
        }
    }  
    
    
    /**
     * 显示图片：动态调整图片高度
     * 
     * @param resId
     * 	图片资源 
     */
    public static void displayImage_DynamicView(final ImageView imageView,final int resId,final boolean isTempFile) {
    	
    	int viewWidth = imageView.getWidth();
        if(viewWidth>0){
            Bitmap bitmap =BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), resId);
            if(bitmap!=null){
            	getInstance().displayCenterImage(imageView, bitmap, viewWidth);
            	if(!bitmap.isRecycled())
            		bitmap.recycle();
            }
        }else{    	
	        final ViewTreeObserver observer = imageView.getViewTreeObserver();
	        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

	                int viewWidth = imageView.getWidth();
	                Bitmap bitmap =BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), resId);
	                if(bitmap!=null){
	                	getInstance().displayCenterImage(imageView, bitmap, viewWidth);
	                	if(!bitmap.isRecycled())
	                		bitmap.recycle();
	                }
	            }
	        });
        }
    }  

    
    /**
     * 显示图片：动态调整图片高度
     */   
    public static void displayImage_DynamicView(final ImageView imageView,final Bitmap bitmap,final boolean isTempFile) {
    	
    	int viewWidth = imageView.getWidth();
        if(viewWidth>0){
            if(bitmap!=null){
            	getInstance().displayCenterImage(imageView, bitmap, viewWidth);
            }        	
        }else{
	        final ViewTreeObserver observer = imageView.getViewTreeObserver();
	        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            @Override
	            public void onGlobalLayout() {
	                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                
	                int viewWidth = imageView.getWidth();
	                if(bitmap!=null){
	                	getInstance().displayCenterImage(imageView, bitmap, viewWidth);
	                }
	            }
	        });
        }
    }    
    
    
    /**加载头像**/
    public static void displayAvatar(ImageView imageView,String url){
    	displayAvatar(imageView,url,true);
    }
    
    /**	加载头像
     * 
     * @param isTempFile
     * 	是否临时文件
     * **/
    public static void displayAvatar(final ImageView imageView,String url,final boolean isTempFile){
    	if(url==null || TextUtils.isEmpty(url)){
    		imageView.setImageResource(R.drawable.ic_launcher);	
    	}else{
    		
    		if(url.startsWith("llss.") || url.startsWith("http://llss.")){
    			url+= "?imageView2/2/w/" + MAX_WIDTH_AVATAR;
    		}
    		getLoader(isTempFile).display(imageView, url,R.drawable.ic_launcher);

    	}
    }
	
}
