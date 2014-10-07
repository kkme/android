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
import android.widget.ImageView;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.activity.DetailsActivity;
import com.link.bianmi.activity.HomeActivity;
import com.link.bianmi.adapter.CardsAnimationAdapter;
import com.link.bianmi.adapter.SecretAdapter;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.utility.SystemBarTintUtil;
import com.link.bianmi.utility.Tools;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.ActivateListener;
import com.link.bianmi.widget.RListView.TouchDirectionState;

public class SecretFragment extends BaseFragment {

	// 根视图
	private View mRootView;
	// 列表
	private RListView mRListView;
	// 列表适配器
	private SecretAdapter mAdapter;
	// 当前页中最后一条内容的ID
	private String mLastId = "";
	// 当前页码
	private int mCurrentPage = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRootView = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_secrets, null);

		mRListView = (RListView) mRootView.findViewById(R.id.rlistview);
		mAdapter = new SecretAdapter(mContext, null);
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				mAdapter);
		adapter.setAbsListView(mRListView);
		mRListView.setAdapter(adapter);
		final int max_tranY = Tools.dip2px(mContext, 48);
		final View tabview = ((HomeActivity) mContext).getViewPagerTab();

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
				((HomeActivity) mContext).getViewPagerTab().animate()
						.translationY(-Tools.dip2px(mContext, 48));
				mRListView.animate().translationY(-Tools.dip2px(mContext, 48));
				long pretime = System.currentTimeMillis();
				// 刷新列表
				refreshList();
				// 菊花至少转1.5秒
				long suftime = System.currentTimeMillis();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mRListView.stopHeadActiving();
					}
				}, suftime - pretime > 1500 ? 0 : 1500);
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

				((HomeActivity) mContext).getViewPagerTab().animate()
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
					Toast.makeText(mContext, "Please wait...",
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

		if (isFirstFragment()) {
			onFirstLoad();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup) mRootView.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}

		initInsetTop(mRListView);
		return mRootView;
	}

	@Override
	public void onFirstLoad() {
		loadCache();
		updateCache();
	}

	// --------------------------protected----------------
	protected SecretManager.TaskType getTaskType() {
		return null;
	}

	protected boolean isFirstFragment() {

		return false;

	}

	// -------------------------private--------------------

	/**
	 * 从缓存中加载数据初始化界面
	 */
	private void loadCache() {
		Cursor cursor = SecretManager.DB.fetch(mCurrentPage);
		refreshRListView(cursor);

	}

	/**
	 * 更新缓存，具体是否需要更新在后台根据时间戳判断
	 */
	private void updateCache() {

		executeTask("");
	}

	private void initInsetTop(View headView) {
		SystemBarTintUtil tintManager = new SystemBarTintUtil(mContext);
		SystemBarTintUtil.SystemBarConfig config = tintManager.getConfig();
		headView.setPadding(0, config.getPixelInsetTop(true),
				config.getPixelInsetRight(), config.getPixelInsetBottom());
		headView.requestLayout();
	}

	private void refreshList() {
		executeTask("");
	}

	private void loadMore() {
		executeTask(mLastId);
	}

	/**
	 * 刷新RListView
	 * 
	 */
	private void refreshRListView(Cursor cursor) {
		if (cursor != null) {
			mAdapter.changeCursor(cursor);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void executeTask(final String startId) {

		SecretManager.Task.getSecrets(startId,
				new OnTaskOverListener<List<Secret>>() {
					@Override
					public void onSuccess(List<Secret> t) {
						mCurrentPage++;
						if (t != null && t.size() > 0) {
							mLastId = t.get(t.size() - 1).id;
						}

						if (startId != null && startId.isEmpty()) {
							SecretManager.DB.cleanSecret();
						}
						SecretManager.DB.addSecrets(t);
						Cursor cursor = SecretManager.DB.fetch(mCurrentPage);
						refreshRListView(cursor);
						((HomeActivity) mContext).finishLoaded(false);
					}

					@Override
					public void onFailure(int code, String msg) {
						((HomeActivity) mContext).finishLoaded(false);
					}
				});
	}

}
