package com.link.bianmi.fragment;

import com.link.bianmi.entity.manager.SecretManager;

public class NearbyFragment extends SecretFragment {

	@Override
	protected SecretManager.TaskType getTaskType() {
		return SecretManager.TaskType.GET_NEARBY;
	}
}
