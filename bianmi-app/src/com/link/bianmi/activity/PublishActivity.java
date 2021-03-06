package com.link.bianmi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.asynctask.listener.OnSimpleTaskOverListener;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.utils.SoftInputUtils;
import com.link.bianmi.widget.InputSuit;
import com.link.bianmi.widget.SuperToast;

/**
 * 发表秘密
 * 
 * @author pangfq
 * @date 2014-10-3 上午7:41:08
 */
public class PublishActivity extends BaseFragmentActivity {

	private InputSuit mInputSuit;
	private EditText mContentEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(
				getResources().getString(R.string.publish_secret_title));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_publish);

		mContentEdit = (EditText) findViewById(R.id.content_edittext);
		mContentEdit.addTextChangedListener(mTextWatcher);

		mInputSuit = (InputSuit) findViewById(R.id.input_suit);
		mInputSuit.init(this, null, mInputListener);
		mInputSuit.setMessageEdit(mContentEdit);

		SoftInputUtils.popupSoftInput(mContentEdit);
	}

	@Override
	public void onPause() {
		super.onPause();
		mInputSuit.stop();
	}

	private MenuItem mLoadingItem;
	private MenuItem mFAQItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.publish, menu);
		mLoadingItem = menu.findItem(R.id.action_loading);
		mFAQItem = menu.findItem(R.id.action_faq);
		mFAQItem.setVisible(true);
		mLoadingItem.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// 如果正在提交，则取消提交
			if (mLoadingItem.isVisible()) {
				mFAQItem.setVisible(true);
				mLoadingItem.setVisible(false);
				return false;
			}

			// 如果内容不为空
			if (!(mInputSuit.isEmpty() && mContentEdit.getText().toString()
					.isEmpty())) {
				showConfirmAbandonInputDialog();
				return false;
			}

			mInputSuit.cleanup();

			finish();
			return true;
		} else if (item.getItemId() == R.id.action_faq) {
			showFAQDialog();
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
			mFAQItem.setVisible(true);
			mLoadingItem.setVisible(false);
			return;
		}

		// 如果内容不为空
		if (!(mInputSuit.isEmpty() && mContentEdit.getText().toString()
				.isEmpty())) {
			showConfirmAbandonInputDialog();
			return;
		}

		mInputSuit.cleanup();

		super.onBackPressed();
	}

	// --------------------------------------Private----------------------------------------------

	/**
	 * 确定放弃输入吗？
	 */
	private void showConfirmAbandonInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setMessage(this.getString(R.string.confirm_abandon_input))
				.setPositiveButton(this.getString(R.string.abandon_input),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mInputSuit.cleanup();
								dialog.dismiss();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										finish();
									}
								}, 300);
							}
						})
				.setNegativeButton(this.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	/**
	 * 输入框的内容变化监听器
	 */
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
	}

	/**
	 * FAQ对话框
	 */
	private void showFAQDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setMessage(getString(R.string.publish_faq))
				.setPositiveButton(getString(R.string.ok_boring),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private InputSuit.Listener mInputListener = new InputSuit.Listener() {
		@Override
		public void onSubmit(String photoPath, String recordPath,
				int recordLen, String message) {
			mLoadingItem.setVisible(true);
			mFAQItem.setVisible(false);
			mInputSuit.startUpload();
		}

		@Override
		public void onUploadAttach(boolean result, String photoUrl,
				String recordUrl, int recordLength) {

			if (!result) {
				Log.d("bianmi", "发表失败");
				mFAQItem.setVisible(true);
				mLoadingItem.setVisible(false);
				SuperToast.makeText(PublishActivity.this,
						getString(R.string.default_failed_tip),
						SuperToast.LENGTH_SHORT).show();
				return;
			}

			Log.d("bianmi", "上传七牛成功");

			Secret secret = new Secret();
			secret.content = mContentEdit.getText().toString();
			secret.audioUrl = recordUrl;
			secret.audioLength = recordLength;
			secret.imageUrl = photoUrl;
			secret.longitude = UserConfig.getInstance().getLongitude();
			secret.latitude = UserConfig.getInstance().getLatitude();
			secret.city = UserConfig.getInstance().getCity();

			SecretManager.Task.publishSecret(secret,
					new OnSimpleTaskOverListener() {
						@Override
						public void onResult(int code, String msg) {
							mFAQItem.setVisible(true);
							mLoadingItem.setVisible(false);
							SuperToast.makeText(PublishActivity.this, msg,
									SuperToast.LENGTH_SHORT).show();
							if (code == Status_.OK)
								finish();
						}
					});
		};
	};

}
