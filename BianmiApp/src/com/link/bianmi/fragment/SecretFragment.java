package com.link.bianmi.fragment;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.link.bianmi.R;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.adapter.SecretAdapter;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.bean.Secret;
import com.link.bianmi.bean.helper.SecretHelper;
import com.link.bianmi.bean.helper.SecretHelper.SecretType;
import com.link.bianmi.fragment.base.TaskFragment;
import com.link.bianmi.utility.SystemBarTintUtil;
import com.link.bianmi.utility.ToastUtil;
import com.link.bianmi.utility.UiUtil;
import com.link.bianmi.widget.ListViewScrollObserver;
import com.link.bianmi.widget.ListViewScrollObserver.OnListViewScrollListener;
import com.link.bianmi.widget.RListView;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class SecretFragment extends TaskFragment {

	private RListView mListView;
	private Context mContext;
	private SecretAdapter mAdapter;
	/**
	 * 任务类型*
	 */
	private final String TASKPARAMS_TYPE = "taskparams_type";
	private final String TASKPARAMS_PAGE = "taskparams_page";
	private final String TASKPARAMS_SECRET_TYPE = "taskparams_secret_type";

	enum TaskType {
		RefreshAll,

		LoadNext
	}

	private SecretHelper.SecretType mSecretType;

	@Override
	public View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mListView = (RListView) inflater.inflate(R.layout.rlistview, null);
		initInsetTop(mListView);
		return mListView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		mAdapter = new SecretAdapter(mContext, null);
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				mAdapter);
		adapter.setAbsListView(mListView);
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
						adapter.setShouldAnimateFromPosition(mListView
								.getLastVisiblePosition());
					}

					@Override
					public void onDoinBackground() {
					}
				});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View convertView,
					int position, long arg3) {
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.feed_item_image);
				if (imageView.getDrawable() == null
						|| imageView.getDrawable().getIntrinsicWidth() == 0) {
					ToastUtil.showToast(getActivity(), "Please wait...");
					return;
				}
			}
		});

		initScrollListener();

		mSecretType = getSecretType();

		loadCache();
		updateCache();
	}

	// -------------------------实现基类方法------------------
	@Override
	protected TaskResult onTaskBackground(TaskParams... params) {
		TaskParams param = params[0];
		TaskType taskType = (TaskType) param.get(TASKPARAMS_TYPE);

		SecretType secretType = (SecretType) param.get(TASKPARAMS_SECRET_TYPE);
		String resultMsg = "";
		TaskResult.TaskStatus resultStatu = TaskResult.TaskStatus.OK;

		try {
			if (taskType == TaskType.RefreshAll) {
				List<Secret> secretsList = SecretHelper.API
						.getSecrets(secretType);
				SecretHelper.DB.addSecrets(secretsList);
				Cursor cursor = SecretHelper.DB.fetch();
				return new TaskResult(resultStatu, resultMsg, taskType, cursor,
						secretsList, 1);
			} else if (taskType == TaskType.LoadNext) {
				// int page = Integer.parseInt(param.getString(TASKPARAMS_PAGE,
				// "1"));
				// Tmodel<Secret[]> tm = SecretHelper.API.getSecretsArray(page);
				// SecretHelper.DB.addSecret(tm.t);
				// Cursor cursor = SecretHelper.DB.fetch();
				// return new TaskResult(resultStatu, resultMsg, taskType,
				// cursor,
				// tm.currentPage, tm.total);
			}
		} catch (Exception ex) {
			resultStatu = TaskResult.TaskStatus.FAILED;
		}
		return new TaskResult(resultStatu, resultMsg, taskType);
	}

	/**
	 * 后台任务结束后更新UI *
	 */
	@Override
	protected void onTaskDoneUI(TaskResult result) {

		Cursor cursor = (Cursor) result.getValues()[1];
		mAdapter.changeCursor(cursor);
		mAdapter.notifyDataSetChanged();

	}

	// -------------------------自定义方法--------------------
	/**
	 * 从缓存中加载数据初始化界面
	 */
	private void loadCache() {

		Cursor cursor = SecretHelper.DB.fetch();
		mAdapter.changeCursor(cursor);
		mAdapter.notifyDataSetChanged();

	}

	/**
	 * 更新缓存，具体是否需要更新在后台根据时间戳判断
	 */
	private void updateCache() {

		TaskParams params = new TaskParams();
		params.put(TASKPARAMS_TYPE, TaskType.RefreshAll);
		params.put(TASKPARAMS_SECRET_TYPE, mSecretType);
		doTask(params);

	}

	private void initInsetTop(View rootView) {
		SystemBarTintUtil tintManager = new SystemBarTintUtil(getActivity());
		SystemBarTintUtil.SystemBarConfig config = tintManager.getConfig();
		rootView.setPadding(0, config.getPixelInsetTop(true),
				config.getPixelInsetRight(), config.getPixelInsetBottom());
		rootView.requestLayout();
	}

	private void initScrollListener() {
		final int max_tranY = UiUtil.dip2px(mContext, 48);
		final View tabview = ((MainActivity) getActivity()).getViewPagerTab();
		ListViewScrollObserver observer = new ListViewScrollObserver(mListView);
		observer.setOnScrollUpAndDownListener(new OnListViewScrollListener() {

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

			@Override
			public void onScrollIdle() {
			}
		});
	}

	public void refresh() {
		mListView.startUpdateImmediate();
	}

	class CardsAnimationAdapter extends AnimationAdapter {
		private float mTranslationY = 400;

		private float mRotationX = 15;

		private long mDuration = 400;

		public CardsAnimationAdapter(BaseAdapter baseAdapter) {
			super(baseAdapter);
		}

		@Override
		protected long getAnimationDelayMillis() {
			return 30;
		}

		@Override
		protected long getAnimationDurationMillis() {
			return mDuration;
		}

		@Override
		public Animator[] getAnimators(ViewGroup parent, View view) {
			return new Animator[] {
					ObjectAnimator.ofFloat(view, "translationY", mTranslationY,
							0),
					ObjectAnimator.ofFloat(view, "rotationX", mRotationX, 0) };
		}
	}

	protected SecretHelper.SecretType getSecretType() {
		return null;
	}
}
