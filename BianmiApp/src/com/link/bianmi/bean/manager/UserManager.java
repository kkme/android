package com.link.bianmi.bean.manager;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.link.bianmi.SysConfig;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.listener.ITaskListener;
import com.link.bianmi.asynctask.listener.OnInsertTaskListener;
import com.link.bianmi.asynctask.listener.OnSelectTaskListener;
import com.link.bianmi.bean.User;
import com.link.bianmi.bean.manager.UserManager.API.TaskType;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class UserManager {

	public static class API {

		enum TaskType {
			TYPE_SIGNUP, // 注册
			TYPE_SIGNIN, // 登录
			TYPE_SIGNOUT, // 登出
		}

		/** 注册 **/
		public static void signUp(String phonenum, String password,
				OnInsertTaskListener listener) {
			ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("phonenum",
					phonenum);
			NameValuePair passwordParam = new BasicNameValuePair("password",
					password);
			requestParams.add(phonenumParam);
			requestParams.add(passwordParam);
			Response response = HttpClient.doPost(requestParams, SysConfig
					.getInstance().getSignUpUrl());
			if (true)
				listener.onFailure(404, "注册失败!");
			listener.onSuccess();
		}

		/** 登录 **/
		@SuppressWarnings("unchecked")
		public static void signIn(String phonenum, String password,
				OnSelectTaskListener<User> listener) {
			TaskParams taskParams = new TaskParams();
			ArrayList<NameValuePair> requestParams = null;
			requestParams = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("phone",
					phonenum);
			NameValuePair passwordParam = new BasicNameValuePair("psd",
					password);
			requestParams.add(phonenumParam);
			requestParams.add(passwordParam);
			taskParams.put("request", requestParams);
			UserAsyncTask userTask = new UserAsyncTask(TaskType.TYPE_SIGNIN,
					listener);
			userTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					taskParams);
			// Response response = HttpClient.doPost(requestParams, SysConfig
			// .getInstance().getSignInUrl());
			// if("18503062935".equals(phonenum) && "321123".equals(password)){
			// User user = new User();
			// user.phonenum = "18503062935";
			// user.sessionId = "woeiru1234asdiwoer3xckcvzlfjskd";
			// listener.onSuccess(user);
			// }else {
			// listener.onFailure();
			// }
		}

		/** 登出 **/
		public static void singOut(User user,
				OnSelectTaskListener<Boolean> listener) {
			ArrayList<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("userid",
					user.userId);
			requestParams.add(phonenumParam);
			// Response response = HttpClient.doPost(requestParams, SysConfig
			// .getInstance().getSignOutUrl());
			// boolean success = false;
			// if (true)
			// success = true;
			// listener.onSuccess(success);
		}

	}

	public static class DB {

	}

	static class UserAsyncTask extends AsyncTask<TaskParams, Void, Void> {

		TaskType taskType;
		ITaskListener listener;

		public UserAsyncTask(TaskType taskType, ITaskListener listener) {
			this.taskType = taskType;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(TaskParams... params) {
			// 注册
			if (taskType == TaskType.TYPE_SIGNUP) {
				// 登录
			} else if (taskType == TaskType.TYPE_SIGNIN) {
				ArrayList<NameValuePair> requestParam = (ArrayList<NameValuePair>) params[0]
						.get("request");
				Response response = HttpClient.doPost(requestParam, SysConfig
						.getInstance().getSignInUrl());
				try {
					JSONObject json = response.asJSONObject();
					String jsonStrng = json.toString();
				} catch (ResponseException e) {
					e.printStackTrace();
				}
				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// 注册
			if (taskType == TaskType.TYPE_SIGNUP) {
				// 登录
			} else if (taskType == TaskType.TYPE_SIGNIN) {
				// 登出
			} else if (taskType == TaskType.TYPE_SIGNOUT) {

			}
		}

	}

}
