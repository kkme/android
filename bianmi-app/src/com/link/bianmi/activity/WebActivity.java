package com.link.bianmi.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.link.bianmi.R;
import com.link.bianmi.entity.WebUrl;

public class WebActivity extends FragmentActivity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebUrl webUrl = (WebUrl) getIntent().getSerializableExtra("weburl");
		if (webUrl == null || webUrl.url.isEmpty())
			finish();

		getActionBar().setTitle(webUrl.title);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_web);

		mWebView = (WebView) findViewById(R.id.webview);

		initWebViewSettings();
		mWebView.loadUrl(webUrl.url);

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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	// ----------------------------Private-----------------------------------

	private void initWebViewSettings() {

		WebSettings setting = mWebView.getSettings();
		setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		setting.setJavaScriptEnabled(true);
		setting.setSupportZoom(true);
		setting.setDomStorageEnabled(true);
		setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		setting.setBuiltInZoomControls(false);

		mWebView.requestFocus();
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);

		mWebView.setWebChromeClient(new WebChrome());

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				mLoadingItem.setVisible(true);
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mLoadingItem.setVisible(true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mLoadingItem.setVisible(false);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.loadData("", "text/html", "UTF-8");
				mLoadingItem.setVisible(false);
			}
		});
	}

	public class WebChrome extends WebChromeClient {

	}

}
