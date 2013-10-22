package com.sharegogo.wireless.data;

public class ScanResult extends BaseResponse{

	public ScanResultItem[] data;
	
	static public class ScanResultItem
	{
		public String ssid;
		public String auth_type;
		public int freq;
		public String rssi;
	}
}
