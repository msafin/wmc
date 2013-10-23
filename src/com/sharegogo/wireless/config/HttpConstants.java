package com.sharegogo.wireless.config;

import com.sharegogo.wireless.manager.SettingManager;

public class HttpConstants {
	public static final String DEFAULT_HOST = "192.168.1.1";
	public static final int DEFAULT_PORT = 8080;
	public static final String ACTION = "/action/";
	public static final String SERVER = "http://www.sharegogo.com";
	public static final String SERVER_ACTION = "/wclient/";

	public static final int ACTION_GET_SHARE_DIRS = 0;
	public static final int ACTION_UPLOAD = 1;
	public static final int ACTION_SET_WIFI = 2;
	public static final int ACTION_GET_WIFI_INFO = 3;
	public static final int ACTION_GET_SCAN_RESULT = 4;
	public static final int ACTION_GET_WIFI_CLIENT = 5;
	public static final int ACTION_LIST_DIR = 6;
	public static final int ACTION_SET_NETWORK = 7;
	public static final int ACTION_GET_STATUS = 8;
	public static final int ACTION_ENABLE_DLNA = 9;
	public static final int ACTION_REBOOT = 10;
	public static final int ACTION_REGISTER = 11;
	public static final int ACTION_GET_NOTIFY = 12;

	public static String getHost() {
		return "http://" + SettingManager.getRouterIp();
	}

	public static String getPort() {
		return SettingManager.getRouterPort();
	}

	public static String getActionUrl(int action) {
		String host = getHost();
		String port = getPort();

		switch (action) {
		case ACTION_GET_SHARE_DIRS:
			return host + ACTION + "getShareDirs" + ".action";
		case ACTION_UPLOAD:
			return host + ":" + port + "/upload";
		case ACTION_SET_WIFI:
			return host + ACTION + "setWifi" + ".action";
		case ACTION_GET_WIFI_INFO:
			return host + ACTION + "getWifiInfo" + ".action";
		case ACTION_GET_SCAN_RESULT:
			return host + ACTION + "getScanResult" + ".action";
		case ACTION_GET_WIFI_CLIENT:
			return host + ACTION + "getWifiClient" + ".action";
		case ACTION_LIST_DIR:
			return host + ACTION + "listDir" + ".action";
		case ACTION_SET_NETWORK:
			return host + ACTION + "setNetwork" + ".action";
		case ACTION_GET_STATUS:
			return host + ACTION + "getSystemStatus" + ".action";
		case ACTION_ENABLE_DLNA:
			return host + ACTION + "enableDlna" + ".action";
		case ACTION_REBOOT:
			return host + ACTION + "reboot" + ".action";
		case ACTION_REGISTER:
			return SERVER + SERVER_ACTION + "regedit.php";
		case ACTION_GET_NOTIFY:
			return SERVER + SERVER_ACTION + "notify.php";
		default:
			break;
		}

		return null;
	}
}
