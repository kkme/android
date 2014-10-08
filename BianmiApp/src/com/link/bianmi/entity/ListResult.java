package com.link.bianmi.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 列表数据模型
 * 
 * @author pangfq
 * @date 2014年10月8日 上午9:15:36
 */
public class ListResult<T> implements Serializable {

	private static final long serialVersionUID = 6507446786618318307L;

	public List<T> list;
	public boolean hasMore;

}
