package com.link.bianmi.http;


/**
 * HTTP StatusCode is 403, Server refuse the request
 */
public class HttpRefusedException extends HttpException {
	private static final long serialVersionUID = 1L;
	/**
	 * 服务器返回来的错误信息
	 */
	private RefuseError mError;

	public HttpRefusedException(Exception cause) {
		super(cause);
		
	}

	public HttpRefusedException(String msg, Exception cause, int statusCode) {
		super(msg, cause, statusCode);
		
	}

	public HttpRefusedException(String msg, Exception cause) {
		super(msg, cause);
		
	}

	public HttpRefusedException(String msg, int statusCode) {
		super(msg, statusCode);
		
	}

	public HttpRefusedException(String msg) {
		super(msg);
		
	}

	public RefuseError getError() {
		return mError;
	}

	public HttpRefusedException setError(RefuseError error) {
		mError = error;
		return this;
	}

}
