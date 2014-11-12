
package com.link.bianmi.imageloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.HttpException;
import com.link.bianmi.http.Response;

/**
 * 延迟加载图片 
 */
public class ImageManager implements ImageCache {
	private static final String TAG = "ImageManager";

	// 目前最大宽度支持596px, 超过则同比缩小
	// 最大高度为1192px, 超过从中截取
	public static final int DEFAULT_COMPRESS_QUALITY = 90;
	public static final int IMAGE_MAX_WIDTH = 720;
	public static final int IMAGE_MAX_HEIGHT = 1280;

	private Context mContext;
//	/** 默认图片 **/
//	private Bitmap mDefaultBitmap;
	// In memory cache.
	private Map<String, SoftReference<Bitmap>> mCache;	//SoftReference内存足时时，GC会回收SoftReference所引用的对象
	// MD5 hasher.
	private MessageDigest mDigest;
	
	//文件存储地址目录
	private String mFileDir="";
	
	
	private HttpClient mHttpClient;
	
	
	
//	/** 加载模式:显示优先 **/
//	public static final int LoadModule_ShowFirst=1;
//	/** 加载模式:速度优先：  如果文件都在本地或者网络情况很好时可以用此模式 **/
//	public static final int LoadModule_SpeedFirst=2;
	/** 加载模式:
	 * 	true:显示优先  
	 * 	false:速度优先 (图片在本地或者网络状况良好时可以用此模式加快速度)
	 *  **/
	private boolean  mShowFirst=true;	
	
	

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	
	
	public ImageManager(Context context,String fileDir){
		this(context,fileDir,true);
	}
	

	/**
	 * 图片管理器
	 * @param context
	 * @param firDir
	 * 		文件夹路径，  为空则使用包内地址
	 * @param showFirst
	 * 		加载模式
	 * 		true:显示优先  
	 * 		false:速度优先 (图片在本地或者网络状况良好时可以用此模式加快速度)
	 */
	public ImageManager(Context context,String fileDir,boolean showFirst) {
		mContext = context;
		mShowFirst=showFirst;
		if(fileDir==null || TextUtils.isEmpty(fileDir)){
			fileDir=mContext.getFilesDir().getPath();
		}
		mFileDir=fileDir+File.separator+"imageloader";
		File dir = new File(mFileDir);
		if (!dir.exists()) {
			dir.mkdirs(); 
		}
		mCache = new HashMap<String, SoftReference<Bitmap>>();
		mHttpClient=new HttpClient(); 
		try {
			mDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No MD5 algorithm.");
		}
	}


	private String getHashString(MessageDigest digest) {
		StringBuilder builder = new StringBuilder();

		for (byte b : digest.digest()) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}

		return builder.toString();
	}

	// MD5 hases are used to generate filenames based off a URL.
	private String getMd5(String url) {
		mDigest.update(url.getBytes());

		return getHashString(mDigest);
	}


	// Looks to see if an image is in the file system.
	private Bitmap lookupFile(String url) {
		String hashedUrl = getMd5(url);
		return getBitmap(mFileDir+File.separator+hashedUrl);

	}
	
	
	/**
     * 以最小内存获取资源图片
     * @param resId
     * @return
     */
    private  Bitmap getBitmap(String path){
    	return getBitmapSafe(path,true);	
    }
	
    
    
    private Bitmap getBitmapSafe(String path,boolean quality){
    	
    	Bitmap bitmap=null;
    	FileInputStream fis=null;
    	
    	boolean tryLow=false;
        try { 
        	fis= new FileInputStream(path);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            opt.inSampleSize = 1;//computeSampleSize(opt, -1, IMAGE_MAX_WIDTH*IMAGE_MAX_HEIGHT);  
            opt.inJustDecodeBounds = false;
            opt.inPreferredConfig =quality ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(fis, null, opt);
        } 
        catch (FileNotFoundException e) {}
        catch(OutOfMemoryError ex){
        	tryLow=true;
        }
        catch(Exception ex){}
        finally{
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {}
			}
        }
        
        if(bitmap==null && quality && tryLow){
        	bitmap=getBitmapSafe(path,false);
        }
        return bitmap;      
        
    }
    
	
	private Bitmap downloadImage(String url) throws HttpException {
		Bitmap bitmap=null;
		
		try{
			Response res = mHttpClient.doGet(url);
			String file = writeToFile(res.asStream(), getMd5(url));
			bitmap=getBitmap(file);
		}catch(HttpException ex){
			throw ex;
		}
		catch(Exception ex){}
		return bitmap;
		
	}

	/**
	 * 下载远程图片 -> 转换为Bitmap -> 写入缓存器.
	 * 
	 * @param url
	 * @param quality
	 *            image quality 1～100
	 * @throws HttpException
	 */
	public void put(String url, int quality, boolean forceOverride)
			throws HttpException {
		if (!forceOverride && contains(url)) {
			return;
		}

		Bitmap bitmap=null;
		try{
			bitmap = downloadImage(url);
		}catch(Exception ex){}
		
		if (bitmap != null) {
			put(url, bitmap, quality); // file cache
		}
	}

	/**
	 * 重载 put(String url, int quality)
	 * 
	 * @param url
	 * @throws HttpException
	 */
	public void put(String url) throws HttpException {
		put(url, DEFAULT_COMPRESS_QUALITY, false);
	}

	/**
	 * 将本地File -> 转换为Bitmap -> 写入缓存器. 如果图片大小超过MAX_WIDTH/MAX_HEIGHT, 则将会对图片缩放.
	 * 
	 * @param file
	 * @param quality
	 *            图片质量(0~100)
	 * @param forceOverride
	 * @throws IOException
	 */
	public void put(File file, int quality, boolean forceOverride)
			throws IOException {
		if (!file.exists()) {
			Log.w(TAG, file.getName() + " is not exists.");
			return;
		}
		if (!forceOverride && contains(file.getPath())) {
			// Image already exists.
			Log.d(TAG, file.getName() + " is exists");
			return;
		}

		Bitmap bitmap= getBitmap(file.getPath());
	//	Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
		// bitmap = resizeBitmap(bitmap, MAX_WIDTH, MAX_HEIGHT);

		if (bitmap == null) {
			Log.w(TAG, "Retrieved bitmap is null.");
		} else {
			put(file.getPath(), bitmap, quality);
		}
	}

	/**
	 * 将Bitmap写入缓存器.
	 * 
	 * @param filePath
	 *            file path
	 * @param bitmap
	 * @param quality
	 *            1~100
	 */
	public void put(String file, Bitmap bitmap, int quality) {
		synchronized (this) {
			mCache.put(file, new SoftReference<Bitmap>(bitmap));
		}

		writeFile(file, bitmap, quality);
	}

	/**
	 * 重载 put(String file, Bitmap bitmap, int quality)
	 * 
	 * @param filePath
	 *            file path
	 * @param bitmap
	 * @param quality
	 *            1~100
	 */
	@Override
	public void put(String file, Bitmap bitmap) {
		put(file, bitmap, DEFAULT_COMPRESS_QUALITY);
	}

	/**
	 * 将Bitmap写入本地缓存文件.
	 * 
	 * @param file
	 *            URL/PATH
	 * @param bitmap
	 * @param quality
	 */
	private void writeFile(String file, Bitmap bitmap, int quality) {
		if (bitmap == null) {
			Log.w(TAG, "Can't write file. Bitmap is null.");
			return;
		}

		BufferedOutputStream bos = null;
		try {
			String hashedUrl = getMd5(file);
			bos = new BufferedOutputStream(new FileOutputStream(mFileDir+File.separator+hashedUrl));	
		//	bos = new BufferedOutputStream(mContext.openFileOutput(hashedUrl,Context.MODE_PRIVATE));
			
			
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos); // PNG
			Log.d(TAG, "Writing file: " + file);
		} catch (IOException ioe) {
			Log.e(TAG, ioe.getMessage());
		} finally {
			try {
				if (bos != null) {
					bitmap.recycle();
					bos.flush();
					bos.close();
				}
				// bitmap.recycle();
			} catch (IOException e) {
				Log.e(TAG, "Could not close file.");
			}
		}
	}

	private String writeToFile(InputStream is, String filename) {
		Log.d("LDS", "new write to file");
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(is);
			out =new BufferedOutputStream(new FileOutputStream(mFileDir+File.separator+filename));
//			out = new BufferedOutputStream(mContext.openFileOutput(filename,Context.MODE_PRIVATE));
			byte[] buffer = new byte[1024];
			int l;
			while ((l = in.read(buffer)) != -1) {
				out.write(buffer, 0, l);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null) {
					Log.d("LDS", "new write to file to -> " + filename);
					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		//return mContext.getFilesDir() + "/" + filename;
		return mFileDir+File.separator+filename;
	}

	
	/**从文件加载图片**/
	private Bitmap cacheFromFile(String filePath){
		Bitmap bitmap = lookupFile(filePath);
		if (bitmap != null) {
			synchronized (this) {
				mCache.put(filePath, new SoftReference<Bitmap>(bitmap));
			}
		}
		return bitmap;
	}


	/**
	 * 获得指定file/URL对应的Bitmap，首先找本地文件，如果有直接使用，否则去网上获取
	 * 
	 * @param file
	 *            file URL/file PATH
	 * @param bitmap
	 * @param quality
	 * @throws HttpException
	 */
	public Bitmap getFromUrl(String url) throws HttpException {
		
		Bitmap bitmap = get(url);
		//缓存中没有找文件
		
		if(bitmap==null && !mShowFirst){
			bitmap=cacheFromFile(url);
		}

		if (bitmap == null) {
			bitmap = downloadImage(url);
		}	
		return bitmap;
	}
	

	/**
	 * 从缓存器中读取文件
	 * 
	 * @param file
	 *            file URL/file PATH
	 * @param bitmap
	 * @param quality
	 */
	public Bitmap get(String filePath) {
		SoftReference<Bitmap> ref;
		Bitmap bitmap=null;

		// 缓存中获取
		synchronized (this) {
			ref = mCache.get(filePath);
		}
		if (ref != null) {
			bitmap = ref.get();
			if(bitmap==null){
				synchronized (this) {
					ref = mCache.remove(filePath);
				}			
			}
		}
		
		if(bitmap==null && mShowFirst){
			bitmap=cacheFromFile(filePath);
		}
		
		return bitmap;
	}
	
	
	private boolean contains(String url) {
		//return get(url) != mDefaultBitmap;
		return mCache.containsKey(url);
	}

	public void clear() {
		String[] files = mContext.fileList();

		for (String file : files) {
			mContext.deleteFile(file);
		}

		synchronized (this) {
			mCache.clear();
			
		}
	}

	public void cleanup(HashSet<String> keepers) {
		String[] files = mContext.fileList();
		HashSet<String> hashedUrls = new HashSet<String>();

		for (String imageUrl : keepers) {
			hashedUrls.add(getMd5(imageUrl));
		}

		for (String file : files) {
			if (!hashedUrls.contains(file)) {
				Log.d(TAG, "Deleting unused file: " + file);
				mContext.deleteFile(file);
			}
		}
	}

	public void clearCache(){
		synchronized (this) {
			mCache.clear();
			File dir = new File(mFileDir);
			if (!dir.exists()) {
				dir.mkdirs(); 
			}
		}
	}


}
