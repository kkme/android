package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.widget.GestureLockView;
import com.link.bianmi.widget.GestureLockView.LockType;
import com.link.bianmi.widget.GestureLockView.OnGestureFinishListener;

public class LockScreenActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lockscreen);

		// 九宫格锁屏
		GestureLockView gv = (GestureLockView) findViewById(R.id.gesturelockview);
		gv.setKey(UserConfig.getInstance().getLockPassKey());
		gv.setLockType(LockType.CheckPass);
		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void onGestureFinish(int resultCode) {
				if (GestureLockView.CHECKPASS_PASSWORD_ERROR == resultCode) {
					Toast.makeText(LockScreenActivity.this, "error",
							Toast.LENGTH_SHORT).show();
				} else if (GestureLockView.CHECKPASS_PASSWORD_OK == resultCode) {
					Toast.makeText(LockScreenActivity.this, "ok",
							Toast.LENGTH_SHORT).show();
					finishActivity();
					UserConfig.getInstance().setLockPassSuccess(true);
					launchActivity(MainActivity.class);
				}
			}

			@Override
			public void onGestureStart() {

			}
		});

		// 忘记密码
		findViewById(R.id.forgot_password_textview).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 清除锁屏密码
						UserConfig.getInstance().setLockPassKey("");
						// 重新登录
						BianmiApplication.getInstance().signOut();
						launchActivity(WelcomeActivity.class);
						ActivitysManager.removeAllActivity();
					}
				});
	}

	@Override
	public void onBackPressed() {
		finishActivity();
		ActivitysManager.removeAllActivity();
	}

}
