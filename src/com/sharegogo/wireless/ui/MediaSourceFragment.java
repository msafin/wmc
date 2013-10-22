package com.sharegogo.wireless.ui;

import org.openintents.filemanager.FileManagerActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.config.StatisticHelper;
import com.umeng.analytics.MobclickAgent;

public class MediaSourceFragment extends Fragment implements OnClickListener{
	public static final String TAG = "MediaSourceFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.media_source_fragment, container, false);
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Button mediaLocal = (Button)view.findViewById(R.id.media_local);
		Button mediaRemote = (Button)view.findViewById(R.id.media_remote);
		
		mediaLocal.setOnClickListener(this);
		mediaRemote.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.media_local:
			onMediaLocalClick();
			MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_MEDIA_SOURCE_CLICKED, StatisticHelper.LABEL_MEDIA_LOCAL_CLICKED);
			break;
		case R.id.media_remote:
			getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.media_share_container,new MediaShareListFragment(),"/")
				.addToBackStack(null)
				.commit();
			MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_MEDIA_SOURCE_CLICKED, StatisticHelper.LABEL_MEDIA_REMOTE_CLICKED);
			break;
		default:
			break;
		}
	}
	
	private void onMediaLocalClick(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(getActivity(), FileManagerActivity.class);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
		}
	}
}
