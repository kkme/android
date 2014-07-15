package com.link.bianmi.fragment.base;

import android.os.AsyncTask;

import com.link.bianmi.asynctask.GenericTask;
import com.link.bianmi.asynctask.TaskAdapter;
import com.link.bianmi.asynctask.TaskListener;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;

/** Fragment一般任务抽象类 **/
public abstract class TaskFragment extends BaseFragment {

	private Task mTask;

	private class Task extends GenericTask {

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {

			return onTaskBackground(params);
		}

		/** 外部调用更新UI **/
		public void publishProgressFromEx(Object... values) {
			publishProgress(values);
		}
	}

	/**
	 * 后台处理
	 * 
	 * @param params
	 * @return
	 */
	protected abstract TaskResult onTaskBackground(TaskParams... params);

	/**
	 * 处理任务前准备工作 更新UI
	 * 
	 * @param task
	 */
	protected void onTaskPreUI(GenericTask task) {
	}

	/**
	 * 处理结束后反馈数据 更新UI
	 * 
	 * @param task
	 * @param result
	 */
	protected void onTaskDoneUI(TaskResult result) {
	}

	/**
	 * 后台任务处理过程中 更新UI
	 * 
	 * @param param
	 */
	protected void onTaskProgressUI(Object... param) {
	}

	/** 更新界面 **/
	protected void publishProgress(Object... values) {
		try {
			mTask.publishProgressFromEx(values);
		} catch (Exception ex) {
		}
	}

	private TaskListener mTaskListener = new TaskAdapter() {

		@Override
		public void onPreExecute(GenericTask task) {
			try {
				onTaskPreUI(task);
			} catch (OutOfMemoryError ex) {
			} catch (Exception ex) {
			}
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			try {
				onTaskDoneUI(result);
			} catch (OutOfMemoryError ex) {
			} catch (Exception ex) {
			}
		}

		@Override
		public void onProgressUpdate(GenericTask task, Object... param) {
			try {
				onTaskProgressUI(param);
			} catch (OutOfMemoryError ex) {
			} catch (Exception ex) {
			}
		}

	};

	@Override
	public void _onDestroyView() {
		super._onDestroyView();
		if (mTask != null && mTask.getStatus() == GenericTask.Status.RUNNING) {
			mTask.cancel(true);
			mTask = null;
		}

	}

	/**
	 * 开始执行任务
	 */
	protected void doTask(TaskParams... params) {
		doTask("", params);
	}

	/**
	 * 开始执行任务
	 * 
	 * @param feedbackMsg
	 *            提示信息
	 * @param params
	 */
	protected void doTask(String feedbackMsg, TaskParams... params) {

		if (mTask != null && mTask.getStatus() == GenericTask.Status.RUNNING)
			return;
		mTask = new Task();
		mTask.setListener(mTaskListener);

		mTask.execute(params);

	}

	public void updateTaskMessage(String message) {

	}

	/**
	 * 开始执行任务：没有反馈提示
	 * 
	 * @param params
	 */
	protected void doTaskNoFeedback(TaskParams... params) {
		doTaskNoFeedback(false, params);
	}

	/**
	 * 开始执行任务：没有反馈提示
	 * 
	 * @param froce
	 *            true ：强制执行
	 * @param params
	 */
	protected void doTaskNoFeedback(boolean froce, TaskParams... params) {
		if (!froce && mTask != null
				&& mTask.getStatus() == GenericTask.Status.RUNNING)
			return;
		mTask = new Task();
		mTask.setListener(mTaskListener);
		mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	}

}
