package com.link.bianmi.bean.manager;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.link.bianmi.SysConfig;
import com.link.bianmi.bean.User;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;

public class UserManager {

	public static class API {

		/** 注册 **/
		public static void signUp(String phonenum, String password, OnSaveListener<Boolean> listener) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam  = new BasicNameValuePair("phonenum", phonenum);
			NameValuePair passwordParam  = new BasicNameValuePair("password", password);
			params.add(phonenumParam);
			params.add(passwordParam);
			Response response = HttpClient.doPost(params, SysConfig.getInstance().getSignUpUrl());
			boolean success = false;
			if(true) success = true;
			listener.onSuccess(success);
		}

		/** 登录 **/
		public static void signIn(String phonenum, String password, OnSaveListener<User> listener) {
//			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//			NameValuePair phonenumParam  = new BasicNameValuePair("phonenum", phonenum);
//			NameValuePair passwordParam  = new BasicNameValuePair("password", password);
//			params.add(phonenumParam);
//			params.add(passwordParam);
//			Response response = HttpClient.doPost(params, SysConfig.getInstance().getSignInUrl());
			if("18503062935".equals(phonenum) && "321123".equals(password)){
				User user = new User();
				user.phonenum = "18503062935";
				user.sessionId = "woeiru1234asdiwoer3xckcvzlfjskd";
				listener.onSuccess(user);
			}else {
				listener.onFailure();
			}
		}

		/** 登出 **/
		public static void singOut(User user, OnSaveListener<Boolean> listener) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair phonenumParam  = new BasicNameValuePair("userid", user.userId);
			params.add(phonenumParam);
			Response response = HttpClient.doPost(params, SysConfig.getInstance().getSignOutUrl());
			boolean success = false;
			if(true) success = true;
			listener.onSuccess(success);
		}

	}

	public static class DB {

	}

	public interface OnSaveListener<T> {

		void onSuccess(T t);

		void onFailure();
	}

}
