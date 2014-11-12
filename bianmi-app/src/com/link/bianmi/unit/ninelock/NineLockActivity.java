package com.link.bianmi.unit.ninelock;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.link.bianmi.BianmiApplication;
import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.ActivitysManager;
import com.link.bianmi.activity.HomeActivity;
import com.link.bianmi.activity.WelcomeActivity;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.unit.ninelock.NineLockView.Cell;
import com.link.bianmi.unit.ninelock.NineLockView.DisplayMode;
import com.link.bianmi.widget.SuperToast;

/**
 * 九宫格密码锁屏
 * 
 * @author pangfq
 * @date 2014-9-17 下午10:31:14
 */
public class NineLockActivity extends BaseFragmentActivity implements
		NineLockView.OnPatternListener {
	private List<Cell> lockPattern;
	private NineLockView nineLockView;

	private boolean mCloseLock = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lockPattern = NineLockView.stringToPattern(UserConfig.getInstance()
				.getLockPassKey());
		setContentView(R.layout.activity_ninelock);
		nineLockView = (NineLockView) findViewById(R.id.ninelockview);
		nineLockView.setOnPatternListener(this);

		mCloseLock = false;
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mCloseLock = bundle.getBoolean("close_lock");
		}

		// 忘记手势密码
		findViewById(R.id.forget_textview).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								NineLockActivity.this);
						final AlertDialog dialog = builder
								.setTitle(
										getString(R.string.forget_ninelock_password_dialog_tip))
								.setPositiveButton(
										getString(R.string.signin_again),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// 清空手势密码
												UserConfig.getInstance()
														.setLockPassKey("");
												// 跳转登录页面
												BianmiApplication.getInstance()
														.signOut();
												launchActivity(WelcomeActivity.class);
												ActivitysManager
														.removeAllActivity();
											}
										})
								.setNegativeButton(getString(R.string.cancel),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).create();
						dialog.show();
					}
				});

	}

	@Override
	public void onBackPressed() {

		if (!mCloseLock) {
			finish();
			ActivitysManager.removeAllActivity();
			return;
		}

		finishActivityWithResult(RESULT_CANCELED);

	}

	@Override
	public void onPatternStart() {
	}

	@Override
	public void onPatternCleared() {
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		// 退出应用
		if (pattern.equals(lockPattern) && !mCloseLock) {
			finish();
			// UserConfig.getInstance().setLockPassSuccess(true);
			launchActivity(HomeActivity.class);
			// 关闭锁屏密码
		} else if (pattern.equals(lockPattern) && mCloseLock) {
			UserConfig.getInstance().setLockPassStartStatus(false);
			finishActivityWithResult(RESULT_OK);
		} else {
			nineLockView.setDisplayMode(DisplayMode.Wrong);
			SuperToast.makeText(this, R.string.ninelock_password_error,
					SuperToast.LENGTH_LONG).show();
		}
	}
}
