package com.example.costbox.preference;

import com.example.costbox.CostBox;
import com.example.costbox.R;
import com.example.costbox.CategoryFragment.OnReturnListener;
import com.example.costbox.R.anim;
import com.example.costbox.R.xml;
import com.example.costbox.facebook.FBShareFragment;
import com.facebook.Session;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class PreFragment extends PreferenceFragment  implements
Preference.OnPreferenceClickListener,
Preference.OnPreferenceChangeListener {
    //widget
	public CheckBoxPreference facebooklog;
	public Preference categoryedit;
    
	//listener
	PreOnReturnListener mcallback;
    
    
    public interface PreOnReturnListener {
        public void onReturnFromPreFragment(int state);
    }
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mypreference);

		//find reference
		facebooklog=(CheckBoxPreference)findPreference("FB");
		categoryedit=(Preference)findPreference("category_edit");
		//Set listener
		facebooklog.setOnPreferenceClickListener(this);
		facebooklog.setOnPreferenceChangeListener(this);
		
		categoryedit.setOnPreferenceClickListener(this);
		
		mcallback.onReturnFromPreFragment(3);
	}

	private void operatePreference(Preference preference) {

	
	}
	
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
		// TODO Auto-generated method stub
		//preserved
		if(preference == categoryedit)
		{
			Intent i=new Intent();
			i.setClass(getActivity(),CategoryEdit.class);
			startActivityForResult(i,1);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		mcallback.onReturnFromPreFragment(3);
		return false;
	}
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(preference==facebooklog)	
		{
			if(facebooklog.isChecked())
			{
				   AlertDialog.Builder dg = new Builder(getActivity());
   				   dg.setMessage("Are you sure to log out FaceBook?");
   				   dg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
   					 @Override
   					public void onClick(DialogInterface dialog, int whichButton) {
   						facebooklog.setChecked(false);
   						mcallback.onReturnFromPreFragment(1);
   					}
   				    })
   				   .setNegativeButton("No", new DialogInterface.OnClickListener() {
   				 	@Override
   					public void onClick(DialogInterface dialog, int whichButton) {
   						// TODO Auto-generated method stub
   					}
   				    })
   				    .show();
				
			}
			else 
			{   mcallback.onReturnFromPreFragment(2);
				facebooklog.setChecked(true);
			}
		}
		return false;
	}
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mcallback = (PreOnReturnListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CategoryFragment.OnReturnListener");
        }
    }

}