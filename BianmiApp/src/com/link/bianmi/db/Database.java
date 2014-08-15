package com.link.bianmi.db;

import java.lang.reflect.Array;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.SysConfig;

public class Database {

	public static final String TAG = "Database";
	private static Database mInstance = null;
	private SQLiteOpenHelper mDBHelper = null;

	private boolean mDebaug = SysConfig.getInstance().isDebug();

	private Database(Context context) {
		mDBHelper = new DatabaseHelper(context);

	}

	public static Database getInstance() {
		if (null == mInstance) {
			mInstance = new Database(BianmiApplication.getInstance());
		}
		return mInstance;
	}

	public SQLiteDatabase getDb(boolean writeable) {

		if (!mDebaug)
			return null;
		if (writeable) {
			return mDBHelper.getWritableDatabase();
		} else {
			return mDBHelper.getReadableDatabase();
		}
	}

	public void CleanData(String tableName) {
		SQLiteDatabase db = getDb(true);
		db.execSQL("DELETE FROM " + tableName);
	}

	public void close() {
		if (null != mInstance) {
			mDBHelper.close();
			mInstance = null;
		}
	}

	/** 开始事务 **/
	public void transactionBegin() {
		SQLiteDatabase db = getDb(true);
		db.beginTransaction();
	}

	/** 事务成功 **/
	public void transactionSuccessful() {
		SQLiteDatabase db = getDb(true);
		db.setTransactionSuccessful();
	}

	/** 结束事务 **/
	public void transactionEnd() {
		SQLiteDatabase db = getDb(true);
		db.endTransaction();
	}

	// -----------------------------泛型处理模板-----------------------
	/**
	 * 删除数据
	 * 
	 * @param builder
	 * @param resourceId
	 *            默认关键字值
	 */
	public <T> void deleteEntity(DatabaseBuilder<T> builder, String resourceId) {
		SQLiteDatabase db = getDb(true);
		db.delete(builder.TABLE_NAME, builder.FIELD_RESOURCEID + "=?",
				new String[] { String.valueOf(resourceId) });
	}

	/**
	 * 新增或更新
	 * 
	 * @param builder
	 * @param entity
	 * @param resourceId
	 *            默认关键字值
	 * @return true新增加 false更新
	 */
	public <T> boolean updateEntityWithCheckExist(DatabaseBuilder<T> builder,
			Object entity, String resourceId) {

		if (!isEntityExist(builder, resourceId)) {
			addEntity(builder, entity);
			return true;
		} else {
			updateEntity(builder, entity, resourceId);
			return false;
		}
	}

	/** 更新数据 **/
	@SuppressWarnings("unchecked")
	public <T> void updateEntity(DatabaseBuilder<T> builder, Object entity,
			String resourceId) {
		SQLiteDatabase db = getDb(true);
		ContentValues values = builder.buildContentValues(((T) entity));
		db.update(builder.TABLE_NAME, values, builder.FIELD_RESOURCEID + "=?",
				new String[] { String.valueOf(resourceId) });
	}

	/**
	 * 更新数据:根据指定关键只更新数据
	 * 
	 * @param builder
	 * @param entity
	 * @param fieldNames
	 *            关键字字段名称
	 * @param fieldValues
	 *            关键字字段值
	 */
	@SuppressWarnings("unchecked")
	public <T> void updateEntityWithKeyField(DatabaseBuilder<T> builder,
			Object entity, String[] fieldNames, String[] fieldValues) {

		if (!isEntityExist(builder, fieldNames, fieldValues)) {
			addEntity(builder, entity);
		} else {
			SQLiteDatabase db = getDb(true);
			ContentValues values = builder.buildContentValues((T) entity);
			String whereClause = "";
			for (int i = 0, count = fieldNames.length; i < count; i++) {
				if (!TextUtils.isEmpty(whereClause))
					whereClause += " and ";
				whereClause += fieldNames[i] + "=? ";
			}
			db.update(builder.TABLE_NAME, values, whereClause, fieldValues);
		}
	}

	/**
	 * 数据是否已经存在
	 * 
	 * @param builder
	 * @param resourceid
	 *            默认关键字值
	 * @return
	 */
	public <T> boolean isEntityExist(DatabaseBuilder<T> builder,
			String resourceid) {
		boolean ret = false;
		SQLiteDatabase db = getDb(true);
		String strSql = " select count(*) as total from " + builder.TABLE_NAME
				+ " where " + builder.FIELD_RESOURCEID + " = '" + resourceid
				+ "'";
		Cursor query = db.rawQuery(strSql, null);
		if (query != null) {
			query.moveToFirst();
			int total = query.getInt(query.getColumnIndex("total"));
			if (total > 0)
				ret = true;
		}
		query.close();
		return ret;
	}

	/**
	 * 数据是否已经存在
	 * 
	 * @param builder
	 * @param fieldNames
	 *            关键字字段名称
	 * @param fieldValues
	 *            关键字字段值
	 * @return
	 */
	public <T> boolean isEntityExist(DatabaseBuilder<T> builder,
			String[] fieldNames, String[] fieldValues) {
		boolean ret = false;
		SQLiteDatabase db = getDb(true);

		String whereClause = "";
		for (int i = 0, count = fieldNames.length; i < count; i++) {
			if (!TextUtils.isEmpty(whereClause))
				whereClause += " and ";
			whereClause += String.format("%s='%s'", fieldNames[i],
					fieldValues[i]);
		}

		String strSql = " select count(*) as total from " + builder.TABLE_NAME
				+ " where " + whereClause;

		Cursor query = db.rawQuery(strSql, null);
		if (query != null) {
			query.moveToFirst();
			int total = query.getInt(query.getColumnIndex("total"));
			if (total > 0)
				ret = true;
		}
		query.close();
		return ret;
	}

	/**
	 * 新增数据
	 * 
	 * @param <T>
	 **/
	@SuppressWarnings("unchecked")
	public <T> void addEntity(DatabaseBuilder<T> builder, Object entity) {
		SQLiteDatabase db = getDb(true);
		ContentValues values = new ContentValues();
		values.putAll(builder.buildContentValues((T) entity));
		db.insert(builder.TABLE_NAME, null, values);
	}

	/**
	 * 获取实例
	 * 
	 * @param <T>
	 **/
	public <T> T getEntity(DatabaseBuilder<T> builder, String selection,
			String[] selectionArgs) {
		return getEntity(builder, selection, selectionArgs, null);
	}

	/**
	 * 获取实例
	 * 
	 * @param <T>
	 **/
	public <T> T getEntity(DatabaseBuilder<T> builder, String selection,
			String[] selectionArgs, String orderBy) {
		SQLiteDatabase db = getDb(false);
		Cursor cursor = db.query(builder.TABLE_NAME, builder.TABLE_COLUMNS,
				selection, selectionArgs, null, null, orderBy, "1");
		T entity = null;
		if (cursor != null) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				entity = builder.buildEntity(cursor);
			}
		}
		cursor.close();
		return entity;
	}

	/** 获取实例数组 **/
	public <T> T[] getEntitys(Class<?> type, DatabaseBuilder<T> builder,
			String selection, String[] selectionArgs) {

		return getEntitys(type, builder, selection, selectionArgs, null, null);
	}

	/** 获取实例数组 **/
	@SuppressWarnings("unchecked")
	public <T> T[] getEntitys(Class<?> type, DatabaseBuilder<T> builder,
			String selection, String[] selectionArgs, String orderBy,
			String limit) {

		SQLiteDatabase db = getDb(false);
		Cursor cursor = db.query(builder.TABLE_NAME, builder.TABLE_COLUMNS,
				selection, selectionArgs, null, null, orderBy, limit);
		T[] entitys = null;
		if (cursor != null) {
			cursor.moveToFirst();
			int count = cursor.getCount();
			entitys = (T[]) Array.newInstance(type, count);
			for (int i = 0; i < count; i++) {
				entitys[i] = builder.buildEntity(cursor);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return entitys;
	}

}
