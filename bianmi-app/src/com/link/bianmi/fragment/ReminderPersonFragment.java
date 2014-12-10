package com.link.bianmi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.link.bianmi.R;
import com.link.bianmi.activity.DetailsActivity;
import com.link.bianmi.activity.ReminderActivity;
import com.link.bianmi.adapter.CardsAnimationAdapter;
import com.link.bianmi.adapter.ReminderPersonAdapter;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Reminder;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.ReminderManager;
import com.link.bianmi.utils.Tools;
import com.link.bianmi.widget.NoDataView;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.OnListener;
import com.link.bianmi.widget.SuperToast;

/**
 * 我的提醒
 * 
 * @author pangfq
 * @date 2014年11月24日 下午4:38:14
 */
public class ReminderPersonFragment extends BaseFragment {

	// 根视图
	private View mRootView;
	// 列表
	private RListView mRListView;
	// 列表适配器
	private ReminderPersonAdapter mAdapter;
	private List<Reminder.Person> mDataList;

	private ReminderActivity mParentActivity;
	// 无数据
	private NoDataView mNoDataView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentActivity = (ReminderActivity) getActivity();
		mRootView = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_rlistview, null);

		mRListView = (RListView) mRootView.findViewById(R.id.rlistview);
		mAdapter = new ReminderPersonAdapter(mContext);
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				mAdapter);
		adapter.setAbsListView(mRListView);
		mRListView.setAdapter(adapter);
		mRListView.setFootVisiable(false);
		final int max_tranY = Tools.dip2px(mContext, 40);
		final View tabview = mParentActivity.getViewPagerTab();

		mRListView.setOnListener(new OnListener() {

			@Override
			public void onHeadLoading() {
				mParentActivity.getViewPagerTab().animate()
						.translationY(-Tools.dip2px(mContext, 40));
				mRListView.animate().translationY(-Tools.dip2px(mContext, 40));
				// 刷新列表
				fetchNew();
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

			@Override
			public void onHeadLoaded() {

				mParentActivity.getViewPagerTab().animate().translationY(0);
				mRListView.animate().translationY(0);
			}

			@Override
			public void onFootLoaded() {

				adapter.setShouldAnimateFromPosition(mRListView
						.getLastVisiblePosition());
			}

			@Override
			public void onScroll(int delta, int scrollPosition, boolean exact) {

				if (exact && tabview != null) {
					float tran_y = tabview.getTranslationY() + delta;
					if (tran_y >= 0) {
						tabview.setTranslationY(0);
					} else if (tran_y < -max_tranY) {
						tabview.setTranslationY(-max_tranY);
					} else {
						tabview.setTranslationY(tran_y);
					}
				}

			}
		});

		mRListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View convertView,
					int position, long arg3) {
				Reminder.Person item = (Reminder.Person) arg0
						.getItemAtPosition(position);
				if (item != null) {
					// 进入秘密详情
					Secret secret = new Secret();
					secret.resourceId = item.secretid;
					secret.content = item.content;
					secret.imageUrl = item.imageUrl;
					launchActivity(DetailsActivity.class, "secret", secret);
				}
			}
		});

		mNoDataView = (NoDataView) mRootView.findViewById(R.id.nodata_view);
		mNoDataView.setTip(R.string.nodata_tip_reminder_person);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup) mRootView.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}

		return mRootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mParentActivity.startLoading();
		fetchNew();
		super.onCreateOptionsMenu(menu, inflater);
	}

	// -------------------------Private--------------------

	/**
	 * 拉取最新
	 */
	private void fetchNew() {
		executeGetPersonRemindersTask("");
	}

	/**
	 * 加载更多
	 */
	private void loadMore() {
		if (mDataList != null && mDataList.size() > 0) {
			executeGetPersonRemindersTask(mDataList.get(mDataList.size() - 1).id);
		}
	}

	/**
	 * 刷新RListView
	 * 
	 * @param hasMore
	 *            是否还有更多
	 */
	private void refreshRListView(boolean hasMore) {
		mRListView.setFootVisiable(hasMore);
		mRListView.setEnableFooter(hasMore);
		mRListView.stopHeadLoading();
		mRListView.stopFootLoading();
	}

	/**
	 * 我的通知
	 * 
	 * @param lastId
	 *            当前页最后一条内容的id
	 * @param pageSize
	 *            页数
	 */
	private void executeGetPersonRemindersTask(final String lastId) {
		ReminderManager.Task.getPersonReminders(lastId,
				new OnTaskOverListener<ListResult<Reminder.Person>>() {
					@Override
					public void onSuccess(ListResult<Reminder.Person> t) {
						if (t != null) {
							if (mDataList == null) {
								mDataList = new ArrayList<Reminder.Person>();
							} else if (lastId == null || lastId.isEmpty()) {
								mDataList.clear();
							}
							mDataList.addAll(t.list);
							if (mDataList.size() <= 0) {
								mNoDataView.show();
							} else {
								mNoDataView.dismiss();
							}

							mAdapter.refresh(mDataList);
							refreshRListView(t.hasMore);
						}

						mParentActivity.finishLoading();
					}

					@Override
					public void onFailure(int code, String msg) {
						SuperToast.makeText(mParentActivity, msg,
								SuperToast.LENGTH_SHORT).show();
						refreshRListView(false);
						mParentActivity.finishLoading();
					}
				});
	}
}