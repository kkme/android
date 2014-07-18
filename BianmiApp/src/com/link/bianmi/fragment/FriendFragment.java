package com.link.bianmi.fragment;

import com.link.bianmi.bean.helper.SecretHelper;
import com.link.bianmi.bean.helper.SecretHelper.SecretType;
import com.link.bianmi.manager.SecretManager;


public class FriendFragment extends SecretFragment {
	
	@Override
	protected SecretManager getFeedsManager() {
		// TODO Auto-generated method stub
		return new SecretManager(SecretManager.TYPE_FRESH, getActivity());
	}

	@Override
	protected SecretType getSecretType() {
		return SecretHelper.SecretType.FRIEND;
	}
	
	
}
