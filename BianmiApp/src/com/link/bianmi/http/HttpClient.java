package com.link.bianmi.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpClient {

	private final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;// 连接超时时间

	private static DefaultHttpClient mClient;

	private HttpClient() {
		mClient = getHttpClient();
	}

	private synchronized DefaultHttpClient getHttpClient() {
		if (mClient == null) {
			final HttpParams httpParams = new BasicHttpParams();

			ConnManagerParams.setTimeout(httpParams, 1000);
			HttpConnectionParams.setConnectionTimeout(httpParams,
					DEFAULT_SOCKET_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					DEFAULT_SOCKET_TIMEOUT);
			HttpProtocolParams.setUseExpectContinue(httpParams, true);
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
			HttpClientParams.setRedirecting(httpParams, false);
			mClient = new DefaultHttpClient();
			mClient.setParams(httpParams);
		}

		return mClient;
	}

	public static Response doGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpRes = null;
		Response res = null;
		try {
			httpRes = mClient.execute(httpGet);
			res = new Response(httpRes);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static HttpResponse doPost() {
		HttpResponse response = null;
		return response;
	}
}
