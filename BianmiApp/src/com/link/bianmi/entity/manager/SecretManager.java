package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.db.Database;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.ResultStatus;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.Tmodel;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class SecretManager {

	public static enum TaskType {
		ADD, HOT, FRIEND, NEARBY
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

		private static Result<Secret> addSecret(Secret secret) {
			if (secret == null)
				return null;
			Result<Secret> result = new Result<Secret>();
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", secret.userId));
			params.add(new BasicNameValuePair("content", secret.content));
			params.add(new BasicNameValuePair("imageUrl", secret.imageUrl));
			params.add(new BasicNameValuePair("audioUrl", secret.audioUrl));
			params.add(new BasicNameValuePair("createAt", String
					.valueOf(secret.createdAt)));

			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getAddSecretUrl());
			try {
				// 解析Status
				JSONObject jsonObj = response.asJSONObject();
				result.status = StatusBuilder.getInstance()
						.buildEntity(jsonObj);
			} catch (ResponseException e) {
				e.printStackTrace();
			}

			return result;
		}

		/** 单页数量 **/
		private static final int pageSize = 20;

		public static List<Secret> getSecrets(TaskType type) {
			String url = null;

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

	public static class Task {
		public static void addSecret(Secret secret,
				OnTaskOverListener<Secret> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("secret", secret);
			SecretTask userTask = new SecretTask(TaskType.ADD, listener);
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
			ResultStatus resultStatus = new ResultStatus();
			TaskResult<?> taskResult = new TaskResult<ResultStatus>(
					TaskStatus.FAILED, resultStatus);
			// 发表
			if (taskType == TaskType.ADD) {
				Secret secret = (Secret) params[0].get("secret");
				Result<Secret> result = API.addSecret(secret);
				if (result.status != null
						&& result.status.code == ResultStatus.RESULT_STATUS_CODE_OK) {
					taskResult = new TaskResult<Secret>(TaskStatus.OK, result.t);
				} else {
					taskResult = new TaskResult<ResultStatus>(
							TaskStatus.FAILED, result.status);
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
			}
		}
	}

}