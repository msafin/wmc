package com.sharegogo.wireless.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sharegogo.wireless.download.DownloadManager;
import com.sharegogo.wireless.ui.DownloadItem.DownloadSelectListener;

/**
 * Adapter for a date-sorted list of downloads.  Delegates all the real work to
 * {@link DownloadAdapter}.
 */
public class DateSortedDownloadAdapter extends DateSortedExpandableListAdapter {
    private DownloadAdapter mDelegate;

    public DateSortedDownloadAdapter(Context context, Cursor cursor,
            DownloadSelectListener selectionListener) {
        super(context, cursor,
                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
        mDelegate = new DownloadAdapter(context, cursor, selectionListener);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
        // The layout file uses a RelativeLayout, whereas the GroupViews use TextView.
        if (null == convertView || !(convertView instanceof RelativeLayout)) {
            convertView = mDelegate.newView();
        }

        // Bail early if the Cursor is closed.
        if (!moveCursorToChildPosition(groupPosition, childPosition)) {
            return convertView;
        }

        mDelegate.bindView(convertView);
        return convertView;
    }
}
