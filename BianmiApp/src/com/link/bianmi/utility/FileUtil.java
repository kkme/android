package com.link.bianmi.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * 
 *  FileUtil
 */
public class FileUtil {
	
	static String ROOT_PATH = "/9gag_mxx";
	static String DOWNLOAD_PATH = "/download";
	static String IMAGE_PATH = "/Image";
	static String AUDIO_PATH = "/Audio";
	static String CRASH_PATH = "/Crash";

	/**
	 *  ��⵱ǰ�豸SD�Ƿ����
	 *  
	 * @return  ����"true"��ʾ���ã����򲻿���
	 */
	public static boolean haveSdCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
	}
	
	/**
	 *  ���SD����Ŀ¼·�� 
	 *  
	 * @return String����  SD����Ŀ¼·��
	 */
	public static String getSdCardAbsolutePath(){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	/**
	 *  ���SD��������Ļ���Ŀ¼
	 *  
	 * @return String����  ���SD��������Ļ���Ŀ¼
	 */
	public static String getCachePath(Context c){
			return c.getExternalCacheDir().getAbsolutePath();
	}
	/**
	 * ���ͼƬ�����ļ����ļ���
	 * 
	 * @return String���� 
	 *         �洢ͼƬ�����ļ����ļ���
	 */
	public static String getImageCacheDir(Context c){
		File file = new File(getCachePath(c) + IMAGE_PATH) ;
		if (!file.exists()) {// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	/**
	 * ��ô��¼���ļ����ļ���
	 * 
	 * @return String���� 
	 *         �洢 ¼���ļ����ļ���
	 */
	public static String getAudioCacheDir(Context c){
		File audioFile = new File(getCachePath(c)  + AUDIO_PATH) ;
		if (!audioFile.exists()) {// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			audioFile.mkdirs();
		}
		return audioFile.getAbsolutePath();
	}
	
	/**
	 * ��ô�� Ӧ��˽���ļ����ļ���
	 * 
	 * @return String���� 
	 *         ˽���ļ����ļ���
	 */
	public static String getPrivateAudioDir(Context c){
		return c.getExternalFilesDir("Audio").getAbsolutePath();
	}
	/**
	 * ��ô�� Ӧ��˽���ļ����ļ��� + Crash
	 * 
	 * @return String���� 
	 *         ˽���ļ����ļ���
	 */
	public static String getPrivateCrashDir(Context c){
		return c.getExternalFilesDir("Crash").getAbsolutePath();
	}
	
	public static String getSystemAlbumDir(){
		File file = new File(getSdCardAbsolutePath() + "/DCIM/Camera");
		if(!file.exists()) file.mkdirs();
		return file.getAbsolutePath();
	}
	
	public File getAlbumStorageDir(Context context, String albumName) { 
		// Get the directory for the app's private pictures directory. 
		    File file = new File(context.getExternalFilesDir( Environment.DIRECTORY_PICTURES), albumName); 
		    if (!file.mkdirs()) {
		        //Log.e(LOG_TAG, "Directory not created"); 
		    }
		    return file; 
	}
	/**
	 * ������ŵ�ַ
	 * @param c
	 * @return
	 */
	public static String getPrivateAttachmentDir(Context c){
		return c.getExternalFilesDir("Attachment").getAbsolutePath();
	}
	
	public static String getPrivateDbDir(Context c){
		return c.getExternalFilesDir("Db").getAbsolutePath();
	}
	
	public static String getAppRootPath(){
		File file = new File(getSdCardAbsolutePath() + ROOT_PATH) ;
		if (!file.exists()) {// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	/**
	 * sdcard/approot/download
	 * @return
	 */
	public static String getDownloadPath(){
		File file = new File(getAppRootPath() + DOWNLOAD_PATH) ;
		if (!file.exists()) {// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	/**
	 * sdcard/approot/image
	 * @return
	 */
	public static String getImagePath(){
		File file = new File(getAppRootPath() + IMAGE_PATH) ;
		if (!file.exists()) {// �˴����ܻᴴ��ʧ�ܣ��ݲ�����
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	/** 
	  * ��ȡ����ʱ�� 
	  *  
	  * @return�����ַ��ʽ yyyy_MM_dd_HH_mm_ss
	  */  
	public static String getStringDate() {  
	  Date currentTime = new Date();  
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");  
	  String dateString = formatter.format(currentTime);  
	  return dateString;  
	} 
	
	
	
	
	/**
	 * �����ı��ļ� �������Ҫ�����String���ļ���������
	 */
	public static void savedata(String data, String filename, Context context) {
		if (data == null || data.trim().equals("")) {
			return; // �������� ���Ϊ���򲻱���
		}
		FileOutputStream outStream;
		try {
			outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
			outStream.write(data.getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ��ȡ�ı���� ���filename�������򷵻�null�����򷵻���string����
	 */
	public static String loaddata(String filename, Context context) {
		// Toast.makeText(context, "������ʾ�������", Toast.LENGTH_SHORT).show();
		if (!isFileExist(filename, context)) {
			// Log.e("Tools", "loaddata" + filename + "������");
			return null;
		}
		// Log.e("Tools", "loaddata" + filename + "����!!");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			FileInputStream inStream = context.openFileInput(filename);
			byte[] buffer = new byte[10 * 1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toString();
	}
	
	public static boolean isFileExist(String filename, Context context) {
		boolean isExist = false;
		File file = context.getFileStreamPath(filename);
		if (file.exists()) {
			isExist = true;
		}
		return isExist;
	}
	
}
