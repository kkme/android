package com.link.bianmi.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.adapter.SecretDetailsAdapter;
import com.link.bianmi.bean.Comment;
import com.link.bianmi.bean.Secret;
import com.link.bianmi.widget.InputSuit;
import com.link.bianmi.widget.RListView;

public class DetailsActivity extends BaseFragmentActivity {

	private InputSuit mInputSuit;
	private RListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBar
		getActionBar().setTitle(
				getResources().getString(R.string.details_action_title));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_details);
		
		Secret secret = (Secret)getIntent().getSerializableExtra("secret");
		
		// 正文内容、评论列表
		mListView = (RListView) findViewById(R.id.rlistview);
		SecretDetailsAdapter adapter = new SecretDetailsAdapter(this, secret);
		ArrayList<Comment> commentsList = new ArrayList<Comment>(); 
		for(int i = 0; i < 20; i++){
			Comment comment = new Comment();
			comment.setAudioLength(60);
			comment.setAudioUrl("http://");
			comment.setAvatarImageUrl("http://www.3gmfw.cn/qqtouxiang/UploadPic/2012-9/20129921285294.jpg");
			comment.setContent("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
			comment.setLikeCount(300);
			commentsList.add(comment);
		}
		adapter.setCommentsList(commentsList);
		mListView.setAdapter(adapter);
		mListView.setOnTopRefreshListener(new RListView.OnTopRefreshListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onEnd() {
			}

			@Override
			public void onDoinBackground() {
			}
		});
		mListView
				.setOnBottomRefreshListener(new RListView.OnBottomRefreshListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onEnd() {
					}

					@Override
					public void onDoinBackground() {
					}
				});

		// 输入套件
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
