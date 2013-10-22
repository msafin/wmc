package com.sharegogo.wireless.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.NetworkInfo;

public class NetworkDhcpFragment extends BaseNetworkFragment{
	public static final String TAG = "dhcp";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.network_dhcp, null);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	void setupNetworkInfo(NetworkInfo networkInfo) {
		// TODO Auto-generated method stub
		networkInfo.network_type = NetworkInfo.NETWORK_DHCP;
	}

	@Override
	boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}
}
