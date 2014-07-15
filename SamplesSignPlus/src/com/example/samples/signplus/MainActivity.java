package com.example.samples.signplus;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	// Request code to use when launching the resolution activity
	public static final int REQUEST_RESOLVE_ERR = 1001;
	// Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    
	// Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private GoogleApiClient mGoogleApiClient;
	//Contains all possible error codes for when a client fails to connect to Google Play services
	private ConnectionResult mConnectionResult;
	private ProgressDialog mConnectionProgressDialog;
	
	private SignInButton mSignInButton;
	private RelativeLayout mRLProfile;
	private Button mSignOutButton;
	private Button mRevokeAccessButton;
	private TextView mTxtName;
	private TextView mTxtMail;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRLProfile = (RelativeLayout) findViewById(R.id.rl_Profile);
		mTxtName = (TextView) findViewById(R.id.txt_name);
		mTxtMail = (TextView) findViewById(R.id.txt_mail);
		mSignInButton = (SignInButton) findViewById(R.id.btn_sin);
		mSignOutButton = (Button) findViewById(R.id.btn_sign_out);
		mRevokeAccessButton = (Button) findViewById(R.id.btn_revoke_access);
		mSignInButton.setOnClickListener(this);
		mSignOutButton.setOnClickListener(this);
		mRevokeAccessButton.setOnClickListener(this);

		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
		mConnectionProgressDialog.setTitle(CONNECTIVITY_SERVICE);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		mResolvingError = savedInstanceState != null
	            && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
		
		
		
	}
	
	protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
 
    protected void onStop() {
    	if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId())
		{
			case R.id.btn_sin:
				signInWithGplus();
				break;
			case R.id.btn_sign_out:
				signOutWithGplus();
				break;
			case R.id.btn_revoke_access:
				revokeAccessGplus();
				break;
		}
	}
	
	private void signInWithGplus(){
		if (!mGoogleApiClient.isConnected()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_SIGN_IN);
				} catch (SendIntentException e) {
					// Riprova a connetterti.
					mConnectionResult = null;
					mGoogleApiClient.connect();
				}
			}
		}
	}
	
	private void signOutWithGplus(){
		if(mGoogleApiClient.isConnected()){
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
	        mGoogleApiClient.disconnect();
	        updateUI(false);
		}
	}
	
	private void revokeAccessGplus(){
		if (mGoogleApiClient.isConnected()) {
	        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
	        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
	                .setResultCallback(new ResultCallback<Status>() {
	                    @Override
	                    public void onResult(Status arg0) {
	                        Log.e("", "User access revoked!");
	                        mGoogleApiClient.connect();
	                        updateUI(false);
	                    }
	 
	                });
	    }
	}
	
	private void updateUI(boolean isSignedIn){
		if(isSignedIn){
			mSignInButton.setVisibility(View.GONE);
			mRLProfile.setVisibility(View.VISIBLE);
			mSignOutButton.setVisibility(View.VISIBLE);
			mRevokeAccessButton.setVisibility(View.VISIBLE);
		}
		else{
			mSignInButton.setVisibility(View.VISIBLE);
			mRLProfile.setVisibility(View.GONE);
			mSignOutButton.setVisibility(View.GONE);
			mRevokeAccessButton.setVisibility(View.GONE);
		}
		
	}

	// @Override
	public void onConnected(Bundle connectionHint) {
		// Connected to Google Play services!
        // The good stuff goes here.
		mConnectionProgressDialog.dismiss();
		if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient)!=null){
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			String name = currentPerson.getDisplayName();
			String mail = Plus.AccountApi.getAccountName(mGoogleApiClient);
			
			mTxtMail.setText(mail);
			mTxtName.setText(name);
			
			Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			updateUI(true);
		}
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		// The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
		mGoogleApiClient.connect();
	    updateUI(false);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        // More about this in the next section.
        
        
    	if(mConnectionProgressDialog.isShowing()){
    		if (mResolvingError) {
                // Already attempting to resolve an error.
                return;
    		}
    		else if (result.hasResolution()) 
    		{
                try 
                {
                	mResolvingError = true;
                    result.startResolutionForResult(this, REQUEST_RESOLVE_ERR);
                } 
                catch (SendIntentException e) 
                {
                	// There was an error with the resolution intent. Try again.
            		mGoogleApiClient.connect();
                }
    		} else {
                // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                showErrorDialog(result.getErrorCode());
                mResolvingError = true;
            }    		
    	}
    	mConnectionResult = result;
    }
	
	/** this code is all about building the error dialog **/
	
	/* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
    	    	
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity)getActivity()).onDialogDismissed();
        }
    }
    
    /** End error Resolution **/
    
    
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_RESOLVE_ERR) {
        	mResolvingError = false;
        	if(responseCode == RESULT_OK){
        		// Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                	mConnectionResult = null;
                    mGoogleApiClient.connect();
                }
        	}
            
           
        }
    }

}
