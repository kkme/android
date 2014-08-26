package com.link.bianmi.asynctask.listener;

public interface ITaskListener {

	public void onSuccess();

	public void onFailure(int code, String msg);

}
