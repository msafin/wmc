package com.sharegogo.wireless.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharegogo.wireless.config.HttpConstants;

public class MediaShareManager {
	static private MediaShareManager mInstance;

	private MediaShareManager() {

	}

	static public MediaShareManager getInstance() {
		if (mInstance == null) {
			synchronized (MediaShareManager.class) {
				if (mInstance == null) {
					mInstance = new MediaShareManager();
				}
			}
		}

		return mInstance;
	}

	public Request<JSONObject> getShareDirs(Listener<JSONObject> listener,
			ErrorListener errorListener) {

		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_GET_SHARE_DIRS), (JSONObject)null, listener,
				errorListener);

		request.setTag("getShareDirs");

		VolleyManager.getInstance().sendRequest(request);

		return request;
	}

	public Request<JSONObject> listDir(String dir, Listener<JSONObject> listener,
			ErrorListener errorListener) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair dirPair = new BasicNameValuePair("dir", dir);
		params.add(dirPair);

		JsonObjectRequest request = new JsonObjectRequest(Method.GET,
				HttpConstants.getActionUrl(HttpConstants.ACTION_LIST_DIR) + "?"
						+ URLEncodedUtils.format(params, "utf-8"), (JSONObject)null,
				listener, errorListener);

		request.setTag("listDir");

		VolleyManager.getInstance().sendRequest(request);

		return request;
	}

	public void enableDlna(String dir) {
		HttpRequest httpRequest = new BasicHttpRequest(HttpPost.METHOD_NAME,
				HttpConstants.getActionUrl(HttpConstants.ACTION_ENABLE_DLNA));
	}
}
