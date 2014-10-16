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

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.activity.DetailsActivity;
import com.link.bianmi.activity.HomeActivity;
import com.link.bianmi.adapter.CardsAnimationAdapter;
import com.link.bianmi.adapter.SecretAdapter;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.utility.SystemBarTintUtil;
import com.link.bianmi.utility.Tools;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.ActivateListener;
import com.link.bianmi.widget.RListView.TouchDirectionState;
import com.link.bianmi.widget.SuperToast;

/**
 * 秘密列表
 * 
 * @author pangfq
 * @date 2014-10-7 下午8:39:43
 */
public class SecretFragment extends BaseFragment {

	// 根视图
	private View mRootView;
	// 列表
	private RListView mRListView;
	// 列表适配器
	private SecretAdapter mAdapter;
	// 当前页中最后一条内容的ID
	private String mLastId = "";
	// 列表的页数
	private int mPageSize = 1;

	private List<Secret> mSecretsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRootView = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_secrets, null);

		final View bannerGroup = mRootView.findViewById(R.id.banner_group);
		bannerGroup.setVisibility(View.GONE);

		mRListView = (RListView) mRootView.findViewById(R.id.rlistview);
		mAdapter = new SecretAdapter(mContext, null);
		final CardsAnimationAdapter adapter = new CardsAnimationAdapter(
				mAdapter);
		adapter.setAbsListView(mRListView);
		mRListView.setAdapter(adapter);
		mRListView.setFootVisiable(false);
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
				// 刷新列表
				fetchNew();
				if (SysConfig.getInstance().showAd()) {
					bannerGroup.setVisibility(View.VISIBLE);
				} else {
					bannerGroup.setVisibility(View.GONE);
				}

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
				bannerGroup.setVisibility(View.GONE);
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
						.findViewById(R.id.picture_imageview);
				if (imageView.getDrawable() == null
						|| imageView.getDrawable().getIntrinsicWidth() == 0) {
					SuperToast.makeText(mContext, "Please wait...",
							SuperToast.LENGTH_SHORT).show();
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
		refreshRListView(SecretManager.DB.fetch(mPageSize), false, -1);

	}

	/**
	 * 更新缓存，具体是否需要更新在后台根据时间戳判断
	 */
	private void updateCache() {
		fetchNew();
	}

	private void initInsetTop(View headView) {
		SystemBarTintUtil tintManager = new SystemBarTintUtil(mContext);
		SystemBarTintUtil.SystemBarConfig config = tintManager.getConfig();
		headView.setPadding(0, config.getPixelInsetTop(true),
				config.getPixelInsetRight(), config.getPixelInsetBottom());
		headView.requestLayout();
	}

	/**
	 * 拉取最新
	 */
	private void fetchNew() {
		mLastId = "";
		mPageSize = 1;
		executeGetSecretsTask(mLastId, mPageSize);
	}

	/**
	 * 加载更多
	 */
	private void loadMore() {
		if (mSecretsList != null && mSecretsList.size() > 0) {
			mLastId = mSecretsList.get(mSecretsList.size() - 1).resourceId;
			mPageSize++;
			executeGetSecretsTask(mLastId, mPageSize);
		}
	}

	/**
	 * 刷新RListView
	 * 
	 * @param cursor
	 *            游标
	 * @param hasMore
	 *            是否还有更多
	 */
	private void refreshRListView(Cursor cursor, boolean hasMore, long beginTime) {
		if (cursor != null) {
			mAdapter.changeCursor(cursor);
			mAdapter.notifyDataSetChanged();
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

	/**
	 * 执行获取秘密列表数据的异步任务
	 * 
	 * @param lastId
	 *            当前页最后一条内容的id
	 * @param pageSize
	 *            页数
	 */
	private void executeGetSecretsTask(final String lastId, final int pageSize) {
		final long beginTime = System.currentTimeMillis();
		SecretManager.Task.getSecrets(lastId,
				new OnTaskOverListener<ListResult<Secret>>() {
					@Override
					public void onSuccess(ListResult<Secret> listResult) {

						// 清空数据库
						if (lastId != null && lastId.isEmpty()) {
							SecretManager.DB.cleanSecret();
						}

						// 刷新列表
						if (listResult != null && listResult.list != null
								&& listResult.list.size() > 0) {
							mSecretsList = listResult.list;
							SecretManager.DB.addSecrets(listResult.list);
							refreshRListView(SecretManager.DB.fetch(pageSize),
									listResult.hasMore, beginTime);
						}
						((HomeActivity) mContext).finishLoaded(false);

					}

					@Override
					public void onFailure(int code, String msg) {
						mRListView.stopHeadActiving();
						((HomeActivity) mContext).finishLoaded(true);
						SuperToast.makeText(mContext, msg,
								SuperToast.LENGTH_SHORT).show();
					}
				});
	}

}
