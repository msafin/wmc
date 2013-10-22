package com.sharegogo.wireless.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.NetworkInfo;

public class NetworkStaticFragment extends BaseNetworkFragment{
	public static final String TAG = "static";
	private EditText mIpAddress;
	private EditText mSubnetMask;
	private EditText mDefaultGateway;
	private EditText mMajorDns;
	private EditText mMinorDns;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.network_static, null);
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
		
		mIpAddress = (EditText)view.findViewById(R.id.ip_address);
		mSubnetMask = (EditText)view.findViewById(R.id.subnet_mask);
		mDefaultGateway = (EditText)view.findViewById(R.id.default_gateway);
		mMajorDns = (EditText)view.findViewById(R.id.major_dns);
		mMinorDns = (EditText)view.findViewById(R.id.minor_dns);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	void setupNetworkInfo(NetworkInfo networkInfo) {
		// TODO Auto-generated method stub
		networkInfo.network_type = NetworkInfo.NETWORK_STATIC;
		
		String ipAddress = mIpAddress.getText().toString();
		ipAddress.trim();
		networkInfo.ip_addr = ipAddress;
		
		String subnetMask = mSubnetMask.getText().toString();
		subnetMask.trim();
		networkInfo.subnet_mask = subnetMask;
		
		String defaultGateway = mDefaultGateway.getText().toString();
		defaultGateway.trim();
		networkInfo.default_gateway = defaultGateway;
		
		String majorDns = mMajorDns.getText().toString();
		majorDns.trim();
		networkInfo.major_dns = majorDns;
		
		String minorDns = mMinorDns.getText().toString();
		minorDns.trim();
		networkInfo.minor_dns = minorDns;
	}

	@Override
	boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}
}
