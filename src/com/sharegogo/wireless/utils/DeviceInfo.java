package com.sharegogo.wireless.utils;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sharegogo.wireless.SharegogoWirelessApp;

/**
 * 获取设备相关信息
 * @author Raymon
 *
 */
public class DeviceInfo {
	
	static public String getDeviceImei()
	{
		Context context = SharegogoWirelessApp.getApplication();
		
		TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		
		return telephonyMgr.getDeviceId();
	}
	
	static public String getDeviceImsi()
	{
		Context context = SharegogoWirelessApp.getApplication();
		
		TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		
		return telephonyMgr.getSubscriberId();
	}
	
	static public String getManufacturer()
	{
		return Build.MANUFACTURER;
	}
	
	static public String getDeviceName()
	{
		if(Build.MODEL != null && Build.MODEL.length() > 0)
		{
			return Build.MODEL;
		}
		else if(Build.DEVICE != null && Build.DEVICE.length() > 0)
		{
			return Build.DEVICE;
		}
		else
		{
			return Build.PRODUCT;
		}
	}
	
	static public String getSystemVersion()
	{
		return "Android" + String.valueOf(Build.VERSION.SDK_INT);
	}
	
	static public String getChannel()
	{
		Resources res = SharegogoWirelessApp.getApplication().getResources();
		
		AssetManager assetMgr = res.getAssets();
		
		try {
			InputStream input = assetMgr.open("channel");
			int count = input.available();
			byte[] buffer = new byte[count];
			input.read(buffer);
			String channel = new String(buffer);
			if(channel != null)
			{
				String[] value = channel.split("=");
				
				return value[1].trim();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return "default";
		}
		
		return "default";
	}
	
	static public int getScreenWidth(Activity activity)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		return metrics.widthPixels;
	}
	
	static public int getScreenHeight(Activity activity)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		return metrics.heightPixels;
	}
	
	static public void test()
	{
		Log.e("test", "imei = " + getDeviceImei());
		Log.e("test", "imsi = " + getDeviceImsi());
		Log.e("test", "version = " + getSystemVersion());
		Log.e("test", "manufacturer = " + getManufacturer());
		Log.e("test", "device = " + getDeviceName());
		Log.e("test", "channel = " + getChannel());
		Log.e("test", "id = " + Build.ID);
	}
	
}
