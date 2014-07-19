package com.link.bianmi.bean.helper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.link.bianmi.SysConfig;
import com.link.bianmi.bean.Secret;
import com.link.bianmi.bean.Tmodel;
import com.link.bianmi.db.Database;
import com.link.bianmi.db.SecretDB;
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
			SQLiteDatabase db = Database.getInstance().getDb(false);
            String orderBy=SecretDB.FIELD_CREATEDAT + " DESC ";//按创建时间的倒叙排
            return db.query(SecretDB.TABLE_NAME, SecretDB.TABLE_COLUMNS, null, null, null, null, orderBy);
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
				secret.setId(secretJSONObject.getString("id"));
				secret.setContent(secretJSONObject.getString("caption"));
				secret.setImageUrl(secretJSONObject.getJSONObject("images")
						.getString("small"));
				secret.setLikeCount(secretJSONObject.getJSONObject("votes")
						.getInt("count"));
				secretList.add(secret);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return secretList;
	}
}
