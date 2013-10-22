package com.sharegogo.wireless.ui;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.SystemStatus;
import com.sharegogo.wireless.manager.NetworkManager;
import com.sharegogo.wireless.utils.ResUtils;

public class SystemStatusFragment extends JsonRequestFragment{
	public SystemStatus mStatus;
	
	//memory
	private TextView mMemTotal;
	private TextView mMemFree;
	
	//wan
	private TextView mWanIpAddr;
	private TextView mWanMac;
		
	//lan
	private TextView mLanIpAddr;
	private TextView mLanMac;
	
	//wifi
	private TextView mSSID;
	private TextView mWifiMac;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.system_status_fragment, null);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mMemTotal = (TextView)view.findViewById(R.id.memory_total);
		mMemFree = (TextView)view.findViewById(R.id.memory_free);
		
		mWanIpAddr = (TextView)view.findViewById(R.id.wan_ip);
		mWanMac = (TextView)view.findViewById(R.id.wan_mac);
		
		mLanIpAddr = (TextView)view.findViewById(R.id.lan_ip);
		mLanMac = (TextView)view.findViewById(R.id.lan_mac);
		
		mSSID = (TextView)view.findViewById(R.id.ssid);
		mWifiMac = (TextView)view.findViewById(R.id.wifi_mac);
		
		NetworkManager.getInstance().getSystemStatus(this,this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		MainActivity activity = (MainActivity)getActivity();
		activity.setMainTitle(R.string.status);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
	}

	@Override
	public void onResponse(JSONObject response) {
		super.onResponse(response);
		
		mStatus = SystemStatus.fromJson(response.toString());
		setupView(mStatus);
	}

	private void setupView(SystemStatus status)
	{
		setupMemoryView(status);
		setupWanView(status);
		setupLanView(status);
		setupWifiView(status);
	}
	
	private void setupMemoryView(SystemStatus status)
	{
		mMemTotal.setText(ResUtils.getString(R.string.memory_total) + ":" + status.mem_total + "KB");
		mMemFree.setText(ResUtils.getString(R.string.memory_free) + ":" + status.mem_free + "KB");
	}
	
	private void setupWanView(SystemStatus status)
	{
		mWanIpAddr.setText(ResUtils.getString(R.string.ip_address)+ status.wan_ip);
		mWanMac.setText(ResUtils.getString(R.string.mac_address) + status.wan_mac);
	}
	
	private void setupLanView(SystemStatus status)
	{
		mLanIpAddr.setText(ResUtils.getString(R.string.ip_address) + status.lan_ip);
		mLanMac.setText(ResUtils.getString(R.string.mac_address) + status.lan_mac);
	}
	
	private void setupWifiView(SystemStatus status)
	{
		mSSID.setText(ResUtils.getString(R.string.ssid) + ":" + status.ssid);
		mWifiMac.setText(ResUtils.getString(R.string.mac_address) + status.wifi_mac);
	}
}
