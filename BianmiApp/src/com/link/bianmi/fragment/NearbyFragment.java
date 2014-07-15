package com.link.bianmi.fragment;

import com.link.bianmi.fragment.base.BaseFragment;
import com.link.bianmi.manager.SecretManager;


public class NearbyFragment extends BaseFragment {
	
	@Override
	protected SecretManager getFeedsManager() {
		// TODO Auto-generated method stub
		return new SecretManager(SecretManager.TYPE_TRENDING, getActivity());
	}
}
