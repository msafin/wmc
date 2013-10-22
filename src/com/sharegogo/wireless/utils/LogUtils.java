package com.sharegogo.wireless.utils;

import android.util.Log;

import com.sharegogo.wireless.BuildConfig;

/**
 * LogÏà¹Øº¯Êý
 * @author Raymon
 *
 */
public class LogUtils {
	
	static public void e(String tag,String message)
	{
		if(!BuildConfig.DEBUG)
		{
			return;
		}
		
		Log.e(tag, message);
	}
}
