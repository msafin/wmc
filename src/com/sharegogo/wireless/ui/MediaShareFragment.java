package com.sharegogo.wireless.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.config.StatisticHelper;
import com.sharegogo.wireless.download.DownloadManager;
import com.umeng.analytics.MobclickAgent;

public class MediaShareFragment extends Fragment implements OnClickListener{
	private ImageButton mDownload;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.media_share_fragment, null);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mDownload.setVisibility(View.GONE);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getFragmentManager().beginTransaction()
			.replace(R.id.media_share_container,new MediaSourceFragment(),MediaSourceFragment.TAG)
			.commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		MainActivity activity = (MainActivity)getActivity();
		activity.setMainTitle(R.string.media_share);
		mDownload = (ImageButton)activity.findViewById(R.id.right_button);
		
		mDownload.setOnClickListener(this);
		mDownload.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.right_button:
				onDownloadClicked();
				break;
			default:
				break;
		}
	}
	
	private void onDownloadClicked()
	{
        Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        
        pageView.setClass(getActivity(), DownloadList.class);
        pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        getActivity().startActivity(pageView);
        MobclickAgent.onEvent(getActivity(), StatisticHelper.EVENT_DOWNLOAD_BUTTON_CLICKED);
	}
}
