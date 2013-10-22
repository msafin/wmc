package com.sharegogo.wireless.manager;

import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.config.Constants;
import com.sharegogo.wireless.config.HttpConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingManager {
	static public String getRouterIp(){
		Context context = SharegogoWirelessApp.getApplication();
		SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return sharePreferences.getString("router_ip", HttpConstants.DEFAULT_HOST);
	}
	
	static public String getRouterPort(){
		Context context = SharegogoWirelessApp.getApplication();
		SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return sharePreferences.getString("router_port", String.valueOf(HttpConstants.DEFAULT_PORT));
	}
	
	static public String getDownloadDir(){
		Context context = SharegogoWirelessApp.getApplication();
		SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return sharePreferences.getString("download_dir", Constants.DEFAULT_DOWNLOAD_DIR);
	}
	
	static public void setDownloadDir(String dir){
		Context context = SharegogoWirelessApp.getApplication();
		SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		sharePreferences.edit().putString("download_dir", dir).commit();
	}
}
