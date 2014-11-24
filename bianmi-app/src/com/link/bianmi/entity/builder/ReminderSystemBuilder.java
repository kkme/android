package com.link.bianmi.entity.builder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Reminder;

public class ReminderSystemBuilder implements
		BaseEntityBuilder<ListResult<Reminder.System>> {

	private static ReminderSystemBuilder mInstance = null;

	private ReminderSystemBuilder() {
	}

	public static ReminderSystemBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new ReminderSystemBuilder();
		}

		return mInstance;
	}

	@Override
	public ListResult<Reminder.System> buildEntity(JSONObject jsonObj) {

		ListResult<Reminder.System> listResult = new ListResult<Reminder.System>();
		listResult.list = new ArrayList<Reminder.System>();
		try {
			if (jsonObj != null && jsonObj.has("list")) {
				JSONObject listJson = jsonObj.getJSONObject("list");
				if (listJson != null && listJson.has("has_more")) {
					listResult.hasMore = listJson.getBoolean("has_more");
				}

				if (listJson != null && listJson.has("system_reminders")) {
					JSONArray jsonArr = listJson
							.getJSONArray("system_reminders");
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonArrObj = jsonArr.getJSONObject(i);

						Reminder.System sysReminder = new Reminder.System();
						sysReminder.title = jsonArrObj.getString("title");
						sysReminder.subtitle = jsonArrObj.getString("subtitle");
						sysReminder.imageUrl = jsonArrObj
								.getString("image_url");
						sysReminder.h5Url = jsonArrObj.getString("h5_url");
						listResult.list.add(sysReminder);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return listResult;

	}
}
