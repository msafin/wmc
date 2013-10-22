package com.sharegogo.wireless.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.sharegogo.wireless.config.BuildingConfig;

/**
 * httpÏà¹Ø¸¨Öúº¯Êý
 * @author Raymon
 *
 */
public class HttpUtils {
	
	static public String getUserAgent()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(DeviceInfo.getSystemVersion());
		builder.append("/");
		builder.append(BuildingConfig.client_name);
		builder.append("/");
		builder.append(DeviceInfo.getManufacturer());
		builder.append("/");
		builder.append(DeviceInfo.getDeviceName());
		builder.append("/");
		builder.append(DeviceInfo.getChannel());
		builder.append("/");
		builder.append(BuildingConfig.client_version);
		
		return builder.toString();
	}
	
	
	static public NameValuePair getTokenPair()
	{
		NameValuePair token = new BasicNameValuePair("token","test");
		
		return token;
	}
	
	static public void httpUtilsTest()
	{
		Log.e("test", getUserAgent());
	}
}
