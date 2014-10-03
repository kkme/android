package com.link.bianmi.fragment;

import com.link.bianmi.entity.manager.SecretManager;

public class HotFragment extends SecretFragment {

	@Override
	protected SecretManager.TaskType getTaskType() {
		return SecretManager.TaskType.HOT;
	}

	@Override
	protected boolean isFirstFragment() {
		return true;
	}
}
