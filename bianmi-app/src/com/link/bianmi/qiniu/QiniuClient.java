package com.link.bianmi.qiniu;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.link.bianmi.MyApplication;
import com.link.bianmi.utils.NetWorkHelper.NetWorkType;
import com.qiniu.auth.Authorizer;
import com.qiniu.io.IO;
import com.qiniu.rs.CallBack;
import com.qiniu.rs.CallRet;
import com.qiniu.rs.PutExtra;
import com.qiniu.rs.UploadCallRet;

/** 七牛客户端封装 **/
public class QiniuClient {

	// private static BasicNameValuePair TokenPair;
	private static HashMap<String, BasicNameValuePair> TokenMap = new HashMap<String, BasicNameValuePair>();

	private OnListener mListener;

	public static final int TokenExpiresIn = 3600 * 24;

	/**
	 * 上传
	 * 
	 * @param @param filePath 文件路径
	 * @param @param key 上传的key
	 * @param @param bucketName 上传空间
	 * @param @param cover 是否覆盖上传
	 * @param @param onlyWifi 是否只在wifi上传
	 * @throws
	 */
	public void doUpload(String filePath, final String key, String bucketName,
			boolean cover, boolean onlyWifi) {
		doUpload(Uri.fromFile(new File(filePath)), key, bucketName, cover,
				onlyWifi);
	}

	public void doUpload(final Uri uri, final String key,
			final String bucketName, final boolean cover, boolean onlyWifi) {
		doUpload(uri, key, bucketName, cover, onlyWifi, "");
	}

	/**
	 * 上传
	 * 
	 * @param @param uri 文件路径
	 * @param @param key 上传的key
	 * @param @param bucketName 上传空间
	 * @param @param cover 是否覆盖上传
	 * @param @param onlyWifi 是否只在wifi上传
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	public void doUpload(final Uri uri, final String key,
			final String bucketName, final boolean cover, boolean onlyWifi,
			String token) {
		if (MyApplication.getInstance().getNetwork() == NetWorkType.NET_INVALID) {
			if (mListener != null) {
				mListener.onFailure(new Exception("NO NETWORK"));
			}
			return;
		}

		if (onlyWifi
				&& MyApplication.getInstance().getNetwork() != NetWorkType.NET_WIFI) {// 仅在WIFI上传
			if (mListener != null) {
				mListener.onFailure(new Exception("NO WIFI"));
			}
			return;
		}

		if (!TextUtils.isEmpty(token)) {
			startUpload(uri, key, bucketName, cover, token);
			return;
		}

		// token已经存在
		BasicNameValuePair tokenPair = TokenMap.get(bucketName);
		if (!cover
				&& tokenPair != null
				&& tokenPair.getName().compareTo(
						String.valueOf(new Date().getDate())) == 0) {
			startUpload(uri, key, bucketName, cover, tokenPair.getValue());
			return;
		}

		// 子线程：直接执行上传
		if (Looper.myLooper() != Looper.getMainLooper()) {
			startUpload(uri, key, bucketName, cover, "");
			return;
		}

		mTokenTemp = "";
		final Handler f_handler = new Handler();
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (TextUtils.isEmpty(mTokenTemp)) {
						if (mListener != null) {
							mListener.onFailure(new Exception("token error"));
						}
					} else {
						startUpload(uri, key, bucketName, cover, mTokenTemp);
					}
				} catch (Exception ex) {
				}
			}

		};

		new Thread() {
			@Override
			public void run() {

				try {
					if (!cover) { // 非覆盖上传
						mTokenTemp = QiniuHelper.API.getUpLoadToken(bucketName);
						BasicNameValuePair tokenPair = new BasicNameValuePair(
								String.valueOf(new Date().getDate()),
								mTokenTemp);
						TokenMap.put(bucketName, tokenPair);
					} else { // 覆盖上传
						mTokenTemp = QiniuHelper.API.getUpLoadToken(bucketName);
					}
				} catch (Exception ex) {
				} finally {
					f_handler.post(runnable);
				}
			}
		}.start();

	}

	private String mTokenTemp = "";

	@SuppressWarnings("deprecation")
	private void startUpload(Uri uri, final String key,
			final String bucketName, final boolean cover, String token) {

		if (TextUtils.isEmpty(token)) { // 没有token，重api获取token
			try {
				if (!cover) { // 非覆盖上传
					token = QiniuHelper.API.getUpLoadToken(bucketName);
					BasicNameValuePair tokenPair = new BasicNameValuePair(
							String.valueOf(new Date().getDate()), token);
					TokenMap.put(bucketName, tokenPair);
				} else { // 覆盖上传
					token = QiniuHelper.API.getUpLoadToken(bucketName + ":"
							+ key);
				}
			} catch (Exception ex) {
			}
		}

		if (TextUtils.isEmpty(token)) {
			mListener.onFailure(new Exception("token error"));
		}

		PutExtra extra = new PutExtra();
		extra.checkCrc = PutExtra.AUTO_CRC32;
		extra.params.put("x:arg", "value");
		Authorizer auth = new Authorizer();
		auth.setUploadToken(token);
		IO.putFile(MyApplication.getInstance(), auth, key, uri, extra,
				new CallBack() {

					@Override
					public void onSuccess(UploadCallRet ret) {
						try {
							if (mListener != null) {
								mListener.onComplete(key);
							}
						} catch (Exception ex) {
						}
					}

					@Override
					public void onProcess(long current, long total) {

					}

					@Override
					public void onFailure(CallRet ret) {

						try {
							if ((ret.getException().getMessage()
									.contains("401") || ret.getException()
									.getMessage().contains("400"))
									&& !cover) {
								TokenMap.remove(bucketName);
							}
							if (mListener != null) {
								mListener.onFailure(ret.getException());
							}
						} catch (Exception e) {
						}
					}
				});
		// IO.putFile(BianmiApplication.getInstance(), token, key, uri, extra,
		// new JSONObjectRet() {
		// @Override
		// public void onSuccess(JSONObject resp) {
		//
		// try{
		// if(mListener!=null){
		// mListener.onComplete(key);
		// }
		// }catch(Exception ex){}
		// }
		//
		// @Override
		// public void onFailure(Exception ex) {
		// try{
		// if ((ex.getMessage().contains("401") ||
		// ex.getMessage().contains("400")) && !cover) {
		// TokenMap.remove(bucketName);
		// }
		// if(mListener!=null){
		// mListener.onFailure(ex);
		// }
		// }catch(Exception e){}
		// }
		// });
	}

	/** 设置监听器 **/
	public void setOnListener(OnListener l) {
		mListener = l;
	}

	/** 监听 **/
	public interface OnListener {
		/** 上传完成 **/
		public void onComplete(String key);

		/** 上传失败 **/
		public void onFailure(Exception ex);
	}

}
