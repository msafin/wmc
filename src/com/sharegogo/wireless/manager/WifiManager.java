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

public class WifiManager {
	static private WifiManager mInstance;

	private WifiManager() {

	}

	static public WifiManager getInstance() {
		if (mInstance == null) {
			synchronized (WifiManager.class) {
				if (mInstance == null) {
					mInstance = new WifiManager();
				}
			}
		}

		return mInstance;
	}

	public Request<JSONObject> getWifiInfo(Listener<JSONObject> listener,
			ErrorListener errorListener) {

		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_GET_WIFI_INFO), (String) null, listener,
				errorListener);

		request.setTag("getWifiInfo");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}

	public Request<JSONObject> setWifi(String ssid, String encryption,
			String key, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		NameValuePair ssidPair = new BasicNameValuePair("ssid", ssid);
		NameValuePair encPair = new BasicNameValuePair("enc", encryption);
		NameValuePair keyPair = new BasicNameValuePair("key", key);

		params.add(ssidPair);
		params.add(encPair);
		params.add(keyPair);

		JsonObjectRequest request = new JsonObjectRequest(Method.POST,
				HttpConstants.getActionUrl(HttpConstants.ACTION_SET_WIFI), URLEncodedUtils.format(params,
						"utf-8"), listener, errorListener);

		request.setTag("setWifi");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}

	public Request<JSONObject> getScanResult(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_GET_SCAN_RESULT), (String) null, listener,
				errorListener);

		request.setTag("getScanResult");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}

	public Request<JSONObject> getWifiClientList(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_GET_WIFI_CLIENT), (String) null, listener,
				errorListener);

		request.setTag("getWifiClientList");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}
}
