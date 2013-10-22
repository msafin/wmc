package com.sharegogo.wireless.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.sharegogo.wireless.R;

public class LoginFragment extends Fragment implements OnClickListener{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.login_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		Button login = (Button)view.findViewById(R.id.login);
		login.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		MainActivity activity = (MainActivity)getActivity();
		activity.setMainTitle(R.string.login);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
			case R.id.login:
				onLogin();
				break;
		}
	}
	
	private void onLogin()
	{
		onLoginSuccess();
	}
	
	private void onLoginSuccess()
	{
		this.getFragmentManager().beginTransaction()
			.replace(R.id.container, new FunctionsFragment())
			.commit();
	}
}
