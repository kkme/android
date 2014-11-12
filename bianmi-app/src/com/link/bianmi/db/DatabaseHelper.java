package com.link.bianmi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.link.bianmi.SysConfig;

public class DatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = SysConfig.getInstance()
			.getDName();
	private static final int DATABASE_VERSION = 1; // 数据库版本号

	// Construct
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, DATABASE_VERSION);
	}

	public DatabaseHelper(Context context) {

		this(context, DATABASE_NAME, DATABASE_VERSION);
	}

	public DatabaseHelper(Context context, int version) {
		this(context, DATABASE_NAME, null, version);
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		reCreateTables(db);
	}

	@Override
	public synchronized void close() {
		super.close();
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	private void reCreateTables(SQLiteDatabase db) {
		dropAllTables(db);
		db.execSQL(SecretDB.CREATE_TABLE_SQL);
	}

	private void dropAllTables(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + SecretDB.TABLE_NAME);
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
