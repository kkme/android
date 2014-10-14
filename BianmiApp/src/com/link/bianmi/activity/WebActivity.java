package com.link.bianmi.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.link.bianmi.R;

public class WebActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(R.string.faq);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_web);

		WebView webview = (WebView) findViewById(R.id.webview);

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

}
