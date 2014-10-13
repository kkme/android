package com.link.bianmi.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.link.bianmi.BianmiApplication;

public class Tools {
	public final static String SDCARD_MNT = "/mnt/sdcard";
	@SuppressLint("SdCardPath")
	public final static String SDCARD = "/sdcard";

	// 验证邮箱是否合法
	public static boolean isEmail(String mEmail) {
		String mPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(mPattern);
		Matcher m = p.matcher(mEmail);
		return m.matches();
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * 通过uri获取文件的绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getAbsoluteImagePath(Activity context, Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}

		return imagePath;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (Tools.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 检测前置摄像头
	 */
	public static Boolean hasFaceCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				return true;
			}
		}
		return false;
	}

	/** 从JSON获取Long **/
	public static long getLongSafe(JSONObject jsonObject, String str) {
		long result = 0;
		try {
			result = jsonObject.getLong(str);
		} catch (Exception ex) {
		}
		return result;
	}

	/** 判断程序已经安装多少了天 **/
	public static int getHasInstalledTime(Context mContext) {
		PackageManager pm = mContext.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = pm.getApplicationInfo("com.liulishuo.engzo", 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String appFile = appInfo.sourceDir;
		long installedTime = new File(appFile).lastModified();
		long nowTime = System.currentTimeMillis();
		int timeBetween = (int) ((((nowTime - installedTime) / 1000) / 3600) / 24);
		return timeBetween;
	}

	public static Bitmap createBitmapSafe(int width, int height,
			Bitmap.Config config) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, config);
		} catch (OutOfMemoryError e) {
			Log.e("test", "OutOfMemoryError");
			try {
				System.gc();
				System.runFinalization();
				bitmap = Bitmap.createBitmap(width, height, config);
			} catch (Exception ex) {

				Log.e("test", "OutOfMemoryError");
			}

		}

		return bitmap;
	}

	/** 复制asset文件到指定目录 **/
	public static boolean copyAsset(String filename, String strOutFileName) {
		boolean result = false;
		try {
			InputStream myInput;
			OutputStream myOutput = new FileOutputStream(strOutFileName);
			myInput = BianmiApplication.getInstance().getAssets()
					.open(filename);
			byte[] buffer = new byte[1024];
			int length = myInput.read(buffer);
			while (length > 0) {
				myOutput.write(buffer, 0, length);
				length = myInput.read(buffer);
			}

			myOutput.flush();
			myInput.close();
			myOutput.close();
			result = true;
		} catch (Exception ex) {
		}

		return result;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {

		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 返回应用当前版本名称
	 * 
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

}