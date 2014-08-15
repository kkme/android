package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.widget.GestureLockView;
import com.link.bianmi.widget.GestureLockView.OnGestureFinishListener;

public class LockScreenActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lockscreen);

		// 九宫格锁屏
		GestureLockView gv = (GestureLockView) findViewById(R.id.gesturelockview);
		gv.setKey("0124678");
		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void OnGestureFinish(boolean success) {
				Toast.makeText(LockScreenActivity.this,
						String.valueOf(success), Toast.LENGTH_SHORT).show();
			}
		});

		// 忘记密码
		findViewById(R.id.forgot_password_textview).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 清除锁屏密码
						// .....
						// 重新登录
						BianmiApplication.getInstance().signOut();
						launchActivity(WelcomeActivity.class);
						ActivitysManager.removeAllActivity();
					}
				});
	}

}
