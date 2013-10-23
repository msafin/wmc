package com.sharegogo.wireless.config;

import android.os.Environment;

public class Constants {
	public static String DEFAULT_DOWNLOAD_DIR = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			.toString();
	
	public static String DEFAULT_USER = "admin";
	public static String DEFAULT_PASSWORD = "admin";
}
