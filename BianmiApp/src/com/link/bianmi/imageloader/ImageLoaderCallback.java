package com.link.bianmi.imageloader;

import android.graphics.Bitmap;

public interface ImageLoaderCallback {
	void refresh(String url, Bitmap bitmap,boolean httperror);
}
