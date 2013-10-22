package com.sharegogo.wireless.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class MediaUtils {
	public static void playVideo(Context context,Uri uri){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String type = "video/*";
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		
		intent.setDataAndType(uri, type);

		try{
			context.startActivity(intent);
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public static void playAudio(Context context,Uri uri){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String type = "audio/*";
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		
		intent.setDataAndType(uri, type);

		try{
			context.startActivity(intent);
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
		}
	}

	public static void openImage(Context context,Uri uri){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String type = "image/*";
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		
		intent.setDataAndType(uri, type);
		
		try{
			context.startActivity(intent);
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public static void openFile(Context context,Uri uri){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String type = "*/*";
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
		type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		
		intent.setDataAndType(uri, type);

		try{
			context.startActivity(intent);
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
			UIUtils.gotoBrowserActivity(context, uri.toString());
		}
	}
}
