package com.link.bianmi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.link.bianmi.R;
import com.link.bianmi.entity.Comment;
import com.link.bianmi.entity.Secret;

/**
 * 通知适配器
 * 
 * @author pangfq
 * @date 2014-11-23 上午11:21:58
 */
public class ReminderAdapter extends BaseAdapter {

	private Context mContext;

	private Secret mSecret;
	private List<Comment> mCommentsList;

	public ReminderAdapter(Context context) {
		mContext = context;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.details_listview_item_comment, null);
		}

		return convertView;
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