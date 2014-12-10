package com.link.bianmi.entity.builder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Reminder;

public class ReminderPersonBuilder implements
		BaseEntityBuilder<ListResult<Reminder.Person>> {

	private static ReminderPersonBuilder mInstance = null;

	private ReminderPersonBuilder() {
	}

	public static ReminderPersonBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new ReminderPersonBuilder();
		}

		return mInstance;
	}

	@Override
	public ListResult<Reminder.Person> buildEntity(JSONObject jsonObj) {

		ListResult<Reminder.Person> listResult = new ListResult<Reminder.Person>();
		listResult.list = new ArrayList<Reminder.Person>();
		try {
			if (jsonObj != null && jsonObj.has("list")) {
				JSONObject listJson = jsonObj.getJSONObject("list");
				if (listJson != null && listJson.has("has_more")) {
					listResult.hasMore = listJson.getBoolean("has_more");
				}

				if (listJson != null && listJson.has("person_reminders")) {
					JSONArray jsonArr = listJson
							.getJSONArray("person_reminders");
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonArrObj = jsonArr.getJSONObject(i);

						Reminder.Person perReminder = new Reminder.Person();
						perReminder.id = jsonArrObj.getString("id");
						perReminder.secretid = jsonArrObj.getString("secretid");
						perReminder.content = jsonArrObj.getString("content");
						perReminder.likes = jsonArrObj.getInt("likes");
						perReminder.comments = jsonArrObj.getInt("comments");
						perReminder.imageUrl = jsonArrObj
								.getString("image_url");
						listResult.list.add(perReminder);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return listResult;

	}
}
