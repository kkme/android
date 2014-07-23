package com.link.bianmi.bean.manager;

import com.link.bianmi.bean.User;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;

public class UserManager {

	public static class API {

		/** 注册 **/
		public static void signUp(User user, OnSaveListener listener) {
			String username = user.getUsername();
			String password = user.getPassword();
			Response response = HttpClient.doPost();
		}

		/** 登录 **/
		public static void signIn(User user, OnSaveListener listener) {

		}

		/** 登出 **/
		public static void singOut(User user, OnSaveListener listener) {

		}

	}

	public static class DB {

	}

	interface OnSaveListener {

		void onSuccess();

		void onFailure();
	}

}
