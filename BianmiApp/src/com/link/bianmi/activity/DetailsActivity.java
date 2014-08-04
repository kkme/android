package com.link.bianmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.widget.InputSuit;

public class DetailsActivity extends BaseFragmentActivity {

	private InputSuit mInputSuit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(
				getResources().getString(R.string.details_action_title));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_details);
		
		mInputSuit = (InputSuit) findViewById(R.id.input_suit);
		mInputSuit.init(this, null, mInputListener);

	}

	private MenuItem mLoadingItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details, menu);
		mLoadingItem = menu.getItem(1);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.action_like) {
			item.setIcon(R.drawable.ic_action_liked);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mInputSuit.onActivityResult(requestCode, resultCode, data);
	}

	private InputSuit.Listener mInputListener = new InputSuit.Listener() {
		@Override
		public void onSubmit(String photoPath, String recordPath,
				int recordLen, String message, String userName, String UserId) {
			mLoadingItem.setVisible(true);
		}

		@Override
		public void onUploadAttach(boolean result, String photoUrl,
				String recordUrl) {
		};
	};

}
