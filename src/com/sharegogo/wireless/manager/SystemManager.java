package com.sharegogo.wireless.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.config.HttpConstants;

public class SystemManager {
	static private SystemManager mInstance;

	private SystemManager() {

	}

	static public SystemManager getInstance() {
		if (mInstance == null) {
			synchronized (SystemManager.class) {
				if (mInstance == null) {
					mInstance = new SystemManager();
				}
			}
		}

		return mInstance;
	}

	public Request<JSONObject> register(Listener<JSONObject> listener,
			ErrorListener errorListener) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		NameValuePair uuid = new BasicNameValuePair("uuid", "uuid test");
		NameValuePair macWan = new BasicNameValuePair("mac_wan", "mac wan");
		NameValuePair macWifi = new BasicNameValuePair("mac_wifi", "mac wifi");
		NameValuePair channel = new BasicNameValuePair("channel", getChannel());

		params.add(uuid);
		params.add(macWan);
		params.add(macWifi);
		params.add(channel);

		JsonObjectRequest request = new JsonObjectRequest(Method.POST,
				HttpConstants.getActionUrl(HttpConstants.ACTION_REGISTER), URLEncodedUtils.format(params,
						"utf-8"), listener, errorListener);

		request.setTag("register");
		VolleyManager.getInstance().sendRequest(request);

		return request;
	}
	
	public String getChannel(){
		String channel = "default";
		AssetManager assetMgr = SharegogoWirelessApp.getApplication().getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetMgr.open("channel");
			int count = inputStream.available();
			byte[] buffer = new byte[count];
			inputStream.read(buffer);
			channel = new String(buffer);
			return channel.split("=")[1];
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				inputStream = null;
			}
		}
		
		return channel;
	}
	
	public void reboot(Listener<JSONObject> listener,
			ErrorListener errorListener) {

		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_REBOOT), (String) null, listener,
				errorListener);

		request.setTag("reboot");
		VolleyManager.getInstance().sendRequest(request);
	}
}
