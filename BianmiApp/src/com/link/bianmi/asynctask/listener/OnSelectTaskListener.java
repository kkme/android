package com.link.bianmi.asynctask.listener;

public abstract class OnSelectTaskListener<T> implements ITaskListener {

	public void onSuccess() {
	}

	public abstract void onSuccess(T t);
}
