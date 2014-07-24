package com.link.bianmi.activity;

import android.os.Bundle;
import android.text.TextUtils;
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
/**
 * 
 * @Description 登录
 * @author pangfq
 * @date 2014年7月24日 下午4:22:55
 */
public class SignInActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signin);

		// 取得刚注册的username(如果有的话)
		String username = null;
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			username = bundle.getString("username");
		}
		
		final EditText usernameEdit = (EditText) findViewById(R.id.username_edittext);
		final EditText passwordEdit = (EditText) findViewById(R.id.password_edittext);
		Button signInButton = (Button) findViewById(R.id.signin_button);

		if(username != null && !TextUtils.isEmpty(username)){
			usernameEdit.setText(username);
			passwordEdit.setText("");
		}
		
		// 点击登录
		signInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = usernameEdit.getText().toString();
				String password = passwordEdit.getText().toString();

				// 校验登录数据合法性
				if (checkSignInData(username, password)) {
					// 数据合法，则进行联网登录
					UserManager.API.signIn(username, password, new OnSaveListener<User>() {
						
						@Override
						public void onSuccess(User user) {
							// 保存sessionId
							UserConfig.getInstance().setSessionId(user.getSessionId());
							// 进入主界面
							launchActivity(MainActivity.class);
							// 关闭Activity
							finishActivityWithResult(8888);
						}
						
						@Override
						public void onFailure() {
							Toast.makeText(getApplicationContext(), "SignIn Error!", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});

	}

	/** 校验数据合法性 **/
	private boolean checkSignInData(String username, String password) {
		boolean ok = true;
		if(TextUtils.isEmpty(username)){
			Toast.makeText(getApplicationContext(), "username为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		}else if(TextUtils.isEmpty(password)){
			Toast.makeText(getApplicationContext(), "password为空！", Toast.LENGTH_SHORT).show();
			ok = false;
		}
		
		return ok;
	}
}