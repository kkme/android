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
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Reminder;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.builder.ReminderBuilder;
import com.link.bianmi.entity.builder.ReminderPersonBuilder;
import com.link.bianmi.entity.builder.ReminderSystemBuilder;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class ReminderManager {

	private enum TaskType {
		TYPE_HAS_REMINDER, // 是否有提醒
		TYPE_SYSTEM_REMINDERS, // 提醒：系统通知
		TYPE_PERSON_REMINDERS, // 提醒：我的通知
	}

	private final static int BATCH_NUM = 12;

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

		public static Result<ListResult<Reminder.Person>> getPersonReminders(
				String lastid) {
			Result<ListResult<Reminder.Person>> result = null;

			Response response = HttpClient.doGet(String.format(
					"%s?userid=%s&token=%s&lastid=%s&batch=%d", SysConfig
							.getInstance().getReminderPersonUrl(), UserConfig
							.getInstance().getUserId(), UserConfig
							.getInstance().getToken(), lastid, BATCH_NUM));
			if (response == null)
				return null;

			try {
				// 解析Result
				JSONObject jsonObj = response.asJSONObject();
				result = new Result<ListResult<Reminder.Person>>();
				result.status = StatusBuilder.getInstance()
						.buildEntity(jsonObj);
				if (result.status != null && result.status.code == Status_.OK) {
					result.t = ReminderPersonBuilder.getInstance().buildEntity(
							jsonObj);
				}
			} catch (ResponseException e) {
				e.printStackTrace();
			}

			return result;
		}

		public static Result<ListResult<Reminder.System>> getSystemReminders(
				String lastid) {
			Result<ListResult<Reminder.System>> result = null;

			Response response = HttpClient.doGet(String.format(
					"%s?token=%s&lastid=%s&batch=%d", SysConfig.getInstance()
							.getReminderSystemUrl(), UserConfig.getInstance()
							.getToken(), lastid, BATCH_NUM));

			if (response == null)
				return null;

			try {
				// 解析Result
				JSONObject jsonObj = response.asJSONObject();
				result = new Result<ListResult<Reminder.System>>();
				result.status = StatusBuilder.getInstance()
						.buildEntity(jsonObj);
				// 返回数据成功
				if (result.status != null && result.status.code == Status_.OK) {
					// 继续解析其他对象
					result.t = ReminderSystemBuilder.getInstance().buildEntity(
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

		/**
		 * 提醒：系统通知
		 */
		public static void getSystemReminders(String lastId,
				OnTaskOverListener<ListResult<Reminder.System>> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("token", UserConfig.getInstance().getToken());
			taskParams.put("lastid", lastId);
			taskParams.put("batch", BATCH_NUM);
			ReminderTask reminderTask = new ReminderTask(
					TaskType.TYPE_SYSTEM_REMINDERS, listener);
			reminderTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		/**
		 * 提醒：我的通知
		 */
		public static void getPersonReminders(String lastId,
				OnTaskOverListener<ListResult<Reminder.Person>> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("token", UserConfig.getInstance().getToken());
			taskParams.put("userid", UserConfig.getInstance().getUserId());
			taskParams.put("lastid", lastId);
			taskParams.put("batch", BATCH_NUM);
			ReminderTask reminderTask = new ReminderTask(
					TaskType.TYPE_PERSON_REMINDERS, listener);
			reminderTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}
	}

	private static class ReminderTask extends BaseAsyncTask {

		TaskType taskType;
		ITaskOverListener<?> listener;

		public ReminderTask(ITaskOverListener<?> listener) {
			this.listener = listener;
		}

		public ReminderTask(TaskType taskType, ITaskOverListener<?> listener) {
			this.taskType = taskType;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			TaskResult<?> taskResult = null;

			if (taskType == TaskType.TYPE_HAS_REMINDER) {
				Result<Reminder> result = API.getReminder();
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<Reminder>(TaskStatus.OK,
							result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					Status_ status = new Status_();
					status.msg = "获取数据失败！";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							status);
				}
			} else if (taskType == TaskType.TYPE_SYSTEM_REMINDERS) {
				String lastid = params[0].getString("lastid");
				Result<ListResult<Reminder.System>> result = API
						.getSystemReminders(lastid);
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<ListResult<Reminder.System>>(
							TaskStatus.OK, result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					Status_ status = new Status_();
					status.msg = "获取数据失败！";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							status);
				}
			} else if (taskType == TaskType.TYPE_PERSON_REMINDERS) {
				String lastid = params[0].getString("lastid");
				Result<ListResult<Reminder.Person>> result = API
						.getPersonReminders(lastid);
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<ListResult<Reminder.Person>>(
							TaskStatus.OK, result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					Status_ status = new Status_();
					status.msg = "获取数据失败！";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							status);
				}
			}

			return taskResult;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);
			if (taskType == TaskType.TYPE_HAS_REMINDER) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					Reminder reminder = (Reminder) taskResult.getEntity();
					((OnTaskOverListener<Reminder>) listener)
							.onSuccess(reminder);
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<Reminder>) listener).onFailure(
							result.code, result.msg);
				}
			} else if (taskType == TaskType.TYPE_SYSTEM_REMINDERS) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<ListResult<Reminder.System>>) listener)
							.onSuccess((ListResult<Reminder.System>) taskResult
									.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<ListResult<Reminder.System>>) listener)
							.onFailure(result.code, result.msg);
				}
			} else if (taskType == TaskType.TYPE_PERSON_REMINDERS) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<ListResult<Reminder.Person>>) listener)
							.onSuccess((ListResult<Reminder.Person>) taskResult
									.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<ListResult<Reminder.Person>>) listener)
							.onFailure(result.code, result.msg);
				}
			}
		}
	}
}