package com.link.bianmi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.entity.Comment;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.imageloader.ImageLoader;
import com.link.bianmi.utility.ViewHolder;
import com.link.bianmi.widget.AudioButton;

public class SecretDetailsAdapter extends BaseAdapter {

	private final int TYPE_SECRET = 0;// 秘密正文
	private final int TYPE_COMMENT = 1;// 评论列表

	private Context mContext;

	private Secret mSecret;
	private List<Comment> mCommentsList;

	public SecretDetailsAdapter(Context context, Secret secret) {
		mContext = context;
		mSecret = secret;
		mCommentsList = new ArrayList<Comment>();
	}

	@Override
	public int getCount() {
		return 1 + getCommentsCount();
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return mSecret;
		} else {
			return mCommentsList.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_SECRET;
		} else {
			return TYPE_COMMENT;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (convertView == null) {
			if (type == TYPE_SECRET) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.details_listview_item_secret, null);
			} else if (type == TYPE_COMMENT) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.details_listview_item_comment, null);
			} else {
				throw new RuntimeException("view type error");
			}
		}

		if (type == TYPE_SECRET) {
			bindSecretView(convertView, (Secret) getItem(position));
		} else if (type == TYPE_COMMENT) {
			bindCommentView(convertView, (Comment) getItem(position));
		}

		return convertView;
	}

	/** 评论列表 **/
	private void bindCommentView(View convertView, Comment comment) {
		// 头像
		ImageView avatarImage = ViewHolder.get(convertView,
				R.id.avatar_imageview);
		ImageLoader.displayImage(avatarImage, comment.avatarUrl,
				R.drawable.ic_comment_avatar, false);
		// 内容
		TextView contentText = ViewHolder.get(convertView,
				R.id.content_textview);
		contentText.setText(comment.content);
		// 楼层、时间、赞数
		TextView floorTimeLikesText = ViewHolder.get(convertView,
				R.id.floor_time_likes_textview);
		floorTimeLikesText.setText(String.format(
				mContext.getString(R.string.details_comments_list_item), 1,
				comment.createdTime, comment.likes));
		// 语音
		AudioButton audioButton = ViewHolder
				.get(convertView, R.id.audio_button);
		audioButton.setAudioFile(comment.audioUrl, comment.audioLength);
		// 点赞
		ImageView likedImage = ViewHolder
				.get(convertView, R.id.liked_imageview);
		if (comment.isLiked) {
			likedImage.setImageResource(R.drawable.ic_action_liked);
		} else {
			likedImage.setImageResource(R.drawable.ic_action_like);
		}
	}

	/** 秘密正文 **/
	private void bindSecretView(View convertView, Secret secret) {
		// 内容
		TextView contentText = ViewHolder.get(convertView,
				R.id.content_textview);
		contentText.setText(secret.content);
		// 语音
		AudioButton audioButton = ViewHolder
				.get(convertView, R.id.audio_button);
		audioButton.setAudioFile(secret.audioUrl, secret.audioLength);
		// 点赞数
		TextView likedText = ViewHolder.get(convertView, R.id.liked_textview);
		likedText.setText(String.valueOf(secret.likes));
		// 评论数
		TextView commentText = ViewHolder.get(convertView,
				R.id.wherefrom_textview);
		commentText.setText(String.valueOf(secret.comments));
		// 图片
		ImageView pictureImage = ViewHolder.get(convertView,
				R.id.picture_imageview);
		ImageLoader.displayImage(pictureImage, secret.imageUrl,
				R.drawable.ic_launcher, false);
		// 来自哪里
		TextView whereText = ViewHolder.get(convertView,
				R.id.wherefrom_textview);
		whereText.setText(secret.from);
	}

	private int getCommentsCount() {
		if (mCommentsList == null)
			return 0;
		return mCommentsList.size();
	}

	public void refresh(List<Comment> commentsList) {
		this.mCommentsList = commentsList;
		this.notifyDataSetChanged();
	}
}