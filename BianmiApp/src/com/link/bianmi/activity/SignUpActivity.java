package com.link.bianmi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.bean.User;
import com.link.bianmi.bean.manager.UserManager;
import com.link.bianmi.bean.manager.UserManager.OnSaveListener;
/**
 * 
 * @Description 注册
 * @author pangfq
 * @date 2014年7月24日 下午4:22:34
 */
public class SignUpActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		final EditText usernameEdit = (EditText) findViewById(R.id.username_edittext);
		final EditText passwordEdit = (EditText) findViewById(R.id.password_edittext);
		final EditText passwordConfirmEdit = (EditText) findViewById(R.id.password_confirm_edittext);
		Button signUpButton = (Button) findViewById(R.id.signup_button);

		// 点击注册
		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String username = usernameEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				String passwordConfirm = passwordConfirmEdit.getText().toString();

				// 校验注册数据合法性
				if (checkSignUpData(username, password, passwordConfirm)) {
					// 数据合法，则跳转登录
					UserManager.API.signUp(username, password, new OnSaveListener<User>() {
						
						@Override
						public void onSuccess(User user) {
							Bundle bundle = new Bundle();
							bundle.putString("username", username);
							launchActivity(SignInActivity.class, bundle);
							finishActivity();
						}
						
						@Override
						public void onFailure() {
							Toast.makeText(getApplicationContext(), "SignUp Error!", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});

	}
	
	/** 校验数据合法性 **/
	private boolean checkSignUpData(String username, String password, String passwordConfirm) {
		boolean ok = true;
		if(TextUtils.isEmpty(username)){
			Toast.makeText(getApplicationContext(), "username为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		}else if(TextUtils.isEmpty(password)){
			Toast.makeText(getApplicationContext(), "password为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		}else if(TextUtils.isEmpty(passwordConfirm)){
			Toast.makeText(getApplicationContext(), "passwordConfirm为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		}else if(!password.equals(passwordConfirm)){
			Toast.makeText(getApplicationContext(), "两次输入密码不一致!", Toast.LENGTH_SHORT).show();
			ok = false;
		}
		
		return ok;
	}
}
