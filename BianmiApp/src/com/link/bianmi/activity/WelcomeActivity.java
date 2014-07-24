package com.link.bianmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
/**
 * 
 * @Description 首次进入的欢迎界面
 * @author pangfq
 * @date 2014年7月24日 下午4:23:55
 */
public class WelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// 点击注册
		findViewById(R.id.signup_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						launchActivity(SignUpActivity.class);
					}
				});

		// 点击登录
		findViewById(R.id.signin_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						launchActivityForResult(SignInActivity.class, 8888);
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 登录成功
		if (resultCode == 8888) {
			finishActivity();
		}
	}
}
