package com.link.bianmi.bean;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 2775509782562497083L;

	private String phonenum;// 手机号
	private String password;// 密码
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
