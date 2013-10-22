package com.sharegogo.wireless.dlna;

import org.fourthline.cling.support.contentdirectory.callback.Browse.Status;
import org.fourthline.cling.support.model.DIDLContent;

public class ContentDirectoryBrowseResult {
	private Status status = Status.NO_CONTENT;
	private DIDLContent result  = null;
	private UpnpFailure upnpFailure;
	
	public ContentDirectoryBrowseResult() {
		super();
		
	}

	public Status getStatus() {
		return status;
	}

	public DIDLContent getResult() {
		return result;
	}

	public UpnpFailure getUpnpFailure() {
		return upnpFailure;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setResult(DIDLContent result) {
		this.result = result;
	}

	public void setUpnpFailure(UpnpFailure upnpFailure) {
		this.upnpFailure = upnpFailure;
	}

}