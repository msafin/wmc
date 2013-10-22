package com.sharegogo.wireless.data;

import com.sharegogo.wireless.json.GsonParser;

public class Register extends BaseResponse{
	public static Register fromJson(String data){
		Register register = GsonParser.fromJson(data, Register.class);
		return register;
	}
	
	public int action;
	public String token;
	public long validity;
	public String uuid;
	public String mac_wan;
	public String mac_wifi;
	public String channel;
}
