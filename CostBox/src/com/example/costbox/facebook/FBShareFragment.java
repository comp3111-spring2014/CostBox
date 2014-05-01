package com.example.costbox.facebook;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.example.costbox.*;
import com.example.costbox.CategoryFragment.OnReturnListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class FBShareFragment extends Fragment{
	//private Button shareButton;
	//private Button skipandenter;
	FBListener status_change;
	private static final String TAG = "FBShareFragment";
	public Bundle postParams;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	public interface FBListener {
        public void FBListener(int state,String reserve);
    }
	//draw the view of this log-in
	/*
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{		
	    View view = inflater.inflate(R.layout.facebook_share, container, false);
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setFragment(this);
	    //call publishStory() with share button
	    shareButton = (Button) view.findViewById(R.id.shareButton);
	    skipandenter = (Button) view.findViewById(R.id.skipandenter);
	   
	   
	    
	    shareButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            //publishStory();	
	        }
	     });
	    skipandenter.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            
	        	Intent intent=new Intent();
	        	intent.setClassName("com.example.costbox","com.example.costbox.CostBox");
	            startActivity(intent);
	            getActivity().finish();
                
	        }
	    });
	    
		if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
	    return view;
	    
	}
	*/
	private Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        
	    	//skipandenter.setVisibility(View.VISIBLE);
	        //Intent intent=new Intent();
        	//intent.setClassName("com.example.costbox","com.example.costbox.CostBox");
            //startActivity(intent);
	        //getActivity().finish();
	    	
	        //check if a reauthorization was in progress and the token was updated
	       Log.d("post","change to this state");
	      
	    	if (pendingPublishReauthorization && 
	                state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	            pendingPublishReauthorization = false;
	            Log.d("post","this is inside the if statement");
	            publishStory();
	        }
	        
	    } else if (state.isClosed()) {
	        //skipandenter.setVisibility(View.INVISIBLE);
	    }
	}


	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private UiLifecycleHelper uiHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (savedInstanceState != null) {
		    pendingPublishReauthorization = 
		        savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);}
	    
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	    Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
		
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	 // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	 
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success!");
	        }
	    });
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	    Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//there may be scenarios where the activity is stopped during the reauthorization flow
	    outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
	    uiHelper.onSaveInstanceState(outState);
	    Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
	}
    public void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            
        } else {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
        }
    }
    public void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    public Session getActiveS()
    {
    	Session session = Session.getActiveSession();
    	return session;
    }
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	//updateStatus();
        }
    }
    private void updateStatus()
    {
    	 Session session = Session.getActiveSession();
         if (session.isOpened()) {
             publishStory();
         } else {
             
         }
    }
	public void publishStory() {
	    
		Session session = Session.getActiveSession();
        
	    if (session != null){

	        // Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	      
	        if (!isSubsetOf(PERMISSIONS, permissions))
	        {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }
           
            //call back
	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i(TAG,
	                        "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Toast.makeText(getActivity()
	                         .getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(getActivity()
	                             .getApplicationContext(), 
	                             "Successful",
	                             Toast.LENGTH_LONG).show();
	                }
	            }
	        };
            //request
	        
	        Request request = new Request(session, "/me/photos", postParams, 
	                              HttpMethod.POST, callback);
	        

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }

	}

	//to determine whether or not the user has granted the necessary permissions to publish the story
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	//to referenced the permissions needed and the pending authorization info
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
}
