package com.sharegogo.wireless.ui;

import java.io.File;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;
import org.openintents.filemanager.IntentFilterActivity;
import org.openintents.intents.FileManagerIntents;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.adapter.MediaShareAdapter;
import com.sharegogo.wireless.config.HttpConstants;
import com.sharegogo.wireless.config.StatisticHelper;
import com.sharegogo.wireless.data.MediaShareDirDetail;
import com.sharegogo.wireless.data.MediaShareDirs;
import com.sharegogo.wireless.data.MediaShareItem;
import com.sharegogo.wireless.download.DownloadManager;
import com.sharegogo.wireless.download.DownloadManager.Request;
import com.sharegogo.wireless.manager.MediaShareManager;
import com.sharegogo.wireless.manager.SettingManager;
import com.sharegogo.wireless.utils.MediaFile;
import com.sharegogo.wireless.utils.MediaFile.MediaFileType;
import com.sharegogo.wireless.utils.MediaUtils;
import com.sharegogo.wireless.utils.ResUtils;
import com.sharegogo.wireless.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

public class MediaShareListFragment extends JsonRequestFragment implements
		OnItemClickListener {
	protected static final int REQUEST_CODE_PICK_FILE_OR_DIRECTORY = 1;
	protected static final int REQUEST_CODE_GET_CONTENT = 2;
	private static final String REQUEST_TYPE = "type";
	private static final String UPLOAD_DIR = "dir";
	private static final String MY_EXTRA = "org.openintents.filemanager.demo.EXTRA_MY_EXTRA";

	private MediaShareAdapter mAdapter = null;
	private ListView mList = null;
	private int mType = MediaShareAdapter.TYPE_ROOT;
	private ProgressDialog mProgressDialog;
	private TextView mDir = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			mType = args.getInt("type");
		}

		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
		}

		mAdapter = new MediaShareAdapter(mType);

		if (mType == MediaShareAdapter.TYPE_ROOT) {
			mProgressDialog.setMessage(ResUtils
					.getString(R.string.get_share_dirs));
			mProgressDialog.show();

			MediaShareManager.getInstance().getShareDirs(this,this);
		} else {
			mProgressDialog.setMessage(ResUtils
					.getString(R.string.get_dir_detail));
			mProgressDialog.show();

			MediaShareManager.getInstance().listDir(getTag(),this,this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.media_share_list_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mList = (ListView) view.findViewById(R.id.media_share_list);
		mList.setAdapter(mAdapter);
		mDir = (TextView)view.findViewById(R.id.current_dir);
		
		setDir();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mList.setOnItemClickListener(this);
		registerForContextMenu(mList);
	}

	private void setDir() {
		String dir = this.getTag();
		if (dir == null) {
			dir = "/";
		}

		dir = String.format(ResUtils.getString(R.string.current_dir), dir);
		setDir(dir);
	}

	public void setDir(String dir)
	{
		mDir.setText(dir);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		MediaShareItem data = (MediaShareItem) mAdapter.getItem(info.position);

		MenuInflater inflater = this.getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_media_share_list, menu);

		if (data.type == MediaShareItem.TYPE_FOLDER) {
			menu.removeItem(R.id.menu_open_online);
			menu.removeItem(R.id.menu_download);
		} else {
			menu.removeItem(R.id.menu_upload);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = ((AdapterContextMenuInfo) item
				.getMenuInfo());
		MediaShareItem data = (MediaShareItem) mAdapter
				.getItem(menuInfo.position);

		switch (item.getItemId()) {
		case R.id.menu_open_online:
			onOpenOnline(data);
			return true;
		case R.id.menu_download:
			onDownload(data);
			return true;
		case R.id.menu_upload:
			onUpload(data);
			return true;
		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	private void onOpenOnline(MediaShareItem item) {
		openMediaShareItem(item);
	}

	private void onDownload(MediaShareItem item) {
		Uri uri = Uri.parse(HttpConstants.HOST + ":" + HttpConstants.PORT + getTag()
				+ File.separator + item.name);
		
		//saveFile(uri.toString(), item.name);
		String path = "file://" + SettingManager.getDownloadDir() + File.separator + item.name;
		startDownload(uri,path);
	}

	private void onUpload(MediaShareItem item) {
		openFileEx(item);
	}

	private void openMediaShareItem(MediaShareItem item) {
		MediaFileType mediaFile = MediaFile.getFileType(item.name);

		if (mediaFile != null) {
			if (MediaFile.isAudioFileType(mediaFile.fileType)) {
				playAudio(item);
			} else if (MediaFile.isVideoFileType(mediaFile.fileType)) {
				openVideo(item);
			} else {
				openFile(item);
			}
		} else {
			openFile(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MediaShareItem item = (MediaShareItem) mAdapter.getItem(arg2);

		// Ŀ¼
		if (item.type == MediaShareItem.TYPE_FOLDER) {
			Bundle args = new Bundle();
			args.putInt("type", MediaShareAdapter.TYPE_SUB);

			String tag = null;

			if (mType == MediaShareAdapter.TYPE_ROOT) {
				if (getTag() != null)
					tag = new String(getTag() + item.name);
				else
					tag = new String("/" + item.name);
			} else {
				if (getTag() != null)
					tag = new String(getTag() + File.separator + item.name);
				else
					tag = new String("/" + File.separator + item.name);
			}

			MediaShareListFragment fragment = new MediaShareListFragment();
			fragment.setArguments(args);

			getFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.media_share_container, fragment, tag)
					.commit();
		} else {
			openMediaShareItem(item);
		}
	}

	public void onFailed(int what, Object msg) {
		mProgressDialog.dismiss();

		if (mType == MediaShareAdapter.TYPE_ROOT) {
			Toast.makeText(getActivity(), R.string.get_share_dirs_failed, 3000)
					.show();
		} else {
			Toast.makeText(getActivity(), R.string.get_dir_detail_failed, 3000)
					.show();
		}
	}

	private void playAudio(MediaShareItem item) {
		if (item.type == MediaShareItem.TYPE_FOLDER) {
			return;
		}

		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_PLAY_AUDIO);
		
		Uri uri = Uri.parse(HttpConstants.HOST + ":" + HttpConstants.PORT + getTag()
				+ File.separator + item.name);
		
		MediaUtils.playAudio(getActivity(), uri);
	}

	private void openVideo(MediaShareItem item) {
		if (item.type == MediaShareItem.TYPE_FOLDER) {
			return;
		}

		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_PLAY_VIDEO);
		
		Uri uri = Uri.parse(HttpConstants.HOST + ":" + HttpConstants.PORT + getTag() + File.separator
				+ item.name);
		
		MediaUtils.playVideo(getActivity(), uri);
	}

	private void openImage(MediaShareItem item) {
		if (item.type == MediaShareItem.TYPE_FOLDER) {
			return;
		}

		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_VIEM_IMAGE);
		
		Uri uri = Uri.parse(HttpConstants.HOST + ":" + HttpConstants.PORT + getTag() + File.separator
				+ item.name);
		
		MediaUtils.openImage(getActivity(), uri);

	}

	private void openFile(MediaShareItem item) {
		if (item.type == MediaShareItem.TYPE_FOLDER) {
			return;
		}

		Uri uri = Uri.parse(HttpConstants.HOST
				+ ":" + HttpConstants.PORT + getTag() + File.separator + item.name);
		
		MediaUtils.openFile(getActivity(), uri);
	}

	private void startDownload(Uri uri, String destination) {
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_DOWNLOAD);
		
		DownloadManager downloadMgr = SharegogoWirelessApp
				.getDownloadManager();

		Request request = new Request(uri);
		request.setDestinationUri(Uri.parse(destination));

		downloadMgr.enqueue(request);
	}

	private void startUpload(Uri uri, String destination) {
		MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_UPLOAD);
		
		DownloadManager downloadMgr = SharegogoWirelessApp
				.getDownloadManager();

		Request request = new Request(uri, HttpPost.METHOD_NAME);
		request.setDestinationUri(Uri.parse(destination));

		downloadMgr.enqueue(request);
	}

	/**
	 * Opens the file manager to select a file to open.
	 */
	public void openFileEx(MediaShareItem item) {
		Intent intent = new Intent(FileManagerIntents.ACTION_PICK_FILE);
		intent.setClass(getActivity(), IntentFilterActivity.class);
		// Set fancy title and button (optional)
		intent.putExtra(FileManagerIntents.EXTRA_TITLE,
				ResUtils.getString(R.string.select_save_direction));
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT,
				ResUtils.getString(R.string.ok));
		intent.putExtra(MY_EXTRA, "magic");
		intent.putExtra(REQUEST_TYPE, HttpPost.METHOD_NAME);
		String tag = getTag();
		if(tag != null && tag.contentEquals("/")){
			intent.putExtra(UPLOAD_DIR, getTag() + item.name);
		}else{
			intent.putExtra(UPLOAD_DIR, getTag() + File.separator + item.name);
		}
		
		try {
			startActivityForResult(intent, REQUEST_CODE_PICK_FILE_OR_DIRECTORY);
		} catch (ActivityNotFoundException e) {
			// No compatible file manager was found.
		}
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
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_PICK_FILE_OR_DIRECTORY:
				String type = data.getStringExtra(REQUEST_TYPE);
				if (HttpPost.METHOD_NAME.contentEquals(type)) {
					Uri fileUri = data.getData();
					String dir = data.getStringExtra(UPLOAD_DIR);
					startUpload(Uri.parse(HttpConstants.ACTION_UPLOAD + "?" + "dir=" + dir.substring(1)),
							fileUri.toString());
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
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
	}

	@Override
	public void onResponse(JSONObject response) {
		super.onResponse(response);
		
		mProgressDialog.dismiss();
		
		String jsonStr = response.toString();
		if (mType == MediaShareAdapter.TYPE_ROOT) {
			MediaShareDirs dirs = MediaShareDirs.fromJson(jsonStr);

			if (dirs.dirs != null && dirs.dirs.length > 0) {
				for (String name : dirs.dirs) {
					MediaShareItem item = new MediaShareItem();

					item.name = name;
					item.type = MediaShareItem.TYPE_FOLDER;
					mAdapter.add(item);
				}
			}
		} else {
			MediaShareDirDetail dirDetail = MediaShareDirDetail.fromJson(jsonStr);
			
			if (dirDetail.data != null && dirDetail.data.size() > 0) {
				mAdapter.addAll(dirDetail.data);
			}
		}
	}
}
