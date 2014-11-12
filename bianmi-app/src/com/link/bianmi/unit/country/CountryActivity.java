package com.link.bianmi.unit.country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.unit.country.SideBar.OnTouchListener;

public class CountryActivity extends BaseFragmentActivity {
	private ListView mSortListView;
	private SideBar mLetterSideBar;
	private TextView mLetterDialog;
	private SortAdapter mAdapter;
	private EditText mClearEditText;

	private CharacterParser mCharacterParser;
	private List<Country> mCountryLists;

	private PinyinComparator mPinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(
				getResources().getString(R.string.country_or_area));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_country);
		mCharacterParser = CharacterParser.getInstance();

		mPinyinComparator = new PinyinComparator();

		mLetterSideBar = (SideBar) findViewById(R.id.sidrbar);
		mLetterDialog = (TextView) findViewById(R.id.dialog);
		mLetterSideBar.setTextView(mLetterDialog);
		mLetterSideBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public void onActionMove(String letter) {
				int position = mAdapter.getPositionForSection(letter.charAt(0));
				if (position != -1) {
					mSortListView.setSelection(position);
				}
			}

			@Override
			public void onActionUp() {
			}

			@Override
			public void onActionDown() {
			}
		});

		mSortListView = (ListView) findViewById(R.id.country_lvcountry);
		mSortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("country", (Country) mAdapter.getItem(position));
				CountryActivity.this.setResult(RESULT_OK, intent);
				CountryActivity.this.finish();
			}
		});

		mCountryLists = new ArrayList<Country>();
		for (int i = R.array.country_group_a; i <= R.array.country_group_z; i++) {
			mCountryLists.addAll(getData(i, getResources().getStringArray(i)));
		}
		mAdapter = new SortAdapter(this, mCountryLists);
		mSortListView.setAdapter(mAdapter);

		mClearEditText = (EditText) findViewById(R.id.filter_edit);

		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private List<Country> getData(int index, String[] array) {
		List<Country> mSortList = new ArrayList<Country>();

		char a = 'A';
		a += Math.abs(R.array.country_group_a - index);
		for (int i = 0; i < array.length; i++) {
			Country country = new Country();
			country.name = array[i].substring(0, array[i].indexOf(","));
			country.code = "+"
					+ array[i].substring(array[i].indexOf(",") + 1,
							array[i].lastIndexOf(","));
			country.letter = String.valueOf(a);
			mSortList.add(country);
		}

		return mSortList;
	}

	private void filterData(String filterStr) {
		List<Country> filterDateList = new ArrayList<Country>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mCountryLists;
		} else {
			filterDateList.clear();
			for (Country country : mCountryLists) {
				String name = country.name;
				if (name.indexOf(filterStr.toString()) != -1
						|| mCharacterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(country);
				}
			}
		}

		Collections.sort(filterDateList, mPinyinComparator);
		mAdapter.updateListView(filterDateList);
	}

}