package com.link.bianmi.imageloader;

import java.lang.Thread.State;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.link.bianmi.http.HttpException;


/**
 * 延迟加载图片 
 * @author sunpf
 */
public class SimpleImageLoader {
	private static final String TAG = "SimpleImageLoader";
	public static final int HANDLER_MESSAGE_ID = 1;
	public static final String EXTRA_BITMAP = "extra_bitmap";
	public static final String EXTRA_IMAGE_URL = "extra_image_url";
	public static final String EXTRA_HTTPERROR = "extra_httperror";

	private ImageManager mImageManager; 
	private BlockingQueue<String> mUrlList = new LinkedBlockingQueue<String>();

	private CallbackManager mCallbackManager = new CallbackManager();
	private GetImageTask mTask = new GetImageTask();

	/** 默认图片 **/
	private Bitmap mDefaultBitmap;

//	//Pair<String, String> keyValue = new Pair<String, String>(first, second);
	
	
	
	
	public void clear(){
		mUrlList.clear();
		mCallbackManager.clear();
		mImageManager.clearCache();
	}
	

	
	

	public Bitmap getDefaultBitmap() {
		return mDefaultBitmap;
	}



	public SimpleImageLoader(Context context,String fileDir,Bitmap defaultBitmap){
		mDefaultBitmap=defaultBitmap;
		mImageManager= new ImageManager(context,fileDir);
	}
	

	
	public  void display(ImageView imageView, String url) {
		if(TextUtils.isEmpty(url))
			return;
		display(imageView,url,true);
		
	}
	
	
	
	public  void display(ImageView imageView, String url,boolean useDefault) {
		try{
			boolean needCallback=false;
			imageView.setTag(url);
			Bitmap bitmap=mImageManager.get(url);
			if(bitmap==null)
				needCallback=true;
			
			
			if(bitmap==null && useDefault){
				bitmap=mDefaultBitmap;
			}
			
			
			
			showImage(imageView,bitmap,needCallback,url);
		}catch(OutOfMemoryError e){}
		catch(Exception ex){}	
	}
	
	
	public  void display(ImageView imageView, String url,int defaultBitmapResId) {
		try{

			imageView.setTag(url);
			Bitmap bitmap=mImageManager.get(url);
			if(bitmap==null){
				showImage(imageView,defaultBitmapResId,true,url);
			}else{
				showImage(imageView,bitmap,false,url);
			}
			
		}catch(OutOfMemoryError e){}
		catch(Exception ex){}			
	}		
	
	public  void display(ImageView imageView, String url,Bitmap defaultBitmap) {
		try{
			boolean needCallback=false;
			imageView.setTag(url);
			Bitmap bitmap=mImageManager.get(url);
			if(bitmap==null)
				needCallback=true;
			
			if(bitmap==null){
				bitmap=defaultBitmap;
			}
			showImage(imageView,bitmap,needCallback,url);
		}catch(OutOfMemoryError e){}
		catch(Exception ex){}			
	}	
	
	
	/**
	 * 
	 * @param imageView
	 * @param url
	 * @param ilc
	 * @return
	 * 		缓存中是否有图片
	 */
	public boolean dispaly(ImageView imageView, String url,ImageLoaderCallback ilc){
		
		boolean result=false;
		imageView.setTag(url);
		try{
			
			Bitmap bitmap=mImageManager.get(url);
			if(bitmap!=null){
				result=true;
				ilc.refresh(url, bitmap,false);
			}else{
				mCallbackManager.put(url, ilc);
				startDownloadThread(url);	
			}
		}catch(OutOfMemoryError e){}
		catch(Exception ex){}
		return result;
	}
	
	
	
	/**
	 * 
	 * @param imageView
	 * @param url
	 * 			需要加载的图片
	 * @param defaultUrl
	 * 			默认URL
	 * @param formNetWork
	 * 		获取网络数据
	 */
	
	
	/**
	 * 加载图片
	 * @param imageView
	 * @param url
	 * 		需要加载的URL
	 * @param defaultUrl
	 * 		默认URL
	 */
	public  void displayWithDefault(ImageView imageView, String url,String defaultUrl) {
		displayWithDefault(imageView,url,defaultUrl,null);	
	}		
	
	/**
	 * 加载图片
	 * @param imageView
	 * @param url
	 * 			默认URL
	 * @param defaultUrl
	 * 			默认URL
	 * @param defaultBitmap
	 * 			默认bitmap
	 */
	public  void displayWithDefault(ImageView imageView, String url,String defaultUrl,Bitmap defaultBitmap) {
		try{
			boolean needCallback=false;
			imageView.setTag(url);
			Bitmap bitmap=mImageManager.get(url);
			if(bitmap==null)
				needCallback=true;

			if(bitmap==null){
				bitmap=mImageManager.get(defaultUrl);
			}
			if(bitmap==null)
				bitmap=defaultBitmap;
			
			
			
			showImage(imageView,bitmap,needCallback,url);
		}catch(OutOfMemoryError e){}
		catch(Exception ex){}		
	}	
	
	
	
	

	
	


	//-----------------显示图像------------------
	
	/**显示图片**/
	private void showImage(ImageView iv,int  bitmapResId,boolean needCallback,String url){
		iv.setImageResource(bitmapResId);
		if(needCallback){
			mCallbackManager.put(url, iv);
			startDownloadThread(url);	
		}	
	}	
	/**显示图片**/
	private void showImage(ImageView iv,Bitmap bitmap,boolean needCallback,String url){
		iv.setImageBitmap(bitmap);
		if(needCallback){
			mCallbackManager.put(url, iv);
			startDownloadThread(url);	
		}	
	}	
	
	
	private void startDownloadThread(String url) {
		if (url != null) {
			addUrlToDownloadQueue(url);
		}

		// Start Thread
		State state = mTask.getState();
		if (Thread.State.NEW == state) {
			mTask.start(); // first start
		} else if (Thread.State.TERMINATED == state) {
			mTask = new GetImageTask(); // restart
			mTask.start();
		}
	}

	private void addUrlToDownloadQueue(String url) {
		if (!mUrlList.contains(url)) {
			try {
				mUrlList.put(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Low-level interface to get ImageManager
	public ImageManager getImageManager() {
		return mImageManager;
	}

	private class GetImageTask extends Thread {
		private volatile boolean mTaskTerminated = false;
		//private static final int TIMEOUT = 3 * 60;
		private static final int TIMEOUT = 10;
		private boolean isPermanent = true;

		@Override
		public void run() {
			try {
				while (!mTaskTerminated) {
					String url;
					if (isPermanent) {
						url = mUrlList.take();
					} else {
						url = mUrlList.poll(TIMEOUT, TimeUnit.SECONDS); // waiting
						if (TextUtils.isEmpty(url)) {
							break;
						}
					}

					if(!url.startsWith("http://")){
						continue;
					}
					
					Bitmap bitmap=null;
					boolean httpError=false;
					try{
					 bitmap = mImageManager.getFromUrl(url);
					}catch(HttpException ex){
						httpError=true;
					}

					Message m = handler.obtainMessage(HANDLER_MESSAGE_ID);
					Bundle bundle = m.getData();
					bundle.putString(EXTRA_IMAGE_URL, url);
					bundle.putParcelable(EXTRA_BITMAP, bitmap);
					bundle.putBoolean(EXTRA_HTTPERROR, httpError);
					handler.sendMessage(m);
				}
			}catch(Exception e){
				Log.e(TAG, "imagelazyloader run error");
			}
			finally {
				mTaskTerminated = true;
			}
		}

		@SuppressWarnings("unused")
		public boolean isPermanent() {
			return isPermanent;
		}

		@SuppressWarnings("unused")
		public void setPermanent(boolean isPermanent) {
			this.isPermanent = isPermanent;
		}

		@SuppressWarnings("unused")
		public void shutDown() throws InterruptedException {
			mTaskTerminated = true;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MESSAGE_ID:
				Bundle bundle = msg.getData();
				String url = bundle.getString(EXTRA_IMAGE_URL);
				Bitmap bitmap = (Bitmap) (bundle.get(EXTRA_BITMAP));
				boolean httperror =bundle.getBoolean(EXTRA_HTTPERROR);
				mCallbackManager.call(url, bitmap,httperror);
				break;
			default:
				// do nothing.
			}
		}
	};

	
	
	
	
	
	
	
	//----------------callback------------
		
	static class CallbackManager {
		
		private ConcurrentHashMap<String, List<SoftReference<ImageView>>> mImageViewMap;
		
		private ConcurrentHashMap<String, List<ImageLoaderCallback>> mCallbackMap;
		
		
		public void clear(){
			mImageViewMap.clear();
			mCallbackMap.clear();
		}
		
		public CallbackManager() {
			mImageViewMap=new ConcurrentHashMap<String, List<SoftReference<ImageView>>>();
			mCallbackMap = new ConcurrentHashMap<String, List<ImageLoaderCallback>>();
		}

		/**添加需要显示的View**/
		public void put(String url, ImageView iv) {
			
			try{

				iv.setTag(url);
				if (!mImageViewMap.containsKey(url)) {	//url不存在，加入列表
					mImageViewMap.put(url, new ArrayList<SoftReference<ImageView>>());
				}
				List<SoftReference<ImageView>> imageViewList = mImageViewMap.get(url);
				imageViewList.add(new SoftReference<ImageView>(iv));
			}catch(Exception ex){}			
		}
		
		/**添加回掉**/
		public void put(String url, ImageLoaderCallback callback) {
			if (!mCallbackMap.containsKey(url)) {	//url不存在，加入列表
				mCallbackMap.put(url, new ArrayList<ImageLoaderCallback>());
			}
			mCallbackMap.get(url).add(callback);
		}		
		

		public void call(String url, Bitmap bitmap,boolean httperror) {	
			
			
			//显示图片
			try{
				List<SoftReference<ImageView>> imageViewList = mImageViewMap.get(url);
				if (imageViewList != null) {
					
					if(!httperror){
						for (SoftReference<ImageView> ivRef : imageViewList) {
							if(ivRef!=null){
								ImageView iv=ivRef.get();
								if(iv!=null && iv.getTag()!=null && url.compareTo((String)iv.getTag())==0)
									iv.setImageBitmap(bitmap);
							}
						}
					}
					imageViewList.clear();
					mImageViewMap.remove(url);					
				}
			
	
			}catch(OutOfMemoryError e){}
			catch(Exception ex){}			
			
			
			//回掉
			try{
				List<ImageLoaderCallback> callbackList = mCallbackMap.get(url);
				if (callbackList != null) {
					for (ImageLoaderCallback callback : callbackList) {
						if (callback != null) {
							callback.refresh(url, bitmap,httperror);
						}
					}
					callbackList.clear();
					mCallbackMap.remove(url);
				}
			
			}catch(OutOfMemoryError e){}
			catch(Exception ex){}		
			
			
		}
	}
	
	
}
