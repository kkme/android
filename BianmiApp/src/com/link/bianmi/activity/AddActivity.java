package com.link.bianmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.widget.InputSuit;

public class AddActivity extends BaseFragmentActivity {

	private InputSuit mInputSuit;
	private EditText mContentEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(
				getResources().getString(R.string.add_action_title));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_add);

		mInputSuit = (InputSuit) findViewById(R.id.input_suit);
		mInputSuit.init(this, null, mInputListener);

		mContentEdit = (EditText) findViewById(R.id.content_edittext);
		mContentEdit.addTextChangedListener(mTextWatcher);
	}

	private MenuItem mSendItem;
	private MenuItem mLoadingItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add, menu);
		mSendItem = menu.getItem(0);
		mLoadingItem = menu.getItem(1);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.action_send) {
			item.setVisible(false);
			mLoadingItem.setVisible(true);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mInputSuit.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// 如果正在提交，则取消提交
		if (mLoadingItem.isVisible()) {
			mLoadingItem.setVisible(false);
			mSendItem.setVisible(true);
			return;
		}

		super.onBackPressed();
	}

	/** 监听标题输入框的内容变化 **/
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			checkEnableSubmit();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};

	/** 是否允许提交内容 **/
	private void checkEnableSubmit() {

		boolean enable = false;

		// 文本内容长度大于0
		if (!enable) {
			if (mContentEdit.getText().toString().trim().length() > 0) {
				enable = true;
			}
		}

		boolean currentStatus = mSendItem.isEnabled();
		if (enable != currentStatus) {
			mSendItem.setEnabled(enable);
		}

	}

	private InputSuit.Listener mInputListener = new InputSuit.Listener() {
		@Override
		public void onSubmit(String photoPath, String recordPath,
				int recordLen, String message, String userName, String UserId) {
		}

		@Override
		public void onUploadAttach(boolean result, String photoUrl,
				String recordUrl) {
		};
	};

}
