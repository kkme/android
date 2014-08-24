package com.link.bianmi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.bean.User;
import com.link.bianmi.bean.manager.UserManager;
import com.link.bianmi.bean.manager.UserManager.OnSaveListener;
import com.link.bianmi.utility.DataCheckUtil;

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

		// 取得刚注册的phonenum(如果有的话)
		String phonenum = null;
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			phonenum = bundle.getString("phonenum");
		}

		final EditText phonenumEdit = (EditText) findViewById(R.id.phonenum_edittext);
		final EditText passwordEdit = (EditText) findViewById(R.id.password_edittext);
		Button signInButton = (Button) findViewById(R.id.signin_button);

		if (phonenum != null && !TextUtils.isEmpty(phonenum)) {
			phonenumEdit.setText(phonenum);
			passwordEdit.setText("");
		}

		// 点击登录
		signInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phonenum = phonenumEdit.getText().toString();
				String password = passwordEdit.getText().toString();

				// 校验登录数据合法性
				if (DataCheckUtil.checkSignInData(SignInActivity.this,
						phonenum, password)) {
					mLoadingMenuItem.setVisible(true);
					// 数据合法，则进行联网登录
					UserManager.API.signIn(phonenum, password,
							new OnSaveListener<User>() {

								@Override
								public void onSuccess(User user) {
									mLoadingMenuItem.setVisible(false);
									// 保存sessionId
									UserConfig.getInstance().setUserId(
											user.userId);
									// 进入主界面
									launchActivity(MainActivity.class);
									ActivitysManager.removeAllActivity();
								}

								@Override
								public void onFailure() {
									mLoadingMenuItem.setVisible(false);
									Toast.makeText(getApplicationContext(),
											"SignIn Error!", Toast.LENGTH_SHORT)
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
