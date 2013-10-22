package com.sharegogo.wireless.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.android.volley.VolleyError;
import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.NetworkInfo;
import com.sharegogo.wireless.manager.NetworkManager;
import com.sharegogo.wireless.utils.ResUtils;

public class NetworkSettingFragment extends JsonRequestFragment implements OnCheckedChangeListener, OnClickListener{
	private RadioButton mNetworkDhcp;
	private RadioButton mNetworkPppoe;
	private RadioButton mNetworkWifiRepeater;
	private RadioButton mNetworkStatic;
	private Button m3GSetting;
	private int mNetworkType = NetworkInfo.NETWORK_DHCP;
	private ProgressDialog mProgressDialog;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage(ResUtils.getString(R.string.setting));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_setting_fragment, null);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mNetworkDhcp = (RadioButton)view.findViewById(R.id.network_dhcp);
		mNetworkPppoe = (RadioButton)view.findViewById(R.id.network_pppoe);
		mNetworkWifiRepeater = (RadioButton)view.findViewById(R.id.network_wifi_repeater);
		mNetworkStatic = (RadioButton)view.findViewById(R.id.network_static);
		m3GSetting = (Button)view.findViewById(R.id.setting_3g);
		Button commit = (Button)view.findViewById(R.id.commit);
		
		mNetworkDhcp.setOnCheckedChangeListener(this);
		mNetworkPppoe.setOnCheckedChangeListener(this);
		mNetworkWifiRepeater.setOnCheckedChangeListener(this);
		mNetworkStatic.setOnCheckedChangeListener(this);
		m3GSetting.setOnClickListener(this);
		commit.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		MainActivity activity = (MainActivity)getActivity();
		activity.setMainTitle(R.string.network_setting);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(!isChecked)
		{
			return;
		}
		
		switch(buttonView.getId())
		{
		case R.id.network_dhcp:
			onNetworkDhcp();
			break;
		case R.id.network_pppoe:
			onNetworkPppoe();
			break;
		case R.id.network_wifi_repeater:
			onNetworkWifiRepeater();
			break;
		case R.id.network_static:
			onNetworkStatic();
			break;
			default:
				break;
		}
	}
	
	private void onNetworkDhcp()
	{
		mNetworkDhcp.setChecked(true);
		mNetworkPppoe.setChecked(false);
		mNetworkWifiRepeater.setChecked(false);
		mNetworkStatic.setChecked(false);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.network_type_detail, new NetworkDhcpFragment(),NetworkDhcpFragment.TAG)
			.commit();
	}
	
	private void onNetworkPppoe()
	{
		mNetworkDhcp.setChecked(false);
		mNetworkPppoe.setChecked(true);
		mNetworkWifiRepeater.setChecked(false);
		mNetworkStatic.setChecked(false);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.network_type_detail, new NetworkPppoeFragment(),NetworkPppoeFragment.TAG)
			.commit();
	}
	
	private void onNetworkWifiRepeater()
	{
		mNetworkDhcp.setChecked(false);
		mNetworkPppoe.setChecked(false);
		mNetworkWifiRepeater.setChecked(true);
		mNetworkStatic.setChecked(false);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.network_type_detail, new NetworkWifiRepeaterFragment(),NetworkWifiRepeaterFragment.TAG)
			.commit();
	}
	
	private void onNetworkStatic()
	{
		mNetworkDhcp.setChecked(false);
		mNetworkPppoe.setChecked(false);
		mNetworkWifiRepeater.setChecked(false);
		mNetworkStatic.setChecked(true);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.network_type_detail, new NetworkStaticFragment(),NetworkStaticFragment.TAG)
			.commit();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.setting_3g:
				on3GSetting();
				break;
			case R.id.commit:
				onCommit();
				break;
			default:
				break;
		}
	}
	
	private void on3GSetting()
	{
		getFragmentManager().beginTransaction()
		.addToBackStack(null)
		.replace(R.id.container, new Network3GFragment(),null)
		.commit();
	}
	
	private void onCommit()
	{
		mProgressDialog.show();
		
		switch(getNetworkType())
		{
			case NetworkInfo.NETWORK_DHCP:
				onDhcpCommit();
				break;
			case NetworkInfo.NETWORK_PPPOE:
				onPppoeCommit();
				break;
			case NetworkInfo.NETWORK_REPEATER:
				onWifiRepeaterCommit();
				break;
			case NetworkInfo.NETWORK_STATIC:
				onStaticCommit();
				break;
			default:
				break;
		}
	}
	
	private void onDhcpCommit()
	{
		NetworkInfo networkInfo = new NetworkInfo();
		
		BaseNetworkFragment fragment = (BaseNetworkFragment)getFragmentManager().findFragmentByTag(NetworkDhcpFragment.TAG);
		
		if(!fragment.validate())
		{
			return;
		}
		
		fragment.setupNetworkInfo(networkInfo);
		
		mNetworkType = NetworkInfo.NETWORK_DHCP;
		
		NetworkManager.getInstance().setNetwork(networkInfo,this,this);
	}
	
	private void onPppoeCommit()
	{
		NetworkInfo networkInfo = new NetworkInfo();
		
		BaseNetworkFragment fragment = (BaseNetworkFragment)getFragmentManager().findFragmentByTag(NetworkPppoeFragment.TAG);
		
		if(!fragment.validate())
		{
			return;
		}
		
		fragment.setupNetworkInfo(networkInfo);
		
		mNetworkType = NetworkInfo.NETWORK_PPPOE;
		
		NetworkManager.getInstance().setNetwork(networkInfo,this,this);
	}
	
	private void onWifiRepeaterCommit()
	{
		NetworkInfo networkInfo = new NetworkInfo();
		
		BaseNetworkFragment fragment = (BaseNetworkFragment)getFragmentManager().findFragmentByTag(NetworkWifiRepeaterFragment.TAG);

		if(!fragment.validate())
		{
			return;
		}
		
		fragment.setupNetworkInfo(networkInfo);
		
		mNetworkType = NetworkInfo.NETWORK_REPEATER;
		
		NetworkManager.getInstance().setNetwork(networkInfo,this,this);
	}
	
	private void onStaticCommit()
	{
		NetworkInfo networkInfo = new NetworkInfo();
		
		BaseNetworkFragment fragment = (BaseNetworkFragment)getFragmentManager().findFragmentByTag(NetworkStaticFragment.TAG);

		if(!fragment.validate())
		{
			return;
		}
		
		fragment.setupNetworkInfo(networkInfo);
		
		mNetworkType = NetworkInfo.NETWORK_STATIC;
		
		NetworkManager.getInstance().setNetwork(networkInfo,this,this);
	}
	
	private int getNetworkType()
	{
		if(mNetworkDhcp.isChecked())
		{
			return NetworkInfo.NETWORK_DHCP;
		}
		else if(mNetworkPppoe.isChecked())
		{
			return NetworkInfo.NETWORK_PPPOE;
		}
		else if(mNetworkWifiRepeater.isChecked())
		{
			return NetworkInfo.NETWORK_REPEATER;
		}
		else if(mNetworkStatic.isChecked())
		{
			return NetworkInfo.NETWORK_STATIC;
		}
		else
		{
			return NetworkInfo.NETWORK_DHCP;
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		mProgressDialog.dismiss();
	}

	@Override
	public void onResponse(JSONObject response) {
		super.onResponse(response);
		mProgressDialog.dismiss();
	}
}
