package com.link.bianmi.entity;

import java.io.Serializable;

import com.link.bianmi.MyApplication;
import com.link.bianmi.R;

public class Status_ implements Serializable {

	private static final long serialVersionUID = -8147338499497529388L;

	public static final int OK = 200;
	public static final int FAILED = 400;

	public int code = FAILED;// 返回码
	public String msg = MyApplication.getInstance().getString(
			R.string.default_failed_tip);// 返回信息

}
