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
import com.link.bianmi.entity.Secret;
import com.link.bianmi.imageloader.ImageLoader;
import com.link.bianmi.utility.ViewHolder;
import com.link.bianmi.widget.AudioButton;

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
		contentText.setText(secret.content);
		TextView likeCountText = ViewHolder.get(view, R.id.feed_item_text_info);
		likeCountText.setText(String.valueOf(secret.likes));
		ImageView pictureImage = ViewHolder.get(view, R.id.feed_item_image);
		new ImageLoader().displayImage(pictureImage, secret.imageUrl,
				R.drawable.ic_launcher, false);
		
		AudioButton audioBtn = ViewHolder.get(view, R.id.audio_button);
		audioBtn.setAudioFile("http://bianmi.qiniudn.com//secret/audio/ffa02859b5214856bae1b5ae8ec3ba5f_1412342833184.amr?attname=&e=1412429240&token=6uoNKRkDEm7rUXoMoEMXffVW8nuKCfW1RRXATji4:4mFgnqJUHtMq5DguLcrUQWdtBoY", 120);
	}
}
