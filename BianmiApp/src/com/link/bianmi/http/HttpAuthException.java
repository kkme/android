package com.link.bianmi.http;

/**
 * NOT AUTHORIZED, HTTP CODE 401
 */
public class HttpAuthException extends HttpRefusedException {

	private static final long serialVersionUID = 4206324519931063593L;

	public HttpAuthException(Exception cause) {
		super(cause);
		
	}

	public HttpAuthException(String msg, Exception cause, int statusCode) {
		super(msg, cause, statusCode);
		
	}

	public HttpAuthException(String msg, Exception cause) {
		super(msg, cause);
		
	}

	public HttpAuthException(String msg, int statusCode) {
		super(msg, statusCode);
		
	}

	public HttpAuthException(String msg) {
		super(msg);
		
	}
}
