package com.link.bianmi.entity.manager;

import java.util.concurrent.Executors;

import org.json.JSONObject;

import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.asynctask.listener.ITaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Reminder;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.builder.ReminderBuilder;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class ReminderManager {

	private static class API {

		public static Result<Reminder> getReminder() {
			Result<Reminder> result = null;

			Response response = HttpClient.doGet(String.format(
					"%s?userid=%s&token=%s", SysConfig.getInstance()
							.getReminderUrl(), UserConfig.getInstance()
							.getUserId(), UserConfig.getInstance().getToken()));
			if (response == null)
				return null;

			try {
				// 解析Result
				JSONObject jsonObj = response.asJSONObject();
				result = new Result<Reminder>();
				result.status = StatusBuilder.getInstance()
						.buildEntity(jsonObj);
				// 返回数据成功
				if (result.status != null && result.status.code == Status_.OK) {
					// 继续解析其他对象
					result.t = ReminderBuilder.getInstance().buildEntity(
							jsonObj);
				}
			} catch (ResponseException e) {
				e.printStackTrace();
			}

			return result;
		}

	}

	public static class Task {
		/**
		 * 获取提醒
		 * 
		 * @param listener
		 */
		public static void getReminder(OnTaskOverListener<Reminder> listener) {
			ReminderTask configTask = new ReminderTask(listener);
			configTask.executeOnExecutor(Executors.newCachedThreadPool());
		}
	}

	private static class ReminderTask extends BaseAsyncTask {
		ITaskOverListener<?> listener;

		public ReminderTask(ITaskOverListener<?> listener) {
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			TaskResult<?> taskResult = null;

			Result<Reminder> result = API.getReminder();

			if (result != null && result.status != null
					&& result.status.code == Status_.OK) {
				taskResult = new TaskResult<Reminder>(TaskStatus.OK, result.t);
			} else if (result != null && result.status != null) {
				taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
						result.status);
			} else {
				Status_ status = new Status_();
				status.msg = "获取数据失败！";
				taskResult = new TaskResult<Status_>(TaskStatus.FAILED, status);
			}

			return taskResult;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);
			if (taskResult.getStatus() == TaskStatus.OK) {
				Reminder reminder = (Reminder) taskResult.getEntity();
				((OnTaskOverListener<Reminder>) listener).onSuccess(reminder);
			}
		}
	}
}