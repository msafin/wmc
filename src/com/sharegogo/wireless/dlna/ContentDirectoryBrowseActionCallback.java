package com.sharegogo.wireless.dlna;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;

import android.os.Handler;
import android.util.Log;

public class ContentDirectoryBrowseActionCallback extends Browse {
	private BrowseListener mListener;
	
	public ContentDirectoryBrowseActionCallback(Service service,
			String objectID, BrowseFlag flag,BrowseListener listener) {
		super(service, objectID, flag);
		
		mListener = listener;
	}	
	
	public ContentDirectoryBrowseActionCallback(Service service,
			String objectID, BrowseFlag flag, String filter, long firstResult,
			Long maxResults, SortCriterion[] orderBy,BrowseListener listener) {
		super(service, objectID, flag, filter, firstResult, maxResults, orderBy);
		
		mListener = listener;
	}

	@Override
	public void received(ActionInvocation actionInvocation, DIDLContent didl) {
		if(mListener != null){
			mListener.success(didl);
		}
	}

	@Override
	public void updateStatus(Status status) {
		if(mListener != null){
			mListener.updateStatus(status);
		}
	}

	@Override
	public void failure(ActionInvocation actionInvocation, UpnpResponse operation, String defaultMsg) {
		if(mListener != null){
			mListener.failure(defaultMsg);
		}
	}
	
	public static interface BrowseListener{
		public void success(final DIDLContent didl);
		public void failure(final String msg);
		public void updateStatus(final Status status);
	}
}