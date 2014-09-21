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
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.ActivitysManager;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.unit.ninelock.NineLockView.Cell;
import com.link.bianmi.unit.ninelock.NineLockView.DisplayMode;

/**
 * 九宫格密码锁屏设置
 * 
 * @author pangfq
 * @date 2014-9-17 下午10:31:36
 */
public class NineLockSettingsActivity extends BaseFragmentActivity implements
		NineLockView.OnPatternListener, View.OnClickListener {

	private NineLockView lockPatternView;
	private Button leftButton;
	private Button rightButton;

	private static final int STEP_1 = 1; // 开始
	private static final int STEP_2 = 2; // 第一次设置手势完成
	private static final int STEP_3 = 3; // 按下继续按钮
	private static final int STEP_4 = 4; // 第二次设置手势完成
	// private static final int SETP_5 = 4; // 按确认按钮

	private int step;

	private List<Cell> choosePattern;

	private boolean confirm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(getString(R.string.setup_password));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_ninelock_setting);
		lockPatternView = (NineLockView) findViewById(R.id.lock_pattern);
		lockPatternView.setOnPatternListener(this);
		leftButton = (Button) findViewById(R.id.left_btn);
		rightButton = (Button) findViewById(R.id.right_btn);

		step = STEP_1;
		updateView();
	}

	private void updateView() {
		switch (step) {
		case STEP_1:
			leftButton.setText(R.string.cancel);
			rightButton.setText("");
			rightButton.setEnabled(false);
			choosePattern = null;
			confirm = false;
			lockPatternView.clearPattern();
			lockPatternView.enableInput();
			break;
		case STEP_2:
			leftButton.setText(R.string.try_again);
			rightButton.setText(R.string.goon);
			rightButton.setEnabled(true);
			lockPatternView.disableInput();
			break;
		case STEP_3:
			leftButton.setText(R.string.cancel);
			rightButton.setText("");
			rightButton.setEnabled(false);
			lockPatternView.clearPattern();
			lockPatternView.enableInput();
			break;
		case STEP_4:
			leftButton.setText(R.string.cancel);
			if (confirm) {
				rightButton.setText(R.string.confirm);
				rightButton.setEnabled(true);
				lockPatternView.disableInput();
			} else {
				rightButton.setText("");
				lockPatternView.setDisplayMode(DisplayMode.Wrong);
				lockPatternView.enableInput();
				rightButton.setEnabled(false);
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
			if (step == STEP_1 || step == STEP_3 || step == STEP_4) {
				finish();
			} else if (step == STEP_2) {
				step = STEP_1;
				updateView();
			}
			break;

		case R.id.right_btn:
			if (step == STEP_2) {
				step = STEP_3;
				updateView();
			} else if (step == STEP_4) {
				// 保存九宫格密码
				UserConfig.getInstance().setLockPassKey(
						NineLockView.patternToString(choosePattern));
				// 是否重新启动
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NineLockSettingsActivity.this);
				final AlertDialog dialog = builder
						.setTitle(getString(R.string.if_restart))
						.setPositiveButton(getString(R.string.yes),
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
						.setNegativeButton(getString(R.string.no),
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
			Toast.makeText(this,
					R.string.lockpattern_recording_incorrect_too_short,
					Toast.LENGTH_LONG).show();
			lockPatternView.setDisplayMode(DisplayMode.Wrong);
			return;
		}

		if (choosePattern == null) {
			choosePattern = new ArrayList<Cell>(pattern);
			step = STEP_2;
			updateView();
			return;
		}

		if (choosePattern.equals(pattern)) {
			confirm = true;
		} else {
			confirm = false;
		}

		step = STEP_4;
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
