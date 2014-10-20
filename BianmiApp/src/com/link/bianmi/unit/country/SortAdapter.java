package com.link.bianmi.unit.country;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.utility.ViewHolder;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<Country> list = null;
	private Context mContext;

	public SortAdapter(Context mContext, List<Country> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public void updateListView(List<Country> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		final Country mContent = list.get(position);
		view = LayoutInflater.from(mContext).inflate(
				R.layout.country_list_item, null);
		TextView titleText = ViewHolder.get(view, R.id.title);
		TextView letterText = ViewHolder.get(view, R.id.catalog);

		int section = getSectionForPosition(position);

		if (position == getPositionForSection(section)) {
			letterText.setVisibility(View.VISIBLE);
			letterText.setText(mContent.letter);
		} else {
			letterText.setVisibility(View.GONE);
		}

		titleText.setText(this.list.get(position).name);

		return view;

	}

	public int getSectionForPosition(int position) {
		return list.get(position).letter.charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).letter;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}