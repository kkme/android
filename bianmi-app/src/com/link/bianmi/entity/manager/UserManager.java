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
import com.link.bianmi.asynctask.listener.OnSimpleTaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.User;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.entity.builder.UserBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class UserManager {

	private enum TaskType {
		TYPE_SIGNUP, // 注册
		TYPE_SIGNIN, // 登录
		TYPE_SIGNOUT, // 登出
		TYPE_CLEAR_PRIVACY, // 清除痕迹
	}

	private static class API {

		public static Result<User> signInOrUp(String phonemd5,
				String pwdmd5, String url) {
			Result<User> result = null;
			Response response = HttpClient.doGet(String.format(
					"%s?phone_md5=%s&pwd_md5=%s", url, phonemd5, pwdmd5));
			try {
				// 解析Result
				if (response != null) {
					JSONObject jsonObj = response.asJSONObject();
					result = new Result<User>();
					result.status = StatusBuilder.getInstance().buildEntity(
							jsonObj);
					// 返回数据成功
					if (result.status != null
							&& result.status.code == Status_.OK) {
						// 解析User
						result.t = UserBuilder.getInstance().buildEntity(
								jsonObj);
					}
				}
			} catch (ResponseException e) {
				e.printStackTrace();
			}

			return result;
		}

		// 登录
		public static Result<User> signIn(String phonemd5, String pwdmd5) {
			return signInOrUp(phonemd5, pwdmd5, SysConfig.getInstance()
					.getSignInUrl());
		}

		// 注册
		public static Result<User> signUp(String phonemd5, String pwdmd5) {
			return signInOrUp(phonemd5, pwdmd5, SysConfig.getInstance()
					.getSignUpUrl());
		}

		// 登出
		public static Status_ signOut() {
			Status_ status = new Status_();

			Response response = HttpClient.doGet(String.format(
					"%s?userid=%s&token=%s", SysConfig.getInstance()
							.getSignOutUrl(), UserConfig.getInstance()
							.getUserId(), UserConfig.getInstance().getToken()));
			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					status = StatusBuilder.getInstance().buildEntity(jsonObj);
				} catch (ResponseException e) {
					e.printStackTrace();
				}

			}
			return status;
		}

		// 清除痕迹
		public static Status_ clearPrivacy() {
			Status_ status = new Status_();

			Response response = HttpClient.doGet(String.format(
					"%s?userid=%s&token=%s", SysConfig.getInstance()
							.getClearPrivacyUrl(), UserConfig.getInstance()
							.getUserId(), UserConfig.getInstance().getToken()));
			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					status = StatusBuilder.getInstance().buildEntity(jsonObj);
				} catch (ResponseException e) {
					e.printStackTrace();
				}

			}
			return status;
		}
	}

	public static class Task {

		/** 登录 **/
		public static void signIn(String phonemd5, String pwdmd5,
				OnTaskOverListener<User> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("phonemd5", phonemd5);
			taskParams.put("pwdmd5", pwdmd5);
			UserTask userTask = new UserTask(TaskType.TYPE_SIGNIN, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		/** 登出 **/
		public static void signOut(OnTaskOverListener<?> listener) {

			UserTask userTask = new UserTask(TaskType.TYPE_SIGNOUT, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool());

		}

		/** 注册 **/
		public static void signUp(String phonemd5, String pwdmd5,
				OnTaskOverListener<User> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("phonemd5", phonemd5);
			taskParams.put("pwdmd5", pwdmd5);
			UserTask userTask = new UserTask(TaskType.TYPE_SIGNUP, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		/**
		 * 清除痕迹
		 */
		public static void clearPrivacy(OnSimpleTaskOverListener listener) {
			UserTask userTask = new UserTask(TaskType.TYPE_CLEAR_PRIVACY,
					listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool());
		}
	}

	public static class DB {

	}

	static class UserTask extends BaseAsyncTask {

		TaskType taskType;
		ITaskOverListener<?> listener;

		public UserTask(TaskType taskType, ITaskOverListener<?> listener) {
			this.taskType = taskType;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			Status_ resultStatus = new Status_();
			TaskResult<?> taskResult = new TaskResult<Status_>(
					TaskStatus.FAILED, resultStatus);
			// 登录
			if (taskType == TaskType.TYPE_SIGNIN) {
				String phonemd5 = params[0].getString("phonemd5");
				String pwdmd5 = params[0].getString("pwdmd5");
				Result<User> result = API.signIn(phonemd5, pwdmd5);
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<User>(TaskStatus.OK, result.t);
				} else if (result != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					Status_ status = new Status_();
					status.msg = "获取数据失败！";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							status);
				}

				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {

				Status_ status = API.signOut();
				// 返回数据成功
				if (status != null && status.code == Status_.OK) {
					taskResult = new TaskResult<Status_>(TaskStatus.OK);
				} else {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							status);
				}

				// 注册
			} else if (taskType == TaskType.TYPE_SIGNUP) {
				String phonemd5 = params[0].getString("phonemd5");
				String pwdmd5 = params[0].getString("pwdmd5");
				Result<User> result = API.signUp(phonemd5, pwdmd5);
				// 返回数据成功
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<User>(TaskStatus.OK, result.t);
				} else if (result != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					Status_ status = new Status_();
					status.msg = "获取数据失败！";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							status);
				}
				// 清除痕迹
			} else if (taskType == TaskType.TYPE_CLEAR_PRIVACY) {

				Status_ status = API.clearPrivacy();
				// 返回数据成功
				if (status != null && status.code == Status_.OK) {
					taskResult = new TaskResult<Status_>(TaskStatus.OK);
				} else {
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

			// 登录
			if (taskType == TaskType.TYPE_SIGNIN) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<User>) listener)
							.onSuccess((User) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<User>) listener).onFailure(
							result.code, result.msg);
				}
				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					listener.onSuccess(null);
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					listener.onFailure(result.code, result.msg);
				}
				// 注册
			} else if (taskType == TaskType.TYPE_SIGNUP) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<User>) listener)
							.onSuccess((User) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					listener.onFailure(result.code, result.msg);
				}
				// 清除痕迹
			} else if (taskType == TaskType.TYPE_CLEAR_PRIVACY) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					listener.onSuccess(null);
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					listener.onFailure(result.code, result.msg);
				}
			}
		}
	}

}
