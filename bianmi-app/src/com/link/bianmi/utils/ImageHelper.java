package com.link.bianmi.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;

/**
 * 图片帮助类
 * 
 * 
 */
public class ImageHelper {

	public static Bitmap decodeFile(String filePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		try {
			bitmap = BitmapFactory.decodeFile(filePath, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeFile(filePath);
		}

		return bitmap;
	}

	/**
	 * 尝试保存图片（按比例缩放）
	 * 		失败则直接复制
	 * 
	 * @param fromFile
	 * @param toFile
	 * @param maxWH
	 *            最大宽和高
	 * @param quality
	 *            图片质量
	 */
	public static void saveImage(String fromFile, String toFile, int maxWH,
			int quality) {
		try {
			if (quality > 0) {
				Bitmap bitmap = null;
				bitmap = decodeFile(fromFile);
				int picWidth = bitmap.getWidth();
				int picHeight = bitmap.getHeight();
				int maxwidth = picWidth > picHeight ? picWidth : picHeight;
				// 缩放图片的尺寸
				float scale = 1.0f;
				if (maxwidth > maxWH)
					scale = (float) maxWH / maxwidth;

				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				// 产生缩放后的Bitmap对象
				Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						picWidth, picHeight, matrix, false);
				// save file
				File myCaptureFile = new File(toFile);
				FileOutputStream out = new FileOutputStream(myCaptureFile);
				if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality,
						out)) {
					out.flush();
					out.close();
				}
				if (!bitmap.isRecycled()) {
					bitmap.recycle();// 记得释放资源，否则会内存溢出
				}
				if (!resizeBitmap.isRecycled()) {
					resizeBitmap.recycle();
				}
			} else {
				FileHelper.copyFile(fromFile, toFile);
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				if(!new File(toFile).exists())
					FileHelper.copyFile(fromFile, toFile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}


	public static Bitmap getResizedImage(String imageFile, int width, int height) {
		File file = new File(imageFile);
		if (file.exists())
			return getResizedImage(file, width, height);
		else
			return null;

	}

	public static Bitmap getResizedImage(File dst, int width, int height) {
		if (null != dst && dst.exists()) {
			BitmapFactory.Options opts = null;
			if (width > 0 && height > 0) {
				opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(dst.getPath(), opts);
				// 计算图片缩放比例
				final int minSideLength = Math.min(width, height);
				opts.inSampleSize = computeSampleSize(opts, minSideLength,
						width * height);
				opts.inJustDecodeBounds = false;
				opts.inInputShareable = true;
				opts.inPurgeable = true;
			}
			try {
				return BitmapFactory.decodeFile(dst.getPath(), opts);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	

	/** 获取指定大小图片 **/
	public  static Bitmap  getScaleImg(Bitmap bm, int newWidth, int newHeight) {
		
		if(bm.getWidth()==newWidth && bm.getHeight()==newHeight)
			return bm;
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth1 = newWidth;
        int newHeight1 = newHeight;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth1) / width;
        float scaleHeight = ((float) newHeight1) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        	return newbm;
       }
	
	
	
	/** 获取指定大小图片: 保持宽度比例 **/
	public  static Bitmap  getScaleImg(Bitmap bm, int newWidth) {
		
		if(bm.getWidth()==newWidth)
			return bm;
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth1 = newWidth;
        int newHeight1 = height *newWidth1/width;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth1) / width;
        float scaleHeight = ((float) newHeight1) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        	return newbm;
       }	
	
	
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 获取图片
	 */
	public static Bitmap getImage(String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, options); // 此时返回myBitmap为空
		// 计算缩放比
		int be = (int) (options.outHeight / (float) 200);
		int ys = options.outHeight % 200;// 求余数
		float fe = ys / (float) 200;
		if (fe >= 0.5)
			be = be + 1;
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		options.inJustDecodeBounds = false;
		myBitmap = BitmapFactory.decodeFile(imagePath, options);
		// .setImageBitmap(myBitmap);
		return myBitmap;
	}

	/**
	 * 获取自定义大小图片
	 */
	public static Bitmap getResizedImage(String imagePath, int targetWidth) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imagePath, options);

			options.inSampleSize = sampleSize(options.outWidth, targetWidth);
			options.inJustDecodeBounds = false;
			options.inInputShareable = true;
			options.inPurgeable = true;
			Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
			return bm;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 动态计算缩放比例
	 */
	private static int sampleSize(int width, int target) {
		int result = 1;
		for (int i = 0; i < 10; i++) {
			if (width < target * 2) {
				break;
			}
			width = width / 2;
			result = result * 2;
		}
		return result;
	}

	/**
	 * 通过文件名 获取视频的缩略图
	 * 
	 * @param context
	 * @param videopath
	 * @return
	 */
	public static Bitmap getVideoThumbnail(Context context, String videopath) {
		ContentResolver cr = context.getContentResolver();
		String[] projection = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, };
		String whereClause = MediaStore.Video.Media.DATA + " = '" + videopath
				+ "'";
		Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				projection, whereClause, null, null);
		int _id = 0;
		String videoPath = "";
		if (cursor == null || cursor.getCount() == 0) {
			return null;
		}
		if (cursor.moveToFirst()) {
			int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
			int _dataColumn = cursor
					.getColumnIndex(MediaStore.Video.Media.DATA);
			do {
				_id = cursor.getInt(_idColumn);
				videoPath = cursor.getString(_dataColumn);
				System.out.println(_id + " " + videoPath);
			} while (cursor.moveToNext());
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, _id,
				Images.Thumbnails.MICRO_KIND, options);
		return bitmap;
	}

	/**
	 * 字符串转Bitmap
	 * 
	 * @param strImg
	 * @return
	 */
	public static Bitmap string2Bitmap(String strImg) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(strImg, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * Bitmap转字符串
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmap2String(Bitmap bitmap) {
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	public static String bitmap2StringWithNoWrap(Bitmap bitmap) {
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.NO_WRAP);
		return string;
	}

	
	
	
	
	public static final int Corner_ALL = 0;    
    public static final int Corner_TOP = 1;    
    public static final int Corner_LEFT = 2;    
    public static final int Corner_RIGHT = 3;    
    public static final int Corner_BOTTOM = 4;
	
    /**
     * 图片切圆角，方向自由
     * @param corner	
     * 		Corner_ALL=0，全部
     * 		Corner_TOP=1，上半部
     * 		Corner_LEFT，左半部
     * 		Corner_RIGHT，右版
     * 		Corner_BOTTOM，下版本
     * @param bitmap
     * @param roundPx
     * 		圆角弧度,像素
     * @return
     */
	public static Bitmap fillet(int corner,Bitmap bitmap,int roundPx) {  
		
		if(bitmap==null)
			return null;
		return fillet(corner,bitmap,roundPx,bitmap.getWidth(),bitmap.getHeight());
	}  
	
//	/**
//	 * 图片切圆角，方向自由
//     * @param corner	
//     * 		Corner_ALL=0，全部
//     * 		Corner_TOP=1，上半部
//     * 		Corner_LEFT，左半部
//     * 		Corner_RIGHT，右版
//     * 		Corner_BOTTOM，下版本
//	 * @param bitmap
//	 * @param context
//	 * @param roundDp
//	 * 		圆角弧度,DP
//	 * @param width
//	 * @param height
//	 * @return
//	 */
//	public static Bitmap fillet(int corner,Bitmap bitmap,Context context,float roundDp,final int width,final int height){
//		return fillet(corner,bitmap,DensityHelper.dip2px(context,roundDp), width,height);
//	}
	
	/**
	 * 图片切圆角，方向自由
     * @param corner	
     * 		Corner_ALL=0，全部
     * 		Corner_TOP=1，上半部
     * 		Corner_LEFT，左半部
     * 		Corner_RIGHT，右版
     * 		Corner_BOTTOM，下版本
	 * @param bitmap
     * @param roundPx
     * 		圆角弧度,像素
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap fillet(int corner,Bitmap bitmap,int roundPx,final int width,final int height) {  
		try {  
			// 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板  
			// 然后在画板上画出一个想要的形状的区域。  
			// 最后把源图片帖上。  
//			final int width = bitmap.getWidth();  
//			final int height = bitmap.getHeight();  
			Bitmap paintingBoard = Bitmap.createBitmap(width,height, Config.ARGB_8888);  
			Canvas canvas = new Canvas(paintingBoard);  
			canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);  

			final Paint paint = new Paint();  
			paint.setAntiAlias(true);  
			paint.setColor(Color.BLACK);     

			if( Corner_TOP == corner ){  
				clipTop(canvas,paint,roundPx,width,height);  
			}else if( Corner_LEFT == corner ){  
				clipLeft(canvas,paint,roundPx,width,height);  
			}else if( Corner_RIGHT == corner ){  
				clipRight(canvas,paint,roundPx,width,height);  
			}else if( Corner_BOTTOM == corner ){  
				clipBottom(canvas,paint,roundPx,width,height);  
			}else{  
				clipAll(canvas,paint,roundPx,width,height);  
			}  

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
			//帖子图  
			final Rect src = new Rect(0, 0, width, height);  
			final Rect dst = src;  
			canvas.drawBitmap(bitmap, src, dst, paint);     
			return paintingBoard;  
		} catch (Exception ex) { }  
		return bitmap;  
	}	
	
	
	 private static void clipLeft(final Canvas canvas,final Paint paint,int offset,int width,int height){  
	        final Rect block = new Rect(offset,0,width,height);  
	        canvas.drawRect(block, paint);  
	        final RectF rectF = new RectF(0, 0, offset * 2 , height);  
	        canvas.drawRoundRect(rectF, offset, offset, paint);  
	    }  
	      
	    private static void clipRight(final Canvas canvas,final Paint paint,int offset,int width,int height){  
	        final Rect block = new Rect(0, 0, width-offset, height);  
	        canvas.drawRect(block, paint);  
	        final RectF rectF = new RectF(width - offset * 2, 0, width , height);  
	        canvas.drawRoundRect(rectF, offset, offset, paint);  
	    }  
	      
	    private static void clipTop(final Canvas canvas,final Paint paint,int offset,int width,int height){  
	        final Rect block = new Rect(0, offset, width, height);  
	        canvas.drawRect(block, paint);  
	        final RectF rectF = new RectF(0, 0, width , offset * 2);  
	        canvas.drawRoundRect(rectF, offset, offset, paint);  
	    }  
	      
	    private static void clipBottom(final Canvas canvas,final Paint paint,int offset,int width,int height){  
	        final Rect block = new Rect(0, 0, width, height - offset);  
	        canvas.drawRect(block, paint);  
	        final RectF rectF = new RectF(0, height - offset * 2 , width , height);  
	        canvas.drawRoundRect(rectF, offset, offset, paint);  
	    }  
	      
	    private static void clipAll(final Canvas canvas,final Paint paint,int offset,int width,int height){  
	        final RectF rectF = new RectF(0, 0, width , height);  
	        canvas.drawRoundRect(rectF, offset, offset, paint);  
	    }  
	    
	    
	    
	    
	    
		/**
		 * 将bitmap转化为drawable
		 * 
		 * @param bitmap
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static Drawable bitmapToDrawable(Bitmap bitmap) {
			Drawable drawable = new BitmapDrawable(bitmap);
			return drawable;
		}
		
		/**
		 * 将Drawable转化为Bitmap
		 * 
		 * @param drawable
		 * @return
		 */
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
		
		/**
		 * 获取bitmap
		 * 
		 * @param filePath
		 * @return
		 */
		public static Bitmap getBitmapByPath(String filePath) {
			return getBitmapByPath(filePath, null);
		}
		
		public static Bitmap getBitmapByPath(String filePath,
				BitmapFactory.Options opts) {
			FileInputStream fis = null;
			Bitmap bitmap = null;
			try {
				File file = new File(filePath);
				fis = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(fis, null, opts);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
			return bitmap;
		}
		
		/**
		 * 获取图片缩略图 只有Android2.1以上版本支持
		 * 
		 * @param imgName
		 * @param kind
		 *            MediaStore.Images.Thumbnails.MICRO_KIND
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static Bitmap loadImgThumbnail(Activity context, String imgName,
				int kind) {
			Bitmap bitmap = null;

			String[] proj = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DISPLAY_NAME };

			Cursor cursor = context.managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
					MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
					null, null);

			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				ContentResolver crThumb = context.getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb, cursor.getInt(0),kind, options);
			}
			return bitmap;
		}

		public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
			Bitmap bitmap = getBitmapByPath(filePath);
			return zoomBitmap(bitmap, w, h);
		}


		
		public static  Bitmap getImageThumbnail(String imagePath, int width, int height) {  
	        Bitmap bitmap = null;  
	        BitmapFactory.Options options = new BitmapFactory.Options();  
	        options.inJustDecodeBounds = true;  
	        // 获取这个图片的宽和高，注意此处的bitmap为null  
	        bitmap = BitmapFactory.decodeFile(imagePath, options);  
	        options.inJustDecodeBounds = false; // 设为 false  
	        // 计算缩放比  
	        int h = options.outHeight;  
	        int w = options.outWidth;  
	        int beWidth = w / width;  
	        int beHeight = h / height;  
	        int be = 1;  
	        if (beWidth < beHeight) {  
	            be = beWidth;  
	        } else {  
	            be = beHeight;  
	        }  
	        if (be <= 0) {  
	            be = 1;  
	        }  
	        options.inSampleSize = be;  
	        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
	        bitmap = BitmapFactory.decodeFile(imagePath, options);  
	        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
	        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
	                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
	        return bitmap;  
	    }  

		/**
		 * 计算缩放图片的宽高
		 * 
		 * @param img_size
		 * @param square_size
		 * @return
		 */
		public static int[] scaleImageSize(int[] img_size, int square_size) {
			if (img_size[0] <= square_size && img_size[1] <= square_size)
				return img_size;
			double ratio = square_size
					/ (double) Math.max(img_size[0], img_size[1]);
			return new int[] { (int) (img_size[0] * ratio),
					(int) (img_size[1] * ratio) };
		}

		/**
		 * 创建缩略图
		 * 
		 * @param context
		 * @param largeImagePath
		 *            原始大图路径
		 * @param thumbfilePath
		 *            输出缩略图路径
		 * @param square_size
		 *            输出图片宽度
		 * @param quality
		 *            输出图片质量
		 * @throws IOException
		 */
		public static void createImageThumbnail(Context context,
				String largeImagePath, String thumbfilePath, int square_size,
				int quality) throws IOException {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			// 原始图片bitmap
			Bitmap cur_bitmap = getBitmapByPath(largeImagePath, opts);

			if (cur_bitmap == null)
				return;

			// 原始图片的高宽
			int[] cur_img_size = new int[] { cur_bitmap.getWidth(),
					cur_bitmap.getHeight() };
			// 计算原始图片缩放后的宽高
			int[] new_img_size = scaleImageSize(cur_img_size, square_size);
			// 生成缩放后的bitmap
			Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0],
					new_img_size[1]);
			// 生成缩放后的图片文件
			saveImageToSD(null,thumbfilePath, thb_bitmap, quality);
		}

		/**
		 * 放大缩小图片
		 * 
		 * @param bitmap
		 * @param w
		 * @param h
		 * @return
		 */
		public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
			Bitmap newbmp = null;
			if (bitmap != null) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				Matrix matrix = new Matrix();
				float scaleWidht = ((float) w / width);
				float scaleHeight = ((float) h / height);
				matrix.postScale(scaleWidht, scaleHeight);
				newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
						true);
			}
			return newbmp;
		}
		
		
		/**
		 * 写图片文件到SD卡
		 * 
		 * @throws IOException
		 */
		public static void saveImageToSD(Context ctx, String filePath,
				Bitmap bitmap, int quality) throws IOException {
			if (bitmap != null) {
				File file = new File(filePath.substring(0,
						filePath.lastIndexOf(File.separator)));
				if (!file.exists()) {
					file.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(filePath));
				bitmap.compress(CompressFormat.JPEG, quality, bos);
				bos.flush();
				bos.close();
				if(ctx!=null){
					scanPhoto(ctx, filePath);
				}
			}
		}
		/**
		 * 让Gallery上能马上看到该图片
		 */
		private static void scanPhoto(Context ctx, String imgFileName) {
			Intent mediaScanIntent = new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			File file = new File(imgFileName);
			Uri contentUri = Uri.fromFile(file);
			mediaScanIntent.setData(contentUri);
			ctx.sendBroadcast(mediaScanIntent);
		}	
		
}
