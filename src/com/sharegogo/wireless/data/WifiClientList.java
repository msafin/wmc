package com.sharegogo.wireless.data;

import java.util.ArrayList;

import com.sharegogo.wireless.json.GsonParser;

public class WifiClientList extends BaseResponse{
	
	public static WifiClientList fromJson(String data){
		WifiClientList clientList = GsonParser.fromJson(data, WifiClientList.class);
		return clientList;
	}
	
	public ArrayList<WifiClient> data;
	
	public static class WifiClient
	{
		public String mac;
		public int tx_packets;
		public int rx_packets;
	}
}
