package com.sharegogo.wireless.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sharegogo.wireless.R;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements OnClickListener {
	private TextView mTitle = null;
	private Button mBack = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTitle = (TextView)findViewById(R.id.title);
        mBack = (Button)findViewById(R.id.back);
        mBack.setOnClickListener(this);
        
        if(savedInstanceState == null){
	        getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new FunctionsFragment())
				.commit();
        }
        
        MobclickAgent.setDebugMode(true);
        MobclickAgent.updateOnlineConfig(this);
    }

    
    @Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
	    	case R.id.menu_settings:
	    		AppSettingActivity.openAppSettingActivity(this);
	    		break;
	    	default:
	    		break;
    	}
		return super.onMenuItemSelected(featureId, item);
	}


	public void setMainTitle(int resid)
    {
    	mTitle.setText(resid);
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.back:
				int count = getSupportFragmentManager().getBackStackEntryCount();
				if(count < 1)
				{
					finish();
				}
				else
				{
					getSupportFragmentManager().popBackStack();
				}
			break;
			default:
				break;
		}
	}
}
