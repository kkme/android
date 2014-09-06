package com.link.bianmi.asynctask;

import android.os.AsyncTask;

public class BaseAsyncTask extends AsyncTask<TaskParams, Void, TaskResult<?>> {

	@Override
	protected TaskResult<?> doInBackground(TaskParams... arg0) {
		return null;
	}
}
