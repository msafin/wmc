package com.sharegogo.wireless.download;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.sharegogo.wireless.SharegogoWirelessApp;

public class MultipartEntityMonitored extends MultipartEntity {

	public class OutputStreamMonitored extends FilterOutputStream {

		public OutputStreamMonitored(OutputStream out, long length) {
			super(out);
			m_out = out;
			m_length = length;
			m_broadcast_trigger = Math.round((double)m_length / 100.0);
			BroadcastPercentUploaded();
			updateTotalLen();
		}

		public void write(byte[] b, int off, int len) throws IOException {
			m_out.write(b, off, len);
			m_bytes_transferred += len;
			
			// We don't want to send a broadcast every time data is written,
			// so only do it when the amount written since the last broadcast
			// is at least 1% of the total size.
			if (m_broadcast_count < m_broadcast_trigger) {
				m_broadcast_count += len;
			}
			else {
				m_broadcast_intent.putExtra("percent", PercentUploaded());
				m_broadcast_intent.putExtra("title", m_title);
				if (m_context != null) {
					m_context.sendBroadcast(m_broadcast_intent);
				}
				
				reportProgress();
				m_broadcast_count = 0;
			}
		}

		public void write(int b) throws IOException {
			m_out.write(b);
			m_bytes_transferred += 1;

			// We don't want to send a broadcast every time data is written,
			// so only do it when the amount written since the last broadcast
			// is at least 1% of the total size.
			if (m_broadcast_count < m_broadcast_trigger) {
				m_broadcast_count += 1;
			}
			else {
				m_broadcast_intent.putExtra("percent", PercentUploaded());
				m_broadcast_intent.putExtra("title", m_title);
				if (m_context != null) {
					m_context.sendBroadcast(m_broadcast_intent);
				}
				m_broadcast_count = 0;
				
				reportProgress();
			}
		}
		
	    public Uri getMyDownloadsUri() {
	        return ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI, mId);
	    }

	    public Uri getAllDownloadsUri() {
	        return ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, mId);
	    }
	    
	    private void updateTotalLen() {
	        ContentValues values = new ContentValues();
	        values.put(Downloads.Impl._DATA, m_title);
	        values.put(Downloads.Impl.COLUMN_TOTAL_BYTES,m_length);
	        m_context.getContentResolver().update(getAllDownloadsUri(), values, null, null);
	    }
	    
	    private void reportProgress() {
            ContentValues values = new ContentValues();
            values.put(Downloads.Impl.COLUMN_CURRENT_BYTES,m_bytes_transferred);
            m_context.getContentResolver().update(getAllDownloadsUri(), values, null, null);
	    }
	    
		private void BroadcastPercentUploaded() {
			if (m_broadcast_intent == null) {
				m_broadcast_intent = new Intent();
				m_broadcast_intent.setAction(SharegogoWirelessApp.INTENT_UPLOAD_PROGRESS_UPDATE);
			}
			m_broadcast_intent.putExtra("percent", PercentUploaded());
			m_broadcast_intent.putExtra("title", m_title);
			if (m_context != null) {
				m_context.sendBroadcast(m_broadcast_intent);
			}
			
			m_broadcast_count = 0;
		}
		
		private int PercentUploaded() {
			return (int)Math.round(100.0 * (double)m_bytes_transferred / (double)m_length);
		}
		
		private long m_length = 0;
		private long m_bytes_transferred = 0;
		private long m_broadcast_count = 0;
		private long m_broadcast_trigger = 0;
		private OutputStream m_out = null;
		
	}

	public MultipartEntityMonitored(Context context, String title,long id) {
		super();
		
		m_context = context;
		m_title = title;
		mId = id;
	}

	public MultipartEntityMonitored(HttpMultipartMode mode) {
		super(mode);
	}

	public MultipartEntityMonitored(HttpMultipartMode mode, String boundary,
			Charset charset) {
		super(mode, boundary, charset);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		Log.d("UPLOAD", "Uploading data");
		if (m_outputstream == null) {
			m_outputstream = new OutputStreamMonitored(outstream, getContentLength());
		}
		super.writeTo(m_outputstream);
	}
	   
	private OutputStreamMonitored m_outputstream = null;
	private Intent m_broadcast_intent = null;
	private Context m_context = null;
	private String m_title = null;
	private long mId = -1;
}
