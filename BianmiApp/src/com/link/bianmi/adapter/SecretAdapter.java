package com.link.bianmi.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.imageloader.ImageLoader;
import com.link.bianmi.utility.ViewHolder;
import com.link.bianmi.widget.AudioButton;
import com.link.bianmi.widget.SuperToast;

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
	public void bindView(View view, Context context, final Cursor cursor) {

		if (mIndexHolder == null) {
			mIndexHolder = new IndexHolder();
			mIndexHolder.resourceIdIndex = cursor
					.getColumnIndex(SecretDB.FIELD_RESOURCEID);
			mIndexHolder.contentIndex = cursor
					.getColumnIndex(SecretDB.FIELD_CONTENT);
			mIndexHolder.likesIndex = cursor
					.getColumnIndex(SecretDB.FIELD_LIKES);
			mIndexHolder.isLikedIndex = cursor
					.getColumnIndex(SecretDB.FIELD_ISLIKED);
			mIndexHolder.commentsIndex = cursor
					.getColumnIndex(SecretDB.FIELD_COMMENTS);
			mIndexHolder.imageUrlIndex = cursor
					.getColumnIndex(SecretDB.FIELD_IMAGE_URL);
			mIndexHolder.audioUrlIndex = cursor
					.getColumnIndex(SecretDB.FIELD_AUDIO_URL);
			mIndexHolder.audioLengthIndex = cursor
					.getColumnIndex(SecretDB.FIELD_AUDIO_LENGTH);
		}

		TextView contentText = ViewHolder.get(view, R.id.content_textview);
		contentText.setText(cursor.getString(mIndexHolder.contentIndex));
		final TextView likesText = ViewHolder.get(view, R.id.likes_textview);
		likesText
				.setText(String.valueOf(cursor.getInt(mIndexHolder.likesIndex)));
		likeOrDislike(likesText, cursor.getInt(mIndexHolder.isLikedIndex) > 0);
		TextView commentsText = ViewHolder.get(view, R.id.comments_textview);
		commentsText.setText(String.valueOf(mIndexHolder.commentsIndex));
		ImageView pictureImage = ViewHolder.get(view, R.id.picture_imageview);
		ImageLoader.displayImage(pictureImage,
				cursor.getString(mIndexHolder.imageUrlIndex),
				R.drawable.ic_launcher, false);

		AudioButton audioBtn = ViewHolder.get(view, R.id.audio_button);
		audioBtn.setAudioFile(cursor.getString(mIndexHolder.audioUrlIndex),
				cursor.getInt(mIndexHolder.audioLengthIndex));

		likesText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String resourceId = cursor
						.getString(mIndexHolder.resourceIdIndex);
				SecretManager.Task.likeOrDislike(resourceId,
						!(cursor.getInt(mIndexHolder.isLikedIndex) > 0),
						new OnTaskOverListener<Boolean>() {
							@Override
							public void onSuccess(Boolean t) {
								SecretManager.DB.like(resourceId, t);
								likeOrDislike(likesText, t);
								SecretAdapter.this.changeCursor(SecretManager.DB.fetch(1));
							}

							@Override
							public void onFailure(int code, String msg) {
								SuperToast.makeText(mContext, msg,
										SuperToast.LENGTH_SHORT).show();
							}
						});
			}
		});
	}

	/**
	 * 缓存ColumnIndex
	 * 
	 */
	private class IndexHolder {
		int resourceIdIndex;
		int contentIndex;
		int likesIndex;
		int isLikedIndex;
		int commentsIndex;
		int imageUrlIndex;
		int audioUrlIndex;
		int audioLengthIndex;
	}

	private void likeOrDislike(TextView likesText, boolean isliked) {
		Drawable leftDrawable = mContext.getResources().getDrawable(
				R.drawable.ic_action_like);
		if (isliked) {
			leftDrawable = mContext.getResources().getDrawable(
					R.drawable.ic_action_liked);

		}

		leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
				leftDrawable.getMinimumHeight());
		likesText.setCompoundDrawables(leftDrawable, null, null, null);
	}
}
