package com.link.bianmi.bean;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 2775509782562497083L;

	private String username;// 用户名
	private String password;// 密码
	private String phonenum;// 手机号

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
