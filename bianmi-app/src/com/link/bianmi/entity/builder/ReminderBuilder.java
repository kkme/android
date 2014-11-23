package com.link.bianmi.entity.builder;

import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.Reminder;

public class ReminderBuilder implements BaseEntityBuilder<Reminder> {

	private static ReminderBuilder mInstance = null;

	private ReminderBuilder() {
	}

	public static ReminderBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new ReminderBuilder();
		}

		return mInstance;
	}

	@Override
	public Reminder buildEntity(JSONObject jsonObj) {
		Reminder reminder = null;
		try {
			if (jsonObj != null && jsonObj.has("reminder")) {
				JSONObject reminderJson = jsonObj.getJSONObject("reminder");
				reminder = new Reminder();
				reminder.hasReminder = reminderJson.getBoolean("has_reminder");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return reminder;
	}
}
