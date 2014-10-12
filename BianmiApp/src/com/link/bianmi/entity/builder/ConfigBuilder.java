package com.link.bianmi.entity.builder;

import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.Config;

public class ConfigBuilder implements BaseEntityBuilder<Config> {

	private static ConfigBuilder mInstance = null;

	private ConfigBuilder() {
	}

	public static ConfigBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new ConfigBuilder();
		}

		return mInstance;
	}

	@Override
	public Config buildEntity(JSONObject jsonObj) {
		Config config = null;
		try {
			if (jsonObj != null && jsonObj.has("config")) {
				JSONObject configJson = jsonObj.getJSONObject("config");
				config = new Config();
				config.showAd = configJson.getBoolean("show_ad");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return config;
	}
}
