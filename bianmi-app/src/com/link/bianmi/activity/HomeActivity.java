package com.link.bianmi.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.adapter.ViewPagerAdapter;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Reminder;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.entity.manager.ConfigManager;
import com.link.bianmi.entity.manager.ReminderManager;
import com.link.bianmi.fragment.FriendFragment;
import com.link.bianmi.fragment.HotFragment;
import com.link.bianmi.fragment.ImageFragment;
import com.link.bianmi.fragment.NearbyFragment;
import com.link.bianmi.utils.UmengSocialClient;
import com.link.bianmi.widget.SuperToast;
import com.link.bianmi.widget.ViewPagerTabBar;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class HomeActivity extends BaseFragmentActivity {
	public ViewPager mViewPager;
	private ViewPagerTabBar mViewPagerTab;
	private ImageFragment mImageFragment;
	private ListPopupWindow mMenuWindow;
	private MenuAdapter mMenuAdapter;
	private ArrayList<Fragment> mFragments;
	private Reminder mReminder;
	private final int REQUEST_CODE_REMINDER = 1111;// 查看提醒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 检查更新
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
		UmengUpdateAgent.update(this);

		// 初始化Push
		XGPushConfig.enableDebug(this, true);
		Context context = getApplicationContext();
		XGPushManager.registerPush(context);
		Intent service = new Intent(context, XGPushService.class);
		context.startService(service);

		// 初始化ActionBar
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(true);
		setContentView(R.layout.activity_home);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPagerTab = (ViewPagerTabBar) findViewById(R.id.viewpagertab);
		mFragments = new ArrayList<Fragment>();

		mFragments.add(new HotFragment());
		mFragments.add(new FriendFragment());
		mFragments.add(new NearbyFragment());
		String fragmentTitles[] = new String[] {
				this.getResources().getString(R.string.hot),
				this.getResources().getString(R.string.friend),
				this.getResources().getString(R.string.nearby) };
		mViewPager.setOffscreenPageLimit(mFragments.size());
		mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				mFragments, fragmentTitles));

		mViewPagerTab.setViewPager(mViewPager);
		mImageFragment = (ImageFragment) getSupportFragmentManager()
				.findFragmentById(R.id.main_image_fragment);
		mViewPagerTab.post(new Runnable() {
			@Override
			public void run() {
				getSupportFragmentManager().beginTransaction()
						.hide(mImageFragment).commit();
			}
		});

		executeGetSysConfigTask();
		executeGetReminderTask();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mMenuAdapter != null) {
			mMenuAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_REMINDER) {
			mReminder = null;
			mReminderItem.setIcon(R.drawable.ab_ic_reminder);
		}
	}

	private MenuItem mMoreItem;
	private MenuItem mLoadingItem;
	private MenuItem mReminderItem;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mImageFragment != null) {

			menu.findItem(R.id.action_add)
					.setVisible(!mImageFragment.canBack());
			menu.findItem(R.id.action_reminder).setVisible(
					!mImageFragment.canBack());
			if (mLoadingItem.isVisible()) {
				mMoreItem.setVisible(false);
			} else {
				mMoreItem.setVisible(!mImageFragment.canBack());
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private boolean mFragmentsLoaded = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		mMoreItem = menu.findItem(R.id.action_more);
		mLoadingItem = menu.findItem(R.id.action_loading);
		mReminderItem = menu.findItem(R.id.action_reminder);
		if (mReminder == null) {
			mReminder = new Reminder();
		}
		mReminderItem
				.setIcon(mReminder.hasReminder ? R.drawable.ab_ic_reminder_has
						: R.drawable.ab_ic_reminder);
		mLoadingItem.setVisible(false);
		mMoreItem.setVisible(true);
		if (!mFragmentsLoaded) {
			for (int i = 0; i < mFragments.size(); i++) {
				mFragments.get(i).onCreateOptionsMenu(menu, inflater);
			}
			mFragmentsLoaded = true;
		}

		return true;// 返回true，否则不显示Menu
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			if (UserConfig.getInstance().getIsGuest()) {
				showGuestTipDialog(getString(R.string.guest_action_publis_msg));
			} else {
				launchActivity(PublishActivity.class);
			}
		} else if (item.getItemId() == R.id.action_reminder) {
			if (UserConfig.getInstance().getIsGuest()) {
				showGuestTipDialog(getString(R.string.guest_action_reminder_msg));
			} else {
				launchActivityForResult(ReminderActivity.class,
						REQUEST_CODE_REMINDER);
			}
		} else if (item.getItemId() == R.id.action_more) {
			showMoreOptionMenu(findViewById(R.id.action_more));
		}
		return true;
	}

	long mLastBackPressedTime = 0;

	@Override
	public void onBackPressed() {
		// 如果正在加载，则取消加载
		if (mLoadingItem.isVisible()) {
			finishLoaded(true);
			return;
		}

		if (mImageFragment.canBack()) {
			mImageFragment.goBack();

		} else {
			long cur_time = System.currentTimeMillis();

			if ((cur_time - mLastBackPressedTime) < 1000) {
				super.onBackPressed();
			} else {
				mLastBackPressedTime = cur_time;
				SuperToast.makeText(HomeActivity.this,
						R.string.press_back_again_to_exit,
						SuperToast.LENGTH_SHORT).show();
			}

		}
	}

	// ------------------------------Private------------------------------

	/**
	 * 更多菜单
	 */
	private void showMoreOptionMenu(View view) {
		if (mMenuWindow != null) {
			mMenuWindow.dismiss();
			mMenuWindow = null;
		}
		mMenuWindow = new ListPopupWindow(this);
		if (mMenuAdapter == null) {
			mMenuAdapter = new MenuAdapter();
		}
		mMenuWindow.setModal(true);
		mMenuWindow.setContentWidth(getResources().getDimensionPixelSize(
				R.dimen.popupmenu_dialog_width));
		mMenuWindow.setAdapter(mMenuAdapter);
		mMenuWindow.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					UmengSocialClient.showShareDialog(HomeActivity.this);
					break;
				case 1:
					launchActivityForResult(SettingsActivity.class, 6666);
					break;
				default:
					break;
				}
				if (mMenuWindow != null) {
					mMenuWindow.dismiss();
					mMenuWindow = null;
				}
			}

		});
		mMenuWindow.setAnchorView(view);
		mMenuWindow.show();
		mMenuWindow.getListView().setDividerHeight(1);
	}

	private class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.popup_menu_item, null);
			TextView name = (TextView) convertView.findViewById(R.id.tv_name);
			int iconResId = R.drawable.ab_menu_ic_share;

			if (position == 0) {
				name.setText(R.string.invite_friends);
				iconResId = R.drawable.ab_menu_ic_share;
			} else if (position == 1) {
				name.setText(R.string.settings);
				iconResId = R.drawable.ab_menu_ic_settings;
			}
			Drawable drawable = getResources().getDrawable(iconResId);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			name.setCompoundDrawables(drawable, null, null, null);
			return convertView;
		}
	}

	// ------------------------------Public------------------------------
	/**
	 * 游客提醒对话框
	 */
	public void showGuestTipDialog(String msg) {
		if (msg == null || msg.isEmpty())
			return;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder
				.setMessage(msg)
				.setPositiveButton(getString(R.string.guest_go_signup),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (SysConfig.getInstance().smsAccess()) {
									launchActivity(SignUpBySmsActivity.class);
								} else {
									launchActivity(SignUpActivity.class);
								}
							}
						}).create();
		dialog.show();
	}

	public View getViewPagerTab() {
		return mViewPagerTab;
	}

	/**
	 * 结束加载
	 * 
	 * @param isStopAtOnce
	 *            true 立即结束
	 */
	public void finishLoaded(boolean isStopAtOnce) {

		if (mLoadingItem == null || mMoreItem == null)
			return;

		if (isStopAtOnce) {
			mLoadingItem.getActionView().clearAnimation();
			mLoadingItem.setVisible(false);
			mMoreItem.setVisible(true);
			return;
		}
		AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setDuration(1000);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				finishLoaded(true);
			}
		});
		mLoadingItem.getActionView().setAnimation(anim);
	}

	/**
	 * 开始加载
	 */
	public void startLoading() {
		if (mLoadingItem == null || mMoreItem == null)
			return;
		mMoreItem.setVisible(false);
		mLoadingItem.setVisible(true);

	}

	/**
	 * 放大图片
	 * 
	 * @param smallImageView
	 * @param show
	 * @param item
	 */
	public void showImageFragment(ImageView smallImageView, boolean show,
			Secret item) {
		if (show) {
			getSupportFragmentManager().beginTransaction().show(mImageFragment)
					.commit();
			mImageFragment.startScaleAnimation(smallImageView, item);
		} else {
			getSupportFragmentManager().beginTransaction().hide(mImageFragment)
					.commit();
		}

	}

	// ------------------------------Task------------------------------
	/**
	 * 初始化系统配置
	 */
	private void executeGetSysConfigTask() {
		ConfigManager.Task.getConfig();
	}

	/**
	 * 获取提醒的Task
	 */
	private void executeGetReminderTask() {
		ReminderManager.Task.getReminder(new OnTaskOverListener<Reminder>() {

			@Override
			public void onSuccess(Reminder t) {
				mReminder = t;
				if (t != null && mReminderItem != null) {
					mReminderItem
							.setIcon(mReminder.hasReminder ? R.drawable.ab_ic_reminder_has
									: R.drawable.ab_ic_reminder);
				}
			}

			@Override
			public void onFailure(int code, String msg) {

			}
		});
	}
}