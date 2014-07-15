package com.link.bianmi.asynctask;

import android.content.Context;

/**
 * 任务反馈
 * @author sunpf
 *
 */
public abstract class TaskFeedback  implements Feedback{


	protected Context mContext;

	protected TaskFeedback(Context context) {
		mContext = context;
	}

	public Context getContent() {
		return mContext;
	}
	
	public boolean isAvailable()
	{
		return true;
	}

	public void start(CharSequence text) {
	};
	
	public void cancel(CharSequence text){
		
	}
	public void success(CharSequence text){
		
	}
	public void failed(CharSequence text){
		
	}
	public void update(Object arg0){
		
	}
	public void setIndeterminate(boolean indeterminate){
		
	}
}


//class DialogFeedback extends TaskFeedback {
//	private static DialogFeedback _instance = null;
//
//	public static DialogFeedback getInstance() {
//		if (_instance == null) {
//			_instance = new DialogFeedback();
//		}
//		return _instance;
//	}
//
//
//	private Dialog _dialog = null;
//	
//	
//	@Override
//	public void cancel() {
//		if (_dialog != null) {
//		_dialog.dismiss();
//	}
//	}
//
//	@Override
//	public void failed(String prompt) {
//		if (_dialog != null) {
//			_dialog.dismiss();
//		}
//
//		Toast toast = Toast.makeText(_context, prompt, Toast.LENGTH_LONG);
//		toast.show();
//	}
//
//	@Override
//	public void start(String prompt) {
//		_dialog = MyAlertDialog.show(_context, "", prompt, true);
//		_dialog.setCancelable(true);
//	}
//	
//	@Override
//	public void start(String title,String prompt) {
//		_dialog = MyAlertDialog.show(_context, title, prompt, true);
//		_dialog.setCancelable(true);
//	}	
//
//	@Override
//	public void success(String prompt) {
//		if (_dialog != null) {
//			_dialog.dismiss();
//		}
//	}
//}


//show (Context context, CharSequence title, CharSequence message, boolean indeterminate) 
//
//class DialogFeedback2 extends TaskFeedback {
//	private static DialogFeedback _instance = null;
//
//	public static DialogFeedback getInstance() {
//		if (_instance == null) {
//			_instance = new DialogFeedback();
//		}
//		return _instance;
//	}
//
//	private ProgressDialog _dialog = null;
//
//	@Override
//	public void cancel() {
//		if (_dialog != null) {
//			_dialog.dismiss();
//		}
//	}
//
//	@Override
//	public void failed(String prompt) {
//		if (_dialog != null) {
//			_dialog.dismiss();
//		}
//
//		Toast toast = Toast.makeText(_context, prompt, Toast.LENGTH_LONG);
//		toast.show();
//	}
//
//	@Override
//	public void start(String prompt) {
//		_dialog = ProgressDialog.show(_context, "", prompt, true);
//		_dialog.setCancelable(true);
//	}
//
//	@Override
//	public void success(String prompt) {
//		if (_dialog != null) {
//			_dialog.dismiss();
//		}
//	}
//}

