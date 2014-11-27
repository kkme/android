package com.link.bianmi.utils;

import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
/**
 * 网络帮助类
 *
 */
public class NetWorkHelper {


	/**
	 * 网络类型
	 */
	public enum NetWorkType implements IntEnum {
		/**
		 *  没有网络
		 */
		NET_INVALID {	
	        @Override
	        public int toInt()
	        {
	            return 0;
	        }
	    },
	    /**
	     * wap网络
	     */
	    NET_WAP {
	        @Override
	        public int toInt()
	        {
	            return 1;
	        }
	    },
	    /**
	     * 2G网络
	     */
	    NET_2G {
	        @Override
	        public int toInt()
	        {
	            return 2;
	        }
	    },
	    /**
	     * 3G和3G以上网络，或统称为快速网络
	     */
	    NET_3G {
	        @Override
	        public int toInt()
	        {
	            return 3;
	        }
	    },
	    /**
	     * wifi网络
	     */
	    NET_WIFI {
	        @Override
	        public int toInt()
	        {
	            return 4;
	        }
	    };
	    private static Map<Integer, NetWorkType> instanceMap = IntEnums.map(NetWorkType.class);
	    public static NetWorkType valueOf(int i)
	    {
	        return instanceMap.get(i);
	    }
	}	
	
	

	private static boolean isFastMobileNetwork(Context context) {  
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
		switch (telephonyManager.getNetworkType()) {  
		       case TelephonyManager.NETWORK_TYPE_1xRTT:  
		           return false; // ~ 50-100 kbps  
		       case TelephonyManager.NETWORK_TYPE_CDMA:  
		           return false; // ~ 14-64 kbps  
		       case TelephonyManager.NETWORK_TYPE_EDGE:  
		           return false; // ~ 50-100 kbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_0:  
		           return true; // ~ 400-1000 kbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_A:  
		           return true; // ~ 600-1400 kbps  
		       case TelephonyManager.NETWORK_TYPE_GPRS:  
		           return false; // ~ 100 kbps  
		       case TelephonyManager.NETWORK_TYPE_HSDPA:  
		           return true; // ~ 2-14 Mbps  
		       case TelephonyManager.NETWORK_TYPE_HSPA:  
		           return true; // ~ 700-1700 kbps  
		       case TelephonyManager.NETWORK_TYPE_HSUPA:  
		           return true; // ~ 1-23 Mbps  
		       case TelephonyManager.NETWORK_TYPE_UMTS:  
		           return true; // ~ 400-7000 kbps  
		       case 14://TelephonyManager.NETWORK_TYPE_EHRPD:  
		           return true; // ~ 1-2 Mbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_B:  
		           return true; // ~ 5 Mbps  
		       case 15://TelephonyManager.NETWORK_TYPE_HSPAP:  
		           return true; // ~ 10-20 Mbps  
		       case TelephonyManager.NETWORK_TYPE_IDEN:  
		           return false; // ~25 kbps  
		       case 13://TelephonyManager.NETWORK_TYPE_LTE:  
		           return true; // ~ 10+ Mbps  
		       case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
		           return false;  
		       default:  
		           return false;  
		    }  
		}  
	
	
	/** 
     * 获取网络状态类型
     * 
     * @param context 上下文 
     * @return NetWorkType 网络类型
     */  
  
    public static NetWorkType getNetWorkType(Context context) {  
  
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
        NetWorkType net=NetWorkType.NET_INVALID;
        if (networkInfo != null && networkInfo.isConnected()) {  
            String type = networkInfo.getTypeName();  
            if (type.equalsIgnoreCase("WIFI")) {  
            	net = NetWorkType.NET_WIFI;  
            } else if (type.equalsIgnoreCase("MOBILE")) {  
                String proxyHost = android.net.Proxy.getDefaultHost();  
                net = TextUtils.isEmpty(proxyHost)  
                        ? (isFastMobileNetwork(context) ? NetWorkType.NET_3G : NetWorkType.NET_2G)  
                        : NetWorkType.NET_WAP;  
            }  
        }
        return net;  
    	
	
//	
//	public String NetType(Context context) {
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo info = cm.getActiveNetworkInfo();
//            String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
//            if (typeName.equalsIgnoreCase("wifi")) {
//            } else {
//                typeName = info.getExtraInfo().toLowerCase();
//                // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
//            }
//            return typeName;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//	
//	
//	
//	
//	public static boolean isConnectionFast(int type, int subType){
//        if(type==ConnectivityManager.TYPE_WIFI){
//            return true;
//        }else if(type==ConnectivityManager.TYPE_MOBILE){
//            switch(subType){
//            case TelephonyManager.NETWORK_TYPE_1xRTT:
//                return false; // ~ 50-100 kbps
//            case TelephonyManager.NETWORK_TYPE_CDMA:
//                return false; // ~ 14-64 kbps
//            case TelephonyManager.NETWORK_TYPE_EDGE:
//                return false; // ~ 50-100 kbps
//            case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                return true; // ~ 400-1000 kbps
//            case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                return true; // ~ 600-1400 kbps
//            case TelephonyManager.NETWORK_TYPE_GPRS:
//                return false; // ~ 100 kbps
//            case TelephonyManager.NETWORK_TYPE_HSDPA:
//                return true; // ~ 2-14 Mbps
//            case TelephonyManager.NETWORK_TYPE_HSPA:
//                return true; // ~ 700-1700 kbps
//            case TelephonyManager.NETWORK_TYPE_HSUPA:
//                return true; // ~ 1-23 Mbps
//            case TelephonyManager.NETWORK_TYPE_UMTS:
//                return true; // ~ 400-7000 kbps
//            // NOT AVAILABLE YET IN API LEVEL 7
//            case Connectivity.NETWORK_TYPE_EHRPD:
//                return true; // ~ 1-2 Mbps
//            case Connectivity.NETWORK_TYPE_EVDO_B:
//                return true; // ~ 5 Mbps
//            case Connectivity.NETWORK_TYPE_HSPAP:
//                return true; // ~ 10-20 Mbps
//            case Connectivity.NETWORK_TYPE_IDEN:
//                return false; // ~25 kbps 
//            case Connectivity.NETWORK_TYPE_LTE:
//                return true; // ~ 10+ Mbps
//            // Unknown
//            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
//                return false; 
//            default:
//                return false;
//            }
//        }else{
//            return false;
//        }
    }	
}
