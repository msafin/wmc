package com.sharegogo.wireless.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.sharegogo.wireless.R;
import com.sharegogo.wireless.data.NetworkInfo;
import com.sharegogo.wireless.manager.NetworkManager;
import com.sharegogo.wireless.utils.ResUtils;

public class Network3GFragment extends BaseNetworkFragment implements
		OnItemSelectedListener, OnClickListener {
	private Spinner mDialMode;
	private EditText mDuration;
	private EditText mUserName;
	private EditText mToken;
	private EditText mDialNumber;
	private EditText mApn;
	private EditText mPinNumber;
	private Spinner mAuthType;
	private Button mCommit;
	private Button mConnect;
	private View mDurationContainer;
	private ProgressDialog mProgressDialog;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage(ResUtils.getString(R.string.setting));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.network_3g, null);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

		mDialMode.setAdapter(null);
		mAuthType.setAdapter(null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		mDialMode = (Spinner) view.findViewById(R.id.dial_mode);
		mDuration = (EditText) view.findViewById(R.id.duration);
		mDurationContainer = view.findViewById(R.id.duration_view);
		mUserName = (EditText) view.findViewById(R.id.user_name);
		mToken = (EditText) view.findViewById(R.id.token);
		mDialNumber = (EditText) view.findViewById(R.id.dial_number);
		mApn = (EditText) view.findViewById(R.id.apn);
		mPinNumber = (EditText) view.findViewById(R.id.pin_number);
		mAuthType = (Spinner) view.findViewById(R.id.auth_type);

		mCommit = (Button) view.findViewById(R.id.commit);
		mCommit.setOnClickListener(this);
		mConnect = (Button) view.findViewById(R.id.usb_3g_modem_connect);
		mConnect.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.usb_3g_modem_dial_mode,
				android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDialMode.setAdapter(adapter);
		mDialMode.setOnItemSelectedListener(this);

		adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.auth_type_3g, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAuthType.setAdapter(adapter);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if (arg0 == mDialMode) {
			switch (arg2) {
			case 0:
			case 1:
				mDurationContainer.setVisibility(View.GONE);
				break;
			case 2:
				mDurationContainer.setVisibility(View.VISIBLE);
				mDuration.setText("5");
				break;
			case 3:
				mDurationContainer.setVisibility(View.VISIBLE);
				mDuration.setText("15");
				break;
			case 4:
				mDurationContainer.setVisibility(View.VISIBLE);
				mDuration.setText("30");
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.commit:
			on3gCommit();
			break;
		case R.id.usb_3g_modem_connect:
			on3gConnect();
			break;
		default:
			break;
		}
	}

	private void on3gConnect() {

	}

	private void on3gCommit() {
		mProgressDialog.show();

		NetworkInfo networkInfo = new NetworkInfo();

		if (!validate()) {
			return;
		}

		setupNetworkInfo(networkInfo);
		NetworkManager.getInstance().setNetwork(networkInfo,this,this);
	}

	@Override
	void setupNetworkInfo(NetworkInfo networkInfo) {
		networkInfo.network_type = NetworkInfo.NETWORK_3G;

		String usrName = mUserName.getText().toString();
		networkInfo.user_name = usrName.trim();

		String token = mToken.getText().toString();
		networkInfo.password = token.trim();

		String dialNumber = mDialNumber.getText().toString();
		networkInfo.dial_number = dialNumber.trim();

		String apn = mApn.getText().toString();
		networkInfo.apn_name = apn.trim();

		String pinNumber = mPinNumber.getText().toString();
		networkInfo.pin_number = pinNumber.trim();

		int position = mDialMode.getSelectedItemPosition();
		if (position == 0) {
			networkInfo.op_mode = NetworkInfo.OP_MODE_KEEP_ALIVE;
		} else if (position == 1) {
			networkInfo.op_mode = NetworkInfo.OP_MODE_MANUAL;
		} else {
			networkInfo.op_mode = NetworkInfo.OP_MODE_DYNAMIC;

			String duration = mDuration.getText().toString();
			networkInfo.duration = Integer.valueOf(duration.trim());
		}

		position = mAuthType.getSelectedItemPosition();
		if (position == 0) {
			networkInfo.auth_type = NetworkInfo.AUTH_TYPE_NONE;
		} else if (position == 1) {
			networkInfo.auth_type = NetworkInfo.AUTH_TYPE_CHAP;
		} else {
			networkInfo.auth_type = NetworkInfo.AUTH_TYPE_PAP;
		}

	}

	@Override
	boolean validate() {
		return true;
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
