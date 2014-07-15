package com.link.bianmi.fragment;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.bean.Secret;
import com.link.bianmi.fragment.base.TaskFragment;
import com.link.bianmi.manager.SecretManager;
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
	private SecretManager mSecretManager;
	private FinalBitmap mFinalBitmap;

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
		mSecretManager = getFeedsManager();
		if (mSecretManager == null)
			return;
		mFinalBitmap = ((MainActivity) getActivity()).getFinalBitmap();

		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				new SecretAdapter());
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
				mSecretManager.updateFirstPage();
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
						mSecretManager.updateNextPage();
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
				((MainActivity) getActivity()).showImageFragment(imageView,
						true, mSecretManager.getFeedItems().get(position - 1));
			}
		});
		mListView.post(new Runnable() {
			@Override
			public void run() {
				if (mSecretManager.loadDbData()) {
					mListView.notifyDataSetChanged();
				} else {
					mListView.startUpdateImmediate();
				}
			}
		});

		loadFromCache();
		updateCache();

		initScrollListener();
	}

	// -------------------------实现基类方法------------------
	@Override
	protected TaskResult onTaskBackground(TaskParams... params) {
		return null;
	}

	/**
	 * 后台任务结束后更新UI *
	 */
	@Override
	protected void onTaskDoneUI(TaskResult result) {
	}

	// -------------------------自定义方法--------------------
	/**
	 * 从缓存中加载数据初始化界面
	 */
	private void loadFromCache() {

	}

	/**
	 * 更新缓存，具体是否需要更新在后台根据时间戳判断
	 */
	private void updateCache() {

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

	protected SecretManager getFeedsManager() {
		return null;
	}

	private class SecretAdapter extends BaseAdapter {

		public SecretAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return mSecretManager.getFeedItems().size();
		}

		@Override
		public Secret getItem(int position) {
			return mSecretManager.getFeedItems().get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.secret_listview_item, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.feed_item_title);
				holder.info = (TextView) convertView
						.findViewById(R.id.feed_item_text_info);
				holder.image = (ImageView) convertView
						.findViewById(R.id.feed_item_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Secret item = mSecretManager.getFeedItems().get(position);
			if (item != null) {
				if (item.getCaption() != null) {
					holder.title.setText(item.getCaption());
				} else {
					holder.title.setText("unknown caption");
				}
				mFinalBitmap.display(holder.image, item.getImages_normal());
				holder.info.setText(String.valueOf(item.getLikeCount()));
			}

			return convertView;
		}

		class ViewHolder {
			public TextView title;
			public TextView info;
			public ImageView image;
		}

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

}
