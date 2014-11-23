package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.asynctask.listener.ITaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;
import com.link.bianmi.utility.SecurityUtils;

/**
 * 联系人管理类
 * 
 * @author pangfq
 * @date 2014-10-26 下午9:59:50
 */
public class ContactsManager {

	private static class API {

		public static Status_ uploadContacts(String userid, String contacts) {
			Status_ status = null;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair useridParam = new BasicNameValuePair("userid", userid);
			params.add(new BasicNameValuePair("token", UserConfig.getInstance()
					.getToken()));
			NameValuePair contactsParam = new BasicNameValuePair("contacts",
					contacts);
			params.add(useridParam);
			params.add(contactsParam);
			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getUploadContactsUrl());
			if (response == null)
				return null;

			try {
				// 解析Status
				JSONObject jsonObj = response.asJSONObject();
				status = StatusBuilder.getInstance().buildEntity(jsonObj);
			} catch (ResponseException e) {
				e.printStackTrace();
			}

			return status;
		}
	}

	public static class Task {
		/** 上传联系人 **/
		public static void uploadContacts(Context context,
				OnTaskOverListener<?> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("userid", UserConfig.getInstance().getUserId());
			taskParams.put("contacts", DB.getContactsJSON(context));
			ContactsTask contactsTask = new ContactsTask(listener);
			contactsTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}
	}

	private static class DB {
		/**
		 * 获取联系人
		 * 
		 * @param context
		 * @return
		 */
		static String getContactsJSON(Context context) {
			Cursor cursor = context.getContentResolver().query(
					Phone.CONTENT_URI, null, null, null, null);
			String phoneNum;
			JSONObject contactsObj = new JSONObject();
			JSONArray contactsArr = new JSONArray();
			while (cursor.moveToNext()) {
				phoneNum = cursor
						.getString(cursor.getColumnIndex(Phone.NUMBER));
				JSONObject contactObj = new JSONObject();
				try {
					contactObj.put("phone", SecurityUtils.getMD5Str(phoneNum));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				contactsArr.put(contactObj);
			}

			try {
				contactsObj.put("contacts", contactsArr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return contactsObj.toString();
		}
	}

	private static class ContactsTask extends BaseAsyncTask {
		ITaskOverListener<?> listener;

		public ContactsTask(ITaskOverListener<?> listener) {
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			TaskResult<?> taskResult = null;
			String userid = (String) params[0].get("userid");
			String contacts = (String) params[0].get("contacts");
			Status_ status = API.uploadContacts(userid, contacts);

			if (status != null && status.code == Status_.OK) {
				taskResult = new TaskResult<Status_>(TaskStatus.OK, status);
			} else if (status != null) {
				taskResult = new TaskResult<Status_>(TaskStatus.FAILED, status);
			} else {
				taskResult = new TaskResult<Status_>(TaskStatus.FAILED, status);
			}

			return taskResult;
		}

		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);
			if (taskResult.getStatus() == TaskStatus.OK) {
				listener.onSuccess(null);
			} else if (taskResult.getStatus() == TaskStatus.FAILED) {
				Status_ status = (Status_) taskResult.getEntity();
				if (status != null) {
					listener.onFailure(status.code, status.msg);
				}
			}
		}
	}
}