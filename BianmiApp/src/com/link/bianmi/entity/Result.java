package com.link.bianmi.entity;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = -8147338499497529388L;

	public static final int RESULT_CODE_OK = 200;

	public int code;// 返回码
	public String msg = "";// 返回信息

}
