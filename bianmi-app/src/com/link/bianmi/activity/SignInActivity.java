package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.User;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.utility.DataCheckUtil;
import com.link.bianmi.utility.SecurityUtils;
import com.link.bianmi.widget.SuperToast;

/**
 * 登录
 * 
 * @author pangfq
 * @date 2014年7月24日 下午4:22:55
 */
public class SignInActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(getResources().getString(R.string.signin));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_signin);

		final EditText phonenumEdit = (EditText) findViewById(R.id.phonenum_edittext);
		final EditText passwordEdit = (EditText) findViewById(R.id.password_edittext);
		Button signInButton = (Button) findViewById(R.id.signin_button);

		// 点击登录
		signInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String phonenum = phonenumEdit.getText().toString();
				final String password = passwordEdit.getText().toString();

				// 校验登录数据合法性
				if (DataCheckUtil.checkSignInUpData(SignInActivity.this,
						phonenum, password)) {
					mLoadingMenuItem.setVisible(true);
					// 数据合法，则进行联网登录
					UserManager.Task.signIn(phonenum,
							SecurityUtils.getMD5Str(password),
							new OnTaskOverListener<User>() {

								@Override
								public void onFailure(int code, String msg) {
									mLoadingMenuItem.setVisible(false);
									SuperToast.makeText(
											getApplicationContext(),
											"SignIn Error!" + "code:" + code
													+ ",msg:" + msg,
											SuperToast.LENGTH_SHORT).show();
								}

								@Override
								public void onSuccess(User user) {
									// 保存登录帐号和密码
									try {
										UserConfig.getInstance().setPhone(
												SecurityUtils.encryptDES(
														phonenum, "bianmi_k"));
										UserConfig.getInstance().setPwd(
												SecurityUtils.encryptDES(
														password, "bianmi_k"));
									} catch (Exception e) {
										e.printStackTrace();
									}
									mLoadingMenuItem.setVisible(false);
									// 保存userId
									UserConfig.getInstance().setUserId(user.id);
									// 保存token
									UserConfig.getInstance().setToken(
											user.token);
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
}