package com.link.bianmi.activity;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.adapter.ReminderAdapter;
import com.link.bianmi.entity.Comment;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.ActivateListener;
import com.link.bianmi.widget.RListView.TouchDirectionState;

/**
 * 通知
 * 
 * @author pangfq
 * @date 2014-11-23 上午11:17:45
 */
public class ReminderActivity extends BaseFragmentActivity {

	private RListView mRListView;
	private ReminderAdapter mAdapter;
	private List<Comment> mCommentsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBar
		getActionBar().setTitle(getResources().getString(R.string.reminder));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_reminder);

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
		mAdapter = new ReminderAdapter(this);
		mRListView.setAdapter(mAdapter);

		fetchNew();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loading, menu);
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

	// --------------------------Task-------------------------------
	/**
	 * 执行获取评论列表任务
	 * 
	 * @param commentid
	 * @param lastid
	 */
	private void executeGetCommentsTask(final String lastid) {
	}

}