package com.link.bianmi.entity;

import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = 4985938307676929167L;

	public ResultStatus status;

	public T t;

}
