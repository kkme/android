package com.link.bianmi.fragment;

import com.link.bianmi.entity.manager.SecretManager;
import com.link.bianmi.entity.manager.SecretManager.SecretType;

public class HotFragment extends SecretFragment {

	@Override
	protected SecretType getSecretType() {
		return SecretManager.SecretType.HOT;
	}

	@Override
	protected boolean isFirstFragment() {
		return true;
	}
}
