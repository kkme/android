package com.link.bianmi.asynctask.listener;

public interface ITaskOverListener<T> {

	public void onSuccess(T t);

	public void onFailure(int code, String msg);

}
