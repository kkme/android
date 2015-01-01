package com.link.bianmi.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.adapter.SecretDetailsAdapter;
import com.link.bianmi.asynctask.listener.OnSimpleTaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Comment;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.manager.CommentManager;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.utils.UmengSocialClient;
import com.link.bianmi.widget.AudioCircleButton;
import com.link.bianmi.widget.InputSuit;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.OnListener;
import com.link.bianmi.widget.SuperToast;

public class DetailsActivity extends BaseFragmentActivity {

	private InputSuit mInputSuit;
	private RListView mRListView;
	private SecretDetailsAdapter mAdapter;
	private List<Comment> mCommentsList;

	private Secret mSecret;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBar
		getActionBar().setTitle(
				getResources().getString(R.string.details_action_title));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_details);

		mSecret = (Secret) getIntent().getSerializableExtra("secret");

		if (mSecret == null)
			finish();

		// 正文内容、评论列表
		mRListView = (RListView) findViewById(R.id.rlistview);
		mRListView.setOnListener(new OnListener() {
			@Override
			public void onScroll(int delta, int scrollPosition, boolean exact) {
			}

			@Override
			public void onHeadLoaded() {
			}

			@Override
			public void onHeadLoading() {
				fetchNew();
			}

			@Override
			public void onFootLoaded() {
			}

			@Override
			public void onFootLoading() {
				// 菊花至少转0.8秒
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						loadMore();
						mRListView.stopFootLoading();
					}
				}, 800);
			}
		});
		mAdapter = new SecretDetailsAdapter(this, mSecret);
		mRListView.setAdapter(mAdapter);
		// 输入套件
		mInputSuit = (InputSuit) findViewById(R.id.input_suit);
		mInputSuit.init(this, null, mInputListener);
		mRListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mInputSuit.close();
			}
		});
		mRListView.setFootVisiable(false);

	}

	@Override
	public void onPause() {
		super.onPause();
		AudioCircleButton.stopPlay();
		mInputSuit.stop();
	}

	private MenuItem mLikeItem;
	private MenuItem mLoadingItem;
	private MenuItem mShareItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details, menu);
		mLikeItem = menu.findItem(R.id.action_like);
		if (mSecret != null) {
			likeOrDislike(mSecret.isLiked);
		}
		mShareItem = menu.findItem(R.id.action_share);
		mLoadingItem = menu.findItem(R.id.action_loading);
		mLoadingItem.setVisible(true);
		mShareItem.setVisible(false);

		fetchNew();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// 如果内容不为空
			if (!mInputSuit.isEmpty()) {
				showConfirmAbandonInputDialog();
				return false;
			}

			mInputSuit.cleanup();

			finish();
			return true;
		} else if (item.getItemId() == R.id.action_like) {
			SecretManager.Task.likeOrDislike(mSecret.resourceId,
					!mSecret.isLiked, new OnTaskOverListener<Boolean>() {
						@Override
						public void onSuccess(Boolean t) {
							likeOrDislike(t);
							mSecret.isLiked = t;
						}

						@Override
						public void onFailure(int code, String msg) {
							SuperToast.makeText(DetailsActivity.this, msg,
									SuperToast.LENGTH_SHORT).show();
						}
					});
		} else if (item.getItemId() == R.id.action_share) {
			UmengSocialClient.showShareDialog(this);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mInputSuit.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// 如果内容不为空
		if (!mInputSuit.isEmpty()) {
			showConfirmAbandonInputDialog();
			return;
		}

		mInputSuit.cleanup();

		super.onBackPressed();
	}

	// -------------------------------Private-------------------------------
	/**
	 * 确定放弃输入吗？
	 */
	private void showConfirmAbandonInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setMessage(this.getString(R.string.confirm_abandon_input))
				.setPositiveButton(this.getString(R.string.abandon_input),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mInputSuit.cleanup();
								dialog.dismiss();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										finish();
									}
								}, 300);
							}
						})
				.setNegativeButton(this.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void likeOrDislike(boolean isliked) {
		if (isliked) {
			mLikeItem.setIcon(getResources()
					.getDrawable(R.drawable.ab_ic_liked));
		} else {
			mLikeItem
					.setIcon(getResources().getDrawable(R.drawable.ab_ic_like));
		}
	}

	/**
	 * 拉取最新
	 */
	private void fetchNew() {
		executeGetCommentsTask("");
		executeSecretDetailsTask();
	}

	/**
	 * 加载更多
	 */
	private void loadMore() {
		if (mCommentsList != null && mCommentsList.size() > 0) {
			executeGetCommentsTask(mCommentsList.get(mCommentsList.size() - 1).resourceId);
		}
	}

	private InputSuit.Listener mInputListener = new InputSuit.Listener() {
		@Override
		public void onSubmit(String photoPath, String recordPath,
				int recordLen, String message) {
			mLoadingItem.setVisible(true);
			mShareItem.setVisible(false);
			mInputSuit.startUpload();
		}

		@Override
		public void onUploadAttach(boolean result, String photoUrl,
				String recordUrl, int recordLength) {

			if (!result) {
				SuperToast.makeText(DetailsActivity.this, "发表失败！",
						SuperToast.LENGTH_SHORT).show();
				return;
			}

			SuperToast.makeText(DetailsActivity.this, "上传七牛成功！",
					SuperToast.LENGTH_SHORT).show();

			Comment comment = new Comment();
			comment.secretid = mSecret.resourceId;
			comment.userid = UserConfig.getInstance().getUserId();
			comment.content = mInputSuit.getMessage();
			comment.audioUrl = recordUrl;
			comment.audioLength = recordLength;
			comment.createdTime = System.currentTimeMillis();

			CommentManager.Task.publishComment(comment,
					new OnSimpleTaskOverListener() {
						@Override
						public void onResult(int code, String msg) {
							SuperToast.makeText(DetailsActivity.this, msg,
									SuperToast.LENGTH_SHORT).show();
							// 发表成功
							if (code == Status_.OK) {
								mLoadingItem.setVisible(false);
								mShareItem.setVisible(true);
								mInputSuit.reset();
								fetchNew();
							} else {
								mLoadingItem.setVisible(false);
								mShareItem.setVisible(true);
							}
						}
					});
		};
	};

	// --------------------------Task-------------------------------
	/**
	 * 执行获取秘密详情的任务
	 */
	private void executeSecretDetailsTask() {
		if (mSecret == null || mSecret.resourceId.isEmpty())
			return;
		SecretManager.Task.details(mSecret.resourceId,
				new OnTaskOverListener<Secret>() {
					@Override
					public void onSuccess(Secret t) {
						if (t == null)
							return;
						mSecret = t;
						likeOrDislike(mSecret.isLiked);
						mAdapter.refresh(null, t);
						mRListView.stopHeadLoading();
					}

					@Override
					public void onFailure(int code, String msg) {
						SuperToast.makeText(DetailsActivity.this, msg,
								SuperToast.LENGTH_SHORT).show();
						mRListView.stopHeadLoading();
					}
				});
	}

	/**
	 * 执行获取评论列表任务
	 * 
	 * @param commentid
	 * @param lastid
	 */
	private void executeGetCommentsTask(final String lastid) {
		if (mSecret == null || mSecret.resourceId.isEmpty())
			return;
		CommentManager.Task.getComments(mSecret.resourceId, lastid,
				new OnTaskOverListener<ListResult<Comment>>() {
					@Override
					public void onSuccess(ListResult<Comment> t) {
						if (t == null || t.list == null)
							return;
						// 每次刷新都要先清空列表
						if (lastid.isEmpty() && mCommentsList != null) {
							mCommentsList.clear();
						}
						// 暂无评论
						if (lastid.isEmpty() && t.list.size() <= 0) {
							mCommentsList = new ArrayList<Comment>();
							mCommentsList.add(new Comment());
							mRListView.setFootVisiable(false);
							mRListView.setEnableFooter(false);
						} else {
							if (mCommentsList == null) {
								mCommentsList = t.list;
							} else {
								mCommentsList.addAll(t.list);
							}

							mRListView.setFootVisiable(t.hasMore);
							mRListView.setEnableFooter(t.hasMore);
						}

						mAdapter.refresh(mCommentsList, null);
						mRListView.stopHeadLoading();
						mShareItem.setVisible(true);
						mLoadingItem.setVisible(false);
					}

					@Override
					public void onFailure(int code, String msg) {
						SuperToast.makeText(DetailsActivity.this, msg,
								SuperToast.LENGTH_SHORT).show();
						mRListView.stopHeadLoading();
						mShareItem.setVisible(true);
						mLoadingItem.setVisible(false);
					}
				});
	}

}