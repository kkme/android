package com.link.bianmi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.imageloader.ImageLoader;
import com.link.bianmi.utility.ViewHolder;
import com.link.bianmi.widget.AudioButton;

public class SecretAdapter extends CursorAdapter {

	private Context mContext;
	private IndexHolder mIndexHolder;

	@SuppressWarnings("deprecation")
	public SecretAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return LayoutInflater.from(mContext).inflate(
				R.layout.secret_listview_item, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		if (mIndexHolder == null) {
			mIndexHolder = new IndexHolder();
			mIndexHolder.contentIndex = cursor
					.getColumnIndex(SecretDB.FIELD_CONTENT);
			mIndexHolder.likesIndex = cursor
					.getColumnIndex(SecretDB.FIELD_LIKES);
			mIndexHolder.imageUrlIndex = cursor
					.getColumnIndex(SecretDB.FIELD_IMAGE_URL);
			mIndexHolder.audioUrlIndex = cursor
					.getColumnIndex(SecretDB.FIELD_AUDIO_LENGTH);
			mIndexHolder.audioLengthIndex = cursor
					.getColumnIndex(SecretDB.FIELD_AUDIO_LENGTH);
		}

		TextView contentText = ViewHolder.get(view, R.id.feed_item_title);
		contentText.setText(cursor.getString(mIndexHolder.contentIndex));
		TextView likeCountText = ViewHolder.get(view, R.id.feed_item_text_info);
		likeCountText.setText(String.valueOf(mIndexHolder.likesIndex));
		ImageView pictureImage = ViewHolder.get(view, R.id.feed_item_image);
		ImageLoader.displayImage(pictureImage,
				cursor.getString(mIndexHolder.imageUrlIndex),
				R.drawable.ic_launcher, false);

		AudioButton audioBtn = ViewHolder.get(view, R.id.audio_button);
		audioBtn.setAudioFile(cursor.getString(mIndexHolder.audioUrlIndex),
				cursor.getInt(mIndexHolder.audioLengthIndex));
	}

	/**
	 * 缓存ColumnIndex
	 * 
	 */
	private class IndexHolder {
		int contentIndex;
		int likesIndex;
		int imageUrlIndex;
		int audioUrlIndex;
		int audioLengthIndex;
	}
}
