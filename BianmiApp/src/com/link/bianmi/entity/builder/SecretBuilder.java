package com.link.bianmi.entity.builder;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.Secret;

public class SecretBuilder implements BaseEntityBuilder<List<Secret>> {

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
	public List<Secret> buildEntity(JSONObject jsonObj) {
		List<Secret> lists = new ArrayList<Secret>();
		try {
			if (jsonObj != null && jsonObj.has("secrets")) {
				JSONArray jsonArr = jsonObj.getJSONArray("secrets");
				for(int i = 0; i < jsonArr.length(); i++){
					JSONObject jsonArrObj = jsonArr.getJSONObject(i);
					
					Secret secret = new Secret();
					secret.id = jsonArrObj.getString("id");
					secret.content = jsonArrObj.getString("content");
					secret.imageUrl = jsonArrObj.getString("image_url");
					secret.audioUrl = jsonArrObj.getString("audio_url");
					secret.audioLength = jsonArrObj.getInt("audio_length");
					secret.likes = jsonArrObj.getInt("likes");
					secret.comments = jsonArrObj.getInt("comments");
					lists.add(secret);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return lists;
	}
}
