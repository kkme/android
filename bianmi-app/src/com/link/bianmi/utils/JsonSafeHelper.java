package com.link.bianmi.utils;

import org.json.JSONObject;


/**json安全数据帮助类**/
public class JsonSafeHelper {

	/** 从JSON获取boolean**/
	public static boolean getBoolean(JSONObject jsonObject,String str){
		boolean result=false;
		try{
			result=jsonObject.getBoolean(str);
		}catch(Exception ex){}	
		
		return result;
	}
	
	
	/** 从JSON获取boolean**/
	public static boolean getBoolean(JSONObject jsonObject,String str,boolean defaultValue){
		boolean result=defaultValue;
		try{
			result=jsonObject.getBoolean(str);
		}catch(Exception ex){}	
		
		return result;
	}	
	
	
	/** 从JSON获取int**/
	public static int getInt(JSONObject jsonObject,String str) {
		int result=0;
		try{
			result=jsonObject.getInt(str);
		}catch(Exception ex){}
		return result;
	}

	
	/** 从JSON获取Long**/
	public static long getLong(JSONObject jsonObject,String str) {
		long result=0;
		try{
			result=jsonObject.getLong(str);
		}catch(Exception ex){}
		return result;
	}
	
	
	/** 从JSON获取int**/
	public static float getFloat(JSONObject jsonObject,String str) {
		float result=0;
		try{
			result=(float)jsonObject.getDouble(str);
		}catch(Exception ex){}
		return result;
	}
	
	/**从JSON获取String，过滤null**/
	public static String getString(JSONObject jsonObject,String str){
		String  result="";
		try{
			result=jsonObject.getString(str);
			if(result.compareTo("null")==0)
				result="";
		}catch(Exception ex){}
		return result;
	}
	
}
