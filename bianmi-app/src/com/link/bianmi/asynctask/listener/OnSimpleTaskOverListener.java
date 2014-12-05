package com.link.bianmi.asynctask.listener;

public abstract class OnSimpleTaskOverListener implements
		ITaskOverListener<Object> {
	public abstract void onResult(int code, String msg);

	public void onSuccess(Object t) {

	}

	public void onFailure(int code, String msg) {

	}
}
