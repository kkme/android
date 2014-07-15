package com.link.bianmi.fragment.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.base.BaseFragmentActivity;

/** Fragment基础抽象类 **/
public abstract class BaseFragment extends Fragment {
	protected String TAG = "BaseFragment";

	/** 用户配置信息 **/
	protected UserConfig mUserConf = UserConfig.getInstance();
	/** 系统配置信息 **/
	protected SysConfig mSysConf = SysConfig.getInstance();
	protected BaseFragmentActivity mContext;

	/** 是否成功创建视图 **/
	protected boolean mSuccessCreate = false;

	/** 是否初次执行OnResume **/
	private boolean mFirstOnResume = true;

	// ----------------------------------------重载方法----------------------------------------

	@Override
	public final void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = (BaseFragmentActivity) activity;
		TAG = this.getClass().getName();

		try {
			_onAttach(activity);
		} catch (OutOfMemoryError ex) {
			Log.e(TAG, "onAttach.OutOfMemoryError");
		} catch (Exception ex) {
		}
	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		View view = null;
		mSuccessCreate = false;
		try {
			view = _onCreateView(inflater, container, savedInstanceState);
			mSuccessCreate = true;
		} catch (OutOfMemoryError ex) {
			mSuccessCreate = false;
			Log.e(TAG, "onCreate.OutOfMemoryError");
		} catch (Exception ex) {
			mSuccessCreate = false;
		}

		if (!mSuccessCreate)
			mContext.finish();

		mFirstOnResume = true;
		return view;
	}

	@Override
	public final void onResume() {
		super.onResume();
		try {
			_onResume();
		} catch (OutOfMemoryError ex) {
			Log.e(TAG, "onResume.OutOfMemoryError");
		} catch (Exception ex) {
		} finally {
			mFirstOnResume = false;
		}

	}

	@Override
	public final void onPause() {
		super.onPause();
		try {
			_onPause();
		} catch (OutOfMemoryError ex) {
			Log.e(TAG, "onResume.OutOfMemoryError");
		} catch (Exception ex) {
		}
	}

	@Override
	public final void onDestroyView() {

		if (mSuccessCreate) {
			try {
				_onDestroyView();
			} catch (OutOfMemoryError ex) {
				Log.e(TAG, "onDestroyView.OutOfMemoryError");
			} catch (Exception ex) {
			}
		}

		super.onDestroyView();
	}

	/** 是否初次执行onresume **/
	protected boolean isFirstOnResume() {
		return mFirstOnResume;
	}

	public void launch(Class<?> cls, int launchMode, Bundle bundle,
			int requestCode) {
		try {
			Intent intent = new Intent(mContext, cls);
			if (bundle != null && bundle.size() > 0)
				intent.putExtras(bundle);
			intent.setFlags(launchMode);
			startActivityForResult(intent, requestCode);
		} catch (Exception ex) {
		}
	}

	public void _onPause() {
	}

	public void _onResume() {
	}

	public void _onDestroyView() {
	}

	public void _onAttach(Activity activity) {
	}

	// ------------------
	public abstract View _onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

}
