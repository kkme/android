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
				user = new User();
				user.userId = jsonObj.getString("id");
				user.phonenum = jsonObj.getString("phone");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;
	}
}
