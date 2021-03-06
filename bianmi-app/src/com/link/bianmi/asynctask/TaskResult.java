package com.link.bianmi.asynctask;

public class TaskResult<T> {

	private T t;

	public TaskResult(TaskStatus status) {
		this.status = status;
		this.msg = "";
	}

	public TaskResult(TaskStatus status, T t) {
		this.status = status;
		this.msg = "";
		this.t = t;
	}

	public TaskResult(TaskStatus status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	private String msg;
	private TaskStatus status;

	public T getEntity() {
		return this.t;
	}

	/**
	 * 获取状态
	 * 
	 * @return
	 */
	public TaskStatus getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public enum TaskStatus {
		OK, FAILED, CANCELLED, FORBID, IO_ERROR, AUTH_ERROR, OTHER
	}

}