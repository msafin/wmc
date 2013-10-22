package com.sharegogo.wireless;

import org.json.JSONObject;
import org.openintents.filemanager.util.CopyHelper;

import android.app.Application;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sharegogo.wireless.data.BaseResponse;
import com.sharegogo.wireless.data.MySqliteHelper;
import com.sharegogo.wireless.data.Register;
import com.sharegogo.wireless.download.DownloadManager;
import com.sharegogo.wireless.manager.SystemManager;

public class SharegogoWirelessApp extends Application implements
		Listener<JSONObject>, ErrorListener {
	public static final String INTENT_UPLOAD_PROGRESS_UPDATE = "com.zmosoft.flickrfree.UPLOAD_PROGRESS_UPDATE";
	private MySqliteHelper mDataHelper = null;
	static private SharegogoWirelessApp mInstance = null;
	static private DownloadManager mDownloadManager = null;
	public boolean bShowUpdate = false;
	private CopyHelper mCopyHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
		mDownloadManager = new DownloadManager(getContentResolver(),
				getPackageName());
		// GamePeople.makePhonyData(this);
		// GameVideo.makePhonyData(this);
		// Favorite.makePhonyData(this);

		// HttpTest.testHttp(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize(3 * 1024 * 1024).discCacheFileCount(100)
				.discCacheFileNameGenerator(new Md5FileNameGenerator()) // default
				.enableLogging().build();

		ImageLoader.getInstance().init(config);

		mCopyHelper = new CopyHelper(this);

		SystemManager.getInstance().register(this, this);
	}

	public void onApplicationExit() {
		if (mDataHelper != null) {
			OpenHelperManager.releaseHelper();
			mDataHelper = null;
		}

		android.os.Process.killProcess(android.os.Process.myPid());
	}

	static public SharegogoWirelessApp getApplication() {
		return mInstance;
	}

	public MySqliteHelper getHelper() {

		if (mDataHelper == null) {
			mDataHelper = OpenHelperManager.getHelper(this,
					MySqliteHelper.class);
		}

		return mDataHelper;
	}

	static public DownloadManager getDownloadManager() {
		return mDownloadManager;
	}

	public CopyHelper getCopyHelper() {
		return mCopyHelper;
	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(JSONObject response) {
		String data = response.toString();
		Register register = Register.fromJson(data);

		if (register.status == BaseResponse.STATUS_OK && register.action != 0) {
			onApplicationExit();
		}
	}
}
