package com.example.costbox;

import com.example.costbox.CostBox;
import com.example.costbox.R;
import com.example.costbox.R.anim;
import com.example.costbox.R.xml;
import com.example.costbox.facebook.FBShareFragment;
import com.example.costbox.preference.PreFragment;
import com.facebook.Session;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MyPreference extends FragmentActivity 
implements com.example.costbox.preference.PreFragment.PreOnReturnListener{
    //fb share fragment
	public FBShareFragment fbShareFragment;
	public PreFragment preFragment;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		//actionbar return button
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true); 
		Log.d("setting","enter in activity!");
		//get preference fragment
		preFragment=new PreFragment();
		getFragmentManager().beginTransaction().replace(android.R.id.content, preFragment).commit(); 
		//get FB share fragment
		if (savedInstanceState == null) {
	        fbShareFragment = new FBShareFragment();
	        getSupportFragmentManager().beginTransaction().add(fbShareFragment, "FBS")
	        .commit();
	    } else {
	        fbShareFragment = (FBShareFragment) getSupportFragmentManager()
	        .findFragmentByTag("FBS");
	    } 
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, CostBox.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            this.finish();
	            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onReturnFromPreFragment(int state) {
		// TODO Auto-generated method stub
		if(state==1)
		{ 
			fbShareFragment.onClickLogout();
		}
		else if(state==2)
		{
			if(CostBox.isNetworkConnected(this)==false)
			{
				Toast.makeText(this, "Please check your network status", 100).show();
				return;
			}
			fbShareFragment.onClickLogin();
		}
		else if(state==3) //checking
		{
			
			Session session = fbShareFragment.getActiveS();
		     //if logged in
           if(session.isOpened())
           {
        	   preFragment.facebooklog.setChecked(true);
           }
           else 
           {
        	   preFragment.facebooklog.setChecked(false);
           }
		}
	}
}
	