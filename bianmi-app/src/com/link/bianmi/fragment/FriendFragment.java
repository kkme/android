package com.link.bianmi.fragment;

import com.link.bianmi.R;
import com.link.bianmi.entity.manager.SecretManager;

public class FriendFragment extends SecretFragment {

	@Override
	protected SecretManager.TaskType getTaskType() {
		return SecretManager.TaskType.GET_FRIENDS;
	}

	@Override
	String getNoDataString() {
		return getString(R.string.nodata_tip_secrets_friends);
	}

}
