package com.link.bianmi.activity.base;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {

	protected void launchActivity(Class<?> cls) {
		startActivity(new Intent(this, cls));
	}

}
