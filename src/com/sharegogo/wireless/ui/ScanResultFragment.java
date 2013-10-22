package com.sharegogo.wireless.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.data.ScanResult.ScanResultItem;

public class ScanResultFragment extends DialogFragment implements OnItemClickListener{
	private ScanResultAdapter mAdapter = new ScanResultAdapter();
	private ListView mList;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setStyle(STYLE_NO_TITLE, getTheme());
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		
		mList.setAdapter(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.scan_result, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		mList = (ListView)view.findViewById(R.id.scan_result_list);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);
	}

	public void setScanResult(List<ScanResultItem> data)
	{
		mAdapter.addAll(data);
	}
	
	private class ScanResultAdapter extends BaseAdapter
	{
		private List<ScanResultItem> data = new ArrayList<ScanResultItem>();
		
		public void addAll(List<ScanResultItem> list)
		{
			data.addAll(list);
			
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder tag = null;
			
			if(convertView == null)
			{
				tag = new ViewHolder();
				
				Context context = SharegogoWirelessApp.getApplication();
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.scan_result_item, null);
				tag.ssid = (TextView)convertView.findViewById(R.id.ssid);
				tag.channel = (TextView)convertView.findViewById(R.id.channel);
				tag.authType = (TextView)convertView.findViewById(R.id.auth_type);
				tag.rssi = (TextView)convertView.findViewById(R.id.rssi);
				
				convertView.setTag(tag);
			}
			
			tag = (ViewHolder)convertView.getTag();
			ScanResultItem item = data.get(position);
			tag.ssid.setText(item.ssid);
			tag.channel.setText(String.valueOf(item.freq));
			tag.authType.setText(item.auth_type);
			tag.rssi.setText(item.rssi);
			
			return convertView;
		}
		
		private class ViewHolder
		{
			TextView ssid;
			TextView channel;
			TextView authType;
			TextView rssi;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		ScanResultItem item = (ScanResultItem)mAdapter.getItem(arg2);
		
		NetworkWifiRepeaterFragment parent = (NetworkWifiRepeaterFragment)getFragmentManager().findFragmentByTag(NetworkWifiRepeaterFragment.TAG);
		parent.onScanResultSelected(item);
		
		dismiss();
	}
}
