package com.link.bianmi.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**转换帮助类**/
public class ConvertHelper {

	/** string转int **/
	public static List<Integer> string2IntegerLst(String[] inList){
		List<String> l=Arrays.asList(inList);
		return string2IntegerLst(l);
	}
	
	
	/** string转int **/
	public static List<Integer> string2IntegerLst(List<String> inList){
		if(inList==null || inList.size()==0)
			return new ArrayList<Integer>(0);
        List<Integer> iList =new ArrayList<Integer>(inList.size());
        try{   
           for(int i=0,j=inList.size();i<j;i++){
             iList.add(Integer.parseInt(inList.get(i)));   
           }   
          }catch(Exception  e){
        }
        return iList;
    }
	
	
	/** int 转 string **/
	public static List<String> integer2StringLst(Integer[] inList){
		List<Integer> l=Arrays.asList(inList);
		return integer2StringLst(l);	
	}
	
	
	/** int 转 string **/
	public static List<String> integer2StringLst(List<Integer> inList){
		if(inList==null || inList.size()==0)
			return new ArrayList<String>(0);
		
        List<String> iList =new ArrayList<String>(inList.size());
        try{   
           for(int i=0,j=inList.size();i<j;i++){
             iList.add(String.valueOf(inList.get(i)));   
           }   
          }catch(Exception  e){
        }
        return iList;
    }	
	
	
	public static String array2String(int[] inList,String split){
        if (inList==null) {
            return "";
        }
        
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (int num : inList) {
            if (flag) {
                result.append(split);
            }else {
                flag=true;
            }
            result.append(num);
        }       
        
        
        
        return result.toString();
	}
	
	
	/**
	 *  list 转 string. 
	 * @param stringList
	 * @param split
	 * 		分隔符
	 * @return
	 */
	public static String list2String(List<String> stringList,String split){
        if (stringList==null) {
            return "";
        }
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (String string : stringList) {
            if (flag) {
                result.append(split);
            }else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }
	
	
	
	
	
	public static int[] string2Int(String str,String split) {  
		 

		
		try{
			int ret[] = new int[str.length()];   
		  
		    StringTokenizer toKenizer = new StringTokenizer(str, split);   
		    int i = 0;  
		    while (toKenizer.hasMoreElements()) {   
		  
		      ret[i++] = Integer.valueOf(toKenizer.nextToken());  
		  
		    }  
		    return ret;  
		}catch(Exception ex){}
	  
	   
		return null;
	  
	 }
	

	public static String uri2StrPath(Activity context,Uri uri){
		if(uri.toString().startsWith("file:///")){
			return uri.toString().replace("file://", "");
		}
		
		String[] projection = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context,uri, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	

	
}
