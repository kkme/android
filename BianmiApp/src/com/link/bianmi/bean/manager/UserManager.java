package com.link.bianmi.bean.manager;

import com.link.bianmi.bean.User;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;

public class UserManager {

	public static class API {

		/** 注册 **/
		public static void signUp(String username, String password, OnSaveListener<User> listener) {
			Response response = HttpClient.doPost();
			listener.onSuccess(null);
		}

		/** 登录 **/
		public static void signIn(String username, String password, OnSaveListener<User> listener) {
			if("18503062935".equals(username) && "321123".equals(password)){
				User user = new User();
				user.setUsername("18503062935");
				user.setSessionId("woeiru1234asdiwoer3xckcvzlfjskd");
				listener.onSuccess(user);
			}else {
				listener.onFailure();
			}
		}

		/** 登出 **/
		public static void singOut(User user, OnSaveListener listener) {

		}

	}

	public static class DB {

	}

	public interface OnSaveListener<T> {

		void onSuccess(T t);

		void onFailure();
	}

}
