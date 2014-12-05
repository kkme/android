package com.link.bianmi.asynctask.listener;


public interface ITaskOverListener<T> {
	public abstract void onSuccess(T t);

	public abstract void onFailure(int code, String msg);

	public abstract void onResult(int code, String msg);
}
