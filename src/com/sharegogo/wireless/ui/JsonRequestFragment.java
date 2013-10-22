package com.sharegogo.wireless.ui;

import org.json.JSONObject;

import android.support.v4.app.Fragment;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

abstract public class JsonRequestFragment extends Fragment implements Listener<JSONObject>,ErrorListener{

	@Override
	public void onErrorResponse(VolleyError error) {
		if(getActivity() == null){
			return;
		}
	}

	@Override
	public void onResponse(JSONObject response){
		if(getActivity() == null){
			return;
		}
		
	}

}
