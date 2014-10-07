package com.link.bianmi.http;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;



/**
 * Caller参数
 *
 */
public class CallerParam{

	ArrayList<BasicNameValuePair> params=null;//参数形式
	JSONObject jsonObject=null; //json形式
	
	File file=null;
	byte[] arrayOfByte =null;
	
	public CallerParam(){}
	
	/**
	 * 添加POST参数
	 * @param key
	 * @param value
	 */
	public void add(String key,String value){
		if(params==null)
			params=new ArrayList<BasicNameValuePair>();
		try{
			
		//	params.add(new BasicNameValuePair(key, HttpClient.encode(value)));
			params.add(new BasicNameValuePair(key,value));
			
			}catch(Exception ex){}
	}
	
	
	public void add(String key,ArrayList<BasicNameValuePair> values){
		for(BasicNameValuePair item:values){
			String newkey=String.format("%s[%s]", key,item.getName());
			if(params==null){
				params=new ArrayList<BasicNameValuePair>();
			}
			params.add(new BasicNameValuePair(newkey,item.getValue().toString()));
		}
		
	}
	
	
	public void add(String key,JSONObject value){
		if(params==null)
			params=new ArrayList<BasicNameValuePair>();
		try{
		//	params.add(new BasicNameValuePair(key, HttpClient.encode(value.toString())));
			params.add(new BasicNameValuePair(key, value.toString()));
			}catch(Exception ex){}
	}	
	
	/**
	 * 添加Json参数
	 * @param key
	 * @param value
	 */
	public void addJson(String key,JSONObject value){
		if(jsonObject==null)
			jsonObject = new JSONObject();
		try{
			jsonObject.put(key, value);
		}catch(Exception ex){}
	}
	/**
	 * 添加Json参数
	 * @param key
	 * @param value
	 */	
	public void addJson(String key,String value){
		
		if(jsonObject==null)
			jsonObject = new JSONObject();
		try{
			jsonObject.put(key, value);
		}catch(Exception ex){}
	}	

	/**
	 * POST时有效
	 * @param file
	 */
	public void setFile(File file)
	{
		this.file=file;
	}
	
	public ArrayList<BasicNameValuePair>  getParams()
	{
		return params;
	}
	
	public JSONObject  getJson()
	{
		return jsonObject;
	}	
	
	/**
	 * 文件
	 * @return
	 */
	public  File getFile(){
		return file;
	}

	/**
	 * 数据流
	 * @return
	 */
	public byte[] getArrayOfByte() {
		return arrayOfByte;
	}
	/**
	 * 数据流
	 * @param arrayOfByte
	 */
	public void setArrayOfByte(byte[] arrayOfByte) {
		this.arrayOfByte = arrayOfByte;
	}
	
	
	
	
}
