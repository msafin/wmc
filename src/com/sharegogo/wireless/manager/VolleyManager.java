package com.sharegogo.wireless.manager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sharegogo.wireless.SharegogoWirelessApp;

public class VolleyManager {
	static private VolleyManager mInstance;
	private RequestQueue mQueue;
	
	private VolleyManager()
	{
		mQueue = Volley.newRequestQueue(SharegogoWirelessApp.getApplication());
	}
	
	static public VolleyManager getInstance()
	{
		if(mInstance == null)
		{
			synchronized(VolleyManager.class)
			{
				if(mInstance == null)
				{
					mInstance = new VolleyManager();
				}
			}
		}
		
		return mInstance;
	}
	
	public void sendRequest(JsonObjectRequest request){
		mQueue.add(request);
		mQueue.start();
	}
	
	public void cancel(String tag){
		mQueue.cancelAll(tag);
	}
	
	public void stop(){
		mQueue.stop();
	}
}
