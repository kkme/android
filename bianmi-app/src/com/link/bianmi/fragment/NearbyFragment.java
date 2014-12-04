package com.link.bianmi.fragment;

import android.os.Bundle;

import com.link.bianmi.R;
import com.link.bianmi.UserConfig;
import com.link.bianmi.activity.PublishActivity;
import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.widget.NoDataView.OnListener;

public class NearbyFragment extends SecretFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mNoDataView.setOnListener(new OnListener() {
			@Override
			public void onClick() {
				if (UserConfig.getInstance().getIsGuest()) {
					mParentActivity
							.showGuestTipDialog(getString(R.string.guest_action_publis_msg));
				} else {
					launchActivity(PublishActivity.class);
				}
			}
		});

		mNoDataView.setTip(R.string.nodata_tip_secrets_nearby);
	}

	@Override
	protected SecretManager.TaskType getTaskType() {
		return SecretManager.TaskType.GET_NEARBY;
	}

}
