package com.example.costbox;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

public class MyPreference extends PreferenceActivity {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mypreference);
		//actionbar return button
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true); 
		Log.d("setting","enter in activity!");
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, CostBox.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
		// TODO Auto-generated method stub
		//preserved
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}