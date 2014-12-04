package com.link.bianmi.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.entity.Reminder;
import com.link.bianmi.utils.ViewHolder;

/**
 * "系统提醒"列表 适配器
 */
public class ReminderSystemAdapter extends BaseAdapter {
	private List<Reminder.System> mDataList;

	private Context mContext;
	private FinalBitmap mFBitmap;

	public ReminderSystemAdapter(Context context) {
		mContext = context;
		mFBitmap = FinalBitmap.create(context);
		mFBitmap.configLoadingImage(R.drawable.ic_launcher);
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
					R.layout.reminder_system_listview_item, null);
		ImageView imageView = ViewHolder.get(convertView, R.id.imageview);
		TextView titleTextView = ViewHolder.get(convertView,
				R.id.title_textview);
		TextView subtitleTextView = ViewHolder.get(convertView,
				R.id.subtitle_textview);
		if (mDataList == null || mDataList.size() <= 0)
			return null;
		Reminder.System systemReminder = mDataList.get(position);
		if (systemReminder != null) {
			mFBitmap.display(imageView, systemReminder.imageUrl);
			titleTextView.setText(systemReminder.title);
			subtitleTextView.setText(systemReminder.subtitle);
		}
		return convertView;
	}

	public void refresh(List<Reminder.System> dataList) {
		mDataList = dataList;
		this.notifyDataSetChanged();
	}
}