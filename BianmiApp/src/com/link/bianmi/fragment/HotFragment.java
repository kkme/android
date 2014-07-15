package com.link.bianmi.fragment;

import com.link.bianmi.manager.SecretManager;


public class HotFragment extends SecretFragment {
	
	@Override
	protected SecretManager getFeedsManager() {
		return new SecretManager(SecretManager.TYPE_HOT, getActivity());
	}
}
