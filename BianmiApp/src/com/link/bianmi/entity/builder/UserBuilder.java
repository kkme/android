package com.link.bianmi.entity.builder;

import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.User;

public class UserBuilder implements BaseEntityBuilder<User> {

	private static UserBuilder mInstance = null;

	private UserBuilder() {

	}

	public static UserBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new UserBuilder();
		}

		return mInstance;
	}

	@Override
	public User buildEntity(JSONObject jsonObj) {
		User user = null;
		try {
			if (jsonObj != null && jsonObj.has("user")) {
				JSONObject userJson = jsonObj.getJSONObject("user");
				user = new User();
				user.id = userJson.getString("userid");
				user.sessionId = jsonObj.getString("sessionid");
				user.phone = jsonObj.getString("phone");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;
	}
}
