package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.asynctask.listener.ITaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.ResultStatus;
import com.link.bianmi.entity.User;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.entity.builder.UserBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class UserManager {

	enum TaskType {
		TYPE_SIGNUP, // 注册
		TYPE_SIGNIN, // 登录
		TYPE_SIGNOUT, // 登出
	}

	public static class API {

		/** 登录 **/
		public static void signIn(String phonenum, String passmd5,
				OnTaskOverListener<User> listener) {
			TaskParams taskParams = new TaskParams();
			ArrayList<NameValuePair> requestParams = null;
			requestParams = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("phonenum",
					phonenum);
			NameValuePair passmd5Param = new BasicNameValuePair("passmd5",
					passmd5);
			requestParams.add(phonenumParam);
			requestParams.add(passmd5Param);
			taskParams.put("request", requestParams);
			UserTask userTask = new UserTask(TaskType.TYPE_SIGNIN, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		/** 登出 **/
		public static void signOut(OnTaskOverListener<?> listener) {

			TaskParams taskParams = new TaskParams();
			ArrayList<NameValuePair> requestParams = null;
			requestParams = new ArrayList<NameValuePair>();
			NameValuePair useridParam = new BasicNameValuePair("userid",
					UserConfig.getInstance().getUserId());
			requestParams.add(useridParam);
			taskParams.put("request", requestParams);
			UserTask userTask = new UserTask(TaskType.TYPE_SIGNOUT, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);

		}

		/** 注册 **/
		public static void signUp(String phonenum, String passmd5,
				OnTaskOverListener<?> listener) {
			TaskParams taskParams = new TaskParams();
			ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("phonenum",
					phonenum);
			NameValuePair passmd5Param = new BasicNameValuePair("passmd5",
					passmd5);
			requestParams.add(phonenumParam);
			requestParams.add(passmd5Param);
			taskParams.put("request", requestParams);
			UserTask userTask = new UserTask(TaskType.TYPE_SIGNUP, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}
	}

	public static class DB {

	}

	static class UserTask extends BaseAsyncTask {

		TaskType taskType;
		ITaskOverListener listener;

		public UserTask(TaskType taskType, ITaskOverListener listener) {
			this.taskType = taskType;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			ResultStatus result = new ResultStatus();
			TaskResult<?> taskResult = new TaskResult<ResultStatus>(
					TaskStatus.FAILED, result);
			// 登录
			if (taskType == TaskType.TYPE_SIGNIN) {
				ArrayList<NameValuePair> requestParam = (ArrayList<NameValuePair>) params[0]
						.get("request");
				Response response = HttpClient.doPost(requestParam, SysConfig
						.getInstance().getSignInUrl());
				try {
					// 解析Result
					JSONObject jsonObj = response.asJSONObject();
					result = StatusBuilder.getInstance().buildEntity(jsonObj);
					// 返回数据成功
					if (result != null
							&& result.code == ResultStatus.RESULT_STATUS_CODE_OK) {
						User user = UserBuilder.getInstance().buildEntity(
								jsonObj);
						taskResult = new TaskResult<User>(TaskStatus.OK, user);
					} else {
						taskResult = new TaskResult<ResultStatus>(
								TaskStatus.FAILED, result);
					}

				} catch (ResponseException e) {
					e.printStackTrace();
				}
				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {

				ArrayList<NameValuePair> requestParam = (ArrayList<NameValuePair>) params[0]
						.get("request");
				Response response = HttpClient.doPost(requestParam, SysConfig
						.getInstance().getSignOutUrl());
				try {
					// 解析Result
					JSONObject jsonObj = response.asJSONObject();
					result = StatusBuilder.getInstance().buildEntity(jsonObj);
					// 返回数据成功
					if (result != null
							&& result.code == ResultStatus.RESULT_STATUS_CODE_OK) {
						taskResult = new TaskResult<ResultStatus>(TaskStatus.OK);
					} else {
						taskResult = new TaskResult<ResultStatus>(
								TaskStatus.FAILED, result);
					}

				} catch (ResponseException e) {
					e.printStackTrace();
				}

				// 注册
			} else if (taskType == TaskType.TYPE_SIGNUP) {
				ArrayList<NameValuePair> requestParam = (ArrayList<NameValuePair>) params[0]
						.get("request");
				Response response = HttpClient.doPost(requestParam, SysConfig
						.getInstance().getSignUpUrl());
				try {
					// 解析Result
					JSONObject jsonObj = response.asJSONObject();
					result = StatusBuilder.getInstance().buildEntity(jsonObj);
					// 返回数据成功
					if (result != null
							&& result.code == ResultStatus.RESULT_STATUS_CODE_OK) {
						User user = UserBuilder.getInstance().buildEntity(
								jsonObj);
						taskResult = new TaskResult<User>(TaskStatus.OK, user);
					} else {
						taskResult = new TaskResult<ResultStatus>(
								TaskStatus.FAILED, result);
					}
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return taskResult;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);

			// 登录
			if (taskType == TaskType.TYPE_SIGNIN) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<User>) listener)
							.onSuccess((User) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					ResultStatus result = (ResultStatus) taskResult.getEntity();
					((OnTaskOverListener<User>) listener).onFailure(
							result.code, result.msg);
				}
				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					listener.onSuccess();
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					ResultStatus result = (ResultStatus) taskResult.getEntity();
					 listener.onFailure(result.code,
							result.msg);
				}
				// 注册
			} else if (taskType == TaskType.TYPE_SIGNUP) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<User>)listener).onSuccess((User) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					ResultStatus result = (ResultStatus) taskResult.getEntity();
					listener.onFailure(result.code,
							result.msg);
				}
			}
		}
	}

}
