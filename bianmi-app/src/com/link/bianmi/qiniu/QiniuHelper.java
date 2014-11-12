package com.link.bianmi.qiniu;

import org.json.JSONException;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.SysConfig;
import com.link.bianmi.http.CallerParam;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.HttpException;
import com.link.bianmi.http.NetInValidException;
import com.link.bianmi.http.Response;

public class QiniuHelper {

	public static class API {

		/** 七牛私有资源上传token **/
		// public static String getUpLoadToken(String bucketName,int expiresIn)
		// throws HttpException,JSONException{
		// if(BianmiApplication.getInstance().isNetworkDown())
		// throw new NetInValidException();
		// CallerParam param=new CallerParam();
		// param.add("scope", bucketName);
		// param.add("expiresIn", String.valueOf(expiresIn));
		// Response
		// res=EngzoApiManager.getInstance().getHttpClient().doPost(true,"tokens/upload",param,false);
		// return res.asJSONObject().getString("uploadToken");
		// }

		/** 七牛私有资源上传token **/
		public static String getUpLoadToken(String bucketName)
				throws HttpException, JSONException {
			if (BianmiApplication.getInstance().isNetworkDown())
				throw new NetInValidException();

			// CallerParam param = new CallerParam();
			// param.add("scope", bucketName);
			// String url = String.format("https://%s/tokens/upload?token=%s",
			// SysConfig.getInstance().getBaseUrl(), ApiToken);
			// Response res = EngzoApiManager.getInstance().getHttpClient()
			// .doPost(true, url, param, false);

			Response res = HttpClient.doGet(SysConfig.getInstance()
					.getQiniuUptoken());
			return res.asJSONObject().getString("uptoken");
		}

		/** 七牛私有资源下载token **/
		// public static String getDownLoadToken(String pattern,int expiresIn)
		// throws HttpException,JSONException{
		// if(BianmiApplication.getInstance().isNetworkDown())
		// throw new NetInValidException();
		// CallerParam param=new CallerParam();
		// param.add("pattern", pattern);
		// param.add("expiresIn", String.valueOf(expiresIn));
		// Response
		// res=EngzoApiManager.getInstance().getHttpClient().doPost(true,"tokens/download",param,false);
		// return res.asJSONObject().getString("downloadToken");
		// }

	}
}
