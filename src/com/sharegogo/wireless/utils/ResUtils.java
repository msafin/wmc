package com.sharegogo.wireless.utils;

import android.content.Context;

import com.sharegogo.wireless.SharegogoWirelessApp;

/**
 * ��Դ��ظ�������
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
