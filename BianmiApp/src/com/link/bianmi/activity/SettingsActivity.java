package com.link.bianmi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.widget.SwitchButton;

public class SettingsActivity extends BaseFragmentActivity {

	// 设置密码开关
	SwitchButton mPassSwitchBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("设置");
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);

		// 设置密码
		findViewById(R.id.password_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						launchActivityForResult(SettingLockPassActivity.class,
								REQUEST_CODE_SETPASS);
					}
				});
		// 设置密码开关
		mPassSwitchBtn = (SwitchButton) findViewById(R.id.settings_item_password_switchbutton);
		mPassSwitchBtn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// UserConfig.getInstance().setIsLockPassSeted(isChecked);
						// launchActivity(LockScreenActivity.class);
					}
				});
		changeSwitchButtonState();

		// 退出登录
		findViewById(R.id.settings_item_exit_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						UserManager.Task
								.signOut(new OnTaskOverListener<Object>() {
									@Override
									public void onFailure(int code, String msg) {
										Toast.makeText(
												SettingsActivity.this,
												"SignOut Error!" + "code:"
														+ code + ",msg:" + msg,
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onSuccess(Object t) {
										BianmiApplication.getInstance()
												.signOut();
										launchActivity(WelcomeActivity.class);
										ActivitysManager.removeAllActivity();
									}
								});
					}
				});
	}

	private final int REQUEST_CODE_SETPASS = 1111;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE_SETPASS
				&& resultCode == Activity.RESULT_OK)
			changeSwitchButtonState();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}

	// -------------------------------自定义方法
	private void changeSwitchButtonState() {
		if (UserConfig.getInstance().getLockPassKey().isEmpty()) {
			mPassSwitchBtn.setVisibility(View.GONE);
			mPassSwitchBtn.setChecked(false);
			return;
		}
		mPassSwitchBtn.setVisibility(View.VISIBLE);
		mPassSwitchBtn.setChecked(true);
	}
}
