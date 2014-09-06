package com.link.bianmi.fragment;

import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.activity.DetailsActivity;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.adapter.SecretAdapter;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.entity.manager.SecretManager.SecretType;
import com.link.bianmi.fragment.base.TaskFragment;
import com.link.bianmi.utility.SystemBarTintUtil;
import com.link.bianmi.utility.Tools;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.ActivateListener;
import com.link.bianmi.widget.RListView.TouchDirectionState;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class SecretFragment extends TaskFragment {

	private RListView mRListView;
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

	private SecretManager.SecretType mSecretType;

	@Override
	public View _onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_secrets, null);

		mRListView = (RListView) view.findViewById(R.id.rlistview);
		initInsetTop(mRListView);

		mAdapter = new SecretAdapter(getActivity(), null);
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				mAdapter);
		adapter.setAbsListView(mRListView);
		mRListView.setAdapter(adapter);
		final int max_tranY = Tools.dip2px(getActivity(), 48);
		final View tabview = ((MainActivity) getActivity()).getViewPagerTab();

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
				((MainActivity) getActivity()).getViewPagerTab().animate()
						.translationY(-Tools.dip2px(getActivity(), 48));
				mRListView.animate().translationY(
						-Tools.dip2px(getActivity(), 48));

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mRListView.stopHeadActiving();
					}
				}, 2000);

			}

			@Override
			public void onFootTouchActivate(boolean activate) {

			}

			@Override
			public void onFootActivate() {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mRListView.stopFootActiving();
					}
				}, 2000);

			}

			@Override
			public void onHeadStop() {

				((MainActivity) getActivity()).getViewPagerTab().animate()
						.translationY(0);
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
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.feed_item_image);
				if (imageView.getDrawable() == null
						|| imageView.getDrawable().getIntrinsicWidth() == 0) {
					Toast.makeText(getActivity(), "Please wait...",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Object item = arg0.getItemAtPosition(position);
				if (item instanceof Cursor) {
					Secret secret = SecretDB.getInstance().buildEntity(
							(Cursor) item);
					if (secret != null) {
						launchActivity(DetailsActivity.class, "secret", secret);
					}
				}
			}
		});

		mSecretType = getSecretType();

		loadCache();
		updateCache();

		return view;
	}

	// -------------------------实现基类方法------------------
	// @Override
	protected TaskResult onTaskBackground(TaskParams... params) {
		TaskParams param = params[0];
		TaskType taskType = (TaskType) param.get(TASKPARAMS_TYPE);

		SecretType secretType = (SecretType) param.get(TASKPARAMS_SECRET_TYPE);
		String resultMsg = "";
		TaskResult.TaskStatus resultStatu = TaskResult.TaskStatus.OK;

		try {
			if (taskType == TaskType.RefreshAll) {
				List<Secret> secretsList = SecretManager.API
						.getSecrets(secretType);
				SecretManager.DB.addSecrets(secretsList);
				Cursor cursor = SecretManager.DB.fetch();
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
		((MainActivity) getActivity()).finishLoaded();
	}

	// -------------------------自定义方法--------------------
	/**
	 * 从缓存中加载数据初始化界面
	 */
	private void loadCache() {

		Cursor cursor = SecretManager.DB.fetch();
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

	private void initInsetTop(View headView) {
		SystemBarTintUtil tintManager = new SystemBarTintUtil(getActivity());
		SystemBarTintUtil.SystemBarConfig config = tintManager.getConfig();
		headView.setPadding(0, config.getPixelInsetTop(true),
				config.getPixelInsetRight(), config.getPixelInsetBottom());
		headView.requestLayout();
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

	protected SecretManager.SecretType getSecretType() {
		return null;
	}
}
