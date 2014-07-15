package com.link.bianmi.asynctask;

public interface TaskListener {

//	String getName();
	
	/**
	 * GenericTask执行后台任务前对UI更新操作
	 * @param task
	 */
	void onPreExecute(GenericTask task);
	/**
	 * 
	 * GenericTask任务操作结束时对UI更新操作
	 * @param task
	 * @param result
	 */
	void onPostExecute(GenericTask task,TaskResult result);
	/**
	 * GenericTaskon执行publishProgress时对UI更新操作
	 * @param task
	 * @param param
	 */
	//void onProgressUpdate(GenericTask task,Object param);
	void onProgressUpdate(GenericTask task,Object... param);
	
	
	/**
	 * GenericTaskon取消任务时操作
	 * @param task
	 */
	void onCancelled(GenericTask task);
}
