package com.link.bianmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.link.bianmi.R;

public class SplashActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		View contentView = new View(this);
		contentView.setBackgroundResource(R.drawable.bg_splash);
		setContentView(contentView);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				finish();
			}
		}, 1000);

	}
}
