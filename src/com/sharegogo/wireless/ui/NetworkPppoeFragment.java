package com.sharegogo.wireless.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.NetworkInfo;

public class NetworkPppoeFragment extends BaseNetworkFragment implements OnItemSelectedListener{
	public static final String TAG = "pppoe";
	
	private Spinner mOperationMode;
	private View mOperaionModeDetail;
	private EditText mOperationDuration;
	private EditText mUserName;
	private EditText mPassword;
	private EditText mConfirmPassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.network_pppoe, null);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		
		mOperationMode.setAdapter(null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		mOperationMode = (Spinner) view.findViewById(R.id.operation_mode);
		mOperaionModeDetail = view.findViewById(R.id.operation_mode_detail_view);
		
		mUserName = (EditText)view.findViewById(R.id.user_name);
		mPassword = (EditText)view.findViewById(R.id.password);
		mConfirmPassword = (EditText)view.findViewById(R.id.confirm_password);
		mOperationDuration = (EditText)view.findViewById(R.id.operation_duration);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
			    	R.array.pppoe_operation_mode, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mOperationMode.setAdapter(adapter);
		mOperationMode.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch(arg2)
		{
		//永久连接
		case 0:
		{
			mOperaionModeDetail.setVisibility(View.GONE);
			TextView title = (TextView)mOperaionModeDetail.findViewById(R.id.operation_mode__detail_text);
			title.setText(R.string.pppoe_keep_alive);
			TextView timeUnit = (TextView)mOperaionModeDetail.findViewById(R.id.operation_mode_time_unit);
			timeUnit.setText(R.string.time_unit_second);
			mOperationDuration = (EditText)mOperaionModeDetail.findViewById(R.id.operation_duration);
			break;
		}
		//动态连接
		case 1:
		{
			mOperaionModeDetail.setVisibility(View.VISIBLE);
			TextView title = (TextView)mOperaionModeDetail.findViewById(R.id.operation_mode__detail_text);
			title.setText(R.string.pppoe_on_demand);
			TextView timeUnit = (TextView)mOperaionModeDetail.findViewById(R.id.operation_mode_time_unit);
			timeUnit.setText(R.string.time_unit_minute);
			mOperationDuration = (EditText)mOperaionModeDetail.findViewById(R.id.operation_duration);
			break;
		}
		//手动
		case 2:
		{
			mOperaionModeDetail.setVisibility(View.GONE);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void setupNetworkInfo(NetworkInfo networkInfo) {
		// TODO Auto-generated method stub
		networkInfo.network_type = NetworkInfo.NETWORK_PPPOE;
		
		String userName = mUserName.getText().toString();
		userName.trim();
		networkInfo.user_name = userName;
		
		String password = mPassword.getText().toString();
		password.trim();
		networkInfo.password = password;
		
		int opMode = mOperationMode.getSelectedItemPosition();
		if(opMode == 0)
		{
			networkInfo.op_mode = NetworkInfo.OP_MODE_KEEP_ALIVE;
		}
		else if(opMode == 1)
		{
			networkInfo.op_mode = NetworkInfo.OP_MODE_DYNAMIC;
		}
		else
		{
			networkInfo.op_mode = NetworkInfo.OP_MODE_MANUAL;
		}
		
		if(networkInfo.op_mode == NetworkInfo.OP_MODE_DYNAMIC)
		{
			String duration = mOperationDuration.getText().toString();
			networkInfo.duration = Integer.valueOf(duration);
		}
	}

	@Override
	boolean validate() {
		// TODO Auto-generated method stub
		
		//check user
		String userName = mUserName.getText().toString();
		if(userName != null)
		{
			userName.trim();
		}
		
		if(userName == null || userName.length() <= 0)
		{
			return false;
		}
		
		
		return true;
	}
}
