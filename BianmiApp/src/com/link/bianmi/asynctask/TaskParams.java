package com.link.bianmi.asynctask;

import java.util.HashMap;


public class TaskParams implements Cloneable{

	private HashMap<String,Object> params=null;
	
	public TaskParams(){
		params=new HashMap<String,Object>();
	}
	
	public TaskParams(String key,Object value){
		this();
		put(key,value);
	}
	
	public void put(String key,Object value){
		if(valueEmpty){
			if(value instanceof String){
				if(((String)value).length()>0)
					valueEmpty=false;
			}else if(value!=null){
				valueEmpty=false;
			}
		}
		params.put(key, value);
	}
	
	public Object get(String key){
		return params.get(key);
	}
	
	/**
	 * 获取String值
	 * 		找不到时返回NULL
	 * @param key
	 * @return
	 */
	public String getString(String key){
	
		Object object=get(key);
		return object ==null ? null :object.toString();
	}
	
	/**
	 * 获取String值
	 * @param key
	 * @param defalutStr
	 * 		找不到时返回指定字符窜
	 * @return
	 */
	public String getString(String key,String defalutStr){
		
		Object object=get(key);
		return object ==null ? defalutStr :object.toString();
	}
	
	/**
	 * return the number of elements in this object
	 * @return
	 */
	public int size(){
		return params.size();
	}
	
	private Boolean valueEmpty=true;
	/**
	 * 值是否为空
	 */
	public Boolean isValueEmpth(){
		return valueEmpty;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TaskParams clone()
	{     
		TaskParams taskparams=null;
	    try {
	    	taskparams=(TaskParams)super.clone();
	    	if(params!=null)
	    		taskparams.params=(HashMap<String,Object>)params.clone();
	    } catch (CloneNotSupportedException e) {
	      System.out.println("MyObject can't clone");
	    }
	    return taskparams;
	}	
	
	
	private int tag=0;

	/** 获取标记 **/
	public int getTag() {
		return tag;
	}
	/** 设置标记 **/
	public void setTag(int tag) {
		this.tag = tag;
	}
	

	
}
