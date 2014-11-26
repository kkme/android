package com.link.bianmi.fragment;

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
import com.link.bianmi.activity.ReminderActivity;
import com.link.bianmi.adapter.CardsAnimationAdapter;
import com.link.bianmi.adapter.ReminderSystemAdapter;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Reminder;
import com.link.bianmi.entity.manager.ReminderManager;
import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.utility.Tools;
import com.link.bianmi.widget.NoDataView;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.ActivateListener;
import com.link.bianmi.widget.RListView.TouchDirectionState;

/**
 * 系统通知
 * 
 * @author pangfq
 * @date 2014年11月24日 下午4:38:25
 */
public class ReminderSystemFragment extends BaseFragment {

	// 根视图
	private View mRootView;
	// 列表
	private RListView mRListView;
	// 列表适配器
	private ReminderSystemAdapter mAdapter;
	// 当前页中最后一条内容的ID
	private String mLastId = "";

	private List<Reminder.System> mDataList;

	private ReminderActivity mParentActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentActivity = (ReminderActivity) getActivity();
		mRootView = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_rlistview, null);

		mRListView = (RListView) mRootView.findViewById(R.id.rlistview);
		mAdapter = new ReminderSystemAdapter(mContext);
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				mAdapter);
		adapter.setAbsListView(mRListView);
		mRListView.setAdapter(adapter);
		mRListView.setFootVisiable(false);
		final int max_tranY = Tools.dip2px(mContext, 40);
		final View tabview = mParentActivity.getViewPagerTab();

		mRListView.setActivateListener(new ActivateListener() {

			@Override
			public void onTouchDirection(TouchDirectionState state) {

			}

			@Override
			public void onMovedIndex(int index) {

			}

			@Override
			public void onHeadTouchActivate(boolean activate) {

			}

			@Override
			public void onHeadActivate() {
				mParentActivity.getViewPagerTab().animate()
						.translationY(-Tools.dip2px(mContext, 40));
				mRListView.animate().translationY(-Tools.dip2px(mContext, 40));
				// 刷新列表
				fetchNew();
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

			@Override
			public void onFootTouchActivate(boolean activate) {

			}

			@Override
			public void onHeadStop() {

				mParentActivity.getViewPagerTab().animate().translationY(0);
				mRListView.animate().translationY(0);
			}

			@Override
			public void onFootStop() {

				adapter.setShouldAnimateFromPosition(mRListView
						.getLastVisiblePosition());
			}

			@Override
			public void onScrollUpDownChanged(int delta, int scrollPosition,
					boolean exact) {

				if (exact) {
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
			}
		});

		NoDataView noDataView = (NoDataView) mRootView
				.findViewById(R.id.nodata_view);
		noDataView.show(R.string.nodata_tip_reminder_system);

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
		mLastId = "";
		executeGetSystemRemindersTask(mLastId);
	}

	/**
	 * 加载更多
	 */
	private void loadMore() {
		if (mDataList != null && mDataList.size() > 0) {
			mLastId = mDataList.get(mDataList.size() - 1).id;
			executeGetSystemRemindersTask(mLastId);
		}
	}

	/**
	 * 刷新RListView
	 * 
	 * @param hasMore
	 *            是否还有更多
	 */
	private void refreshRListView(boolean hasMore, long beginTime) {
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

	/**
	 * 系统通知
	 * 
	 * @param lastId
	 *            当前页最后一条内容的id
	 * @param pageSize
	 *            页数
	 */
	private void executeGetSystemRemindersTask(final String lastId) {
		final long beginTime = System.currentTimeMillis();
		ReminderManager.Task.getSystemReminders(lastId,
				new OnTaskOverListener<ListResult<Reminder.System>>() {
					@Override
					public void onSuccess(ListResult<Reminder.System> t) {
						if (t != null) {
							mAdapter.refresh(t.list);
							refreshRListView(t.hasMore, beginTime);
						}
					}

					@Override
					public void onFailure(int code, String msg) {

					}
				});
	}

}