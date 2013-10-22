package com.sharegogo.wireless.ui;

import com.sharegogo.wireless.data.NetworkInfo;

abstract class BaseNetworkFragment extends JsonRequestFragment{

	abstract void setupNetworkInfo(NetworkInfo networkInfo);
	abstract boolean validate();
}
