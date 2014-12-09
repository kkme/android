package com.link.bianmi.qiniu;

import org.json.JSONException;

import com.link.bianmi.MyApplication;
import com.link.bianmi.SysConfig;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.HttpException;
import com.link.bianmi.http.NetInValidException;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class QiniuManager {

	public static class API {

		/** 七牛私有资源上传token **/
		public static String getUpLoadToken(String bucketName)
				throws HttpException, JSONException {
			if (MyApplication.getInstance().isNetworkDown())
				throw new NetInValidException();
			Response response = HttpClient.doGet(SysConfig.getInstance()
					.getQiniuUptoken());
			String uptoken = null;
			try {
				if (response != null) {
					uptoken = response.asJSONObject().getString("uptoken");
				}
			} catch (ResponseException e) {
			}

			return uptoken;
		}
	}
}
