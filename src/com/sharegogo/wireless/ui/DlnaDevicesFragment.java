package com.sharegogo.wireless.ui;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.model.meta.Device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.config.StatisticHelper;
import com.sharegogo.wireless.dlna.UpnpClient;
import com.sharegogo.wireless.dlna.UpnpClientListener;
import com.sharegogo.wireless.download.DownloadManager;
import com.umeng.analytics.MobclickAgent;

public class DlnaDevicesFragment extends Fragment implements OnClickListener,
		UpnpClientListener, OnItemClickListener {
	public static final String TAG = "DlnaDevicesFragment";
	private ListView mListView;
	public static UpnpClient mUpnpClient = null;
	private ImageButton mDownload;
	private BrowseDeviceAdapter mDeviceAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		MainActivity activity = (MainActivity) getActivity();
		activity.setMainTitle(R.string.dlna);

		mDownload = (ImageButton) activity.findViewById(R.id.right_button);

		mDownload.setOnClickListener(this);
		mDownload.setVisibility(View.VISIBLE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUpnpClient = UpnpClient.getInstance();
		
		Context context = SharegogoWirelessApp.getApplication();
		mDeviceAdapter = new BrowseDeviceAdapter(mUpnpClient, context);
		mUpnpClient.addUpnpClientListener(this);
	}

	@Override
	public void onDestroy() {
		mUpnpClient.removeUpnpClientListener(this);
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		mDownload.setVisibility(View.GONE);
		mListView.setAdapter(null);
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dlna_media_server_list, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView = (ListView) view.findViewById(R.id.itemList);

		mListView.setAdapter(mDeviceAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void deviceAdded(Device<?, ?, ?> device) {
		mDeviceAdapter.add(device);

		Activity activity = getActivity();
		if (activity != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDeviceAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void deviceRemoved(Device<?, ?, ?> device) {
		mDeviceAdapter.remove(device);

		Activity activity = getActivity();
		if (activity != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDeviceAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void deviceUpdated(Device<?, ?, ?> device) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_button:
			onDownloadClicked();
			break;
		default:
			break;
		}
	}

	private void onDownloadClicked() {
		Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);

		pageView.setClass(getActivity(), DownloadList.class);
		pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		getActivity().startActivity(pageView);
		MobclickAgent.onEvent(getActivity(),
				StatisticHelper.EVENT_DOWNLOAD_BUTTON_CLICKED);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		
		Device device= (Device) mDeviceAdapter.getItem(position);

		String udn = device.getIdentity().getUdn().getIdentifierString();
		
		Bundle args = new Bundle();
		args.putString(DlnaDetailFragment.KEY_UDN, udn);
		args.putString(DlnaDetailFragment.KEY_OBJECT_ID, "0");
		
		DlnaDetailFragment fragment = (DlnaDetailFragment) Fragment
				.instantiate(getActivity(), DlnaDetailFragment.class.getName(),
						args);

		getFragmentManager().beginTransaction()
				.replace(R.id.dlna_container, fragment, DlnaDetailFragment.TAG)
				.addToBackStack(null).commit();
	}
	
	public class BrowseDeviceAdapter extends BaseAdapter {
		private UpnpClient mUpnpClient;
		private List<Device> mDevices;
		private LayoutInflater mInflator;	
		
		public BrowseDeviceAdapter(UpnpClient upnpClient,Context ctx) {
			super();
			mUpnpClient = upnpClient;
			mUpnpClient.storeNewVisitedObjectId("-1");
			mDevices = new ArrayList<Device>();

			mInflator = LayoutInflater.from(ctx);
		}

		public void add(Device device){
			mDevices.add(device);
		}
		
		public void remove(Device device){
			mDevices.remove(device);
		}
		
		@Override
		public int getCount() {
			return mDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return mDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			
			if(convertView == null){
				convertView = mInflator.inflate(R.layout.browse_item,parent,false);
				
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.browseItemIcon);
				holder.name = (TextView) convertView.findViewById(R.id.browseItemName);			
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.icon.setImageResource(R.drawable.device);
			holder.name.setText(((Device)getItem(position)).getDisplayString());
			
			return convertView;
		}
	}
	
	static class ViewHolder{
		ImageView icon;
		TextView name;
	}
}
