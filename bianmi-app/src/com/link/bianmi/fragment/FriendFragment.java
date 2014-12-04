package com.link.bianmi.fragment;

import android.os.Bundle;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.utils.UmengSocialClient;
import com.link.bianmi.widget.NoDataView.OnListener;

public class FriendFragment extends SecretFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!UserConfig.getInstance().getIsGuest()) {
			mNoDataView.setOnListener(new OnListener() {
				@Override
				public void onClick() {
					UmengSocialClient.showShareDialog(getActivity());
				}
			});

			mNoDataView.setTip(R.string.nodata_tip_secrets_friends);
		}
	}

	@Override
	protected SecretManager.TaskType getTaskType() {
		return SecretManager.TaskType.GET_FRIENDS;
	}

}
