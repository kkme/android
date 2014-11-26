package com.link.bianmi.asynctask.listener;

import com.link.bianmi.entity.Status_;

public abstract class OnTaskOverListener<T> implements ITaskOverListener<T> {
	public abstract void onSuccess(T t);

	public abstract void onFailure(int code, String msg);

	public void onResult(Status_ status) {

	}
}
