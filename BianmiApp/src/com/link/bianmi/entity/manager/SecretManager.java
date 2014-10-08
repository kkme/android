package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.asynctask.listener.ITaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.db.Database;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.ResultStatus;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.builder.SecretBuilder;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class SecretManager {

	public static enum TaskType {
		ADD, GET_SECRETS, HOT, FRIEND, NEARBY
	}

	/** 单页数量 **/
	private static final int BATCH = 8;

	public static class DB {

		/**
		 * 清空秘密表
		 */
		public static void cleanSecret() {
			Database.getInstance().cleanData(SecretDB.TABLE_NAME);
		}

		public static Cursor fetch(int page) {
			SQLiteDatabase db = Database.getInstance().getDb(false);
			String orderBy = SecretDB.FIELD_CREATED_TIME + " DESC ";// 按创建时间的倒叙排
			return db.query(SecretDB.TABLE_NAME, SecretDB.TABLE_COLUMNS, null,
					null, null, null, orderBy, String.valueOf(page * BATCH));
		}

		public static void addSecrets(List<Secret> secretsList) {
			for (Secret s : secretsList) {
				addSecret(s);
			}
		}

		public static void addSecret(Secret secret) {
			Database.getInstance().addEntity(SecretDB.getInstance(), secret);
		}

	}

	public static class API {

		private static Result<Secret> addSecret(Secret secret) {
			if (secret == null)
				return null;
			Result<Secret> result = null;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", secret.userId));
			params.add(new BasicNameValuePair("content", secret.content));
			params.add(new BasicNameValuePair("image_url", secret.imageUrl));
			params.add(new BasicNameValuePair("audio_url", secret.audioUrl));
			params.add(new BasicNameValuePair("created_time", String
					.valueOf(secret.createdTime)));

			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getAddSecretUrl());
			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					result = new Result<Secret>();
					result.status = StatusBuilder.getInstance().buildEntity(
							jsonObj);
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return result;
		}

		public static Result<ListResult<Secret>> getSecrets(String userid,
				String secretid) {
			Result<ListResult<Secret>> result = null;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", userid));
			params.add(new BasicNameValuePair("lastid", secretid));
			params.add(new BasicNameValuePair("batch", String.valueOf(BATCH)));
			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getSecretsUrl());

			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					result = new Result<ListResult<Secret>>();
					result.status = StatusBuilder.getInstance().buildEntity(
							jsonObj);
					if (result.status != null
							&& result.status.code == ResultStatus.RESULT_STATUS_CODE_OK) {
						result.t = SecretBuilder.getInstance().buildEntity(
								jsonObj);
					}
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return result;
		}
	}

	public static class Task {
		public static void addSecret(Secret secret,
				OnTaskOverListener<Secret> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("secret", secret);
			SecretTask userTask = new SecretTask(TaskType.ADD, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		public static void getSecrets(String secretId,
				OnTaskOverListener<ListResult<Secret>> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("userid", UserConfig.getInstance().getUserId());
			taskParams.put("secretid", secretId);
			SecretTask userTask = new SecretTask(TaskType.GET_SECRETS, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}
	}

	static class SecretTask extends BaseAsyncTask {

		TaskType taskType;
		ITaskOverListener<?> listener;

		public SecretTask(TaskType taskType, ITaskOverListener<?> listener) {
			this.taskType = taskType;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			ResultStatus resultStatus = null;
			TaskResult<?> taskResult = null;
			// 发表
			if (taskType == TaskType.ADD) {
				Secret secret = (Secret) params[0].get("secret");
				Result<Secret> result = API.addSecret(secret);
				if (result != null
						&& result.status != null
						&& result.status.code == ResultStatus.RESULT_STATUS_CODE_OK) {
					taskResult = new TaskResult<Secret>(TaskStatus.OK, result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<ResultStatus>(
							TaskStatus.FAILED, result.status);
				} else {
					resultStatus = new ResultStatus();
					resultStatus.msg = "获取数据失败!";
					taskResult = new TaskResult<ResultStatus>(
							TaskStatus.FAILED, resultStatus);
				}

				// 秘密列表
			} else if (taskType == TaskType.GET_SECRETS) {
				String userid = params[0].getString("userid");
				String secretid = params[0].getString("secretid");
				Result<ListResult<Secret>> result = API.getSecrets(userid,
						secretid);
				if (result != null
						&& result.status != null
						&& result.status.code == ResultStatus.RESULT_STATUS_CODE_OK) {
					taskResult = new TaskResult<ListResult<Secret>>(
							TaskStatus.OK, result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<ResultStatus>(
							TaskStatus.FAILED, result.status);
				} else {
					resultStatus = new ResultStatus();
					resultStatus.msg = "获取数据失败!";
					taskResult = new TaskResult<ResultStatus>(
							TaskStatus.FAILED, resultStatus);
				}

			}

			return taskResult;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);

			// 发表
			if (taskType == TaskType.ADD) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<Secret>) listener)
							.onSuccess((Secret) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					ResultStatus result = (ResultStatus) taskResult.getEntity();
					((OnTaskOverListener<Secret>) listener).onFailure(
							result.code, result.msg);
				}
				// 秘密列表
			} else if (taskType == TaskType.GET_SECRETS) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<ListResult<Secret>>) listener)
							.onSuccess((ListResult<Secret>) taskResult
									.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					ResultStatus result = (ResultStatus) taskResult.getEntity();
					((OnTaskOverListener<ListResult<Secret>>) listener)
							.onFailure(result.code, result.msg);
				}
			}
		}
	}

}