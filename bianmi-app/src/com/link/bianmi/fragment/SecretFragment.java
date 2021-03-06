package com.link.bianmi.fragment;

import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.DetailsActivity;
import com.link.bianmi.activity.HomeActivity;
import com.link.bianmi.activity.SignUpActivity;
import com.link.bianmi.activity.SignUpBySmsActivity;
import com.link.bianmi.adapter.CardsAnimationAdapter;
import com.link.bianmi.adapter.SecretAdapter;
import com.link.bianmi.asynctask.listener.OnSimpleTaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.db.SecretDB;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.ContactsManager;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.entity.manager.SecretManager.TaskType;
import com.link.bianmi.utils.Tools;
import com.link.bianmi.widget.NoDataView;
import com.link.bianmi.widget.RListView;
import com.link.bianmi.widget.RListView.OnListener;
import com.link.bianmi.widget.SuperToast;

/**
 * 秘密列表
 * 
 * @author pangfq
 * @date 2014-10-7 下午8:39:43
 */
public abstract class SecretFragment extends BaseFragment {

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

	protected HomeActivity mParentActivity;
	// 无数据
	protected NoDataView mNoDataView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentActivity = (HomeActivity) getActivity();
		if (getTaskType() == TaskType.GET_FRIENDS
				&& UserConfig.getInstance().getIsGuest()) {
			mRootView = LayoutInflater.from(mContext).inflate(
					R.layout.guest_secrets_layout, null);
			// 快去注册吧
			mRootView.findViewById(R.id.signup_button).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (SysConfig.getInstance().smsAccess()) {
								launchActivity(SignUpBySmsActivity.class);
							} else {
								launchActivity(SignUpActivity.class);
							}
						}
					});
			return;
		}
		mRootView = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_secrets, null);

		mRListView = (RListView) mRootView.findViewById(R.id.rlistview);
		mAdapter = new SecretAdapter(mContext, null, getTaskType());
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

				if (UserConfig.getInstance().getIsGuest()) {
					mParentActivity
							.showGuestTipDialog(getString(R.string.guest_action_comments_msg));
					return;
				}
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

		if (UserConfig.getInstance().hasSignined()) {
			// 上传联系人
			ContactsManager.Task.uploadContacts(getActivity(),
					new OnSimpleTaskOverListener() {
						@Override
						public void onResult(int code, String msg) {
							SuperToast.makeText(mParentActivity, msg,
									SuperToast.LENGTH_SHORT).show();
						}
					});
		}

		mNoDataView = (NoDataView) mRootView.findViewById(R.id.nodata_view);
		mNoDataView.show();

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
		if (getTaskType() == TaskType.GET_FRIENDS
				&& UserConfig.getInstance().getIsGuest())
			return;

		mParentActivity.startLoading();
		loadCache();
		updateCache();

		super.onCreateOptionsMenu(menu, inflater);
	}

	// ------------------------------Abstract------------------------------
	abstract SecretManager.TaskType getTaskType();

	// ------------------------------Protected------------------------------
	protected boolean isFirstFragment() {

		return false;

	}

	// ------------------------------Private------------------------------

	/**
	 * 从缓存中加载数据初始化界面
	 */
	private void loadCache() {
		refreshRListView(SecretManager.DB.fetch(mPageSize, getTaskType()),
				false, -1);

	}

	/**
	 * 更新缓存，具体是否需要更新在后台根据时间戳判断
	 */
	private void updateCache() {
		fetchNew();
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
		// 还没有秘密
		if (cursor == null || cursor.getCount() <= 0) {
			mAdapter.changeCursor(null);
			mAdapter.notifyDataSetChanged();
			mNoDataView.show();
			return;
		}

		mNoDataView.dismiss();

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
				mRListView.stopHeadLoading();
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
							SecretManager.DB.cleanSecret(getTaskType());
						}

						// 刷新列表
						if (listResult != null && listResult.list != null
								&& listResult.list.size() >= 0) {
							mSecretsList = listResult.list;
							SecretManager.DB.addSecrets(listResult.list,
									getTaskType());
							refreshRListView(SecretManager.DB.fetch(pageSize,
									getTaskType()), listResult.hasMore,
									beginTime);
						}
						mRListView.stopHeadLoading();
						mParentActivity.finishLoaded(false);
					}

					@Override
					public void onFailure(int code, String msg) {
						mRListView.stopHeadLoading();
						mParentActivity.finishLoaded(true);
						SuperToast.makeText(mContext, msg,
								SuperToast.LENGTH_SHORT).show();
						// mNoDataView.dismiss();
					}
				}, getTaskType());
	}

}