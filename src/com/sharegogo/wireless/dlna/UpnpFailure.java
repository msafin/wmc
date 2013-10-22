package com.sharegogo.wireless.dlna;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;

public class UpnpFailure {
	private ActionInvocation invocation;
	private UpnpResponse response;
	private String defaultMsg;

	public UpnpFailure(ActionInvocation invocation, UpnpResponse response,
			String defaultMsg) {
		super();
		this.invocation = invocation;
		this.response = response;
		this.defaultMsg = defaultMsg;
	}

	public ActionInvocation getInvocation() {
		return invocation;
	}

	public UpnpResponse getOperation() {
		return response;
	}

	public String getDefaultMsg() {
		return defaultMsg;
	}

	@Override
	public String toString() {
		return "UpnpFailure ["
				+ (invocation != null ? "invocation=" + invocation + ", " : "")
				+ (response != null ? "response=" + response + ", " : "")
				+ (defaultMsg != null ? "defaultMsg=" + defaultMsg : "") + "]";
	}
}
