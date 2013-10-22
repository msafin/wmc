package com.sharegogo.wireless.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.config.StatisticHelper;
import com.umeng.analytics.MobclickAgent;

public class FunctionsFragment extends Fragment implements OnClickListener{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.functions_fragment, null);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		TextView systemStatus = (TextView)view.findViewById(R.id.status);
		TextView wifiSetting = (TextView)view.findViewById(R.id.wifi_setting);
		TextView networkSetting = (TextView)view.findViewById(R.id.network_setting);
		TextView mediaShare = (TextView)view.findViewById(R.id.media_share);
		TextView dlna = (TextView)view.findViewById(R.id.dlna);
		TextView appSetting = (TextView)view.findViewById(R.id.app_setting);
		
		systemStatus.setOnClickListener(this);
		wifiSetting.setOnClickListener(this);
		networkSetting.setOnClickListener(this);
		mediaShare.setOnClickListener(this);
		dlna.setOnClickListener(this);
		appSetting.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		MainActivity activity = (MainActivity)getActivity();
		activity.setMainTitle(R.string.functions);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.status:
			onStatusClick();
			break;
		case R.id.wifi_setting:
			onWifiSettingClick();
			break;
		case R.id.network_setting:
			onNetworkSettingClick();
			break;
		case R.id.media_share:
			onMediaShareClick();
			break;
		case R.id.dlna:
			onDlnaClick();
			break;
		case R.id.app_setting:
			onAppSettingClick();
			break;
		default:
			break;
		}
	}
	
	
	private void onStatusClick()
	{
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_SYSTEM_STATUS_CLICKED);
		
		getFragmentManager().beginTransaction()
		.addToBackStack(null)
		.replace(R.id.container, new SystemStatusFragment(),"status")
		.commit();
	}
	
	private void onWifiSettingClick()
	{
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_WIFI_SETTING_CLICKED);
		
		getFragmentManager().beginTransaction()
		.addToBackStack(null)
		.replace(R.id.container, new WifiSettingFragment(),"wifisetting")
		.commit();
	}
	
	private void onNetworkSettingClick()
	{
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_NETWORK_SETTING_CLICKED);
		
		getFragmentManager().beginTransaction()
		.addToBackStack(null)
		.replace(R.id.container, new NetworkSettingFragment(),"networksetting")
		.commit();
	}
	
	private void onMediaShareClick()
	{
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_MEDIA_SHARE_CLICKED);
		
		getFragmentManager().beginTransaction()
		.addToBackStack(null)
		.replace(R.id.container, new MediaShareFragment(),"mediashare")
		.commit();
	}
	
	private void onDlnaClick()
	{
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_DLNA_CLICKED);
		
		getFragmentManager().beginTransaction()
		.addToBackStack(null)
		.replace(R.id.container, new DlnaFragment(),DlnaFragment.TAG)
		.commit();
	}
	
	private void onAppSettingClick()
	{
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_APP_SETTING_CLICKED);
		
		AppSettingActivity.openAppSettingActivity(getActivity());
	}
}
