package com.link.bianmi.entity.builder;

import org.json.JSONObject;

import com.link.bianmi.entity.ListResult;

public interface BaseEntitysBuilder<T> {
	ListResult<T> buildEntitys(JSONObject jsonObj);
}
