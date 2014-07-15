package com.link.bianmi.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @TODO 数据构造抽象类
 * @author pangfq
 * @date 2014-7-13 上午10:43:57
 */
public abstract class DatabaseBuilder<T> {

	/** 表名 **/
	public final String TABLE_NAME;
	/** 关键字段名 **/
	public final String FIELD_RESOURCEID;
	/** 所有字段 **/
	public final String[] TABLE_COLUMNS;

	protected DatabaseBuilder(String tablename, String resourceid,
			String[] columns) {
		TABLE_NAME = tablename;
		FIELD_RESOURCEID = resourceid;
		TABLE_COLUMNS = columns;
	}

	/**
	 * 
	 * 构造Entity
	 * 
	 * @param c
	 * @return
	 */
	public abstract T buildEntity(Cursor c);

	/**
	 * 构造ContentValues
	 * 
	 * @param t
	 * @return
	 */
	public abstract ContentValues buildContentValues(T t);
}
