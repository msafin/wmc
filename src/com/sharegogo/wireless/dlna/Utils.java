package com.sharegogo.wireless.dlna;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.PlaylistItem;
import org.fourthline.cling.support.model.item.TextItem;
import org.fourthline.cling.support.model.item.VideoItem;

public class Utils {
	public static boolean isVideo(DIDLObject object){
		return object instanceof VideoItem;
	}
	
	public static boolean isAudio(DIDLObject object){
		return object instanceof AudioItem;
	}
	
	public static boolean isImage(DIDLObject object){
		return object instanceof ImageItem;
	}
	
	public static boolean isPlayList(DIDLObject object){
		return object instanceof PlaylistItem;
	}
	
	public static boolean isText(DIDLObject object){
		return object instanceof TextItem;
	}
}
