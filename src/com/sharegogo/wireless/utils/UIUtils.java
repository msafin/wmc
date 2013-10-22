package com.sharegogo.wireless.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
/**
 * UIÏà¹Ø¸¨Öúº¯Êý
 * @author Raymon
 *
 */
public class UIUtils {
	
	static public void DisplayImage(String uri,ImageView imageView,int defaultImage)
	{
		DisplayImage(uri,imageView,defaultImage,null);
	}
	
	static public void DisplayImage(String uri,ImageView imageView,int defaultImage,ImageLoadingListener listener)
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showStubImage(defaultImage)
        .resetViewBeforeLoading()
        .delayBeforeLoading(1000)
        .showImageForEmptyUri(defaultImage)
        .cacheInMemory()
        .cacheOnDisc()
        .imageScaleType(ImageScaleType.EXACTLY)
        .build();
		
		if(listener != null)
		{
			ImageLoader.getInstance().displayImage(uri, imageView, options,listener);
		}
		else
		{
			ImageLoader.getInstance().displayImage(uri, imageView, options);
		}
	}
	
	static public void gotoBrowserActivity(Context context,String url)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(url));
		
		context.startActivity(intent);
	}
}
