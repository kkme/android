package com.link.bianmi.asynctask;

import java.util.Observable;
import java.util.Observer;


import android.os.AsyncTask;



/**
 * 后台任务
 * @author sunpf
 *
 */
public abstract class GenericTask  extends
	AsyncTask<TaskParams,Object,TaskResult> implements Observer  {

	private TaskListener mListener=null;
	private Feedback mFeedback=null;
	private boolean isCancelable=true;
	
	abstract protected TaskResult _doInBackground(TaskParams...params);
	
	public void setListener(TaskListener taskListerner){
		mListener=taskListerner;
	}
	public TaskListener getListener(){
		return mListener;
	}
	
	@Override
	protected void onCancelled(){
		super.onCancelled();
		if(mListener!=null){
			mListener.onCancelled(this);
		}
	//	Log.d(TAG,mListener.getName()+"has benn Cancelled.");
		if(mFeedback!=null){
			mFeedback.cancel("");
		}		
	}
	
	
	@Override
	/**
	 * 该方法将在执行实际的后台操作前被UI thread调用
	 * (在execute(Params... params)被调用后立即执行)。
	 * 可以在该方法中做一些准备工作，如在界面上显示一个进度条。 
	 */

	protected void onPreExecute(){
		super.onPreExecute();
		if(mListener!=null){
			mListener.onPreExecute(this);
		}
		if(mFeedback!=null){
			mFeedback.start("");
		}
	}
	
	@Override
	/**
	 * 在onPreExecute()完成后立即执行，用于执行较为费时的操作，
	 * 此方法将接收输入参数和返回计算结果。
	 * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息。
	 */
	protected TaskResult doInBackground(TaskParams...params){
		TaskResult result=_doInBackground(params);
		if(mFeedback!=null){
			mFeedback.update(99);
		}
		return result;
	}

	
	
	@Override
	/**
	 * 在调用publishProgress(Progress... values)时，此方法被执行
	 * 直接将进度信息更新到UI组件上。
	 */

	protected void onProgressUpdate(Object... values){
		super.onProgressUpdate(values);
		
		if(mListener!=null){
			if(values!=null && values.length>0){
				mListener.onProgressUpdate(this,values);
				//mListener.onProgressUpdate(this,values[0]);
			}
		}
		if(mFeedback!=null){
			mFeedback.update(values[0]);
		}
	}
		
	
	
	@Override
	/**
	 * 在doInBackground 执行完成后，onPostExecute 方法将被UI thread调用
	 * 后台的计算结果将通过该方法传递到UI thread. 
	 */
	protected void onPostExecute(TaskResult result){
		super.onPostExecute(result);
		if(mListener!=null){
			mListener.onPostExecute(this, result);
		}
		
		if(mFeedback!=null){
			mFeedback.success("");
		}
	}
	



	/**
	 * Observer接口实现
	 */
	@Override
	public void update(Observable o,Object arg) {
		if(TaskManager.CANCEL_ALL==(Integer) arg && isCancelable){
			if(getStatus()==GenericTask.Status.RUNNING){
				cancel(true);
			}
		}
	}
	
	public void setCancelable(boolean flag){
		isCancelable=flag;
	}
	
	public void setFeedback(Feedback feedback){
		mFeedback=feedback;
	}
	
	
}
