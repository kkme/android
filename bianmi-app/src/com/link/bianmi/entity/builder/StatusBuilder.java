package com.link.bianmi.entity.builder;

import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.Status_;

public class StatusBuilder implements BaseEntityBuilder<Status_> {

	private static StatusBuilder mInstance = null;

	private StatusBuilder() {

	}

	public static StatusBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new StatusBuilder();
		}

		return mInstance;
	}

	@Override
	public Status_ buildEntity(JSONObject jsonObj) {
		Status_ status = null;
		try {
			if (jsonObj != null && jsonObj.has("status")) {
				JSONObject statusJson = jsonObj.getJSONObject("status");
				status = new Status_();
				status.code = statusJson.getInt("code");
				status.msg = statusJson.getString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return status;
	}
}
