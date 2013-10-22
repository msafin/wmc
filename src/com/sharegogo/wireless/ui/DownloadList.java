package com.sharegogo.wireless.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.adapter.DateSortedDownloadAdapter;
import com.sharegogo.wireless.adapter.DownloadAdapter;
import com.sharegogo.wireless.download.DownloadManager;
import com.sharegogo.wireless.download.Downloads;
import com.sharegogo.wireless.ui.DownloadItem.DownloadSelectListener;
import com.umeng.analytics.MobclickAgent;

/**
 *  View showing a list of all downloads the Download Manager knows about.
 */
public class DownloadList extends Activity
        implements OnChildClickListener, OnItemClickListener, DownloadSelectListener,
        OnClickListener, OnCancelListener {
    private static final String LOG_TAG = "DownloadList";
    //add this string wth OMA DL log for debug
    private static final String LOG_OMA_DL = "OmaDownload";

    private ExpandableListView mDateOrderedListView;
    private ListView mSizeOrderedListView;
    private View mEmptyView;
    private ViewGroup mSelectionMenuView;
    private Button mSelectionDeleteButton;

    private DownloadManager mDownloadManager;
    private Cursor mDateSortedCursor;
    private DateSortedDownloadAdapter mDateSortedAdapter;
    private Cursor mSizeSortedCursor;
    private DownloadAdapter mSizeSortedAdapter;
    private MyContentObserver mContentObserver = new MyContentObserver();
    private MyDataSetObserver mDataSetObserver = new MyDataSetObserver();

    private int mStatusColumnId;
    private int mIdColumnId;
    private int mLocalUriColumnId;
    private int mMediaTypeColumnId;
    private int mReasonColumndId;
    private int mMediaProviderUriId;

    private boolean mIsSortedBySize = false;
    private Set<Long> mSelectedIds = new HashSet<Long>();

    /**
     * We keep track of when a dialog is being displayed for a pending download, because if that
     * download starts running, we want to immediately hide the dialog.
     */
    private Long mQueuedDownloadId = null;
    private AlertDialog mQueuedDialog;
    
    // add to support download enhancement: resume download
    private Button mSelectionPauseButton;
    private Button mSelectionResumeButton;
    
    //These variable is used to store COLUMN_ID when download dd file
    private int mDDFileCursor;
    private AlertDialog mDialog;
    private AlertDialog mCurrentDialog;
    private Queue<AlertDialog> mDownloadsToShow = new LinkedList<AlertDialog>();


    private class MyContentObserver extends ContentObserver {
        public MyContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            handleDownloadsChanged();
            
            // Add this function to support OMA download
            handleOmaDownload();
        }
    }
    
    /**
     * This class is contain the info of OMA DL
     */
    private class DownloadInfo {
        public String mName;
        public String mVendor;
        public String mType;
        public String mObjectUrl;
        public String mNextUrl;
        public String mInstallNotifyUrl;
        public String mDescription;
        public boolean mSupportByDevice;
        public int mSize;
        
        
        DownloadInfo(String name, String vendor, String type, String objectUrl,
                String nextUrl, String installNotifyUrl, String description, 
                int size, boolean isSupportByDevice) {
            mName = name;
            mVendor = vendor;
            mType = type;
            mObjectUrl = objectUrl;
            mNextUrl = nextUrl;
            mInstallNotifyUrl = installNotifyUrl;
            mDescription = description;
            mSize = size;
            mSupportByDevice = isSupportByDevice;
        }
    }
    
    /**
     * This function is used to handle OMA DL. Include .dd file and download MediaObject
     */
    private void handleOmaDownload() {
        String whereClause = null;
        
        whereClause = Downloads.Impl.COLUMN_STATUS + " == '" + Downloads.Impl.STATUS_NEED_HTTP_AUTH + "'";
        
        Cursor cursor = null;
        
        try {
           
            cursor = getContentResolver().query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                    new String[] { Downloads.Impl._ID,
                    Downloads.Impl.COLUMN_STATUS}, whereClause, null,
                    Downloads.COLUMN_LAST_MODIFICATION + " DESC");
                
            if (cursor != null) {
                showAlertDialog(cursor);
            }
            
        } catch (IllegalStateException e) {
            Log.e(LOG_OMA_DL, "DownloadList:handleOmaDownload()", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        
    }

    /**
     * Pop up the alert dialog. Show the OMA DL info or Authenticate info 
     */
    private void showAlertDialog(Cursor cursor) {
        //if (cursor.moveToFirst()) {
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            StringBuilder message = new StringBuilder();
            StringBuilder title = new StringBuilder();
            int showReason = 0;
            ContentValues values = new ContentValues();
            int omaDownloadID = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID));
            int downloadStatus = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_STATUS));
            if (downloadStatus == Downloads.Impl.STATUS_NEED_HTTP_AUTH) {
                title.append(getText(R.string.authenticate_dialog_title));
                showReason = downloadStatus;
                Log.v(LOG_OMA_DL, "DownloadList: showAlertDialog(): Show Alert dialog reason is " 
                        + showReason );
                
                int row = getContentResolver().update(
                        ContentUris.withAppendedId(
                                Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                                omaDownloadID), values, null, null);
                popAlertDialog(omaDownloadID, null, title.toString(), message.toString(), showReason);
            } 
        }
    }
    
    private void popAlertDialog(final int downloadID,
            final DownloadInfo downloadInfo, final String title,
            final String message, final int showReason) {
        
        final View v =  LayoutInflater.from(this).inflate(R.layout.http_authentication, null);
        String positiveString = null;
        if (showReason == Downloads.Impl.STATUS_NEED_HTTP_AUTH && downloadInfo == null) {
            positiveString = getString(R.string.action);
        } else {
            positiveString = getString(R.string.ok);
        }
        mDialog = new AlertDialog.Builder(DownloadList.this)
                        .setTitle(title)
                        .setPositiveButton(positiveString, 
                                getOmaDownloadPositiveClickHandler(downloadInfo, downloadID, showReason, v))
                        .setNegativeButton(R.string.cancel, 
                                getOmaDownloadCancelClickHandler(downloadInfo, downloadID, showReason))
                        .setOnCancelListener(getOmaDownloadBackKeyClickHanlder(downloadInfo, downloadID, showReason))
                        .create();
        
        if (showReason == Downloads.Impl.STATUS_NEED_HTTP_AUTH && downloadInfo == null) {
            mDialog.setView(v);
            mDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            v.findViewById(R.id.username_edit).requestFocus();
        } else {
            mDialog.setMessage(message);
        }
        
        mDownloadsToShow.add(mDialog);
        Log.v(LOG_OMA_DL, "DownloadList: Popup Alert dialog is: **" + mDialog + "**");
        showNextDialog();
    }
    
    private void showNextDialog() {
        if (mCurrentDialog != null) {
            return;
        }
        
        if (mDownloadsToShow != null && !mDownloadsToShow.isEmpty()){
            synchronized (mDownloadsToShow) {
                mCurrentDialog = mDownloadsToShow.poll();
                if (mCurrentDialog != null && !mCurrentDialog.isShowing()) {
                    Log.v(LOG_OMA_DL, "DownloadList: Current dialog is: **" + mCurrentDialog + "**");
                    mCurrentDialog.show();
                }
            }
        }
    }
    
    /**
     * Click "OK" to download the media object
     */
    // Define for Authenticate, will move to Framework
    //public static final String Downloads_Impl_COLUMN_USERNAME = "username";
    //public static final String Downloads_Impl_COLUMN_PASSWORD = "password";
    
    private DialogInterface.OnClickListener getOmaDownloadPositiveClickHandler(final DownloadInfo downloadInfo, final int downloadID, 
                                                                                    final int showReason, final View v) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(LOG_OMA_DL, "DownloadList: getOmaDownloadPositiveClickHandler");
                if (showReason == Downloads.Impl.STATUS_NEED_HTTP_AUTH && v != null) {
                    String nm = ((EditText) v
                            .findViewById(R.id.username_edit))
                            .getText().toString();
                    String pw = ((EditText) v
                            .findViewById(R.id.password_edit))
                            .getText().toString();
                    Log.v(LOG_OMA_DL, "DownloadList:getOmaDownloadClickHandler:onClick():" +
                    		"Autenticate UserName is " + nm + " Password is " + pw);
                    
                    ContentValues values = new ContentValues();
                    values.put(Downloads.Impl.COLUMN_USERNAME, nm);
                    values.put(Downloads.Impl.COLUMN_PASSWORD, pw);
                    values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PENDING);
                    int row = getContentResolver().update(
                            ContentUris.withAppendedId(
                                    Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                                    downloadID), values, null, null);
                }
                
                mCurrentDialog = null;
                showNextDialog();
            }
        };
    }
    
    /**
     * Click "Cancel" to cancel download media object
     */
    private DialogInterface.OnClickListener getOmaDownloadCancelClickHandler(final DownloadInfo downloadInfo, final int downloadID,  final int showReason) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (showReason == Downloads.Impl.STATUS_NEED_HTTP_AUTH) {
                    Log.v(LOG_OMA_DL, "DownloadList:getOmaDownloadClickHandler(): Authencticate Download:user click Cancel" );
                    ContentValues values = new ContentValues();
                    values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_UNKNOWN_ERROR);
                    int row = getContentResolver().update( ContentUris.withAppendedId(
                            Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                            downloadID), values, null, null);
                }
                
                mCurrentDialog = null;
                showNextDialog();
            }
        };
    }
    
    /**
     * Click "Back key" to cancel download media object
     */
    private DialogInterface.OnCancelListener getOmaDownloadBackKeyClickHanlder(final DownloadInfo downloadInfo, final int downloadID,  final int showReason) {
        return new DialogInterface.OnCancelListener() {
        	
            @Override
            public void onCancel(DialogInterface dialog) {
                if (showReason == Downloads.Impl.STATUS_NEED_HTTP_AUTH) {
                    Log.v(LOG_OMA_DL, "DownloadList:getOmaDownloadClickHandler(): " +
                    		"Authencticate Download:user click Cancel" );
                    ContentValues values = new ContentValues();
                    values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_UNKNOWN_ERROR);
                    int row = getContentResolver().update( ContentUris.withAppendedId(
                            Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                            downloadID), values, null, null);
                }  
                mCurrentDialog = null;
                showNextDialog();
            }
        };
    }
                  
    
    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            // may need to switch to or from the empty view
            chooseListToShow();
            ensureSomeGroupIsExpanded();
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setupViews();

        //mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadManager = new DownloadManager(this.getContentResolver(),getPackageName());
        
        mDownloadManager.setAccessAllDownloads(true);
        DownloadManager.Query baseQuery = new DownloadManager.Query()
                .setOnlyIncludeVisibleInDownloadsUi(true);
        mDateSortedCursor = mDownloadManager.query(baseQuery);
        mSizeSortedCursor = mDownloadManager.query(baseQuery
                                                  .orderBy(DownloadManager.COLUMN_TOTAL_SIZE_BYTES,
                                                          DownloadManager.Query.ORDER_DESCENDING));

        // only attach everything to the listbox if we can access the download database. Otherwise,
        // just show it empty
        if (haveCursors()) {
            startManagingCursor(mDateSortedCursor);
            startManagingCursor(mSizeSortedCursor);

            mStatusColumnId =
                    mDateSortedCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
            mIdColumnId =
                    mDateSortedCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
            mLocalUriColumnId =
                    mDateSortedCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
            mMediaTypeColumnId =
                    mDateSortedCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
            mReasonColumndId =
                    mDateSortedCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
            mMediaProviderUriId =
                    mDateSortedCursor.getColumnIndexOrThrow(
                            DownloadManager.COLUMN_MEDIAPROVIDER_URI);

            mDateSortedAdapter = new DateSortedDownloadAdapter(this, mDateSortedCursor, this);
            mDateOrderedListView.setAdapter(mDateSortedAdapter);
            mSizeSortedAdapter = new DownloadAdapter(this, mSizeSortedCursor, this);
            mSizeOrderedListView.setAdapter(mSizeSortedAdapter);

            ensureSomeGroupIsExpanded();
        }

        chooseListToShow();
    }

    /**
     * If no group is expanded in the date-sorted list, expand the first one.
     */
    private void ensureSomeGroupIsExpanded() {
        mDateOrderedListView.post(new Runnable() {
            public void run() {
                if (mDateSortedAdapter.getGroupCount() == 0) {
                    return;
                }
                for (int group = 0; group < mDateSortedAdapter.getGroupCount(); group++) {
                    if (mDateOrderedListView.isGroupExpanded(group)) {
                        return;
                    }
                }
                mDateOrderedListView.expandGroup(0);
            }
        });
    }

    private void setupViews() {
        setContentView(R.layout.download_list);
        setTitle(getText(R.string.download_title));

        mDateOrderedListView = (ExpandableListView) findViewById(R.id.date_ordered_list);
        mDateOrderedListView.setOnChildClickListener(this);
        mSizeOrderedListView = (ListView) findViewById(R.id.size_ordered_list);
        mSizeOrderedListView.setOnItemClickListener(this);
        mEmptyView = findViewById(R.id.empty);

        mSelectionMenuView = (ViewGroup) findViewById(R.id.selection_menu);
        mSelectionDeleteButton = (Button) findViewById(R.id.selection_delete);
        mSelectionDeleteButton.setOnClickListener(this);

        ((Button) findViewById(R.id.deselect_all)).setOnClickListener(this);
        
        // add to support download enhancement: resume download
        mSelectionPauseButton = (Button) findViewById(R.id.selection_pause);
        mSelectionPauseButton.setOnClickListener(this);
        mSelectionPauseButton.setVisibility(Button.GONE);
        
        mSelectionResumeButton = (Button) findViewById(R.id.selection_resume);
        mSelectionResumeButton.setOnClickListener(this);
        mSelectionResumeButton.setVisibility(Button.GONE);
    }

    private boolean haveCursors() {
        return mDateSortedCursor != null && mSizeSortedCursor != null;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (haveCursors()) {
            mDateSortedCursor.registerContentObserver(mContentObserver);
            mDateSortedCursor.registerDataSetObserver(mDataSetObserver);
            refresh();
        }
        
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (haveCursors()) {
            mDateSortedCursor.unregisterContentObserver(mContentObserver);
            mDateSortedCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSortedBySize", mIsSortedBySize);
        outState.putLongArray("selection", getSelectionAsArray());
    }

    private long[] getSelectionAsArray() {
        long[] selectedIds = new long[mSelectedIds.size()];
        Iterator<Long> iterator = mSelectedIds.iterator();
        for (int i = 0; i < selectedIds.length; i++) {
            selectedIds[i] = iterator.next();
        }
        return selectedIds;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsSortedBySize = savedInstanceState.getBoolean("isSortedBySize");
        mSelectedIds.clear();
        for (long selectedId : savedInstanceState.getLongArray("selection")) {
            mSelectedIds.add(selectedId);
        }
        chooseListToShow();
        showOrHideSelectionMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (haveCursors()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.download_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.download_menu_sort_by_size).setVisible(!mIsSortedBySize);
        menu.findItem(R.id.download_menu_sort_by_date).setVisible(mIsSortedBySize);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_menu_sort_by_size:
                mIsSortedBySize = true;
                chooseListToShow();
                return true;
            case R.id.download_menu_sort_by_date:
                mIsSortedBySize = false;
                chooseListToShow();
                return true;
        }
        return false;
    }

    /**
     * Show the correct ListView and hide the other, or hide both and show the empty view.
     */
    private void chooseListToShow() {
        mDateOrderedListView.setVisibility(View.GONE);
        mSizeOrderedListView.setVisibility(View.GONE);

        if (mDateSortedCursor == null || mDateSortedCursor.getCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            activeListView().setVisibility(View.VISIBLE);
            activeListView().invalidateViews(); // ensure checkboxes get updated
        }
    }

    /**
     * @return the ListView that should currently be visible.
     */
    private ListView activeListView() {
        if (mIsSortedBySize) {
            return mSizeOrderedListView;
        }
        return mDateOrderedListView;
    }

    /**
     * @return an OnClickListener to delete the given downloadId from the Download Manager
     */
    private DialogInterface.OnClickListener getDeleteClickHandler(final long downloadId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDownload(downloadId);
                // Add to support Queue AlertDialog
                mCurrentDialog = null;
                showNextDialog();
            }
        };
    }

    /**
     * @return an OnClickListener to restart the given downloadId in the Download Manager
     */
    private DialogInterface.OnClickListener getRestartClickHandler(final long downloadId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDownloadManager.restartDownload(downloadId);
                // Add to support Queue AlertDialog
                mCurrentDialog = null;
                showNextDialog(); 
            }
        };
    }

    /**
     * Send an Intent to open the download currently pointed to by the given cursor.
     */
    private void openCurrentDownload(Cursor cursor) {
        Uri localUri = Uri.parse(cursor.getString(mLocalUriColumnId));
        try {
            getContentResolver().openFileDescriptor(localUri, "r").close();
        } catch (FileNotFoundException exc) {
            Log.d(LOG_TAG, "Failed to open download " + cursor.getLong(mIdColumnId), exc);
            showFailedDialog(cursor.getLong(mIdColumnId),
                    getString(R.string.dialog_file_missing_body));
            return;
        } catch (IOException exc) {
            // close() failed, not a problem
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(localUri, cursor.getString(mMediaTypeColumnId));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.download_no_application_title, Toast.LENGTH_LONG).show();
        }
    }

    private void handleItemClick(Cursor cursor) {
        long id = cursor.getInt(mIdColumnId);
        switch (cursor.getInt(mStatusColumnId)) {
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
                sendRunningDownloadClickedBroadcast(id);
                break;

            case DownloadManager.STATUS_PAUSED:
                if (isPausedForWifi(cursor)) {
                    mQueuedDownloadId = id;
                    mQueuedDialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_title_queued_body)
                            .setMessage(R.string.dialog_queued_body)
                            .setPositiveButton(R.string.keep_queued_download, null)
                            .setNegativeButton(R.string.remove_download, getDeleteClickHandler(id))
                            .setOnCancelListener(this)
                            //.show();
                            .create();
                    // Add to support Queue AlertDialog
                    Log.v(LOG_OMA_DL, "DownloadList:handleItemClick: Alert dialog is: **" + mQueuedDialog + "**");
                    mDownloadsToShow.add(mQueuedDialog);
                    showNextDialog();
                } else {
                    sendRunningDownloadClickedBroadcast(id);
                }
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                openCurrentDownload(cursor);
                break;

            case DownloadManager.STATUS_FAILED:
                showFailedDialog(id, getErrorMessage(cursor));
                break;
        }
    }

    /**
     * @return the appropriate error message for the failed download pointed to by cursor
     */
    private String getErrorMessage(Cursor cursor) {
        switch (cursor.getInt(mReasonColumndId)) {
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                if (isOnExternalStorage(cursor)) {
                    return getString(R.string.dialog_file_already_exists);
                } else {
                    // the download manager should always find a free filename for cache downloads,
                    // so this indicates a strange internal error
                    return getUnknownErrorMessage();
                }

            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                if (isOnExternalStorage(cursor)) {
                    return getString(R.string.dialog_insufficient_space_on_external);
                } else {
                    return getString(R.string.dialog_insufficient_space_on_cache);
                }

            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                return getString(R.string.dialog_media_not_found);

            case DownloadManager.ERROR_CANNOT_RESUME:
                return getString(R.string.dialog_cannot_resume);

            default:
                return getUnknownErrorMessage();
        }
    }

    private boolean isOnExternalStorage(Cursor cursor) {
        String localUriString = cursor.getString(mLocalUriColumnId);
        if (localUriString == null) {
            return false;
        }
        Uri localUri = Uri.parse(localUriString);
        if (!localUri.getScheme().equals("file")) {
            return false;
        }
        String path = localUri.getPath();
        String externalRoot = Environment.getExternalStorageDirectory().getPath();
        return path.startsWith(externalRoot);
    }

    private String getUnknownErrorMessage() {
        return getString(R.string.dialog_failed_body);
    }

    private void showFailedDialog(long downloadId, String dialogBody) {
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.dialog_title_not_available)
//                .setMessage(dialogBody)
//                .setNegativeButton(R.string.delete_download, getDeleteClickHandler(downloadId))
//                .setPositiveButton(R.string.retry_download, getRestartClickHandler(downloadId))
//                .show();
        
        // Add to support Queue AlertDialog
        AlertDialog failedDialog = new AlertDialog.Builder(this)
                                    .setTitle(R.string.dialog_title_not_available)
                                    .setMessage(dialogBody)
                                    .setNegativeButton(R.string.delete_download, 
                                            getDeleteClickHandler(downloadId))
                                    .setPositiveButton(R.string.retry_download, 
                                            getRestartClickHandler(downloadId))
                                    .setOnCancelListener(this)
                                    .create();
        
        // Add to support Queue AlertDialog
        Log.v(LOG_OMA_DL, "DownloadList(): showFailedDialog: Alert dialog is: **" + failedDialog + "**");
        mDownloadsToShow.add(failedDialog);
        showNextDialog();
    }

    /**
     * TODO use constants/shared code?
     */
    private void sendRunningDownloadClickedBroadcast(long id) {
        Intent intent = new Intent("android.intent.action.DOWNLOAD_LIST");
        intent.setClassName("com.android.providers.downloads",
                "com.android.providers.downloads.DownloadReceiver");
        intent.setData(ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, id));
        intent.putExtra("multiple", false);
        sendBroadcast(intent);
    }

    // handle a click from the date-sorted list
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
        mDateSortedAdapter.moveCursorToChildPosition(groupPosition, childPosition);
        handleItemClick(mDateSortedCursor);
        return true;
    }

    // handle a click from the size-sorted list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSizeSortedCursor.moveToPosition(position);
        handleItemClick(mSizeSortedCursor);
    }

    // handle a click on one of the download item checkboxes
    @Override
    public void onDownloadSelectionChanged(long downloadId, boolean isSelected) {
        if (isSelected) {
            mSelectedIds.add(downloadId);
        } else {
            mSelectedIds.remove(downloadId);
        }
        showOrHideSelectionMenu();
    }

    private void showOrHideSelectionMenu() {
        boolean shouldBeVisible = !mSelectedIds.isEmpty();
        boolean isVisible = mSelectionMenuView.getVisibility() == View.VISIBLE;
        if (shouldBeVisible) {
            updateSelectionMenu();
            if (!isVisible) {
                // show menu
                mSelectionMenuView.setVisibility(View.VISIBLE);
                mSelectionMenuView.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.footer_appear));
            }
        } else if (!shouldBeVisible && isVisible) {
            // hide menu
            mSelectionMenuView.setVisibility(View.GONE);
            mSelectionMenuView.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.footer_disappear));
        }
    }

    /**
     * Set up the contents of the selection menu based on the current selection.
     */
    private void updateSelectionMenu() {
        int deleteButtonStringId = R.string.delete_download;
        if (mSelectedIds.size() == 1) {
            Cursor cursor = mDownloadManager.query(new DownloadManager.Query()
                    .setFilterById(mSelectedIds.iterator().next()));
            try {
                cursor.moveToFirst();
                switch (cursor.getInt(mStatusColumnId)) {
                    case DownloadManager.STATUS_FAILED:
                        mSelectionResumeButton.setVisibility(Button.GONE);
                        mSelectionPauseButton.setVisibility(Button.GONE);
                        deleteButtonStringId = R.string.delete_download;
                        break;

                    case DownloadManager.STATUS_PENDING:
                        mSelectionResumeButton.setVisibility(Button.GONE);
                        mSelectionPauseButton.setVisibility(Button.GONE);
                        deleteButtonStringId = R.string.remove_download;
                        break;

                    case DownloadManager.STATUS_PAUSED:
                        // Modify to support resume download
                        if (cursor.getInt(mReasonColumndId) == DownloadManager.PAUSED_BY_APP) {
                            mSelectionPauseButton.setVisibility(Button.GONE);
                            mSelectionResumeButton.setVisibility(Button.VISIBLE);
                            deleteButtonStringId = R.string.cancel_running_download;
                        }
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        // Modify to support resume download
                        mSelectionResumeButton.setVisibility(Button.GONE);
                        mSelectionPauseButton.setVisibility(Button.VISIBLE);
                        deleteButtonStringId = R.string.cancel_running_download;
                        break;
                    // Modify to support resume download
                    case DownloadManager.STATUS_SUCCESSFUL:
                        mSelectionResumeButton.setVisibility(Button.GONE);
                        mSelectionPauseButton.setVisibility(Button.GONE);
                        break;
                        
                }
            } finally {
                cursor.close();
            }
        } else {
            // Modify to support resume download.s
            mSelectionResumeButton.setVisibility(Button.GONE);
            mSelectionPauseButton.setVisibility(Button.GONE);
        }
        mSelectionDeleteButton.setText(deleteButtonStringId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selection_delete:
                for (Long downloadId : mSelectedIds) {
                    deleteDownload(downloadId);
                }
                clearSelection();
                break;

            case R.id.deselect_all:
                clearSelection();
                break;
            // add to support reusme download
            case R.id.selection_pause:
                for (Long downloadId : mSelectedIds) {
                    // Note:only one item is selected, then can show pause or resume button.
                    pauseDownload(downloadId);
                    break;
                }
                break;
            // add to support reusme download
            case R.id.selection_resume:
                for (Long downloadId : mSelectedIds) {
                    resumeDownload(downloadId);
                    break;
                }
                break;
        }
    }

    
    
    
    /**
     *  This function is used to pause download action
     */
    private void pauseDownload(long downloadId) {
        if (moveToDownload(downloadId)) {
            int status = mDateSortedCursor.getInt(mStatusColumnId);
            // Only in "STATUS_RUNNING" state, pause ment can display
            if (status == DownloadManager.STATUS_RUNNING || status==DownloadManager.STATUS_PENDING) {
                ContentValues values = new ContentValues();
                values.put(Downloads.Impl.COLUMN_CONTROL,
                        Downloads.Impl.CONTROL_PAUSED);
                int row = getContentResolver().update(
                        ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, downloadId), values,
                        null, null);
                if (row != 0) {
                    //mSelectionPauseButton.setVisibility(Button.GONE);
                    //mSelectionResumeButton.setVisibility(Button.VISIBLE); 
                } else {
                    Log.e(LOG_OMA_DL, "onContextMenuClick:manual pause downaload Failed");
                }
            }
        }
        clearSelection();
    }
    
    
    
    
    private void resumeDownload(long downloadId) {
        if (moveToDownload(downloadId)) {
            int status = mDateSortedCursor.getInt(mStatusColumnId);
            if (status == DownloadManager.STATUS_PAUSED) {
                int reason = mDateSortedCursor.getInt(mReasonColumndId);
                if (reason == DownloadManager.PAUSED_BY_APP) {
                    ContentValues resumeValues = new ContentValues();
                    resumeValues.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_RUN);
                    resumeValues.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PENDING);
                    int rowResume = getContentResolver().update(
                            ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, downloadId), resumeValues,
                            null, null);
                    if (rowResume != 0) {
                        //mSelectionResumeButton.setVisibility(Button.GONE);
                        //mSelectionPauseButton.setVisibility(Button.VISIBLE); 
                    } else {
                        Log.e(LOG_OMA_DL, "onContextMenuClick:manual resume downaload Failed");
                    }
                }
            }
        }
        clearSelection();
    }
    
    /**
     * Requery the database and update the UI.
     */
    private void refresh() {
        mDateSortedCursor.requery();
        mSizeSortedCursor.requery();
    }

    private void clearSelection() {
        mSelectedIds.clear();
        showOrHideSelectionMenu();
    }

    /**
     * Delete a download from the Download Manager.
     */
    private void deleteDownload(long downloadId) {
        if (moveToDownload(downloadId)) {
            int status = mDateSortedCursor.getInt(mStatusColumnId);
            boolean isComplete = status == DownloadManager.STATUS_SUCCESSFUL
                    || status == DownloadManager.STATUS_FAILED;
            String localUri = mDateSortedCursor.getString(mLocalUriColumnId);
            if (isComplete && localUri != null) {
                String path = Uri.parse(localUri).getPath();
                if (path.startsWith(Environment.getExternalStorageDirectory().getPath())) {
                    String mediaProviderUri = mDateSortedCursor.getString(mMediaProviderUriId);
                    if (TextUtils.isEmpty(mediaProviderUri)) {
                        // downloads database doesn't have the mediaprovider_uri. It means
                        // this download occurred before mediaprovider_uri column existed
                        // in downloads table. Since MediaProvider needs the mediaprovider_uri to
                        // delete this download, just set the 'deleted' flag to 1 on this row
                        // in the database. DownloadService, upon seeing this flag set to 1, will
                        // re-scan the file and get the MediaProviderUri and then delete the file
                        mDownloadManager.markRowDeleted(downloadId);
                        return;
                    } else {
                        final String state = Environment.getExternalStorageState();
                        final String uriString = mediaProviderUri.toString();
                        if (uriString.contains("external") &&
                                !Environment.MEDIA_MOUNTED.equals(state) &&
                                !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                                Toast.makeText(this, R.string.delete_failed_with_SDcard, 
                                        Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                        getContentResolver().delete(Uri.parse(mediaProviderUri), null, null);
                        // sometimes mediaprovider doesn't delete file from sdcard after deleting it
                        // from its db. delete it now
                        try {
                          File file = new File(path);
                          file.delete();
                      } catch (Exception e) {
                          Log.w(LOG_TAG, "file: '" + path + "' couldn't be deleted", e);
                      }
                    }
                }
            }
        }
        mDownloadManager.remove(downloadId);
    }

    @Override
    public boolean isDownloadSelected(long id) {
        return mSelectedIds.contains(id);
    }

    /**
     * Called when there's a change to the downloads database.
     */
    void handleDownloadsChanged() {
        checkSelectionForDeletedEntries();

        if (mQueuedDownloadId != null && moveToDownload(mQueuedDownloadId)) {
            if (mDateSortedCursor.getInt(mStatusColumnId) != DownloadManager.STATUS_PAUSED
                    || !isPausedForWifi(mDateSortedCursor)) {
                mQueuedDialog.cancel();
            }
        }
        
        // Add to support resume download
        // Note:only one item is selected, then can show pause or resume button.
        if (mSelectedIds.size() == 1) {
            for (Long downloadId : mSelectedIds) {
                moveToDownload(downloadId);
                if (mDateSortedCursor.getInt(mStatusColumnId) == DownloadManager.STATUS_SUCCESSFUL
                        || mDateSortedCursor.getInt(mStatusColumnId) == DownloadManager.STATUS_FAILED) {
                    mSelectionResumeButton.setVisibility(Button.GONE);
                    mSelectionPauseButton.setVisibility(Button.GONE);
                }
                break;
            } 
        }
    }


    private boolean isPausedForWifi(Cursor cursor) {
        return cursor.getInt(mReasonColumndId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI;
    }

    /**
     * Check if any of the selected downloads have been deleted from the downloads database, and
     * remove such downloads from the selection.
     */
    private void checkSelectionForDeletedEntries() {
        // gather all existing IDs...
        Set<Long> allIds = new HashSet<Long>();
        for (mDateSortedCursor.moveToFirst(); !mDateSortedCursor.isAfterLast();
                mDateSortedCursor.moveToNext()) {
            allIds.add(mDateSortedCursor.getLong(mIdColumnId));
        }

        // ...and check if any selected IDs are now missing
        for (Iterator<Long> iterator = mSelectedIds.iterator(); iterator.hasNext(); ) {
            if (!allIds.contains(iterator.next())) {
                iterator.remove();
            }
        }
    }

    /**
     * Move {@link #mDateSortedCursor} to the download with the given ID.
     * @return true if the specified download ID was found; false otherwise
     */
    private boolean moveToDownload(long downloadId) {
        for (mDateSortedCursor.moveToFirst(); !mDateSortedCursor.isAfterLast();
                mDateSortedCursor.moveToNext()) {
            if (mDateSortedCursor.getLong(mIdColumnId) == downloadId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when a dialog for a pending download is canceled.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        mQueuedDownloadId = null;
        mQueuedDialog = null;
        
        // Add to support Queue AlertDialog
        mCurrentDialog = null;
        showNextDialog();
    }
}
