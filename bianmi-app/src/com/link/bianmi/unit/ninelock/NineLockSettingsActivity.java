package com.link.bianmi.unit.ninelock;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.ActivitysManager;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.unit.ninelock.NineLockView.Cell;
import com.link.bianmi.unit.ninelock.NineLockView.DisplayMode;
import com.link.bianmi.widget.SuperToast;

/**
 * 九宫格密码锁屏设置
 * 
 * @author pangfq
 * @date 2014-9-17 下午10:31:36
 */
public class NineLockSettingsActivity extends BaseFragmentActivity implements
		NineLockView.OnPatternListener, View.OnClickListener {

	private NineLockView mLockPatternView;
	private Button mLeftButton;
	private Button mRightButton;

	private static final int STEP_1 = 1; // 开始
	private static final int STEP_2 = 2; // 第一次设置手势完成
	private static final int STEP_3 = 3; // 按下继续按钮
	private static final int STEP_4 = 4; // 第二次设置手势完成
	// private static final int SETP_5 = 4; // 按确认按钮

	private int mStep;

	private List<Cell> mChoosePattern;

	private boolean mConfirm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(getString(R.string.settings_pwd));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_ninelock_setting);
		mLockPatternView = (NineLockView) findViewById(R.id.lock_pattern);
		mLockPatternView.setOnPatternListener(this);
		mLeftButton = (Button) findViewById(R.id.left_btn);
		mRightButton = (Button) findViewById(R.id.right_btn);

		mStep = STEP_1;
		updateView();
	}

	private void updateView() {
		switch (mStep) {
		case STEP_1:
			mLeftButton.setText(R.string.cancel);
			mRightButton.setText("");
			mRightButton.setEnabled(false);
			mChoosePattern = null;
			mConfirm = false;
			mLockPatternView.clearPattern();
			mLockPatternView.enableInput();
			break;
		case STEP_2:
			mLeftButton.setText(R.string.try_again);
			mRightButton.setText(R.string.goon);
			mRightButton.setEnabled(true);
			mLockPatternView.disableInput();
			break;
		case STEP_3:
			mLeftButton.setText(R.string.cancel);
			mRightButton.setText("");
			mRightButton.setEnabled(false);
			mLockPatternView.clearPattern();
			mLockPatternView.enableInput();
			break;
		case STEP_4:
			mLeftButton.setText(R.string.cancel);
			if (mConfirm) {
				mRightButton.setText(R.string.mConfirm);
				mRightButton.setEnabled(true);
				mLockPatternView.disableInput();
			} else {
				mRightButton.setText("");
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
				mLockPatternView.enableInput();
				mRightButton.setEnabled(false);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.left_btn:
			if (mStep == STEP_1 || mStep == STEP_3 || mStep == STEP_4) {
				finish();
			} else if (mStep == STEP_2) {
				mStep = STEP_1;
				updateView();
			}
			break;

		case R.id.right_btn:
			if (mStep == STEP_2) {
				mStep = STEP_3;
				updateView();
			} else if (mStep == STEP_4) {
				// 保存手势密码
				UserConfig.getInstance().setLockPassKey(
						NineLockView.patternToString(mChoosePattern));
				// 开启手势密码
				UserConfig.getInstance().setLockPassStartStatus(true);
				// 是否重新启动
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NineLockSettingsActivity.this);
				final AlertDialog dialog = builder
						.setMessage(getString(R.string.if_restart))
						.setPositiveButton(getString(R.string.restart_at_once),
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 退出APP
										ActivitysManager.removeAllActivity();
										// 重新启动
										new Handler().postDelayed(
												new Runnable() {
													@Override
													public void run() {
														launchActivity(MainActivity.class);
													}
												}, 500);
									}
								})
						.setNegativeButton(getString(R.string.cancel),
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}

			break;

		default:
			break;
		}

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

		if (pattern.size() < NineLockView.MIN_LOCK_PATTERN_SIZE) {
			SuperToast.makeText(this,
					R.string.lockpattern_recording_incorrect_too_short,
					SuperToast.LENGTH_LONG).show();
			mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			return;
		}

		if (mChoosePattern == null) {
			mChoosePattern = new ArrayList<Cell>(pattern);
			mStep = STEP_2;
			updateView();
			return;
		}

		if (mChoosePattern.equals(pattern)) {
			mConfirm = true;
		} else {
			mConfirm = false;
		}

		mStep = STEP_4;
		updateView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finishActivityWithResult(RESULT_OK);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finishActivityWithResult(RESULT_OK);
	}

}
