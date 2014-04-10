package com.example.costbox.facebook;

import java.util.Arrays;

import com.example.costbox.R;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class FirstStartCostBox extends FragmentActivity {
	public MainFragment mainFragment;
	private Button shareButton;
	private Button skipandenter;
	
	//static Bundle postParams;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_start);
	   
	    skipandenter = (Button) findViewById(R.id.skipandenter);
	    LoginButton authButton = (LoginButton) findViewById(R.id.authButton);

	    Log.d("share","skip the pass");
	    if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(mainFragment, "FBS")
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager()
	        .findFragmentByTag("FBS");
	    }
	    
	    authButton.setFragment(mainFragment);
	    //authButton.setReadPermissions(Arrays.asList("publish_actions"));
	    
	    //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
	    skipandenter.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            
	        	Intent intent=new Intent();
	        	intent.setClassName("com.example.costbox","com.example.costbox.CostBox");
	            startActivity(intent);
	            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                FirstStartCostBox.this.finish();
	        }
	    });
	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}