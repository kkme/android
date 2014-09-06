package com.link.bianmi.asynctask.listener;

public interface ITaskOverListener {

	public void onSuccess();

	public void onFailure(int code, String msg);

}
