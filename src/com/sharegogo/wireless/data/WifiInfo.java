package com.sharegogo.wireless.data;

import com.sharegogo.wireless.json.GsonParser;

public class WifiInfo extends BaseResponse{
	public static WifiInfo fromJson(String data){
		WifiInfo wifiInfo = GsonParser.fromJson(data, WifiInfo.class);
		return wifiInfo;
	}
	
	public String device;
	public String network;
	public String mode;
	public String encryption;
	public String ssid;
	public String key;
}
