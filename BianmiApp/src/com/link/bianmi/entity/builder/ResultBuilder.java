package com.link.bianmi.entity.builder;

import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.Result;

public class ResultBuilder implements BaseEntityBuilder<Result> {
	
	private static ResultBuilder mInstance = null;

	private ResultBuilder() {

	}

	public static ResultBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new ResultBuilder();
		}

		return mInstance;
	}

	@Override
	public Result buildEntity(JSONObject jsonObj) {
		Result result = null;
		try {
			if (jsonObj != null && jsonObj.has("result")) {
				result = new Result();
				result.code = jsonObj.getInt("code");
				result.msg = jsonObj.getString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
}
