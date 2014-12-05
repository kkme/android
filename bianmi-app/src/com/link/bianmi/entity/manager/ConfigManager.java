package com.link.bianmi.entity.manager;

import java.util.concurrent.Executors;

import org.json.JSONObject;

import com.link.bianmi.SysConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.entity.Config;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.builder.ConfigBuilder;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class ConfigManager {

	private static class API {

		public static Result<Config> getConfig() {
			Result<Config> result = null;

			Response response = HttpClient.doGet(SysConfig.getInstance()
					.getConfigUrl());
			if (response == null)
				return null;

			try {
				// 解析Result
				JSONObject jsonObj = response.asJSONObject();
				result = new Result<Config>();
				result.status = StatusBuilder.getInstance()
						.buildEntity(jsonObj);
				// 返回数据成功
				if (result.status != null && result.status.code == Status_.OK) {
					// 继续解析其他对象
					result.t = ConfigBuilder.getInstance().buildEntity(jsonObj);
				}
			} catch (ResponseException e) {
				e.printStackTrace();
			}

			return result;
		}
	}

	public static class Task {
		/** 获取服务端下发的配置 **/
		public static void getConfig() {
			ConfigTask configTask = new ConfigTask();
			configTask.executeOnExecutor(Executors.newCachedThreadPool());
		}
	}

	private static class ConfigTask extends BaseAsyncTask {

		public ConfigTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			TaskResult<?> taskResult = null;

			Result<Config> result = API.getConfig();

			if (result != null && result.status != null
					&& result.status.code == Status_.OK) {
				taskResult = new TaskResult<Config>(TaskStatus.OK, result.t);
			} else if (result != null && result.status != null) {
				taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
						result.status);
			} else {
				Status_ status = new Status_();
				taskResult = new TaskResult<Status_>(TaskStatus.FAILED, status);
			}

			return taskResult;
		}

		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);
			if (taskResult.getStatus() == TaskStatus.OK) {
				Config config = (Config) taskResult.getEntity();
				SysConfig.getInstance().setShowAd(config.showAd);
				SysConfig.getInstance().setSmsAccess(config.smsAccess);
			}
		}
	}
}