package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;

public class SettingsActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("设置");
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);

		// 退出登录
		findViewById(R.id.settings_item_exit_group).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						UserConfig.getInstance().setSessionId("");
						UserConfig.getInstance().setIsGuest(false);
						launchActivity(WelcomeActivity.class);
						finishActivityWithResult(6666);
					}
				});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
}
