package com.link.bianmi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.link.bianmi.R;
import com.link.bianmi.entity.Reminder;

/**
 * "我的提醒"列表 适配器
 */
public class ReminderPersonAdapter extends BaseAdapter {
	private List<Reminder.Person> mDataList;

	private Context mContext;

	public ReminderPersonAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList == null ? null : mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.reminder_person_listview_item, null);
		return null;
	}

	public void refresh(List<Reminder.Person> dataList) {
		mDataList = dataList;
		this.notifyDataSetChanged();
	}
}