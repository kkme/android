package com.link.bianmi.entity.builder;

import org.json.JSONObject;

public interface BaseEntityBuilder<T> {
	T buildEntity(JSONObject jsonObj);
}
