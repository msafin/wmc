package com.sharegogo.wireless.download;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.sharegogo.wireless.R;

/**
 * This receiver clicks to notifications that
 * downloads for the OMA DL are in progress/complete.  Clicking on an
 * in-progress or failed download will open the download manager.  Clicking on
 * a complete, successful download will open the file.
 */
public class OpenDownloadReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ContentResolver cr = context.getContentResolver();
        
        Uri data = intent.getData();
        Cursor cursor = null;
        try {
            cursor = cr.query(data,
                    new String[] { Downloads.Impl._ID, Downloads.Impl._DATA,
                    Downloads.Impl.COLUMN_MIME_TYPE, Downloads.COLUMN_STATUS },
                    null, null, null);
            if (cursor == null) {
            	return;
            }
            if (cursor.moveToFirst()) {
                String filename = cursor.getString(1);
                String mimetype = cursor.getString(2);
                String action = intent.getAction();
                if (Downloads.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                    int status = cursor.getInt(3);
                    if (Downloads.isStatusCompleted(status)
                            && Downloads.isStatusSuccess(status)) {
                        Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                        Uri path = Uri.parse(filename);
                        // If there is no scheme, then it must be a file
                        if (path.getScheme() == null) {
                            path = Uri.fromFile(new File(filename));
                        }
                        launchIntent.setDataAndType(path, mimetype);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            context.startActivity(launchIntent);
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(context,
                                    R.string.download_no_application_title,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Open the downloads page
                        Intent pageView = new Intent(
                                DownloadManager.ACTION_VIEW_DOWNLOADS);
                        pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(pageView);
                    }
                } 
            } else {
                Log.v("OmaDownload", "OMAReceiver:cursor.moveToFirst() failed:");
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
