package com.link.bianmi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.link.bianmi.entity.Secret;

public class SecretDB extends DatabaseBuilder<Secret> implements BaseColumns {

	public static final String TABLE_NAME = "Secret";

	private static final String FIELD_RESOURCEID = "resourceid";
	private static final String FIELD_CONTENT = "content";
	private static final String FIELD_WHEREFROM = "wherefrom";
	public static final String FIELD_LIKECOUNT = "likecount";
	private static final String FIELD_REPLYCOUNT = "replycount";
	private static final String FIELD_AUDIOURL = "audiourl";
	private static final String FIELD_IMAGEURL = "imageurl";
	public static final String FIELD_CREATEDAT = "createdat";
	private static final String FIELD_REPLIEDAT = "repliedat";

	public final static String[] TABLE_COLUMNS = { _ID, FIELD_RESOURCEID,
			FIELD_CONTENT, FIELD_WHEREFROM, FIELD_LIKECOUNT, FIELD_REPLYCOUNT,
			FIELD_AUDIOURL, FIELD_IMAGEURL, FIELD_CREATEDAT, FIELD_REPLIEDAT };

	public static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME
			+ " (" + _ID + " text primary key on conflict replace, "
			+ FIELD_RESOURCEID + " text ,"
			+ FIELD_CONTENT + " text ,"
			+ FIELD_WHEREFROM + " text, "
			+ FIELD_LIKECOUNT + " integer , "
			+ FIELD_REPLYCOUNT + " integer , "
			+ FIELD_AUDIOURL + " text , "
			+ FIELD_IMAGEURL + " text, "	
			+ FIELD_CREATEDAT + " integer , "
			+ FIELD_REPLIEDAT + " integer )";

	private static SecretDB mInstance = null;

	public static SecretDB getInstance() {
		if (mInstance == null) {
			mInstance = new SecretDB();
		}
		return mInstance;
	}

	private SecretDB() {
		super(TABLE_NAME, FIELD_RESOURCEID, TABLE_COLUMNS);
	}

	@Override
	public Secret buildEntity(Cursor c) {
		if (c == null || c.getCount() <= 0)
			return null;
		Secret s = new Secret();
		s.setResourceId(c.getString(c.getColumnIndex(FIELD_RESOURCEID)));
		s.setContent(c.getString(c.getColumnIndex(FIELD_CONTENT)));
		s.setWherefrom(c.getString(c.getColumnIndex(FIELD_WHEREFROM)));
		s.setLikeCount(c.getInt(c.getColumnIndex(FIELD_LIKECOUNT)));
		s.setReplyCount(c.getInt(c.getColumnIndex(FIELD_REPLYCOUNT)));
		s.setImageUrl(c.getString(c.getColumnIndex(FIELD_IMAGEURL)));
		s.setCreatedAt(c.getLong(c.getColumnIndex(FIELD_CREATEDAT)));
		s.setRepliedAt(c.getLong(c.getColumnIndex(FIELD_REPLIEDAT)));
		return s;
	}

	@Override
	public ContentValues buildContentValues(Secret s) {
		ContentValues cv = new ContentValues();
		cv.put(FIELD_RESOURCEID, s.getResourceId());
		cv.put(FIELD_CONTENT, s.getContent());
		cv.put(FIELD_WHEREFROM, s.getWherefrom());
		cv.put(FIELD_LIKECOUNT, s.getLikeCount());
		cv.put(FIELD_REPLYCOUNT, s.getReplyCount());
		cv.put(FIELD_IMAGEURL, s.getImageUrl());
		cv.put(FIELD_CREATEDAT, s.getCreatedAt());
		cv.put(FIELD_REPLIEDAT, s.getRepliedAt());
		return cv;
	}

}
