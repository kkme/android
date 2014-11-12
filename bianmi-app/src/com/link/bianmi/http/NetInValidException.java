package com.link.bianmi.http;


/**
 * 没有网络
 *
 */
public class NetInValidException extends HttpException {
	
	private static final long serialVersionUID = 1L;
	
	
	public NetInValidException(String msg) {
		super(msg);
	}
	public NetInValidException() {
		super("");
	}
}
