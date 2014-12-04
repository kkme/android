package com.link.bianmi.fragment;

import android.os.Bundle;

import com.link.bianmi.R;
import com.link.bianmi.entity.manager.SecretManager;

public class HotFragment extends SecretFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mNoDataView.setTip(R.string.nodata_tip_secrets_hot);
	}

	@Override
	protected SecretManager.TaskType getTaskType() {
		return SecretManager.TaskType.GET_HOTS;
	}

	@Override
	protected boolean isFirstFragment() {
		return true;
	}

}
