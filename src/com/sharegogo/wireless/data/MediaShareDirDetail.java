package com.sharegogo.wireless.data;

import java.util.ArrayList;

import com.sharegogo.wireless.json.GsonParser;

public class MediaShareDirDetail extends BaseResponse{
	
	public static MediaShareDirDetail fromJson(String data){
		MediaShareDirDetail dirDetail = GsonParser.fromJson(data, MediaShareDirDetail.class);
		return dirDetail;
	}
	
	public ArrayList<MediaShareItem> data;
}
