package com.sharegogo.wireless.ui;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.NetworkInfo;
import com.sharegogo.wireless.data.ScanResult;
import com.sharegogo.wireless.data.ScanResult.ScanResultItem;
import com.sharegogo.wireless.manager.WifiManager;
import com.sharegogo.wireless.utils.ResUtils;

public class NetworkWifiRepeaterFragment extends BaseNetworkFragment implements OnClickListener{
	public static final String TAG = "repeater";
	private EditText mSSID;
	private EditText mPassword;
	private Button mScan;
	private ProgressDialog mProgressDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage(ResUtils.getString(R.string.get_scan_result));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_wifi_repeater, null);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mSSID = (EditText)view.findViewById(R.id.ssid);
		mPassword = (EditText)view.findViewById(R.id.password);
		mScan = (Button)view.findViewById(R.id.scan);
		mScan.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	void setupNetworkInfo(NetworkInfo networkInfo) {
		networkInfo.network_type = NetworkInfo.NETWORK_REPEATER;
		
		String ssid = mSSID.getText().toString();
		ssid.trim();
		networkInfo.ssid = ssid;
		
		String password = mPassword.getText().toString();
		password.trim();
		networkInfo.password = password;
	}

	@Override
	boolean validate() {
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.scan:
				onScan();
				break;
			default:
				break;
		}
	}
	
	private void onScan()
	{
		mProgressDialog.show();
		WifiManager.getInstance().getScanResult(this,this);
	}

	public void onSuccess(Object data) {
		mProgressDialog.dismiss();
		
		ScanResultFragment fragment = new ScanResultFragment();
		
		ScanResult result = (ScanResult)data;
		if(result.data != null && result.data.length > 0)
		{
			fragment.setScanResult(Arrays.asList(result.data));
			fragment.show(getFragmentManager(), "scan");
		}
		else
		{
			onFailed(-1,null);
		}
	}

	public void onFailed(int what, Object msg) {
		mProgressDialog.dismiss();
		Toast.makeText(getActivity(), R.string.get_scan_result_failed, 1000).show();
	}

	public boolean onPersistent(Object data) {
		return true;
	}
	
	public void onScanResultSelected(ScanResultItem item)
	{
		mSSID.setText(item.ssid);
	}
}
