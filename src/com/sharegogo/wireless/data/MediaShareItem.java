package com.sharegogo.wireless.data;

public class MediaShareItem {
	public static final int TYPE_FOLDER = 0;
	public static final int TYPE_FILE_VIDEO = 1;
	public static final int TYPE_FILE_NORMAL = 2;
	
	public int type;
	public String name;
	public long size;
	public long time;
}
