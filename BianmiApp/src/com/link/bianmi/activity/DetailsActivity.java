package com.link.bianmi.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.adapter.SecretDetailsAdapter;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Comment;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.CommentManager;
import com.link.bianmi.widget.InputSuit;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.ActivateListener;
import com.link.bianmi.widget.RListView.TouchDirectionState;
import com.link.bianmi.widget.SuperToast;

public class DetailsActivity extends BaseFragmentActivity {

	private InputSuit mInputSuit;
	private RListView mRListView;
	private SecretDetailsAdapter mAdapter;
	private List<Comment> mCommentsList;
	private String mSecretId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBar
		getActionBar().setTitle(
				getResources().getString(R.string.details_action_title));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_details);

		final Secret secret = (Secret) getIntent().getSerializableExtra(
				"secret");

		if (secret == null)
			finish();

		mSecretId = secret.resourceId;
		// 正文内容、评论列表
		mRListView = (RListView) findViewById(R.id.rlistview);
		mRListView.setActivateListener(new ActivateListener() {
			@Override
			public void onTouchDirection(TouchDirectionState state) {
			}

			@Override
			public void onScrollUpDownChanged(int delta, int scrollPosition,
					boolean exact) {
			}

			@Override
			public void onMovedIndex(int index) {
			}

			@Override
			public void onHeadTouchActivate(boolean activate) {
			}

			@Override
			public void onHeadStop() {
			}

			@Override
			public void onHeadActivate() {
				fetchNew();
			}

			@Override
			public void onFootTouchActivate(boolean activate) {
			}

			@Override
			public void onFootStop() {
			}

			@Override
			public void onFootActivate() {
				// 菊花至少转0.8秒
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						loadMore();
						mRListView.stopFootActiving();
					}
				}, 800);
			}
		});
		mAdapter = new SecretDetailsAdapter(this, secret);
		mRListView.setAdapter(mAdapter);
		// 输入套件
		mInputSuit = (InputSuit) findViewById(R.id.input_suit);
		mInputSuit.init(this, null, mInputListener);

		fetchNew();

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

	// -----------------------------自定义---------------------------

	/**
	 * 拉取最新
	 */
	private void fetchNew() {
		executeGetCommentsTask("");
	}

	/**
	 * 加载更多
	 */
	private void loadMore() {
		if (mCommentsList != null && mCommentsList.size() > 0) {
			executeGetCommentsTask(mCommentsList.get(mCommentsList.size() - 1).resourceId);
		}
	}

	/**
	 * 执行获取评论列表任务
	 * 
	 * @param secretid
	 * @param lastid
	 */
	private void executeGetCommentsTask(String lastid) {
		final long beginTime = System.currentTimeMillis();
		if (mSecretId == null || mSecretId.isEmpty())
			return;
		CommentManager.Task.getComments(mSecretId, lastid,
				new OnTaskOverListener<ListResult<Comment>>() {
					@Override
					public void onSuccess(ListResult<Comment> t) {
						if (t != null) {
							refreshRListView(t.list, t.hasMore, beginTime);
						}
					}

					@Override
					public void onFailure(int code, String msg) {
						SuperToast.makeText(DetailsActivity.this, msg,
								SuperToast.LENGTH_SHORT).show();
					}
				});
	}

	private void refreshRListView(List<Comment> comments, boolean hasMore,
			long beginTime) {
		if (comments != null && comments.size() > 0) {
			if (mCommentsList == null) {
				mCommentsList = comments;
			} else {
				mCommentsList.addAll(comments);
			}
			mAdapter.refresh(mCommentsList);
		}
		mRListView.setFootVisiable(hasMore);
		mRListView.setEnableFooter(hasMore);
		long endTime = System.currentTimeMillis();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mRListView.stopHeadActiving();
			}
		}, endTime - beginTime > 1500 ? 0 : 1500 - (endTime - beginTime));
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
