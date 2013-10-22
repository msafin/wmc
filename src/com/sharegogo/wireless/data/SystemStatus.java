package com.sharegogo.wireless.data;

import com.sharegogo.wireless.json.GsonParser;

public class SystemStatus extends BaseResponse{
	public static SystemStatus fromJson(String data){
		SystemStatus systemStatus = GsonParser.fromJson(data, SystemStatus.class);
		return systemStatus;
	}

	//memory
	public long mem_total;
	public long mem_free;
	
	//lan
	public String lan_ip;
	public String lan_mac;
	
	//wan
	public String wan_ip;
	public String wan_mac;
	
	//wifi
	public String ssid;
	public String wifi_mac;
}
