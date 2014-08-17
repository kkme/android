package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.widget.GestureLockView;
import com.link.bianmi.widget.GestureLockView.OnGestureFinishListener;

/**
 * 设置锁定密码
 * 
 * @author pangfq
 * @date 2014-8-16 下午11:22:44
 */
public class SettingLockPassActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle("设置密码");
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_setting_lockpass);

		// 提示信息
		final TextView tipText = (TextView) findViewById(R.id.tip_textview);
		tipText.setText("绘制锁定密码");

		// 重置密码
		final Button resetBtn = (Button) findViewById(R.id.reset_button);
		resetBtn.setVisibility(View.GONE);

		// 九宫格锁屏
		final GestureLockView glv = (GestureLockView) findViewById(R.id.gesturelockview);
		glv.clean();
		glv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void onGestureFinish(int resultCode) {
				if (GestureLockView.SETPASS_RESULT_RECORDED == resultCode) {
					tipText.setText("图案已记录，请再次绘制确认");
					resetBtn.setVisibility(View.VISIBLE);
				} else if (GestureLockView.SETPASS_RESULT_DIFFERENT == resultCode) {
					tipText.setText("与刚才绘制不一致，请重新绘制！");
				} else if (GestureLockView.SETPASS_RESULT_OK == resultCode) {
					tipText.setText("图案密码，设置成功！");

					UserConfig.getInstance().setLockPassKey(glv.getKey());
				}
			}

			@Override
			public void onGestureStart() {
				tipText.setText("完成后松开手指");
			}
		});

		resetBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				glv.clean();
				UserConfig.getInstance().setLockPassKey("");
				resetBtn.setVisibility(View.GONE);
			}
		});

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