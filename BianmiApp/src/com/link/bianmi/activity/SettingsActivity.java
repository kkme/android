package com.link.bianmi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.unit.ninelock.NineLockActivity;
import com.link.bianmi.unit.ninelock.NineLockSettingsActivity;
import com.link.bianmi.widget.SuperToast;
import com.link.bianmi.widget.SwitchButton;

public class SettingsActivity extends BaseFragmentActivity {

	// 设置密码开关
	SwitchButton mPassSwitchBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(getString(R.string.settings));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);

		// 设置密码
		findViewById(R.id.password_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						launchActivityForResult(NineLockSettingsActivity.class,
								REQUEST_CODE_SETPASS);
					}
				});
		// 设置密码开关
		mPassSwitchBtn = (SwitchButton) findViewById(R.id.settings_item_password_switchbutton);
		changeSwitchButtonState();
		mPassSwitchBtn
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 开启手势锁屏
						if (isChecked
								&& !UserConfig.getInstance().getLockPassKey()
										.isEmpty()) {
							UserConfig.getInstance().setLockPassStartStatus(
									true);
							return;
						}
						// 关闭手势锁屏
						if (!isChecked
								&& !UserConfig.getInstance().getLockPassKey()
										.isEmpty()) {
							Bundle bundle = new Bundle();
							bundle.putBoolean("close_lock", true);
							launchActivityForResult(NineLockActivity.class,
									bundle, REQUEST_CODE_CLOSELOCK);
						}
					}
				});
		// 退出登录
		findViewById(R.id.settings_item_exit_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mLoadingItem.setVisible(true);
						UserManager.Task
								.signOut(new OnTaskOverListener<Object>() {
									@Override
									public void onFailure(int code, String msg) {
										SuperToast.makeText(
												SettingsActivity.this,
												"SignOut Error!" + "code:"
														+ code + ",msg:" + msg,
												SuperToast.LENGTH_SHORT).show();
										mLoadingItem.setVisible(false);
									}

									@Override
									public void onSuccess(Object t) {
										mLoadingItem.setVisible(false);
										BianmiApplication.getInstance()
												.signOut();
										launchActivity(WelcomeActivity.class);
										ActivitysManager.removeAllActivity();
									}
								});
					}
				});
	}

	private final int REQUEST_CODE_SETPASS = 1111;// 设置手势密码
	private final int REQUEST_CODE_CLOSELOCK = 2222;// 关闭手势密码

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((requestCode == REQUEST_CODE_SETPASS || requestCode == REQUEST_CODE_CLOSELOCK)
				&& resultCode == Activity.RESULT_OK)
			changeSwitchButtonState();
		else if (requestCode == REQUEST_CODE_CLOSELOCK
				&& resultCode != Activity.RESULT_OK) {
			mPassSwitchBtn.setChecked(true);
		}
	}

	private MenuItem mLoadingItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loading, menu);
		mLoadingItem = menu.findItem(R.id.action_loading);
		mLoadingItem.setVisible(false);
		return true;
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
			return;
		}
		mPassSwitchBtn.setVisibility(View.VISIBLE);
		mPassSwitchBtn.setChecked(UserConfig.getInstance()
				.getLockPassStartStatus());

	}
}
