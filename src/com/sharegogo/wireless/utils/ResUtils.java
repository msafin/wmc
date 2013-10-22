package com.sharegogo.wireless.utils;

import android.content.Context;

import com.sharegogo.wireless.SharegogoWirelessApp;

/**
 * 资源相关辅助函数
 * @author Raymon
 *
 */
public class ResUtils {
	static public String getString(int resId)
	{
		Context context = SharegogoWirelessApp.getApplication();
		
		return context.getResources().getString(resId);
	}
}
