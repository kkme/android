package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.link.bianmi.SysConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.asynctask.listener.ITaskOverListener;
import com.link.bianmi.db.Database;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.ResultStatus;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.Tmodel;
import com.link.bianmi.entity.User;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.entity.builder.UserBuilder;
import com.link.bianmi.entity.manager.UserManager.TaskType;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class SecretManager {

	public static enum SecretType {
		HOT, FRIEND, NEARBY
	}

	public static class DB {

		public static void cleanSecret() {

		}

		public static Cursor fetch() {
			SQLiteDatabase db = Database.getInstance().getDb(false);
			String orderBy = SecretDB.FIELD_CREATEDAT + " DESC ";// 按创建时间的倒叙排
			return db.query(SecretDB.TABLE_NAME, SecretDB.TABLE_COLUMNS, null,
					null, null, null, orderBy);
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

		/** 单页数量 **/
		private static final int pageSize = 20;

		public static List<Secret> getSecrets(SecretType type) {
			String url = null;
			if (type == SecretType.HOT) {
				url = SysConfig.getInstance().getHotUrl();
			} else if (type == SecretType.FRIEND) {
				url = SysConfig.getInstance().getFriendUrl();
			} else if (type == SecretType.NEARBY) {
				url = SysConfig.getInstance().getNearbyUrl();
			}

			Response res = HttpClient.doGet(url);
			List<Secret> secretsList = null;
			try {
				secretsList = parseSecrets(res.asJSONObject());
			} catch (ResponseException e) {
				e.printStackTrace();
			}
			return secretsList;
		}

		public static Tmodel<Secret[]> getSecretsArray(int page) {

			return null;
		}

	}

	private static List<Secret> parseSecrets(JSONObject jsonObject) {
		ArrayList<Secret> secretList = new ArrayList<Secret>();
		try {
			JSONArray dataJSONArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < dataJSONArray.length(); i++) {
				JSONObject secretJSONObject = dataJSONArray.getJSONObject(i);
				Secret secret = new Secret();
				secret.resourceId = secretJSONObject.getString("id");
				secret.content = secretJSONObject.getString("caption");
				secret.imageUrl = secretJSONObject.getJSONObject("images")
						.getString("small");
				secret.likes = secretJSONObject.getJSONObject("votes").getInt(
						"count");
				secretList.add(secret);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return secretList;
	}

	class SecretTask extends BaseAsyncTask {

		TaskType taskType;
		ITaskOverListener listener;

		public SecretTask(TaskType taskType, ITaskOverListener listener) {
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
						taskResult = new TaskResult<ResultStatus>(TaskStatus.OK);
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

		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);

			// 登录
			if (taskType == TaskType.TYPE_SIGNIN) {
				if (taskResult.getStatus() == TaskStatus.OK) {
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
				}
				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					listener.onSuccess();
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					ResultStatus result = (ResultStatus) taskResult.getEntity();
					listener.onFailure(result.code, result.msg);
				}
				// 注册
			} else if (taskType == TaskType.TYPE_SIGNUP) {
				if (taskResult.getStatus() == TaskStatus.OK) {
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
				}
			}
		}
	}

}