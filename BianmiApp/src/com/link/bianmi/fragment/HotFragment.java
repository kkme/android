package com.link.bianmi.fragment;

import com.link.bianmi.bean.helper.SecretHelper;
import com.link.bianmi.bean.helper.SecretHelper.SecretType;


public class HotFragment extends SecretFragment {
	
	@Override
	protected SecretType getSecretType() {
		return SecretHelper.SecretType.HOT;
	}
}
