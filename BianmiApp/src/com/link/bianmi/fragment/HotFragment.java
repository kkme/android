package com.link.bianmi.fragment;

import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.manager.SecretManager;


public class HotFragment extends BaseFragment {
	
	@Override
	protected SecretManager getFeedsManager() {
		return new SecretManager(SecretManager.TYPE_HOT, getActivity());
	}
}
