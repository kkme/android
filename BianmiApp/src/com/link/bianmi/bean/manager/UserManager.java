package com.link.bianmi.bean.manager;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.link.bianmi.SysConfig;
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
				OnSaveListener<Boolean> listener) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("phonenum",
					phonenum);
			NameValuePair passwordParam = new BasicNameValuePair("password",
					password);
			params.add(phonenumParam);
			params.add(passwordParam);
			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getSignUpUrl());
			boolean success = false;
			if (true)
				success = true;
			listener.onSuccess(success);
		}

		static ArrayList<NameValuePair> params = null;

		/** 登录 **/
		@SuppressWarnings("unchecked")
		public static void signIn(String phonenum, String password,
				OnSaveListener<User> listener) {
			params = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("phone",
					phonenum);
			NameValuePair passwordParam = new BasicNameValuePair("psd",
					password);
			params.add(phonenumParam);
			params.add(passwordParam);
			UserAsyncTask userTask = new UserAsyncTask(TaskType.TYPE_SIGNIN);
			userTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
			// Response response = HttpClient.doPost(params, SysConfig
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
		public static void singOut(User user, OnSaveListener<Boolean> listener) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam = new BasicNameValuePair("userid",
					user.userId);
			params.add(phonenumParam);
			// Response response = HttpClient.doPost(params, SysConfig
			// .getInstance().getSignOutUrl());
			// boolean success = false;
			// if (true)
			// success = true;
			// listener.onSuccess(success);
		}

	}

	public static class DB {

	}

	public interface OnSaveListener<T> {

		void onSuccess(T t);

		void onFailure();
	}

	static class UserAsyncTask extends
			AsyncTask<ArrayList<NameValuePair>, Void, Void> {

		TaskType taskType;

		public UserAsyncTask(TaskType taskType) {
			this.taskType = taskType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(ArrayList<NameValuePair>... arg0) {
			// 注册
			if (taskType == TaskType.TYPE_SIGNUP) {
				// 登录
			} else if (taskType == TaskType.TYPE_SIGNIN) {
				Response response = HttpClient.doPost(arg0[0], SysConfig
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
