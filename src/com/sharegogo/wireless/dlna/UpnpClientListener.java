package com.sharegogo.wireless.dlna;

import org.fourthline.cling.model.meta.Device;

public interface UpnpClientListener  {

	void deviceAdded(Device<?, ?, ?> device);
	void deviceRemoved(Device<?, ?, ?> device);
	void deviceUpdated(Device<?, ?, ?> device);	
	                	
}

