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
import com.link.bianmi.bean.Secret;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.imageloader.ImageLoader;
import com.link.bianmi.utility.ViewHolder;

public class SecretAdapter extends CursorAdapter {

	private Context mContext;

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

		int count = cursor.getCount();
		Secret secret = SecretDB.getInstance().buildEntity(cursor);

		TextView contentText = ViewHolder.get(view, R.id.feed_item_title);
		contentText.setText(secret.getContent());
		TextView likeCountText = ViewHolder.get(view, R.id.feed_item_text_info);
		likeCountText.setText(String.valueOf(secret.getLikeCount()));
		ImageView pictureImage = ViewHolder.get(view, R.id.feed_item_image);
		new ImageLoader().displayImage(pictureImage, secret.getImageUrl(),
				R.drawable.ic_launcher, false);
	}
}
