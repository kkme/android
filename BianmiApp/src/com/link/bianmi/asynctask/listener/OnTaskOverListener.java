package com.link.bianmi.asynctask.listener;

public abstract class OnTaskOverListener<T> implements ITaskOverListener {

	public void onSuccess() {
	}

	public abstract void onSuccess(T t);
}
