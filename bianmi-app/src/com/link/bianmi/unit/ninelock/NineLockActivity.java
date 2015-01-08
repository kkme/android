package com.link.bianmi.unit.ninelock;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.ActivitysManager;
import com.link.bianmi.activity.BaseFragmentActivity;
import com.link.bianmi.activity.HomeActivity;
import com.link.bianmi.activity.WelcomeActivity;
import com.link.bianmi.unit.ninelock.NineLockView.Cell;
import com.link.bianmi.unit.ninelock.NineLockView.DisplayMode;

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
	private TextView mTipText;

	private int mActionType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lockPattern = NineLockView.stringToPattern(UserConfig.getInstance()
				.getLockPassKey());
		setContentView(R.layout.activity_ninelock);

		mTipText = (TextView) findViewById(R.id.tip_textview);
		nineLockView = (NineLockView) findViewById(R.id.ninelockview);
		nineLockView.setOnPatternListener(this);

		mActionType = 0;
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mActionType = bundle.getInt("close_lock");
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
										getString(R.string.forget_ninelock_pwd_dialog_tip))
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
												UserConfig.getInstance()
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

		if (mActionType == 0) {
			finish();
			ActivitysManager.removeAllActivity();
			return;
		}

		finishActivityWithResult(RESULT_CANCELED);

	}

	@Override
	public void onPatternStart() {
		mTipText.setText("");
	}

	@Override
	public void onPatternCleared() {
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		// 退出锁屏，跳转主页
		if (pattern.equals(lockPattern) && mActionType == 0) {
			finish();
			launchActivity(HomeActivity.class);
			// 关闭锁屏密码
		} else if (pattern.equals(lockPattern) && mActionType == 1) {
			UserConfig.getInstance().setLockPassStartStatus(false);
			finishActivityWithResult(RESULT_OK);
			// 修改秘密
		} else if (pattern.equals(lockPattern) && mActionType == 2) {
			finishActivityWithResult(RESULT_OK);
		} else {
			nineLockView.setDisplayMode(DisplayMode.Wrong);
			mTipText.setText(getString(R.string.ninelock_pwd_error));
		}
	}
}
