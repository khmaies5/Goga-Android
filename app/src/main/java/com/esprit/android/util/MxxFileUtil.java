package com.esprit.android.util;

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
public class MxxFileUtil {
	
	static String ROOT_PATH = "/9gag_mxx";
	static String DOWNLOAD_PATH = "/download";
	static String IMAGE_PATH = "/Image";
	static String AUDIO_PATH = "/Audio";
	static String CRASH_PATH = "/Crash";

	/**
	 * Check if the current device SD is available
	 *
	 @return returns "true" for availability, otherwise not available
	 */
	public static boolean haveSdCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
	}
	
	/**
	 * Obtain SD card root directory path
	 *
	 * @return String SD card root directory path
	 */
	public static String getSdCardAbsolutePath(){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	/**
	 *  Get SD card this program cache directory
	 *
	 * @ Return String type SD card to obtain the program's cache directory
	 */
	public static String getCachePath(Context c){
			return c.getExternalCacheDir().getAbsolutePath();
	}
	/**
	 * Get the image cache file folder
	 *
	 * @return String type
	 * Folder for storing image cache files
	 */
	public static String getImageCacheDir(Context c){
		File file = new File(getCachePath(c) + IMAGE_PATH) ;
		if (!file.exists()) {// Here may create a failure, not to consider
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	/**
	 * Get the folder where the recording files are stored
	 *
	 * @return String type
	 * The folder where the recording files are stored
	 */
	public static String getAudioCacheDir(Context c){
		File audioFile = new File(getCachePath(c)  + AUDIO_PATH) ;
		if (!audioFile.exists()) {// Here may create a failure, not to consider
			audioFile.mkdirs();
		}
		return audioFile.getAbsolutePath();
	}
	
	/**
	 * Get the folder where your app's private files are stored
	 *
	 * @return String type
	 * Private file folder
	 */
	public static String getPrivateAudioDir(Context c){
		return c.getExternalFilesDir("Audio").getAbsolutePath();
	}
	/**
	 * Get the folder where the app's private files are stored + Crash
	 *
	 * @return String type
	 * Private file folder
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
	 * Attachment storage address
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
		if (!file.exists()) {// Here may create a failure, not to consider
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
		if (!file.exists()) {// Here may create a failure, not to consider
			file.mkdirs();
		}
		System.out.println("get download path util "+file.getAbsolutePath());
		return file.getAbsolutePath();
	}
	/**
	 * sdcard/approot/image
	 * @return
	 */
	public static String getImagePath(){
		File file = new File(getAppRootPath() + IMAGE_PATH) ;
		if (!file.exists()) {// Here may create a failure, not to consider
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
	/** 
	  * Get the current time
	 *
	 @return returns the string format yyyy_MM_dd_HH_mm_ss
	  */  
	public static String getStringDate() {  
	  Date currentTime = new Date();  
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");  
	  String dateString = formatter.format(currentTime);  
	  return dateString;  
	} 
	
	
	
	
	/**
	 * Save text file Input parameters: String, file name, context to save
	 */
	public static void savedata(String data, String filename, Context context) {
		if (data == null || data.trim().equals("")) {
			return; // Bin correction is not saved if empty
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
	 * If the filename does not exist, it returns null, otherwise it returns the string content
	 */
	public static String loaddata(String filename, Context context) {
		// Toast.makeText(context, "Try to show cached data", Toast.LENGTH_SHORT).show();
		if (!isFileExist(filename, context)) {
			// Log.e("Tools", "loaddata" + filename + "does not exist");
			return null;
		}
		// Log.e("Tools", "loaddata" + filename + "exist!!");
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
