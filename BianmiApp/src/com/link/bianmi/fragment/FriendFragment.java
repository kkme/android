package com.link.bianmi.fragment;

import com.link.bianmi.bean.manager.SecretManager;
import com.link.bianmi.bean.manager.SecretManager.SecretType;


public class FriendFragment extends SecretFragment {
	
	@Override
	protected SecretType getSecretType() {
		return SecretManager.SecretType.FRIEND;
	}
	
	
}
