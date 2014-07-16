package com.link.bianmi.http;

/**
 * 解析response时出现IOException, JSONException等
 */
public class ResponseException extends HttpException {

	private static final long serialVersionUID = -9161304367990941666L;

	public ResponseException(Exception cause) {
		super(cause);
		
	}

	public ResponseException(String msg, Exception cause, int statusCode) {
		super(msg, cause, statusCode);
		
	}

	public ResponseException(String msg, Exception cause) {
		super(msg, cause);
		
	}

	public ResponseException(String msg, int statusCode) {
		super(msg, statusCode);
		
	}

	public ResponseException(String msg) {
		super(msg);
		
	}

}
