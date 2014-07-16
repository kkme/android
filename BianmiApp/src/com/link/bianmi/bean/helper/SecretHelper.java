package com.link.bianmi.bean.helper;

import org.json.JSONObject;

import android.database.Cursor;

import com.link.bianmi.SysConfig;
import com.link.bianmi.bean.Secret;
import com.link.bianmi.bean.Tmodel;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class SecretHelper {

	public static enum SecretType {
		HOT, FRIEND, NEARBY
	}

	public static class DB {

		public static void cleanSecret() {

		}

		public static Cursor fetch() {

			return null;
		}

		public static void addSecret(Secret[] t) {

		}

	}

	public static class API {

		/** 单页数量 **/
		private static final int pageSize = 20;

		public static Secret[] getSecrets(SecretType type) {
			String url = null;
			if (type == SecretType.HOT) {
				url = SysConfig.getInstance().getHotUrl();
			} else if (type == SecretType.FRIEND) {
				url = SysConfig.getInstance().getFriendUrl();
			} else if (type == SecretType.NEARBY) {
				url = SysConfig.getInstance().getNearbyUrl();
			}

			Response res = HttpClient.doGet(url);
			Secret[] secrets = null;
			try {
				secrets = parseSecrets(res.asJSONObject());
			} catch (ResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return secrets;
		}

		public static Tmodel<Secret[]> getSecretsArray(int page) {

			return null;
		}

	}

	private static Secret[] parseSecrets(JSONObject jsonObject) {
		return null;
	}
}
