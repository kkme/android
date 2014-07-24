package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;

public class WelcomeActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// 注册
		findViewById(R.id.signup_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						launchActivity(SignUpActivity.class);
					}
				});

		// 登录
		findViewById(R.id.signin_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						launchActivity(SignInActivity.class);
					}
				});
	}

}
