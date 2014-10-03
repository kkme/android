package com.link.bianmi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.widget.InputSuit;

/**
 * 发表秘密
 * 
 * @author pangfq
 * @date 2014-10-3 上午7:41:08
 */
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
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 弹出软键盘
				InputMethodManager inputManager = (InputMethodManager) mContentEdit
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mContentEdit, 0);
			}
		}, 500);
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
			mInputSuit.startUpload();
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

			if (!result) {
				Toast.makeText(AddActivity.this, "发表失败！", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Toast.makeText(AddActivity.this, "上传七牛成功！", Toast.LENGTH_SHORT)
					.show();

			Secret secret = new Secret();
			secret.userId = UserConfig.getInstance().getUserId();
			secret.content = mContentEdit.getText().toString();
			secret.audioUrl = recordUrl;
			secret.imageUrl = photoUrl;
			secret.createdAt = System.currentTimeMillis();

			SecretManager.Task.addSecret(secret,
					new OnTaskOverListener<Secret>() {

						@Override
						public void onSuccess(Secret t) {
							Toast.makeText(AddActivity.this, "发表成功!",
									Toast.LENGTH_SHORT).show();
							mLoadingItem.setVisible(false);
							mSendItem.setVisible(true);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									finish();
								}
							}, 500);
						}

						@Override
						public void onFailure(int code, String msg) {
							Toast.makeText(AddActivity.this, "发表失败!",
									Toast.LENGTH_SHORT).show();
							mLoadingItem.setVisible(false);
							mSendItem.setVisible(true);
						}
					});

		};
	};

}
