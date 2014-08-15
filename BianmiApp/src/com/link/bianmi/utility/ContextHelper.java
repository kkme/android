package com.link.bianmi.utility;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.R;
import com.link.bianmi.UserConfig;




/**
 * 上下文信息
 * @author sunpf
 *
 */
public class ContextHelper {
	
	private static Context mContent=BianmiApplication.getInstance().getApplicationContext();
	/**
	 * 获取操作系统版本
	 * @return
	 */
	public static String getOSVersion(){
		return android.os.Build.VERSION.RELEASE;
	}	
	
	/**获取设备名**/
	public static String getModel(){
		return android.os.Build.MODEL;
	}

	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getVerCode() {
		int verCode = -1;
		try {
			verCode = mContent.getPackageManager().getPackageInfo(
					"com.liulishuo.engzo", 0).versionCode;
		} catch (Exception e) {}
		return verCode;
	}
	
	/**
	 * //获取版本名称
	 * @param context
	 * @return
	 */
	public static String getVerName() {
		String verName = "";
		try {
			verName = mContent.getPackageManager().getPackageInfo(
					"com.liulishuo.engzo", 0).versionName;
		} catch (Exception e) {}
		return verName;	

	}
	
	/**
	 * 获取软件名称
	 * @param context
	 * @return
	 */
	public static String getAppName() {
		
		return mContent.getResources().getText(R.string.app_name).toString();
	}	
	
	/**
	 * 获取设备IMEI
	 * @return
	 */
	public static String getDeviceId(){
		TelephonyManager tm = (TelephonyManager) mContent.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId= tm.getDeviceId();
		if(deviceId==null){
			deviceId = Secure.getString(mContent.getContentResolver(), Secure.ANDROID_ID);
		}

		if( deviceId==null || deviceId.compareTo("000000000000000")==0){
			deviceId=tm.getSimSerialNumber();
		}
		//deviceId正常返回
		if(deviceId!=null && !TextUtils.isEmpty(deviceId)){
			return deviceId;
		}

		//创建虚拟deviceId
		deviceId=UserConfig.getInstance().getVirtualDeviceId();
		if(TextUtils.isEmpty(deviceId)){
			deviceId=UUID.randomUUID().toString().replace("-", "");
			UserConfig.getInstance().setVirtualDeviceId(deviceId);
		}
		
		return deviceId;
	}
	
	
	/**
	 * 是否存在SDCard
	 * @return
	 */
	public static boolean  isExistSDCard(){
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	
	/**获取屏幕密度**/
	public static float getScaledDensity(Activity activity){
	    DisplayMetrics dm=new DisplayMetrics(); 
	    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    return dm.scaledDensity;
	}
	
	

}
