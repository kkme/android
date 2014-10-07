package com.link.bianmi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.link.bianmi.entity.Secret;

public class SecretDB extends DatabaseBuilder<Secret> implements BaseColumns {

	public static final String TABLE_NAME = "Secret";

	public static final String FIELD_RESOURCEID = "resourceid";
	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_WHEREFROM = "wherefrom";
	public static final String FIELD_LIKES = "likes";
	public static final String FIELD_REPLIES = "replies";
	public static final String FIELD_AUDIO_URL = "audio_url";
	public static final String FIELD_AUDIO_LENGTH = "audio_length";
	public static final String FIELD_IMAGE_URL = "image_url";
	public static final String FIELD_CREATED_TIME = "created_time";
	public static final String FIELD_REPLIED_TIME = "replied_time";

	public final static String[] TABLE_COLUMNS = { _ID, FIELD_RESOURCEID,
			FIELD_CONTENT, FIELD_WHEREFROM, FIELD_LIKES, FIELD_REPLIES,
			FIELD_AUDIO_URL, FIELD_AUDIO_LENGTH, FIELD_IMAGE_URL,
			FIELD_CREATED_TIME, FIELD_REPLIED_TIME };

	public static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME
			+ " (" + _ID + " text primary key on conflict replace, "
			+ FIELD_RESOURCEID + " text ," + FIELD_CONTENT + " text ,"
			+ FIELD_WHEREFROM + " text, " + FIELD_LIKES + " integer , "
			+ FIELD_REPLIES + " integer , " + FIELD_AUDIO_URL + " text , "
			+ FIELD_AUDIO_LENGTH + " integer, " + FIELD_IMAGE_URL + " text, "
			+ FIELD_CREATED_TIME + " integer , " + FIELD_REPLIED_TIME + " integer )";

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
		s.resourceId = c.getString(c.getColumnIndex(FIELD_RESOURCEID));
		s.content = c.getString(c.getColumnIndex(FIELD_CONTENT));
		s.from = c.getString(c.getColumnIndex(FIELD_WHEREFROM));
		s.likes = c.getInt(c.getColumnIndex(FIELD_LIKES));
		s.comments = c.getInt(c.getColumnIndex(FIELD_REPLIES));
		s.audioUrl = c.getString(c.getColumnIndex(FIELD_AUDIO_URL));
		s.audioLength = c.getInt(c.getColumnIndex(FIELD_AUDIO_LENGTH));
		s.imageUrl = c.getString(c.getColumnIndex(FIELD_IMAGE_URL));
		s.createdTime = c.getLong(c.getColumnIndex(FIELD_CREATED_TIME));
		s.repliedTime = c.getLong(c.getColumnIndex(FIELD_REPLIED_TIME));
		return s;
	}

	@Override
	public ContentValues buildContentValues(Secret s) {
		ContentValues cv = new ContentValues();
		cv.put(FIELD_RESOURCEID, s.resourceId);
		cv.put(FIELD_CONTENT, s.content);
		cv.put(FIELD_WHEREFROM, s.from);
		cv.put(FIELD_LIKES, s.likes);
		cv.put(FIELD_REPLIES, s.comments);
		cv.put(FIELD_AUDIO_URL, s.audioUrl);
		cv.put(FIELD_AUDIO_LENGTH, s.audioLength);
		cv.put(FIELD_IMAGE_URL, s.imageUrl);
		cv.put(FIELD_CREATED_TIME, s.createdTime);
		cv.put(FIELD_REPLIED_TIME, s.repliedTime);
		return cv;
	}

}
