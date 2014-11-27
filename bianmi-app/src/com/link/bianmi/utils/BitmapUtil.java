package com.link.bianmi.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.link.bianmi.widget.SuperToast;

public class BitmapUtil {

	private static final int max_size = 500;// ����bitmap��С ��Ȼ��oom

	/**
	 * ��ȡ����ͼ ������500*500
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getThumbnailBitmapFromPath(String filePath) {
		if (!FileUtil.haveSdCard()) {
			return null;
		}
		Bitmap bitmap = null;
		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			// ����inJustDecodeBoundsΪtrue
			options.inJustDecodeBounds = true;
			// ʹ��decodeFile�����õ�ͼƬ�Ŀ�͸�
			BitmapFactory.decodeFile(filePath, options);
			int reqWidth = max_size;
			int reqHeight = max_size;
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filePath, options);// , options);
			return bitmap;
			// this.setImageBitmap(bitmap);
			// hasUrlBitmap = true;
		} catch (java.lang.OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// UiUtils.showToast(getContext(),
			// "error in decodeFile:\n" + e.toString());
		}
		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		Log.e("TEST", "inSampleSize-->" + inSampleSize);
		return inSampleSize;
	}

	/**
	 * ����bitmap��ϵͳ���
	 * 
	 * @param bitmap
	 * @param name
	 * @return
	 */
	public static boolean saveBitmap2SystemAlbum(Bitmap bitmap, String name) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DCIM/Camera/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		path = path + name;
		return saveBitmapFile(bitmap, path);
	}

	/**
	 * ����bitmap�ļ�
	 * 
	 * @param bitmap
	 * @param path
	 * @return
	 */
	public static boolean saveBitmapFile(Bitmap bitmap, String path) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}
		if (bitmap == null) {
			return false;
		}
		File file = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
				return true;
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	// public static Bitmap setAlpha(Bitmap sourceImg, int number) {
	//
	// int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
	//
	// sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
	// sourceImg.getWidth(), sourceImg.getHeight());
	//
	// // ���ͼƬ��ARGBֵ
	//
	// number = number * 255 / 100;
	//
	// for (int i = 0; i < argb.length; i++) {
	// argb = (number << 24) | (argb & 0x00FFFFFF);
	// // �޸����2λ��ֵ
	// }
	//
	// sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
	// sourceImg.getHeight(), Config.ARGB_8888);
	//
	// return sourceImg;
	//
	// }

	public static class SaveBitampTask extends AsyncTask<Bitmap, Void, Boolean> {

		private String path;
		private Dialog dialog;

		public SaveBitampTask(String path, Dialog dialog) {
			super();
			this.path = path;
			this.dialog = dialog;
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					// SaveBitampTask.this.cancel(true);
				}
			});
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Bitmap... params) {
			// TODO Auto-generated method stub
			return saveBitmapFile(params[0], path);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				SuperToast.makeText(dialog.getContext(), "Save success.",
						SuperToast.LENGTH_SHORT).show();
			} else {
				SuperToast.makeText(dialog.getContext(), "Save failed.",
						SuperToast.LENGTH_SHORT).show();
			}
		}
	}

}
