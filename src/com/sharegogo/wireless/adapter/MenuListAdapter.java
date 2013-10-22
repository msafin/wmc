package com.sharegogo.wireless.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;

public class MenuListAdapter extends BaseAdapter{
	private List<String> mData = new ArrayList<String>();
	
	public MenuListAdapter()
	{
		Context context = SharegogoWirelessApp.getApplication();
		Resources res = context.getResources();
		
		String[] menus = res.getStringArray(R.array.sliding_menus);
		mData.addAll(Arrays.asList(menus));
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
			
			convertView = inflater.inflate(R.layout.sliding_menu_item, null);
			
			tag.mMenu = (TextView)convertView.findViewById(R.id.menu);
			
			convertView.setTag(tag);
		}
		
		tag = (ViewTag)convertView.getTag();
		
		setupView(position,tag);
		
		return convertView;
	}

	static class ViewTag
	{
		TextView mMenu;
	}
	
	private void setupView(int position,ViewTag tag)
	{
		String name = mData.get(position);
		
		tag.mMenu.setText(name);
	}
}
