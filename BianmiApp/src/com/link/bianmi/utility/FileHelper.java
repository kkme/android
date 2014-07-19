package com.link.bianmi.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件帮助
 * 
 */
public class FileHelper {


	/**
	 * @Methods: getFileSize
	 * @Description: 获取文件夹的大小，包含子文件夹也可以
	 * @param f
	 *            File 实例
	 * @return 文件夹大小，单位：字节
	 * @throws Exception
	 * @throws
	 */
	public static long getFileSize(File f) {
		long size = 0;
		try {
			if(f.isFile()){
				return f.length();
			}
			File flist[] = f.listFiles();
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
		} catch (Exception ex) {
		}
		return size;
	}

	/** 递归获取 获取文件/文件夹 大小**/
	public static long getFileSize(String  filePath) {
		long size=0;
		try{
			size=getFileSize(new File(filePath));
		} catch (Exception ex) {}
		

		return size;

	}
	
	public static void delete(File file, ArrayList<String> exceptFiles){
		delete(file,exceptFiles,true,false);
	}
	
	public static void delete(File file, ArrayList<String> exceptFiles,boolean atom,boolean reserveHomeDir){
		String homeDir="";
		if (reserveHomeDir && file.isDirectory()){
			homeDir=file.getPath();
		}
		delete(file,exceptFiles,atom,homeDir);
	}

	
	/**
	 * 删除文件或递归删除文件夹
	 * 
	 * @param file
	 * @param exceptFilePath
	 *            不需要删除的文件列表
	 * @atom	
	 * 		atom=true	指定文件不删除		
	 * 		atom=false  指定文件夹下所有文件不删除
	 * 
	 * @homeDir
	 * 		需要保留的主文件夹
	 * 			
	 */
	public static void delete(File file, ArrayList<String> exceptFiles,boolean atom,String homeDir) {
		
		try{
			if (file.isFile()) {
				if(atom){
					if (exceptFiles.contains(file.getPath()))
						return;
				}else{
					if (exceptFiles.contains(file.getParentFile().toString()))
						return;					
				}

				file.delete();
				return;
			}
	
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles == null || childFiles.length == 0) {
					for (String filePath : exceptFiles) {
						if (filePath.contains(file.getPath()))
							return;
					}
					//不是主目录就删除
					if(TextUtils.isEmpty(homeDir) || file.getPath().compareTo(homeDir)!=0)
						file.delete();
					return;
				}
	
				for (int i = 0; i < childFiles.length; i++) {
					delete(childFiles[i], exceptFiles,atom,homeDir);
				}
	
				boolean deleteFlag = true;
				for (String filePath : exceptFiles) {
					if (filePath.contains(file.getPath())) {
						deleteFlag = false;
						break;
					}
				}
				//不是主目录就删除
				if (deleteFlag || TextUtils.isEmpty(homeDir) || file.getPath().compareTo(homeDir)!=0)
					file.delete();
			}
		}catch(Exception ex){}
	}


	/** 删除文件或递归删除文件夹**/
	public static void delete(String filePath){
		try{
			File file=new File(filePath);
			delete(file);
		} catch (Exception ex) {
		}
	}
	
	/**删除目录下所有内容**/
	public static void deleteSub(String fileDir){

		try{
			delete(new File(fileDir),fileDir);
		} catch (Exception ex) {
		}finally{
			try{
				File dir = new File(fileDir);
				if (!dir.exists()) {
					dir.mkdirs(); 
				}
			}catch (Exception ex){}
			
		}
	}
	
	
	/**根据文件路径删除父文件夹**/
	public  static void deleteDirParent(String filePath){
		
		if(TextUtils.isEmpty(filePath))
			return;
		try{
			String file=FileHelper.getFileDir(filePath);
			delete(getFileDir(file));
		}catch(Exception ex){
		}	
	}
	
	/**
	 * 删除文件或递归删除文件夹
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		try {
			if (file.isFile()) {
				file.delete();
				return;
			}

			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles == null || childFiles.length == 0) {
					file.delete();
					return;
				}

				for (int i = 0; i < childFiles.length; i++) {
					delete(childFiles[i]);
				}
				file.delete();
			}
		} catch (Exception ex) {
		}
	}
	
	
	
	public static void delete(File file,final String noDelDir) {
		try {
			if (file.isFile()) {
				file.delete();
				return;
			}

			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles == null || childFiles.length == 0) {
					if(file.getPath().compareTo(noDelDir)!=0){
						file.delete();
					}
					return;
				}

				for (int i = 0; i < childFiles.length; i++) {
					delete(childFiles[i],noDelDir);
				}
				if(file.getPath().compareTo(noDelDir)!=0){
					file.delete();
				}
			}
		} catch (Exception ex) {
		}
	}
	
	
	/**复制文件夹**/
    public static void copyDirectiory(String sourceDir, String targetDir)  
            throws IOException {  
        // 新建目标目录   
        (new File(targetDir)).mkdirs();  
        // 获取源文件夹当前下的文件或目录   
        File[] file = (new File(sourceDir)).listFiles();  
        for (int i = 0; i < file.length; i++) {  
            if (file[i].isFile()) {  
                // 源文件   
                File sourceFile=file[i];  
                // 目标文件   
               File targetFile=new   File(new File(targetDir).getAbsolutePath()   +File.separator+file[i].getName());  
               copyFile(sourceFile,targetFile);  
            }  
            if (file[i].isDirectory()) {  
                // 准备复制的源文件夹   
                String dir1=sourceDir + "/" + file[i].getName();  
                // 准备复制的目标文件夹   
                String dir2=targetDir + "/"+ file[i].getName();  
                copyDirectiory(dir1, dir2);  
            }  
        }  
    } 
    
    
    /**复制文件 **/
    public static void copyFile(File sourceFile,File targetFile)   throws IOException{  
            // 新建文件输入流并对它进行缓冲   
            FileInputStream input = new FileInputStream(sourceFile);  
            BufferedInputStream inBuff=new BufferedInputStream(input);  
      
            // 新建文件输出流并对它进行缓冲   
            FileOutputStream output = new FileOutputStream(targetFile);  
            BufferedOutputStream outBuff=new BufferedOutputStream(output);  
              
            // 缓冲数组   
            byte[] b = new byte[1024 * 5];  
            int len;  
            while ((len =inBuff.read(b)) != -1) {  
                outBuff.write(b, 0, len);  
            }  
            // 刷新此缓冲的输出流   
            outBuff.flush();  
              
            //关闭流   
            inBuff.close();  
            outBuff.close();  
            output.close();  
            input.close();  
        } 
   

	/**
	 * 文件复制
	 * 
	 * @param fromFile
	 * @param toFile
	 * @return
	 */
	public static boolean copyFile(String fromFile, String toFile) {
		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			String dir = toFile
					.substring(0, toFile.lastIndexOf(File.separator));
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosfrom.close();
			fosto.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}



	/**
	 * 根据URI获取文件绝对路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getAbsoluteImagePath(Activity context, Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	/**
	 * 根据文件路径获取文件名称
	 * 
	 * @param apath
	 * @return
	 */
	public static String getFileName(String filePath) {

		String filename = "";
		try {
			int start = filePath.lastIndexOf("/");

			if (start != -1)
				filename = filePath.substring(start + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;

	}

	/**
	 * 根据文件路径获取文件夹地址
	 * 
	 * @param apath
	 * @return
	 */
	public static String getFileDir(String filePath) {

		String dir = "";
		try {
			int end = filePath.lastIndexOf("/");

			if (end != -1)
				dir = filePath.substring(0, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dir;

	}

	/**
	 * 获取单个文件的MD5值！
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	/**
	 * 获取文件夹中文件的MD5值
	 * 
	 * @param file
	 * @param listChild
	 *            ;true递归子目录中的文件
	 * @return
	 */
	public static Map<String, String> getDirMD5(File file, boolean listChild) {
		if (!file.isDirectory()) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(getDirMD5(f, listChild));
			} else {
				md5 = getFileMD5(f);
				if (md5 != null) {
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}

	/**
	 * 格式化文件大小
	 * 
	 * @param strFileSize
	 * @return
	 */
	public static String formatFileSize(Long fileSize)
	{

		if (fileSize <= 0)
			return "0 KB";
			
		
        String fileSizeString = "";
        if (fileSize < 1024)
        {
        	fileSizeString= "< 1 KB";
        }
        else if (fileSize < 1048576)
        {
        	DecimalFormat df = new DecimalFormat("#");
            fileSizeString = df.format((double) fileSize / 1024) + " KB";
        }
        else if (fileSize < 1073741824)
        {
        	DecimalFormat df = new DecimalFormat("#.0");
            fileSizeString = df.format((double) fileSize / 1048576) + " MB";
        }
        else
        {
        	DecimalFormat df = new DecimalFormat("#.00");
            fileSizeString = df.format((double) fileSize / 1073741824) + " GB";
        }
        return fileSizeString;		
		
	}

	// comma separated list of all file extensions supported by the media
	// scanner
	public static String sFileExtensions;

	// Audio file types
	public static final int FILE_TYPE_MP3 = 1;
	public static final int FILE_TYPE_M4A = 2;
	public static final int FILE_TYPE_WAV = 3;
	public static final int FILE_TYPE_AMR = 4;
	public static final int FILE_TYPE_AWB = 5;
	public static final int FILE_TYPE_WMA = 6;
	public static final int FILE_TYPE_OGG = 7;
	public static final int FILE_TYPE_AAC = 8;
	private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;
	private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_AAC;

	// MIDI file types
	public static final int FILE_TYPE_MID = 11;
	public static final int FILE_TYPE_SMF = 12;
	public static final int FILE_TYPE_IMY = 13;
	private static final int FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID;
	private static final int LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY;

	// Video file types
	public static final int FILE_TYPE_MP4 = 21;
	public static final int FILE_TYPE_M4V = 22;
	public static final int FILE_TYPE_3GPP = 23;
	public static final int FILE_TYPE_3GPP2 = 24;
	public static final int FILE_TYPE_WMV = 25;
	private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
	private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_WMV;

	// Image file types
	public static final int FILE_TYPE_JPEG = 31;
	public static final int FILE_TYPE_GIF = 32;
	public static final int FILE_TYPE_PNG = 33;
	public static final int FILE_TYPE_BMP = 34;
	public static final int FILE_TYPE_WBMP = 35;
	private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
	private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_WBMP;

	// Playlist file types
	public static final int FILE_TYPE_M3U = 41;
	public static final int FILE_TYPE_PLS = 42;
	public static final int FILE_TYPE_WPL = 43;
	private static final int FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U;
	private static final int LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL;

	public static class MediaFileType {

		public int fileType;
		public String mimeType;

		MediaFileType(int fileType, String mimeType) {
			this.fileType = fileType;
			this.mimeType = mimeType;
		}
	}

	private static HashMap<String, MediaFileType> sFileTypeMap = new HashMap<String, MediaFileType>();
	private static HashMap<String, Integer> sMimeTypeMap = new HashMap<String, Integer>();

	static void addFileType(String extension, int fileType, String mimeType) {
		sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
		sMimeTypeMap.put(mimeType,  Integer.valueOf(fileType));
	}

	static {
		addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
		addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
		addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
		addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
		addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
		addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma");

		addFileType("AAC", FILE_TYPE_AAC, "audio/aac");
		addFileType("MID", FILE_TYPE_MID, "audio/midi");
		addFileType("XMF", FILE_TYPE_MID, "audio/midi");
		addFileType("RTTTL", FILE_TYPE_MID, "audio/midi");
		addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
		addFileType("IMY", FILE_TYPE_IMY, "audio/imelody");
		addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl");
		addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls");
		
		
		
		addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
		addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
		addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
		addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
		addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
		addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");
		addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv");
		addFileType("RMVB", FILE_TYPE_3GPP, "audio/x-pn-realaudio");
		addFileType("MOV", FILE_TYPE_3GPP, "video/quicktime");
		
		
		
		addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
		addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
		addFileType("GIF", FILE_TYPE_GIF, "image/gif");
		addFileType("PNG", FILE_TYPE_PNG, "image/png");
		addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
		addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");


		addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");
		addFileType("OGG", FILE_TYPE_OGG, "application/ogg");
		
		// compute file extensions list for native Media Scanner
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = sFileTypeMap.keySet().iterator();

		while (iterator.hasNext()) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(iterator.next());
		}
		sFileExtensions = builder.toString();
	}

	public static final String UNKNOWN_STRING = "<unknown>";

	public static boolean isAudioFileType(int fileType) {
		return ((fileType >= FIRST_AUDIO_FILE_TYPE && fileType <= LAST_AUDIO_FILE_TYPE) || (fileType >= FIRST_MIDI_FILE_TYPE && fileType <= LAST_MIDI_FILE_TYPE));
	}

	public static boolean isVideoFileType(int fileType) {
		return (fileType >= FIRST_VIDEO_FILE_TYPE && fileType <= LAST_VIDEO_FILE_TYPE);
	}

	public static boolean isImageFileType(int fileType) {
		return (fileType >= FIRST_IMAGE_FILE_TYPE && fileType <= LAST_IMAGE_FILE_TYPE);
	}

	public static boolean isPlayListFileType(int fileType) {
		return (fileType >= FIRST_PLAYLIST_FILE_TYPE && fileType <= LAST_PLAYLIST_FILE_TYPE);
	}

	public static MediaFileType getMediaFileType(String path) {
		int lastDot = path.lastIndexOf(".");
		if (lastDot < 0)
			return null;
		return sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
	}


	/**
	 * 是否音频
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isAudioFileType(String path) {
		MediaFileType type = getMediaFileType(path);
		if (null != type) {
			return isAudioFileType(type.fileType);
		}
		return false;
	}

	/**
	 * 是否视频
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isVideoFileType(String path) {
		MediaFileType type = getMediaFileType(path);
		if (null != type) {
			return isAudioFileType(type.fileType);
		}
		return false;
	}

	/**
	 * 是否图片
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isImageFileType(String path) {
		MediaFileType type = getMediaFileType(path);
		if (null != type) {
			return isImageFileType(type.fileType);
		}
		return false;
	}

	// 根据mime类型查看文件类型
	public static int getFileTypeForMimeType(String mimeType) {
		Integer value = sMimeTypeMap.get(mimeType);
		return (value == null ? 0 : value.intValue());
	}
	

	
	/** asset文件复制**/
	public static void copyAssets(Context context,String assetDir,String dir) {
        String[] files;    
         try    
         {    
             files = context.getResources().getAssets().list(assetDir);    
         
         }catch (IOException e1) {    
             return;    
         }    
         File mWorkingPath = new File(dir);
         if(!mWorkingPath.exists()){    
             mWorkingPath.mkdirs();
         }    

         for(int i = 0; i < files.length; i++)    
         {    
             try    
             {    
                 String fileName = files[i]; 
//                 //we make sure file name not contains '.' to be a folder. 
//                 if(!fileName.contains("."))
//                 {
//                     if(0==assetDir.length())
//                     {
//                    	 CopyAssets(context,fileName,dir+fileName+"/");
//                     }
//                     else
//                     {
//                         CopyAssets(context,assetDir+"/"+fileName,dir+fileName+"/");
//                     }
//                     continue;
//                 }
                 File outFile = new File(mWorkingPath, fileName);    
                 if(outFile.exists()) 
                     outFile.delete();
                 InputStream in =null;
                 if(0!=assetDir.length())
                     in = context.getAssets().open(assetDir+"/"+fileName);    
                 else
                     in = context.getAssets().open(fileName);
                 OutputStream out = new FileOutputStream(outFile); 
                 // Transfer bytes from in to out   
                 byte[] buf = new byte[1024];    
                 int len;    
                 while ((len = in.read(buf)) > 0)    
                 {    
                     out.write(buf, 0, len);    
                 }    

                 in.close();    
                 out.close();    
             }catch (FileNotFoundException e)    
             {    
                 e.printStackTrace();    
             }catch (IOException e)    
             {    
                 e.printStackTrace();    
             }         
        }
	}
	
	/** 读取文件**/
	 public static byte[] readAllBytes(String filename) throws IOException {
	    RandomAccessFile file = new RandomAccessFile(new File(filename), "r");
	    byte[] content = new byte[(int) file.length()];
	    file.readFully(content);
	    file.close();
	    return content;
	}
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 /**
      * 解压缩功能.
      * 将ZIP_FILENAME文件解压到ZIP_DIR目录下.
      * @throws Exception
      */
      @SuppressWarnings("rawtypes")
	public static void upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
      //public static void upZipFile() throws Exception{
              ZipFile zfile=new ZipFile(zipFile);
              
              
  			File dir = new File(folderPath);
  			if(!dir.exists()){
  				dir.mkdirs(); 
  			}
              try{
	              Enumeration zList=zfile.entries();
	              ZipEntry ze=null;
	              byte[] buf=new byte[1024];
	              while(zList.hasMoreElements()){
                      ze=(ZipEntry)zList.nextElement();
                      if(ze.getName().startsWith("__MACOSX"))	//过滤mac隐藏文件
                    	  continue;
                      if(ze.isDirectory()){
                              Log.d("upZipFile", "ze.getName() = "+ze.getName());
                              String dirstr = folderPath + ze.getName();
                              //dirstr.trim();
                              dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                              Log.d("upZipFile", "str = "+dirstr);
                              File f=new File(dirstr);
                              f.mkdir();
                              continue;
                      }
                      Log.d("upZipFile", "ze.getName() = "+ze.getName());
                      
                      OutputStream os=new BufferedOutputStream(new FileOutputStream(folderPath+File.separator+ze.getName()));
                      
                      //OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
                      InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
                      int readLen=0;
                      while ((readLen=is.read(buf, 0, 1024))!=-1) {
                              os.write(buf, 0, readLen);
                      }
                      is.close();
                      os.close();
	              }
              }finally{
            	  try{
            		  zfile.close();
            	  }catch(Exception ex){}
              }
      }

 /**
      * 给定根目录，返回一个相对路径所对应的实际文件名.
      * @param baseDir 指定根目录
      * @param absFileName 相对路径名，来自于ZipEntry中的name
      * @return java.io.File 实际的文件
      */
      public static File getRealFileName(String baseDir, String absFileName){
              String[] dirs=absFileName.split("/");
              File ret=new File(baseDir);
              String substr = null;
              if(dirs.length>1){
                  for (int i = 0; i < dirs.length-1;i++) {
                      substr = dirs[i];
                      try {
                              substr = new String(substr.getBytes("8859_1"), "GB2312");
                      } catch (UnsupportedEncodingException e) {
                              e.printStackTrace();
                      }
                      ret=new File(ret, substr);

                  }
                  Log.d("upZipFile", "1ret = "+ret);
                  if(!ret.exists())
                          ret.mkdirs();
                  substr = dirs[dirs.length-1];
                  try {
                          //substr.trim();
                          substr = new String(substr.getBytes("8859_1"), "GB2312");
                          Log.d("upZipFile", "substr = "+substr);
                  } catch (UnsupportedEncodingException e) {
                          e.printStackTrace();
                  }

                  ret=new File(ret, substr);
                  Log.d("upZipFile", "2ret = "+ret);
                  return ret;
              }

           return ret;
      }	 
	 
	 
      /* 
       * Java文件操作 获取文件扩展名 
       *  
       */   
          public static String getExtensionName(String filename) {    
              if ((filename != null) && (filename.length() > 0)) {    
                  int dot = filename.lastIndexOf('.');    
                  if ((dot >-1) && (dot < (filename.length() - 1))) {    
                      return filename.substring(dot + 1);    
                  }    
              }    
              return filename;    
          }    
      /* 
       * Java文件操作 获取不带扩展名的文件名 
       */   
          public static String getFileNameNoEx(String filename) {    
              if ((filename != null) && (filename.length() > 0)) {    
                  int dot = filename.lastIndexOf('.');    
                  if ((dot >-1) && (dot < (filename.length()))) {    
                      return filename.substring(0, dot);    
                  }    
              }    
              return filename;    
          }   
	 
}
