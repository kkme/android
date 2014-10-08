package com.link.bianmi.entity.builder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Secret;

public class SecretBuilder implements BaseEntityBuilder<ListResult<Secret>> {

	private static SecretBuilder mInstance = null;

	private SecretBuilder() {

	}

	public static SecretBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new SecretBuilder();
		}

		return mInstance;
	}

	@Override
	public ListResult<Secret> buildEntity(JSONObject jsonObj) {
		ListResult<Secret> listResult = new ListResult<Secret>();
		listResult.list = new ArrayList<Secret>();
		try {
			if (jsonObj != null && jsonObj.has("list")) {
				JSONObject listJson = jsonObj.getJSONObject("list");
				if (listJson != null && listJson.has("has_more")) {
					listResult.hasMore = listJson.getBoolean("has_more");
				}

				if (listJson != null && listJson.has("secrets")) {
					JSONArray jsonArr = listJson.getJSONArray("secrets");
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonArrObj = jsonArr.getJSONObject(i);

						Secret secret = new Secret();
						secret.id = jsonArrObj.getString("id");
						secret.content = jsonArrObj.getString("content");
						secret.imageUrl = jsonArrObj.getString("image_url");
						secret.audioUrl = jsonArrObj.getString("audio_url");
						secret.audioLength = jsonArrObj.getInt("audio_length");
						secret.likes = jsonArrObj.getInt("likes");
						secret.comments = jsonArrObj.getInt("comments");
						listResult.list.add(secret);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return listResult;
	}
}
