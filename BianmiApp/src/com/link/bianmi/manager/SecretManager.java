package com.link.bianmi.manager;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.link.bianmi.bean.Secret;
import com.link.bianmi.utility.HttpUtil;

public class SecretManager {

	public static final String TYPE_FRESH = "fresh";
	public static final String TYPE_HOT = "hot";
	public static final String TYPE_TRENDING = "trending";
	private ArrayList<Secret> secretItems;
	private String next = "";
	private String base_url;
	private FinalDb finalDb;

	public SecretManager(String type, Context context) {
		this.secretItems = new ArrayList<Secret>();
		next = "";
		base_url = "http://infinigag-us.aws.af.cm/" + type + "/";
		finalDb = FinalDb.create(context, type + "_db", false);
	}

	public ArrayList<Secret> getFeedItems() {
		return secretItems;
	}

	/**
	 * ����finalDb�洢������
	 * 
	 * @return �Ƿ���ڻ�������
	 */
	public boolean loadDbData() {
		try {
			this.secretItems.addAll(finalDb.findAll(Secret.class));
			if (secretItems.size() > 0) {
				this.next = secretItems.get(secretItems.size() - 1).getNext();
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	public void updateFirstPage() {
		this.next = "";
		updateListInBackground();
	}

	public void updateNextPage() {
		updateListInBackground();
	}

	/**
	 * ��ȡ���� �����ڷ�UI�߳�
	 */
	private void updateListInBackground() {
		String json = HttpUtil.get(getRequestUrl());

		if (TextUtils.isEmpty(json))
			return;
		ArrayList<Secret> secretItems_tmp = new ArrayList<Secret>();
		String next_tmp = "";
		// Log.e("JSON", json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray feedList = jsonObject.getJSONArray("data");
			if (this.next.equals("")) {
				finalDb.deleteByWhere(Secret.class, null);
			}
			next_tmp = jsonObject.getJSONObject("paging").getString("next");
			for (int i = 0; i < feedList.length(); i++) {
				Secret item = new Secret(feedList.getJSONObject(i));
				item.setNext(next_tmp);
				secretItems_tmp.add(item);
				finalDb.save(item);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (this.next.equals("")) {
			secretItems.clear();
		}
		this.next = next_tmp;
		secretItems.addAll(secretItems_tmp);
	}

	/**
	 * �������� ��http://infinigag-us.aws.af.cm/fresh/aAYLQ7p
	 * 
	 * @return
	 */
	private String getRequestUrl() {
		return base_url + next;
	}

}
