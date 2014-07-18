package com.link.bianmi.fragment;

import com.link.bianmi.bean.helper.SecretHelper;
import com.link.bianmi.bean.helper.SecretHelper.SecretType;
import com.link.bianmi.manager.SecretManager;


public class HotFragment extends SecretFragment {
	
	@Override
	protected SecretManager getFeedsManager() {
		return new SecretManager(SecretManager.TYPE_HOT, getActivity());
	}
	
	@Override
	protected SecretType getSecretType() {
		return SecretHelper.SecretType.HOT;
	}
}
