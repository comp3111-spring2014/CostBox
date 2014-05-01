package com.example.costbox.facebook;

import java.util.Arrays;

import com.example.costbox.R;
import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class FirstStartCostBox extends Activity {
    //delay time
	private final int SPLASH_DISPLAY_LENGHT = 1000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main_start);
	   
	    new Handler().postDelayed(new Runnable() {  
            public void run() {  
	     Intent intent=new Intent();
	     intent.setClassName("com.example.costbox","com.example.costbox.CostBox");
	     startActivity(intent);
	     overridePendingTransition(R.anim.fadein, R.anim.fadeout);
         FirstStartCostBox.this.finish();
            }
	    }, SPLASH_DISPLAY_LENGHT);
	}

}