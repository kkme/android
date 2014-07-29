package com.link.bianmi.utility;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.SysConfig;

public class CameraCrop {

	
	public static final int REQUEST_CAMERA = 11001; 
	public static final int REQUEST_CALENDAR = 11002;
	public static final int REQUEST_AFTER_CROP=11003;

	private  String FILE_SAVEPATH = SysConfig.getInstance().getPathTemp()+File.separator;
	private Activity mContext;
	private Fragment mFragment;
	private String protraitPath="";
	

	
	public  CameraCrop(Activity context){
		this(context,null);
	}	
	
	public  CameraCrop(Activity context,Fragment fragment){
		mContext=context;
		mFragment=fragment;
	}
	
	

	private void startActivityForResult(Intent intent,int resultCode){
		if(mFragment!=null){
			mFragment.startActivityForResult(intent, resultCode);  
		}else{
			mContext.startActivityForResult(intent, resultCode);  
		}
	}
	
	

	
	
	/**开始拍照**/
	public void startActionCamera() {
//		Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");  
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);        
		if (Tools.hasFaceCamera()) {
			//前置摄像头
			intent.putExtra("camerasensortype", 2);
		}
		//设置照片的质量
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getCameraTempFile());
		startActivityForResult(intent, REQUEST_CAMERA);  
	}
	
	
	/**重相册选择**/
	@TargetApi(19)
	public void startImagePick() {
		if (Build.VERSION.SDK_INT <19){
		    Intent intent = new Intent(); 
		    intent.setType("image/*");
		    intent.setAction(Intent.ACTION_GET_CONTENT);
		    startActivityForResult(Intent.createChooser(intent, mContext.getString(R.string.photo_pick)),REQUEST_CALENDAR);

		} else {
		    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		    intent.addCategory(Intent.CATEGORY_OPENABLE);
		    intent.setType("image/*");
		    startActivityForResult(intent, REQUEST_CALENDAR);
		}	
	}
    
	
	



	/**
	 * 	开始拍照
	 * @param title
	 * 		对话框标题
	 * @param resultCode
	 * @param outputX
	 * 		裁剪宽度
	 * @param outputY
	 * 		裁剪高度
	 */
	public void chose(String title) {

		protraitPath="";
		CharSequence[] items = { mContext.getString(R.string.photo_from_camera),
				mContext.getString(R.string.photo_from_album) };
		AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
		if(!TextUtils.isEmpty(title))
			builder.setTitle(title);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 拍照
				if (item == 0) {
					startActionCamera();
				}
				// 相册
				else if (item == 1) {
					startImagePick();
				}
			}
		});

		AlertDialog imageDialog = builder.create();
       imageDialog.setCanceledOnTouchOutside(true);
       imageDialog.show();	
	}
	
	
	/**
	 * 裁剪
	 * @param data
	 * @param outputX
	 * 		图片宽度
	 * @param outputY
	 * 		图片高度
	 * @param resultCode
	 */
	@TargetApi(19)
	public void startActionCrop(Uri uri,int outputX,int outputY) {
		
		if (Build.VERSION.SDK_INT >=19 && DocumentsContract.isDocumentUri(mContext, uri)){	//android 4.4开始的新方法
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    sel, new String[] { id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                String filePath = cursor.getString(columnIndex);
                uri=Uri.fromFile(new File(filePath));
            }
            cursor.close();
			
        }		

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//intent.putExtra("output", getCorpTempFile(data));
		intent.putExtra("output", getCameraTempFile());
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);// 输出图片大小
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent,REQUEST_AFTER_CROP);
		
	}
	
	
	
	/**裁剪**/
	public void startActionCrop(int outputX,int outputY) {
		startActionCrop(Uri.fromFile(new File(protraitPath)),outputX,outputY);
	}
	
	
	/**获取uri**/
	public Uri getUri(Intent data){
		Uri uri=null;
		if(!TextUtils.isEmpty(protraitPath)){
			uri=  Uri.fromFile(new File(protraitPath));
		}else{
			uri=data.getData();
			if (Build.VERSION.SDK_INT >=19 && DocumentsContract.isDocumentUri(mContext, uri)){	//android 4.4开始的新方法
	            String wholeID = DocumentsContract.getDocumentId(uri);
	            String id = wholeID.split(":")[1];
	            String[] column = { MediaStore.Images.Media.DATA };
	            String sel = MediaStore.Images.Media._ID + "=?";
	            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
	                    sel, new String[] { id }, null);
	            int columnIndex = cursor.getColumnIndex(column[0]);
	            if (cursor.moveToFirst()) {
	                String filePath = cursor.getString(columnIndex);
	                uri=Uri.fromFile(new File(filePath));
	            }
	            cursor.close();
	        }	
		}
		return uri;
	}

	
	
	
	
	// 拍照保存的绝对路径
	private Uri getCameraTempFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.photo_sd_error), Toast.LENGTH_SHORT).show();
			return null;
		}

		// 照片命名
		protraitPath =FILE_SAVEPATH +String.valueOf(System.currentTimeMillis()) + ".jpg";
		// 裁剪头像的绝对路径
		return Uri.fromFile(new File(protraitPath));

	}	
	
	
	public String getCorpFile(){
		return protraitPath;
	}
	
}
