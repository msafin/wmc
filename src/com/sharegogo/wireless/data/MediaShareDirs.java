package com.sharegogo.wireless.data;

import com.sharegogo.wireless.json.GsonParser;

public class MediaShareDirs extends BaseResponse{
	
	public static MediaShareDirs fromJson(String data){
		MediaShareDirs shareDirs = GsonParser.fromJson(data, MediaShareDirs.class);
		return shareDirs;
	}
	
	public String vendor;
	public String product;
	public String[] dirs;
}
