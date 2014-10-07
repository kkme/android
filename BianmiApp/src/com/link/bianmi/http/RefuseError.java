package com.link.bianmi.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 服务器响应的错误信息
 *
 */
public class RefuseError implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	public static final int ERROR_A = 1;
	public static final int ERROR_B = 1;
	public static final int ERROR_C = 1;

	private int mErrorCode = -1;
	private String mRequestUrl = "";
	private String mResponseError = "";



	public RefuseError(Response res) throws HttpException {
		String error = res.asString();
		try {
			// 先尝试作为json object进行处理
			JSONObject json = new JSONObject(error);
			init(json);
		} catch (Exception e1) {
			// 如果失败，则作为XML再进行处理
			try {
				XmlObject xml = new XmlObject(error);
				init(xml);
			} catch (Exception e2) {
				// 再失败就作为普通字符串进行处理，这个处理保证不会出错
				init(error);
			}
		}
	}

	public void init(JSONObject json) throws HttpException {
		try {
			mRequestUrl = json.getString("request");
			mResponseError = json.getString("error");
			parseError(mResponseError);
		} catch (JSONException je) {
			throw new HttpException(je.getMessage() + ":" + json.toString(), je);
		}
	}

	public void init(XmlObject xml) throws HttpException {
		try {
			mRequestUrl = xml.getString("request");
			mResponseError = xml.getString("error");
			parseError(mResponseError);
		} catch (Exception e) {
			throw new HttpException(e.getMessage() + ":" + xml.toString(), e);
		}
	}

	public void init(String error) {
		mRequestUrl = "";
		mResponseError = error;
		parseError(mResponseError);
	}

	private void parseError(String error) {
		if (error.equals("")) {
			mErrorCode = ERROR_A;
		}
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public String getRequestUrl() {
		return mRequestUrl;
	}

	public String getMessage() {
		return mResponseError;
	}
}


/**
 * 模仿JSONObject的XML实现
 * 
 * @author jmx
 * 
 */
class XmlObject {
	private String str;

	public XmlObject(String s) {
		this.str = s;
	}

	//  这里用的是一个专有的ugly实现
	public String getString(String name) throws Exception {
		Pattern p = Pattern
				.compile(String.format("<%s>(.*?)</%s>", name, name));
		Matcher m = p.matcher(this.str);
		if (m.find()) {
			return m.group(1);
		} else {
			throw new Exception(String.format("<%s> value not found", name));
		}
	}

	@Override
	public String toString() {
		return this.str;
	}
}