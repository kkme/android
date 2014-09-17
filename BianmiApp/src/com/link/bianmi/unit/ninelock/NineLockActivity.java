package com.link.bianmi.unit.ninelock;

import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.ActivitysManager;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.activity.base.BaseFragmentActivity;
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
	private NineLockView lockPatternView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lockPattern = NineLockView.stringToPattern(UserConfig.getInstance()
				.getLockPassKey());
		setContentView(R.layout.activity_ninelock);
		lockPatternView = (NineLockView) findViewById(R.id.lock_pattern);
		lockPatternView.setOnPatternListener(this);

	}

	@Override
	public void onBackPressed() {
		finishActivity();
		ActivitysManager.removeAllActivity();
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
		if (pattern.equals(lockPattern)) {
			finishActivity();
			UserConfig.getInstance().setLockPassSuccess(true);
			launchActivity(MainActivity.class);
		} else {
			lockPatternView.setDisplayMode(DisplayMode.Wrong);
			Toast.makeText(this, R.string.lockpattern_error, Toast.LENGTH_LONG)
					.show();
		}
	}

}
