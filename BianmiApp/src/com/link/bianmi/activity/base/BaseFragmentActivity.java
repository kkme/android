package com.link.bianmi.activity.base;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {

	protected void launchActivity(Class<?> cls) {
		startActivity(new Intent(this, cls));
	}

	protected void launchActivityForResult(Class<?> cls, int resultCode) {
		startActivityForResult(new Intent(this, cls), resultCode);
	}

	protected void finishActivity() {
		this.finish();
	}

	protected void finishActivityWithResult(int resultCode) {
		this.setResult(resultCode);
		this.finish();
	}

}
