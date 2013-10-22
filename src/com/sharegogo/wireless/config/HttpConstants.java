package com.sharegogo.wireless.config;

import com.sharegogo.wireless.manager.SettingManager;

public class HttpConstants {
	public static final String DEFAULT_HOST = "192.168.1.1";
	public static final int DEFAULT_PORT = 8080;
	public static String HOST = "http://" + SettingManager.getRouterIp();
	public static int PORT = Integer.valueOf(SettingManager.getRouterPort());
	public static final String ACTION = "/action/";
	public static final String SERVER = "http://www.sharegogo.com";
	public static final String SERVER_ACTION = "/wclient/";
	
	public static final String ACTION_GET_SHARE_DIRS = HOST + ACTION +
								"getShareDirs" + ".action";
	
	public static final String ACTION_UPLOAD = HOST + ":" + PORT + "/upload";
	
	public static final String ACTION_SET_WIFI = HOST + ACTION +
			"setWifi" + ".action";
	
	public static final String ACTION_GET_WIFI_INFO = HOST + ACTION +
			
			"getWifiInfo" + ".action";
	public static final String ACTION_GET_SCAN_RESULT = HOST + ACTION +
			"getScanResult" + ".action";
	
	public static final String ACTION_GET_WIFI_CLIENT = HOST + ACTION +
			"getWifiClient" + ".action";
	
	public static final String ACTION_LIST_DIR = HOST + ACTION +
			"listDir" + ".action";
	
	public static final String ACTION_SET_NETWORK = HOST + ACTION +
			"setNetwork" + ".action";
	
	public static final String ACTION_GET_STATUS = HOST + ACTION +
			"getSystemStatus" + ".action";
	
	public static final String ACTION_ENABLE_DLNA = HOST + ACTION +
			"enableDlna" + ".action";
	
	public static final String ACTION_REBOOT = HOST + ACTION +
	"reboot" + ".action";

	public static final String ACTION_REGISTER = SERVER + SERVER_ACTION +
			"regedit.php";
	
	public static final String ACTION_GET_NOTIFY = SERVER + SERVER_ACTION +
			"notify.php";
}
