package com.sharegogo.wireless.data;

public class NetworkInfo{
	public static final String PROTO_DHCP = "dhcp";
	public static final String PROTO_PPPOE = "pppoe";
	public static final String PROTO_STATIC = "static";
	public static final String PROTO_3G = "3g";
	
	public static final int NETWORK_DHCP = 0;
	public static final int NETWORK_PPPOE = 1;
	public static final int NETWORK_REPEATER = 2;
	public static final int NETWORK_STATIC = 3;
	public static final int NETWORK_3G = 4;
	public static final int NETWORK_UNKNOWN = -1;

	public static final int OP_MODE_KEEP_ALIVE = 0;
	public static final int OP_MODE_DYNAMIC = 1;
	public static final int OP_MODE_MANUAL = 2;
	
	public static final int AUTH_TYPE_NONE = 0;
	public static final int AUTH_TYPE_CHAP = 1;
	public static final int AUTH_TYPE_PAP = 2;
	
	public int network_type;
	
	//pppoe
	public String user_name;
	public String password;
	public int op_mode;
	public int duration;
	
	//repeater
	public String ssid;
	//password
	
	//static
	public String ip_addr;
	public String subnet_mask;
	public String default_gateway;
	public String major_dns;
	public String minor_dns;
	
	//3G
	//op_mode
	//user_name
	//password
	public String dial_number;
	public String apn_name;
	public String pin_number;
	public int auth_type;
}
