package com.sharegogo.wireless.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.data.MediaShareItem;

public class MediaShareAdapter extends BaseAdapter{
	public static final int TYPE_ROOT = 0;
	public static final int TYPE_SUB = 1;
	private int mType = TYPE_ROOT;
	private List<MediaShareItem> mData = new ArrayList<MediaShareItem>();
	
	public MediaShareAdapter(int type)
	{
		mType = type;
		
		if(mType == TYPE_ROOT)
		{
			//makeRootData();
		}
		else
		{
			//makeSubData();
		}
	}
	
	public void addAll(List<MediaShareItem> list)
	{
		mData.addAll(list);
		this.notifyDataSetChanged();
	}
	
	public void add(MediaShareItem item)
	{
		mData.add(item);
		this.notifyDataSetChanged();
	}
	
	public void clear()
	{
		
		mData.clear();
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewTag tag = null;
		
		if(convertView == null)
		{
			tag = new ViewTag();
			
			Context context = SharegogoWirelessApp.getApplication();
			
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.media_share_item, null);
			
			tag.mItemType = (ImageView)convertView.findViewById(R.id.item_type);
			tag.mName = (TextView)convertView.findViewById(R.id.item_name);
			tag.mTime = (TextView)convertView.findViewById(R.id.item_time);
			tag.mLen = (TextView)convertView.findViewById(R.id.item_len);
			
			convertView.setTag(tag);
		}
		
		tag = (ViewTag)convertView.getTag();
		
		setupView(position,tag);
		
		return convertView;
	}

	static class ViewTag
	{
		ImageView mItemType;
		TextView mName;
		TextView mTime;
		TextView mLen;
	}
	
	private void setupView(int position,ViewTag tag)
	{
		MediaShareItem item = mData.get(position);
		
		tag.mName.setText(item.name);
		
		if(item.type == MediaShareItem.TYPE_FOLDER)
		{
			tag.mLen.setVisibility(View.GONE);
			
			tag.mItemType.setImageResource(R.drawable.folder_indicator);
		}
		else
		{
			switch(item.type)
			{
			case MediaShareItem.TYPE_FILE_NORMAL:
				tag.mItemType.setImageResource(R.drawable.file_indicator);
				break;
			case MediaShareItem.TYPE_FILE_VIDEO:
				tag.mItemType.setImageResource(R.drawable.video_indicator);
				break;
			default:
				
				break;
			}
			
			tag.mLen.setVisibility(View.VISIBLE);
			tag.mLen.setText(String.valueOf(item.size));
		}
		
	}
	
	private void makeRootData()
	{
		MediaShareItem item = new MediaShareItem();
		
		item.type = MediaShareItem.TYPE_FOLDER;
		item.name = "udisk";
		
		mData.add(item);
		
		item = new MediaShareItem();
		
		item.type = MediaShareItem.TYPE_FOLDER;
		item.name = "test";
		
		mData.add(item);
	}
	
	private void makeSubData()
	{
		MediaShareItem item = new MediaShareItem();
		
		item.type = MediaShareItem.TYPE_FOLDER;
		item.name = "sub";
		
		mData.add(item);
		
		item = new MediaShareItem();
		
		item.type = MediaShareItem.TYPE_FILE_NORMAL;
		item.name = "file";
		item.size = 64;
		
		mData.add(item);
	}
}
