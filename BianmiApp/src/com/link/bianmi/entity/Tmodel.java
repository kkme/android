package com.link.bianmi.entity;

import org.json.JSONObject;

import com.link.bianmi.utility.JsonSafeHelper;

/** 带分页数据模型 **/
public class Tmodel<T> {
	
	public T t;
	/** 总数 **/
	public int total=0;
	/**当前页**/
	public int currentPage=0;
	
	
	/**解析total,currentPage数据**/
	@SuppressWarnings("hiding")
	public  <T>Tmodel(JSONObject jsonObject){
		if(jsonObject==null)
			return;
		
		if(jsonObject.has("currentPage")){
			currentPage=JsonSafeHelper.getInt(jsonObject, "currentPage");
		}
		if(jsonObject.has("total")){
			total=JsonSafeHelper.getInt(jsonObject, "total");
		}
	}

}
