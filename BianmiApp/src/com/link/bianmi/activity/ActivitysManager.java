package com.link.bianmi.activity;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class ActivitysManager extends Service implements Runnable {
	public static List<Activity> s_allActivity = new ArrayList<Activity>();
	
	public static Activity getActivityByName(String name) {

		for (Activity ac : s_allActivity) {
			
			if (ac.getClass().getName().substring(ac.getClass().getName().lastIndexOf(".")+1).equals(name)) {
				return ac;
			}
		}

		return null;
	}
	
	public static void removeAllActivity() {

		for (int i = 0; i < s_allActivity.size(); i++) {
			Activity ac = s_allActivity.get(i);
			if(null!=ac)
			{
				ac.finish();
			}
		}
		s_allActivity.clear();
	}
	
	public void onDestroy() {
		removeAllActivity();
		super.onDestroy();
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void newTask() {
		// TODO Auto-generated method stub
		
	}


}
