package com.sharegogo.wireless.ui;

import org.json.JSONObject;
import org.openintents.filemanager.IntentFilterActivity;
import org.openintents.intents.FileManagerIntents;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sharegogo.wireless.R;
import com.sharegogo.wireless.config.Constants;
import com.sharegogo.wireless.config.HttpConstants;
import com.sharegogo.wireless.manager.SettingManager;
import com.sharegogo.wireless.manager.SystemManager;
import com.sharegogo.wireless.utils.ResUtils;

public class AppSettingActivity extends PreferenceActivity implements
		OnClickListener, OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final int REQUEST_CODE_PICK_FILE_OR_DIRECTORY = 1;
	private Button mResetBtn;
	private EditTextPreference mIpPreference;
	private EditTextPreference mPortPreference;
	private Preference mDownloadDir;

	public static void openAppSettingActivity(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(context, AppSettingActivity.class);

		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.app_setting);

		ListView listView = getListView();
		Button resetBtn = new Button(this);
		resetBtn.setGravity(Gravity.CENTER);
		resetBtn.setText(R.string.reboot_router);
		mResetBtn = resetBtn;
		mResetBtn.setOnClickListener(this);

		listView.addFooterView(resetBtn);

		setupPreference();
	}

	private void setupPreference() {
		mIpPreference = (EditTextPreference) findPreference("router_ip");
		mPortPreference = (EditTextPreference) findPreference("router_port");
		mDownloadDir = findPreference("download_dir");

		mIpPreference.setOnPreferenceChangeListener(this);
		mPortPreference.setOnPreferenceChangeListener(this);
		mDownloadDir.setOnPreferenceClickListener(this);

		String ip = SettingManager.getRouterIp();
		String port = SettingManager.getRouterPort();
		String downloadDir = SettingManager.getDownloadDir();

		mIpPreference.setSummary(ip);
		mIpPreference.setDefaultValue(HttpConstants.DEFAULT_HOST);

		mPortPreference.setSummary(port);
		mPortPreference.setDefaultValue(HttpConstants.DEFAULT_PORT);

		mDownloadDir.setSummary(downloadDir);
		mDownloadDir.setDefaultValue(Constants.DEFAULT_DOWNLOAD_DIR);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v == mResetBtn) {
			SystemManager.getInstance().reboot(new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

				}
			});
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mIpPreference || preference == mPortPreference) {
			preference.setSummary((String) newValue);

			if (preference == mIpPreference) {
				HttpConstants.HOST = "http://" + (String) newValue;
			} else if (preference == mPortPreference) {
				HttpConstants.PORT = Integer.valueOf((String) newValue);
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == mDownloadDir) {
			pickDownloadDir();
			return true;
		}

		return false;
	}

	private void pickDownloadDir() {
		Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);
		intent.setClass(this, IntentFilterActivity.class);

		// Set fancy title and button (optional)
		intent.putExtra(FileManagerIntents.EXTRA_TITLE,
				ResUtils.getString(R.string.select_save_direction));
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT,
				ResUtils.getString(R.string.ok));

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
				if (data != null) {
					Uri fileUri = data.getData();
					if (fileUri != null) {
						String dir = fileUri.getPath();
						SettingManager.setDownloadDir(dir);
						mDownloadDir.setSummary(dir);
					}
				}
				break;
			default:
				break;
			}
		}
	}
}
