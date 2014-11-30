package com.link.bianmi.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpClient {

	private final static int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;// 连接超时时间

	private static DefaultHttpClient mClient;

	public HttpClient() {
		mClient = getHttpClient();
	}

	private synchronized static DefaultHttpClient getHttpClient() {
		if (mClient == null) {
			final HttpParams httpParams = new BasicHttpParams();
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			final SSLSocketFactory sslSocketFactory = SSLSocketFactory
					.getSocketFactory();
			schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

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
			ClientConnectionManager cm = new ThreadSafeClientConnManager(
					httpParams, schemeRegistry);
			mClient = new DefaultHttpClient(cm, httpParams);
		}

		return mClient;
	}

	public static Response doGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpRes = null;
		Response res = null;
		try {
			if (mClient == null) {
				mClient = getHttpClient();
			}
			httpRes = mClient.execute(httpGet);
			res = new Response(httpRes);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static Response doPost(List<NameValuePair> params, String url) {
		if (url == null || url.isEmpty())
			return null;
		HttpPost httpPost = new HttpPost(url);
		HttpResponse httpRes = null;
		Response res = null;
		HttpEntity httpEntity = null;
		try {
			if (mClient == null) {
				mClient = getHttpClient();
			}
			httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			httpRes = mClient.execute(httpPost);
			res = new Response(httpRes);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
