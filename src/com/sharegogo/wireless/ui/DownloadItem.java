package com.sharegogo.wireless.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.sharegogo.wireless.R;

/**
 * This class customizes RelativeLayout to directly handle clicks on the left part of the view and
 * treat them at clicks on the checkbox. This makes rapid selection of many items easier. This class
 * also keeps an ID associated with the currently displayed download and notifies a listener upon
 * selection changes with that ID.
 */
public class DownloadItem extends RelativeLayout {
    private static float CHECKMARK_AREA = -1;

    private boolean mIsInDownEvent = false;
    private CheckBox mCheckBox;
    private long mDownloadId;
    private DownloadSelectListener mListener;

    public static interface DownloadSelectListener {
        public void onDownloadSelectionChanged(long downloadId, boolean isSelected);
        public boolean isDownloadSelected(long id);
    }

    public DownloadItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public DownloadItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DownloadItem(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        if (CHECKMARK_AREA == -1) {
            CHECKMARK_AREA = getResources().getDimensionPixelSize(R.dimen.checkmark_area);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCheckBox = (CheckBox) findViewById(R.id.download_checkbox);
    }

    public void setDownloadId(long downloadId) {
        mDownloadId = downloadId;
    }

    public void setSelectListener(DownloadSelectListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < CHECKMARK_AREA) {
                    mIsInDownEvent = true;
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mIsInDownEvent = false;
                break;

            case MotionEvent.ACTION_UP:
                if (mIsInDownEvent && event.getX() < CHECKMARK_AREA) {
                    toggleCheckMark();
                    handled = true;
                }
                mIsInDownEvent = false;
                break;
        }

        if (handled) {
            postInvalidate();
        } else {
            handled = super.onTouchEvent(event);
        }

        return handled;
    }

    private void toggleCheckMark() {
        mCheckBox.toggle();
        mListener.onDownloadSelectionChanged(mDownloadId, mCheckBox.isChecked());
    }
}
