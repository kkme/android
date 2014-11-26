package com.link.bianmi.asynctask.listener;

import com.link.bianmi.entity.Status_;

public abstract class OnSimpleTaskOverListener implements
		ITaskOverListener<Status_> {
	public abstract void onResult(Status_ status);

	public void onSuccess(Status_ t) {

	}

	public void onFailure(int code, String msg) {

	}
}
