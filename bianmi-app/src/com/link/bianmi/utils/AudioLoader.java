package com.link.bianmi.utils;

public class AudioLoader {

	private static AudioLoader mInstance = null;

	private AudioLoader() {

	}

	public static AudioLoader getInstance() {
		if (mInstance == null) {
			mInstance = new AudioLoader();
		}

		return mInstance;
	}

	// ------------------------------Public------------------------------
	public void load(String audioUrl){
		
	}
}
