package com.link.bianmi.imageloader;


import android.graphics.Bitmap;

/**
 * 延迟加载图片 
 * @author sunpf
 */
public interface ImageCache {
	

//	public Bitmap getDefaultBitmap();

	public Bitmap get(String url);

	public void put(String url, Bitmap bitmap);
}
