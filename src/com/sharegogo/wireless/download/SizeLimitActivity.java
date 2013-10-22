package com.sharegogo.wireless.download;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Activity to show dialogs to the user when a download exceeds a limit on download sizes for
 * mobile networks.  This activity gets started by the background download service when a download's
 * size is discovered to be exceeded one of these thresholds.
 */
public class SizeLimitActivity extends Activity
        implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener {
    private Dialog mDialog;
    private Queue<Intent> mDownloadsToShow = new LinkedList<Intent>();
    private Uri mCurrentUri;
    private Intent mCurrentIntent;
    public static final int NOTIFY_PAUSE_DUE_TO_SIZE = 0;
    public static final int NOTIFY_FILE_ALREADY_EXIST = 1;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            mDownloadsToShow.add(intent);
            setIntent(null);
            showNextDialog();
        }
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void showNextDialog() {
        if (mDialog != null) {
            return;
        }

        if (mDownloadsToShow.isEmpty()) {
            finish();
            return;
        }

        mCurrentIntent = mDownloadsToShow.poll();
        mCurrentUri = mCurrentIntent.getData();
        Cursor cursor = getContentResolver().query(mCurrentUri, null, null, null, null);
        try {
            if (!cursor.moveToFirst()) {
                Log.e(Constants.TAG, "Empty cursor for URI " + mCurrentUri);
                dialogClosed();
                return;
            }
            showDialog(cursor);
        } finally {
            cursor.close();
        }
    }

    private void showDialog(Cursor cursor) {
        int size = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.Impl.COLUMN_TOTAL_BYTES));
        
        // Add this for support CU customization
        int showDialogReason = mCurrentIntent.getExtras().getInt(DownloadInfo.SHOW_DIALOG_REASON);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
//        if (showDialogReason == NOTIFY_PAUSE_DUE_TO_SIZE) {
//            String sizeString = Formatter.formatFileSize(this, size);
//            String queueText = getString(R.string.button_queue_for_wifi);
//            boolean isWifiRequired =
//                mCurrentIntent.getExtras().getBoolean(DownloadInfo.EXTRA_IS_WIFI_REQUIRED);
//    
//            
//            if (isWifiRequired) {
//                builder.setTitle(R.string.wifi_required_title)
//                        .setMessage(getString(R.string.wifi_required_body, sizeString, queueText))
//                        .setPositiveButton(R.string.button_queue_for_wifi, this)
//                        .setNegativeButton(R.string.button_cancel_download, this);
//            } else {
//                builder.setTitle(R.string.wifi_recommended_title)
//                        .setMessage(getString(R.string.wifi_recommended_body, sizeString, queueText))
//                        .setPositiveButton(R.string.button_start_now, this)
//                        .setNegativeButton(R.string.button_queue_for_wifi, this);
//            } 
//        } else {
//            //This for support CU customization
//            builder.setTitle(getText(R.string.app_label))
//                    .setMessage(getText(R.string.download_file_already_exist))
//                    .setPositiveButton(getString(R.string.ok), this)
//                    .setNegativeButton(getString(R.string.cancel), this);
//        }
        
        mDialog = builder.setOnCancelListener(this).show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Modify to support CU customization
        int showDialogReason = mCurrentIntent.getExtras().getInt(DownloadInfo.SHOW_DIALOG_REASON);
        if (showDialogReason == 0) {
            dialogClosed();
        } else {
            Log.e(Constants.TAG, "SizeLimitActivity.onCancel:FileAlreadyExist," +
            		" Delete download uri is"+
            		mCurrentUri);
            getContentResolver().delete(mCurrentUri, null, null);
        }
    }

    private void dialogClosed() {
        mDialog = null;
        mCurrentUri = null;
        showNextDialog();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int showDialogReason = mCurrentIntent.getExtras().getInt(DownloadInfo.SHOW_DIALOG_REASON);
        if (showDialogReason == NOTIFY_PAUSE_DUE_TO_SIZE) {
            boolean isRequired =
                    mCurrentIntent.getExtras().getBoolean(DownloadInfo.EXTRA_IS_WIFI_REQUIRED);
            if (isRequired && which == AlertDialog.BUTTON_NEGATIVE) {
                getContentResolver().delete(mCurrentUri, null, null);
            } else if (!isRequired && which == AlertDialog.BUTTON_POSITIVE) {
                ContentValues values = new ContentValues();
                values.put(Downloads.Impl.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT, true);
                getContentResolver().update(mCurrentUri, values , null, null);
            }
        } else {
            // This for support OMA DL next Url
            if (which == AlertDialog.BUTTON_NEGATIVE) {
                Log.e(Constants.TAG, "SizeLimitActivity.onClick:FileAlreadyExist," +
                        " Delete download uri is"+
                        mCurrentUri);
                getContentResolver().delete(mCurrentUri, null, null);
            } else if (which == AlertDialog.BUTTON_POSITIVE) {
                ContentValues values = new ContentValues();
                values.put(Downloads.Impl.CONTINUE_DOWNLOAD_WITH_SAME_FILENAME, 1);
                values.put(Downloads.Impl.COLUMN_STATUS,
                        Downloads.Impl.STATUS_PENDING);
                getContentResolver().update(mCurrentUri, values,
                        null, null);
                Log.e(Constants.TAG, "SizeLimitActivity.onClick:FileAlreadyExist," +
                        " continue download uri is"+
                        mCurrentUri);
            }
        }
        dialogClosed();
    }
}
