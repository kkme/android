package com.link.bianmi.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.User;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.utils.DataCheckUtil;
import com.link.bianmi.utils.SecurityUtils;
import com.link.bianmi.widget.ClearEditText;
import com.link.bianmi.widget.ClearEditText.OnFocusListener;
import com.link.bianmi.widget.SuperToast;

/**
 * 注册
 * 
 * @author pangfq
 * @date 2014年7月24日 下午4:22:34
 */
public class SignUpActivity extends BaseFragmentActivity implements
		OnFocusListener {

	private ClearEditText mPwdEdit = null;
	private CheckBox mSwitchPwdCheckbox = null;
	private View mSwitchPwdCheckboxGroup = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(getResources().getString(R.string.signup));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_signup);

		final ClearEditText phoneEdit = (ClearEditText) findViewById(R.id.phone_edittext);
		mPwdEdit = (ClearEditText) findViewById(R.id.pwd_edittext);
		Button signUpButton = (Button) findViewById(R.id.signup_button);

		mPwdEdit.setOnFocusListener(this);
		// 切换密码明文、暗文
		mSwitchPwdCheckbox = (CheckBox) findViewById(R.id.switch_pwd_checkbox);
		mSwitchPwdCheckbox.setChecked(false);
		mSwitchPwdCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 明文
							mPwdEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						} else {
							// 暗文
							mPwdEdit.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
					}
				});
		mSwitchPwdCheckboxGroup = findViewById(R.id.checkbox_group);

		// 点击注册
		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String phone = phoneEdit.getText().toString();
				String password = mPwdEdit.getText().toString();

				// 校验注册数据合法性
				if (DataCheckUtil.checkSignInUpData(SignUpActivity.this, phone,
						password)) {
					mLoadingMenuItem.setVisible(true);
					// 数据合法，则跳转登录
					UserManager.Task.signUp(SecurityUtils.getMD5Str(phone),
							SecurityUtils.getMD5Str(password),
							new OnTaskOverListener<User>() {

								@Override
								public void onFailure(int code, String msg) {
									mLoadingMenuItem.setVisible(false);
									SuperToast.makeText(
											getApplicationContext(), msg,
											SuperToast.LENGTH_SHORT).show();
								}

								@Override
								public void onSuccess(User user) {
									mLoadingMenuItem.setVisible(false);
									// 保存userId
									UserConfig.getInstance().setUserId(user.id);
									// 进入主界面
									launchActivity(HomeActivity.class);
									ActivitysManager.removeAllActivity();
								}
							});
				}
			}
		});

	}

	private MenuItem mLoadingMenuItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loading, menu);
		mLoadingMenuItem = menu.findItem(R.id.action_loading);
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.equals(mPwdEdit)) {
			if (hasFocus) {
				mSwitchPwdCheckboxGroup
						.setBackgroundResource(R.drawable.input_bg_special_focus);
			} else {
				mSwitchPwdCheckboxGroup
						.setBackgroundResource(R.drawable.input_bg_special_normal);
			}
		}
	}

}