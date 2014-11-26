package com.link.bianmi.asynctask.listener;

import com.link.bianmi.entity.Status_;

public interface ITaskOverListener<T> {
	public abstract void onSuccess(T t);

	public abstract void onFailure(int code, String msg);

	public abstract void onResult(Status_ status);
}
