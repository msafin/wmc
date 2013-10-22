package com.sharegogo.wireless.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.contentdirectory.callback.Browse.Status;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.PlaylistItem;
import org.fourthline.cling.support.model.item.TextItem;
import org.fourthline.cling.support.model.item.VideoItem;
import org.openintents.filemanager.IntentFilterActivity;
import org.openintents.intents.FileManagerIntents;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.dlna.ContentDirectoryBrowseActionCallback.BrowseListener;
import com.sharegogo.wireless.dlna.UpnpClient;
import com.sharegogo.wireless.dlna.Utils;
import com.sharegogo.wireless.download.DownloadManager;
import com.sharegogo.wireless.download.DownloadManager.Request;
import com.sharegogo.wireless.utils.MediaUtils;
import com.sharegogo.wireless.utils.ResUtils;

public class DlnaDetailFragment extends Fragment implements OnClickListener,
		BrowseListener, OnItemClickListener {
	public static final String TAG = "DlnaDetailFragment";
	public static final String KEY_OBJECT_ID = "object_id";
	public static final String KEY_UDN = "udn";
	protected static final int REQUEST_CODE_PICK_FILE_OR_DIRECTORY = 1;
	protected static final int REQUEST_CODE_GET_CONTENT = 2;
	private static final String REQUEST_TYPE = "type";
	private static final String MY_EXTRA = "org.openintents.filemanager.demo.EXTRA_MY_EXTRA";

	private ListView mListView;
	public static UpnpClient mUpnpClient;
	private ImageButton mDownload;
	private BrowseItemAdapter mBrowseItemAdapter;
	private String mObjectId;
	private String mUDN;
	private Handler mHandler;

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
		mHandler = new Handler();

		Bundle args = getArguments();
		if (args != null) {
			mUDN = args.getString(KEY_UDN);
			mObjectId = args.getString(KEY_OBJECT_ID);
		}

		if (mObjectId == null) {
			mObjectId = "0";
		}

		mBrowseItemAdapter = new BrowseItemAdapter(mUpnpClient, getActivity(),
				mObjectId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dlna_media_server_list, null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		mDownload.setVisibility(View.GONE);
		mListView.setAdapter(null);

		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mListView = (ListView) view.findViewById(R.id.itemList);
		mListView.setAdapter(mBrowseItemAdapter);
		mListView.setOnItemClickListener(this);

		registerForContextMenu(mListView);

		if (mBrowseItemAdapter.getCount() <= 0) {
			Device device = mUpnpClient.getDevice(mUDN);

			if (device != null) {
				mUpnpClient.browseAsync(device, mObjectId, this);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) (item
				.getMenuInfo());

		Object dataItem = mListView.getAdapter().getItem(info.position);
		if (dataItem instanceof DIDLObject) {

			// обть
			String title = item.getTitle().toString();

			if (title.contentEquals(ResUtils
					.getString(R.string.browse_context_download))) {
				DIDLObject mediaItem = (DIDLObject) dataItem;
				Res res = mediaItem.getFirstResource();

				saveFile(res.getValue(), mediaItem.getTitle());
			} else if (title.contentEquals(ResUtils
					.getString(R.string.browse_context_play))
					|| title.contentEquals(ResUtils
							.getString(R.string.browse_context_open))) {
				DIDLObject mediaItem = (DIDLObject) dataItem;
				performAction(mediaItem);
			}
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		DIDLObject item = (DIDLObject) mBrowseItemAdapter
				.getItem(info.position);

		if(item instanceof Container){
			return;
		}
		
		menu.setHeaderTitle(v.getContext().getString(
				R.string.browse_context_title));

		ArrayList<String> menuItems = new ArrayList<String>();

		if (Utils.isAudio(item) || Utils.isVideo(item)) {
			menuItems.add(v.getContext()
					.getString(R.string.browse_context_play));
		} else if (Utils.isImage(item) || Utils.isText(item)) {
			menuItems.add(v.getContext()
					.getString(R.string.browse_context_open));
		}

		menuItems.add(v.getContext()
				.getString(R.string.browse_context_download));

		for (int i = 0; i < menuItems.toArray(new String[menuItems.size()]).length; i++) {
			menu.add(Menu.NONE, i, i, menuItems.get(i));
		}
	}

	private void startDownload(Uri uri, String destination) {
		DownloadManager downloadMgr = SharegogoWirelessApp.getDownloadManager();

		Request request = new Request(uri);
		request.setDestinationUri(Uri.parse(destination));

		downloadMgr.enqueue(request);
	}

	private void saveFile(String url, String name) {
		String fileName = name;

		Intent intent = new Intent(FileManagerIntents.ACTION_PICK_FILE);
		intent.setClass(getActivity(), IntentFilterActivity.class);
		// Construct URI from file name.
		File file = new File(fileName);
		intent.setData(Uri.fromFile(file));

		// Set fancy title and button (optional)
		intent.putExtra(FileManagerIntents.EXTRA_TITLE,
				ResUtils.getString(R.string.select_save_direction));
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT,
				ResUtils.getString(R.string.ok));
		intent.putExtra(MY_EXTRA, url);
		intent.putExtra(REQUEST_TYPE, HttpGet.METHOD_NAME);

		try {
			startActivityForResult(intent, REQUEST_CODE_PICK_FILE_OR_DIRECTORY);
		} catch (ActivityNotFoundException e) {
			// No compatible file manager was found.
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_PICK_FILE_OR_DIRECTORY:
				String type = data.getStringExtra(REQUEST_TYPE);
				if (HttpPost.METHOD_NAME.contentEquals(type)) {
					Uri fileUri = data.getData();

				} else {
					Uri fileUri = data.getData();
					String url = data.getStringExtra(MY_EXTRA);
					startDownload(Uri.parse(url), fileUri.toString());
				}
				break;
			default:
				break;
			}
		}
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
	}

	private void performAction(DIDLObject object) {
		if (Utils.isVideo(object)) {
			playVideo((VideoItem) object);
		} else if (Utils.isAudio(object)) {
			playAudio((AudioItem) object);
		} else if (Utils.isImage(object)) {
			openImage((ImageItem) object);
		} else if (Utils.isPlayList(object)) {

		} else {
			openFile(object);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> ada, View view, int position, long id) {
		DIDLObject item = (DIDLObject) mBrowseItemAdapter.getItem(position);

		if (item instanceof Container) {
			String newObjectId = item.getId() == null ? "0"
					: ((DIDLObject) mBrowseItemAdapter.getItem(position))
							.getId();

			Bundle args = new Bundle();
			args.putString(DlnaDetailFragment.KEY_UDN, mUDN);
			args.putString(DlnaDetailFragment.KEY_OBJECT_ID, newObjectId);

			DlnaDetailFragment fragment = (DlnaDetailFragment) Fragment
					.instantiate(getActivity(),
							DlnaDetailFragment.class.getName(), args);

			getFragmentManager()
					.beginTransaction()
					.replace(R.id.dlna_container, fragment,
							DlnaDetailFragment.TAG).addToBackStack(null)
					.commit();
		} else {
			performAction(item);
		}
	}

	private void playVideo(VideoItem item) {
		Res res = item.getFirstResource();
		Uri uri = Uri.parse(res.getValue());

		MediaUtils.playVideo(getActivity(), uri);
	}

	private void playAudio(AudioItem item) {
		Res res = item.getFirstResource();
		Uri uri = Uri.parse(res.getValue());

		MediaUtils.playAudio(getActivity(), uri);
	}

	private void openImage(ImageItem item) {
		Res res = item.getFirstResource();
		Uri uri = Uri.parse(res.getValue());

		MediaUtils.openImage(getActivity(), uri);
	}

	private void openFile(DIDLObject item) {
		Res res = item.getFirstResource();
		Uri uri = Uri.parse(res.getValue());

		MediaUtils.openFile(getActivity(), uri);
	}
	
	@Override
	public void success(final DIDLContent didl) {
		if (getActivity() == null) {
			return;
		}

		mHandler.post(new Runnable() {
			public void run() {
				mBrowseItemAdapter.addAll(didl.getContainers());
				mBrowseItemAdapter.addAll(didl.getItems());
				mBrowseItemAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void failure(final String msg) {
		if (getActivity() == null) {
			return;
		}

		mHandler.post(new Runnable() {
			public void run() {
				String text = getString(R.string.error_upnp_generic);

				int duration = Toast.LENGTH_SHORT;
				text = getString(R.string.error_upnp_specific) + " " + msg;

				Toast toast = Toast.makeText(getActivity(), text, duration);
				toast.show();
			}
		});
	}

	@Override
	public void updateStatus(final Status status) {
		if (getActivity() == null) {
			return;
		}
	}

	public class BrowseItemAdapter extends BaseAdapter {
		private UpnpClient mUpnpClient;
		private LayoutInflater mInflator;
		private List<DIDLObject> mObjects;

		public BrowseItemAdapter(UpnpClient upnpClient, Context ctx,
				String objectId) {
			mUpnpClient = upnpClient;
			mUpnpClient.storeNewVisitedObjectId(objectId);
			mInflator = LayoutInflater.from(ctx);
			mObjects = new ArrayList<DIDLObject>();
		}

		public void addAll(List<? extends DIDLObject> list) {
			if (list != null)
				mObjects.addAll(list);
		}

		@Override
		public int getCount() {
			return mObjects.size();
		}

		@Override
		public Object getItem(int position) {
			return mObjects.get(position);
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				convertView = mInflator.inflate(R.layout.browse_item, parent,
						false);

				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.browseItemIcon);
				holder.name = (TextView) convertView
						.findViewById(R.id.browseItemName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			DIDLObject currentObject = (DIDLObject) getItem(position);

			holder.name.setText(currentObject.getTitle());

			if (currentObject instanceof Container) {
				holder.icon.setImageResource(R.drawable.folder);
			} else if (currentObject instanceof AudioItem) {
				holder.icon.setImageResource(R.drawable.cdtrack);
			} else if (currentObject instanceof ImageItem) {
				holder.icon.setImageResource(R.drawable.image);
			} else if (currentObject instanceof VideoItem) {
				holder.icon.setImageResource(R.drawable.video);
			} else if (currentObject instanceof PlaylistItem) {
				holder.icon.setImageResource(R.drawable.playlist);
			} else if (currentObject instanceof TextItem) {
				holder.icon.setImageResource(R.drawable.txt);
			} else {
				holder.icon.setImageResource(R.drawable.unknown);
			}

			return convertView;
		}
	}

	public class ViewHolder {
		ImageView icon;
		TextView name;
	}
}
