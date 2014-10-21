package com.link.bianmi.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.User;
import com.link.bianmi.entity.manager.UserManager;
import com.link.bianmi.receiver.SMSReceiver;
import com.link.bianmi.receiver.SMSReceiver.SMSVerifyCodeListener;
import com.link.bianmi.unit.country.Country;
import com.link.bianmi.unit.country.CountryActivity;
import com.link.bianmi.utility.DataCheckUtil;
import com.link.bianmi.utility.SecurityUtils;
import com.link.bianmi.widget.SuperToast;

/**
 * 注册
 * 
 * @author pangfq
 * @date 2014年7月24日 下午4:22:34
 */
public class SignUpBySmsActivity extends BaseFragmentActivity implements
		Callback {
	private final int REQUEST_CODE_COUNTRY = 1111;// 请求码:进入国家代码查询页面
	private boolean ready;

	private TextView mCountryNameText = null;
	private TextView mCountryCodeText = null;

	private View mSmsGroup = null;

	private EditText mPhonenumEdit = null;
	private EditText mPasswordEdit = null;

	private SMSReceiver mSMSReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle(getResources().getString(R.string.signup));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_signup_sms);

		mCountryNameText = (TextView) findViewById(R.id.country_name_textview);
		mCountryCodeText = (TextView) findViewById(R.id.country_code_textview);
		mPhonenumEdit = (EditText) findViewById(R.id.phonenum_edittext);
		mPasswordEdit = (EditText) findViewById(R.id.password_edittext);
		final EditText smsVerifyCodeEdit = (EditText) findViewById(R.id.smscode_edittext);
		mSmsGroup = findViewById(R.id.sms_group);
		mSmsGroup.setVisibility(View.INVISIBLE);
		final Button signUpButton = (Button) findViewById(R.id.signup_button);

		initSDK();

		// 国家或地区
		findViewById(R.id.country_group).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						launchActivityForResult(CountryActivity.class,
								REQUEST_CODE_COUNTRY);
					}
				});

		// 切换密码明文、暗文
		CheckBox switchPwdCheckbox = (CheckBox) findViewById(R.id.switch_pwd_checkbox);
		switchPwdCheckbox.setChecked(false);
		switchPwdCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 明文
							mPasswordEdit
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						} else {
							// 暗文
							mPasswordEdit
									.setInputType(InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
					}
				});

		// 收不到验证码
		final View resendSMSView = findViewById(R.id.resend_sms_textview);
		resendSMSView.setVisibility(View.INVISIBLE);
		resendSMSView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SignUpBySmsActivity.this);
				final AlertDialog dialog = builder
						.setMessage(getString(R.string.resend_sms_verify_code))
						.setPositiveButton(getString(R.string.yes),
								new Dialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										smsVerifyCodeEdit.setText("");
										signUpButton
												.setText(getString(R.string.signup));
										// 发送短信验证码
										mLoadingItem.setVisible(true);
										SMSSDK.getVerificationCode(
												mCountryCodeText.getText()
														.toString(),
												mPhonenumEdit.getText()
														.toString());
									}
								})
						.setNegativeButton(getString(R.string.no),
								new Dialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
		});

		// 点击注册
		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String code = mCountryCodeText.getText().toString()
						.substring(1);
				final String phone = mPhonenumEdit.getText().toString();
				// 注册按钮，点击提示发送验证码
				if (signUpButton.getText().equals(getString(R.string.signup))) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SignUpBySmsActivity.this);
					final AlertDialog dialog = builder
							.setTitle(getString(R.string.sms_dialog_title))
							.setMessage(
									String.format(
											getString(R.string.sms_dialog_msg),
											phone))
							.setPositiveButton(getString(R.string.yes),
									new Dialog.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 发送短信验证码
											SMSSDK.getVerificationCode(code,
													phone);
											mLoadingItem.setVisible(true);
											resendSMSView
													.setVisibility(View.VISIBLE);
										}
									})
							.setNegativeButton(getString(R.string.no),
									new Dialog.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					dialog.show();
					// 完成注册按钮
				} else if (signUpButton.getText().equals(
						getString(R.string.signup_finish))) {
					SMSSDK.submitVerificationCode(code, phone,
							smsVerifyCodeEdit.getText().toString());
				}
			}
		});

		mSMSReceiver = new SMSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SMSReceiver.SMS_RECEIVED_ACTION);
		mSMSReceiver.setSMSVerifyCodeListener(new SMSVerifyCodeListener() {
			@Override
			public void receiveVerifyCode(String verifyCode) {
				mLoadingItem.setVisible(false);
				smsVerifyCodeEdit.setText(verifyCode);
				signUpButton.setText(getString(R.string.signup_finish));
			}
		});
		registerReceiver(mSMSReceiver, filter);

	}

	@Override
	public void onResume() {
		super.onResume();
		if (ready) {
			StatisticManager.onResume(SignUpBySmsActivity.this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (ready) {
			SMSSDK.unregisterAllEventHandler();
		}

		unregisterReceiver(mSMSReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ready) {
			StatisticManager.onPause(SignUpBySmsActivity.this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_COUNTRY && resultCode == RESULT_OK) {
			Country country = (Country) data.getSerializableExtra("country");
			if (country != null) {
				mCountryNameText.setText(country.name);
				mCountryCodeText.setText(country.code);
			}
		}
	}

	private MenuItem mLoadingItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loading, menu);
		mLoadingItem = menu.findItem(R.id.action_loading);
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

	@Override
	public void onBackPressed() {
		// 如果正在加载，则取消加载
		if (mLoadingItem.isVisible()) {
			mLoadingItem.setVisible(false);
			return;
		}

		super.onBackPressed();
	}

	// ------------------初始化SMSSDK-----------------
	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, "33ca2b67479f", "cce3bfdaaabf5f392828cc2962f2e5eb");
		final Handler handler = new Handler(this);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
		ready = true;

		StatisticManager.initAnalysisSDK(SignUpBySmsActivity.this);
		StatisticManager.registerAnalysisHandler(SignUpBySmsActivity.this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		int event = msg.arg1;
		int result = msg.arg2;
		Object data = msg.obj;
		if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
			// 获取验证码
			SuperToast.makeText(getApplicationContext(), "获取验证码",
					SuperToast.LENGTH_SHORT).show();
			mSmsGroup.setVisibility(View.VISIBLE);
		} else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
			// 提交验证码
			SuperToast.makeText(getApplicationContext(), "提交验证码",
					SuperToast.LENGTH_SHORT).show();
			if (result == SMSSDK.RESULT_COMPLETE) {
				SuperToast.makeText(getApplicationContext(), "验证成功",
						SuperToast.LENGTH_SHORT).show();
				finishSignUp();
			} else {
				((Throwable) data).printStackTrace();
				SuperToast.makeText(getApplicationContext(), "验证失败",
						SuperToast.LENGTH_SHORT).show();
				mLoadingItem.setVisible(false);
			}
		}
		return false;
	}

	/**
	 * 完成注册
	 */
	private void finishSignUp() {
		final String phonenum = mPhonenumEdit.getText().toString();
		String password = mPasswordEdit.getText().toString();

		// 校验注册数据合法性
		if (DataCheckUtil.checkSignInData(SignUpBySmsActivity.this, phonenum,
				password)) {
			mLoadingItem.setVisible(true);
			// 数据合法，则跳转登录
			UserManager.Task.signUp(phonenum,
					SecurityUtils.getMD5Str(password),
					new OnTaskOverListener<User>() {

						@Override
						public void onFailure(int code, String msg) {
							mLoadingItem.setVisible(false);
							SuperToast.makeText(getApplicationContext(),
									"SignUp Error!", SuperToast.LENGTH_SHORT)
									.show();
						}

						@Override
						public void onSuccess(User user) {
							mLoadingItem.setVisible(false);
							// 保存userId
							UserConfig.getInstance().setUserId(user.id);
							// 进入主界面
							launchActivity(HomeActivity.class);
							ActivitysManager.removeAllActivity();
						}
					});
		}
	}

}