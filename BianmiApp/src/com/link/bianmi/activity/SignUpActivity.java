package com.link.bianmi.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnInsertTaskListener;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.utility.DataCheckUtil;
import com.link.bianmi.utility.SecurityUtils;

/**
 * 注册
 * 
 * @author pangfq
 * @date 2014年7月24日 下午4:22:34
 */
public class SignUpActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(getResources().getString(R.string.signup));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_signup);

		final EditText phonenumEdit = (EditText) findViewById(R.id.phonenum_edittext);
		final EditText passwordEdit = (EditText) findViewById(R.id.password_edittext);
		final EditText passwordConfirmEdit = (EditText) findViewById(R.id.password_confirm_edittext);
		Button signUpButton = (Button) findViewById(R.id.signup_button);

		// 点击注册
		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String phonenum = phonenumEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				String passwordConfirm = passwordConfirmEdit.getText()
						.toString();

				// 校验注册数据合法性
				if (DataCheckUtil.checkSignUpData(SignUpActivity.this,
						phonenum, password, passwordConfirm)) {
					mLoadingMenuItem.setVisible(true);
					// 数据合法，则跳转登录
					UserManager.API.signUp(phonenum,
							SecurityUtils.getMD5Str(passwordConfirm),
							new OnInsertTaskListener() {

								@Override
								public void onSuccess() {
									mLoadingMenuItem.setVisible(false);
									Bundle bundle = new Bundle();
									bundle.putString("phonenum", phonenum);
									launchActivity(SignInActivity.class, bundle);
									finishActivity();
								}

								@Override
								public void onFailure(int code, String msg) {
									mLoadingMenuItem.setVisible(false);
									Toast.makeText(getApplicationContext(),
											"SignUp Error!", Toast.LENGTH_SHORT)
											.show();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}