package com.sharegogo.wireless.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.data.WifiClientList;
import com.sharegogo.wireless.data.WifiClientList.WifiClient;
import com.sharegogo.wireless.data.WifiInfo;
import com.sharegogo.wireless.manager.WifiManager;
import com.sharegogo.wireless.utils.ResUtils;

public class WifiSettingFragment extends JsonRequestFragment implements
		OnClickListener, OnItemSelectedListener {
	private Button mCommit;
	private EditText mSSID;
	private EditText mKey;
	private TextView mCurrentSSID;
	private ProgressDialog mProgressDialog;
	private WifiInfo mWifiInfo;
	private Spinner mAuthType;
	private Spinner mEncType;
	private boolean mIsAdvance;
	private ListView mClientList;
	private WifiClientAdapter mClientAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mClientAdapter = new WifiClientAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.wifi_setting_fragment, null);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mClientList.setAdapter(null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mCommit = (Button) view.findViewById(R.id.commit);
		mSSID = (EditText) view.findViewById(R.id.ssid);
		mKey = (EditText) view.findViewById(R.id.key);
		mCurrentSSID = (TextView) view.findViewById(R.id.current_ssid);
		//mAuthType = (Spinner) view.findViewById(R.id.auth_type);
		//mEncType = (Spinner) view.findViewById(R.id.enc_type);
		
		mClientList = (ListView) view.findViewById(R.id.wifi_client_list);
		mClientList.setAdapter(mClientAdapter);
		mCommit.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		MainActivity activity = (MainActivity) getActivity();
		activity.setMainTitle(R.string.wireless_setting);

//		// auth type
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//				getActivity(), R.array.auth_type_wifi,
//				android.R.layout.simple_spinner_item);
//
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		mAuthType.setAdapter(adapter);
//		mAuthType.setOnItemSelectedListener(this);
//
//		// enc type
//		adapter = ArrayAdapter.createFromResource(getActivity(),
//				R.array.enc_type_wifi, android.R.layout.simple_spinner_item);
//
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		mEncType.setAdapter(adapter);
//		mEncType.setOnItemSelectedListener(this);

		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
		}

		if (mWifiInfo == null) {
			mProgressDialog.setMessage(ResUtils
					.getString(R.string.get_wifi_info));
			mProgressDialog.show();
			WifiManager.getInstance().getWifiInfo(this, this);
		}

		WifiManager.getInstance().getWifiClientList(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				WifiClientList list = WifiClientList.fromJson(response.toString());
				mClientAdapter.clear();
				mClientAdapter.addAll(list.data);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mProgressDialog.dismiss();
		mProgressDialog = null;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.commit:
			onCommit();
			break;
		default:
			break;
		}
	}

	private void onCommit() {
		String ssid = mSSID.getText().toString();
		String key = mKey.getText().toString();

		mProgressDialog.setMessage(ResUtils.getString(R.string.setting));
		mProgressDialog.show();

		WifiManager.getInstance().setWifi(ssid, "WPA2PSK", key,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mCurrentSSID.setText(mSSID.getText().toString());
						Toast.makeText(getActivity(), R.string.set_success,
								3000).show();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
	}

	public void onFailed(int what, Object msg) {
		if(getActivity() == null){
			return;
		}
		
		mProgressDialog.dismiss();

		if (isGetWifiInfo()) {
			Toast.makeText(getActivity(), R.string.get_wifi_info_failed, 3000)
					.show();
		} else {
			if (msg == null) {
				Toast.makeText(getActivity(), R.string.set_failed, 3000).show();
			} else {
				if (msg instanceof String)
					Toast.makeText(getActivity(), (String) msg, 3000).show();
				else
					Toast.makeText(getActivity(), ((WifiInfo) msg).msg, 3000)
							.show();
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		if(getActivity() == null){
			return;
		}
		mProgressDialog.dismiss();
	}

	@Override
	public void onResponse(JSONObject response) {
		if(getActivity() == null){
			return;
		}
		super.onResponse(response);
		mProgressDialog.dismiss();
		onGetWifiInfo(WifiInfo.fromJson(response.toString()));
	}

	private void onGetWifiInfo(WifiInfo info) {
		if (info != null) {
			if (info.ssid != null)
				mCurrentSSID.setText(info.ssid);
		}
	}

	private boolean isGetWifiInfo() {
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private static class WifiClientAdapter extends BaseAdapter {
		private List<WifiClient> mData = new ArrayList<WifiClient>();
		private Context mContext = SharegogoWirelessApp
				.getApplication();
		private LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		public void clear() {
			mData.clear();

			notifyDataSetChanged();
		}

		public void addAll(List<WifiClient> clients) {
			mData.addAll(clients);

			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder tag = null;
			WifiClient client = mData.get(position);

			if (convertView == null) {
				tag = new ViewHolder();

				convertView = mInflater
						.inflate(R.layout.wifi_client_item, null);

				tag.mMac = (TextView) convertView.findViewById(R.id.client_mac);
				tag.mTxPackets = (TextView) convertView
						.findViewById(R.id.tx_packets);
				tag.mRxPackets = (TextView) convertView
						.findViewById(R.id.rx_packets);

				convertView.setTag(tag);
			}

			tag = (ViewHolder) convertView.getTag();

			tag.mMac.setText(client.mac);
			tag.mTxPackets.setText(String.valueOf(client.tx_packets));
			tag.mRxPackets.setText(String.valueOf(client.rx_packets));

			return convertView;
		}
	}

	private static class ViewHolder {
		public TextView mMac;
		public TextView mRxPackets;
		public TextView mTxPackets;
	}
}
