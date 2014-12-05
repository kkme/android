package com.link.bianmi.activity;

import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.views.Switch.OnSwitchChangeListener;
import com.link.bianmi.R;
import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnSimpleTaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.unit.ninelock.NineLockActivity;
import com.link.bianmi.unit.ninelock.NineLockSettingsActivity;
import com.link.bianmi.utils.Tools;
import com.link.bianmi.utils.UmengSocialClient;
import com.link.bianmi.widget.SuperToast;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SettingsActivity extends BaseFragmentActivity {

	// 设置密码开关
	private Switch mPassSwitchBtn = null;
	private TextView mVersionText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(getString(R.string.settings));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);

		// 设置密码
		findViewById(R.id.password_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// 之前已经设置过密码
						if (!UserConfig.getInstance().getLockPassKey()
								.isEmpty()) {
							Bundle bundle = new Bundle();
							bundle.putInt("close_lock", 2);
							launchActivityForResult(NineLockActivity.class,
									bundle, REQUEST_CODE_UPDATEPASS);

							return;
						}
						launchActivityForResult(NineLockSettingsActivity.class,
								REQUEST_CODE_SETPASS);
					}
				});
		// 设置密码开关
		mPassSwitchBtn = (Switch) findViewById(R.id.settings_item_password_switchbutton);
		changeSwitchState();
		mPassSwitchBtn.setOnSwitchChangeListener(new OnSwitchChangeListener() {
			@Override
			public void onCheckedChanged(boolean isChecked) {
				// 开启手势锁屏
				if (isChecked
						&& !UserConfig.getInstance().getLockPassKey().isEmpty()) {
					UserConfig.getInstance().setLockPassStartStatus(true);
					return;
				}
				// 关闭手势锁屏
				if (!isChecked
						&& !UserConfig.getInstance().getLockPassKey().isEmpty()) {
					Bundle bundle = new Bundle();
					bundle.putInt("close_lock", 1);
					launchActivityForResult(NineLockActivity.class, bundle,
							REQUEST_CODE_CLOSELOCK);
				}
			}
		});
		// 精品推荐
		AdManager.getInstance(this).init("89eba374087fbb60",
				"9d3ea5e812d946d1", false);
		View adGroup = findViewById(R.id.ad_group);
		adGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DiyManager.showRecommendWall(SettingsActivity.this);
			}
		});
		if (!SysConfig.getInstance().showAd()) {
			adGroup.setVisibility(View.VISIBLE);
		} else {
			adGroup.setVisibility(View.GONE);
		}
		// 常见问题
		findViewById(R.id.faq_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchActivity(WebActivity.class);
			}
		});
		// 邀请好友
		findViewById(R.id.invite_friends_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						UmengSocialClient
								.showShareDialog(SettingsActivity.this);
					}
				});
		// 检查更新
		findViewById(R.id.update_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						checkUpdate();
					}
				});
		// 当前版本名称
		mVersionText = (TextView) findViewById(R.id.version_textview);
		mVersionText.setText(String.format(getString(R.string.current_version),
				Tools.getVersionName(SettingsActivity.this)));

		// 清楚痕迹
		View clearGroup = findViewById(R.id.settings_item_clear_group);
		clearGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SettingsActivity.this);
				final AlertDialog dialog = builder
						.setMessage(getString(R.string.clear_privacy_tip))
						.setPositiveButton(
								getString(R.string.continue_to_clear),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mLoadingItem.setVisible(true);
										// 继续清除
										UserManager.Task
												.clearPrivacy(new OnSimpleTaskOverListener() {

													@Override
													public void onResult(
															int code, String msg) {
														SuperToast
																.makeText(
																		SettingsActivity.this,
																		msg,
																		SuperToast.LENGTH_SHORT)
																.show();
														mLoadingItem
																.setVisible(false);
													}
												});
									}
								})
						.setNegativeButton(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 取消
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
		});
		// 退出登录
		View exitGroup = findViewById(R.id.settings_item_exit_group);
		exitGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 退出游客登录
				if (UserConfig.getInstance().getIsGuest()) {
					UserConfig.getInstance().signOut();
					launchActivity(WelcomeActivity.class);
					ActivitysManager.removeAllActivity();
					return;
				}
				mLoadingItem.setVisible(true);
				UserManager.Task.signOut(new OnTaskOverListener<Object>() {
					@Override
					public void onFailure(int code, String msg) {
						SuperToast.makeText(SettingsActivity.this, msg,
								SuperToast.LENGTH_SHORT).show();
						mLoadingItem.setVisible(false);
					}

					@Override
					public void onSuccess(Object t) {
						mLoadingItem.setVisible(false);
						UserConfig.getInstance().signOut();
						launchActivity(WelcomeActivity.class);
						ActivitysManager.removeAllActivity();
					}
				});
			}
		});

		// 游客:立即注册
		View guestGroup = findViewById(R.id.settings_item_guest_group);
		guestGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SysConfig.getInstance().smsAccess()) {
					launchActivity(SignUpBySmsActivity.class);
				} else {
					launchActivity(SignUpActivity.class);
				}
			}
		});
		if (UserConfig.getInstance().getIsGuest()) {
			clearGroup.setVisibility(View.GONE);
			guestGroup.setVisibility(View.VISIBLE);
		} else {
			clearGroup.setVisibility(View.VISIBLE);
			guestGroup.setVisibility(View.GONE);
		}
	}

	private final int REQUEST_CODE_SETPASS = 1111;// 设置手势密码
	private final int REQUEST_CODE_CLOSELOCK = 2222;// 关闭手势密码
	private final int REQUEST_CODE_UPDATEPASS = 3333;// 修改手势密码

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((requestCode == REQUEST_CODE_SETPASS || requestCode == REQUEST_CODE_CLOSELOCK)
				&& resultCode == Activity.RESULT_OK)
			changeSwitchState();
		else if (requestCode == REQUEST_CODE_CLOSELOCK
				&& resultCode != Activity.RESULT_OK) {
			mPassSwitchBtn.setChecked(true);
		} else if (requestCode == REQUEST_CODE_UPDATEPASS
				&& resultCode == Activity.RESULT_OK) {
			launchActivity(NineLockSettingsActivity.class);
		}
	}

	private MenuItem mLoadingItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loading, menu);
		mLoadingItem = menu.findItem(R.id.action_loading);
		mLoadingItem.setVisible(false);
		return true;
	}

	// ------------------------------Private------------------------------
	private void changeSwitchState() {
		if (UserConfig.getInstance().getLockPassKey().isEmpty()) {
			mPassSwitchBtn.setVisibility(View.GONE);
			return;
		}
		mPassSwitchBtn.setVisibility(View.VISIBLE);
		mPassSwitchBtn.setChecked(UserConfig.getInstance()
				.getLockPassStartStatus());
	}

	/**
	 * 检查是否有更新
	 * 
	 * @return
	 */
	private void checkUpdate() {
		mLoadingItem.setVisible(true);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_DIALOG);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				mLoadingItem.setVisible(false);
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(SettingsActivity.this,
							updateInfo);
					break;
				case UpdateStatus.No: // has no update
					SuperToast.makeText(SettingsActivity.this, "没有更新",
							SuperToast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.NoneWifi: // none wifi
					SuperToast.makeText(SettingsActivity.this,
							"没有wifi连接， 只在wifi下更新", SuperToast.LENGTH_SHORT)
							.show();
					break;
				case UpdateStatus.Timeout: // time out
					SuperToast.makeText(SettingsActivity.this, "超时",
							SuperToast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		UmengUpdateAgent.update(this);
	}

}