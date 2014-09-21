package com.link.bianmi.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.link.bianmi.activity.ActivitysManager;

public class BaseFragmentActivity extends FragmentActivity {

	@Override
	public void onResume() {
		super.onResume();
		ActivitysManager.onResume(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivitysManager.onDestory(this);
	}

	protected void launchActivity(Class<?> cls) {
		startActivity(new Intent(this, cls));
	}

	protected void launchActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent(this, cls);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void launchActivityForResult(Class<?> cls, int requestCode) {
		startActivityForResult(new Intent(this, cls), requestCode);
	}

	protected void launchActivityForResult(Class<?> cls, Bundle bundle,
			int requestCode) {
		Intent intent = new Intent(this, cls);
		intent.putExtras(bundle);
		startActivityForResult(intent, requestCode);
	}

	protected void finishActivityWithResult(int resultCode) {
		this.setResult(resultCode);
		this.finish();
	}

}
