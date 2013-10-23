package com.sharegogo.wireless.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharegogo.wireless.config.HttpConstants;
import com.sharegogo.wireless.data.NetworkInfo;


public class NetworkManager {
	static private NetworkManager mInstance;
	
	private NetworkManager()
	{
		
	}
	
	static public NetworkManager getInstance()
	{
		if(mInstance == null)
		{
			synchronized(NetworkManager.class)
			{
				if(mInstance == null)
				{
					mInstance = new NetworkManager();
				}
			}
		}
		
		return mInstance;
	}
	
	public Request<JSONObject> getSystemStatus(Listener<JSONObject> listener, ErrorListener errorListener)
	{
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_GET_STATUS), (JSONObject)null, listener,
				errorListener);

		request.setTag("getSystemStatus");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}
	
	public Request<JSONObject> setNetwork(NetworkInfo networkInfo,Listener<JSONObject> listener, ErrorListener errorListener)
	{		
		List<NameValuePair> params = buildParams(networkInfo);
		
		JsonObjectRequest request = new JsonObjectRequest(Method.POST,
				HttpConstants.getActionUrl(HttpConstants.ACTION_SET_NETWORK), URLEncodedUtils.format(params,
						"utf-8"), listener, errorListener);

		request.setTag("setNetwork");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}
	
	private List<NameValuePair> buildParams(NetworkInfo networkInfo)
	{
		if(networkInfo == null)
		{
			return null;
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		NameValuePair type = new BasicNameValuePair("type",String.valueOf(networkInfo.network_type));
		params.add(type);
		
		switch(networkInfo.network_type)
		{
		case NetworkInfo.NETWORK_DHCP:
			break;
		case NetworkInfo.NETWORK_PPPOE:
		{
			NameValuePair userName = new BasicNameValuePair("pppoeUser",networkInfo.user_name);
			params.add(userName);
			
			NameValuePair password = new BasicNameValuePair("pppoePass",networkInfo.password);
			params.add(password);
			
			NameValuePair opMode = new BasicNameValuePair("pppoeOPMode",String.valueOf(networkInfo.op_mode));
			params.add(opMode);
			
			if(networkInfo.op_mode == NetworkInfo.OP_MODE_DYNAMIC)
			{
				NameValuePair duration = new BasicNameValuePair("duration",String.valueOf(networkInfo.duration));
				params.add(duration);
			}
			
			break;
		}
		case NetworkInfo.NETWORK_REPEATER:
		{
			NameValuePair ssid = new BasicNameValuePair("ssid",networkInfo.ssid);
			params.add(ssid);
			
			NameValuePair password = new BasicNameValuePair("password",networkInfo.password);
			params.add(password);
			
			break;
		}
		case NetworkInfo.NETWORK_STATIC:
		{	
			NameValuePair ipAddr = new BasicNameValuePair("staticIp",networkInfo.ip_addr);
			params.add(ipAddr);
			
			NameValuePair subnetMask = new BasicNameValuePair("staticNetmask",networkInfo.subnet_mask);
			params.add(subnetMask);
			
			NameValuePair defaultGateway = new BasicNameValuePair("staticGateway",networkInfo.default_gateway);
			params.add(defaultGateway);
			
			NameValuePair majorDns = new BasicNameValuePair("staticPriDns",networkInfo.major_dns);
			params.add(majorDns);
			
			NameValuePair minorDns = new BasicNameValuePair("staticSecDns",networkInfo.minor_dns);
			params.add(minorDns);
			
			break;
		}
		case NetworkInfo.NETWORK_3G:
		{	
			NameValuePair opMode = new BasicNameValuePair("op_mode",String.valueOf(networkInfo.op_mode));
			params.add(opMode);
			
			if(networkInfo.op_mode == NetworkInfo.OP_MODE_DYNAMIC)
			{
				NameValuePair duration = new BasicNameValuePair("duration",String.valueOf(networkInfo.duration));
				params.add(duration);
			}
			
			NameValuePair userName = new BasicNameValuePair("User3G",networkInfo.user_name);
			params.add(userName);
			
			NameValuePair password = new BasicNameValuePair("Password3G",networkInfo.password);
			params.add(password);
			
			NameValuePair dialNumber = new BasicNameValuePair("Dial3G",networkInfo.dial_number);
			params.add(dialNumber);
			
			NameValuePair apn = new BasicNameValuePair("APN3G",networkInfo.apn_name);
			params.add(apn);
			
			NameValuePair pinNumber = new BasicNameValuePair("PIN3G",networkInfo.pin_number);
			params.add(pinNumber);
			
			NameValuePair authType = new BasicNameValuePair("auth_type",String.valueOf(networkInfo.auth_type));
			params.add(authType);
			
			break;
		}
		
		default:
			break;
		}
		
		return params;
	}
}
