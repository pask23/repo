package com.example.samples.signplus;

import java.io.InputStream;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";
	private static final String STATE_RESOLVING_ERROR = "resolving_error";
	private static final int REQUEST_CODE_SIGN_IN = 1;
	public static final String PREFS_NAME = "MyPrefsFile";

	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	private GoogleApiClient mGoogleApiClient;
	// Contains all possible error codes for when a client fails to connect to
	// Google Play services
	private ConnectionResult mConnectionResult;
	private ProgressDialog mConnectionProgressDialog;

	private SignInButton mSignInButton;
	private RelativeLayout mRLProfile;
	private Button mSignOutButton;
	private Button mRevokeAccessButton;
	private TextView mTxtName;
	private TextView mTxtMail;
	private ImageView mImgProfile;

	private boolean signed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mRLProfile = (RelativeLayout) findViewById(R.id.rl_Profile);
		mImgProfile = (ImageView) findViewById(R.id.img_profile);
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

		mGoogleApiClient = build_GoogleApiClient();

		mResolvingError = savedInstanceState != null
				&& savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		signed = settings.getBoolean("isSigned", false);

	}

	protected void onStart() {
		super.onStart();
		if(signed){//già fatto signin quindi in precedenza mi sono solo sloggato con tasto logout o nell'onstop
			mSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
			signInWithGplus();
		}

		// if (!mResolvingError)
		// mGoogleApiClient.connect();
	}

	protected void onStop() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("isSigned", signed);

		// Commit the edits!
		editor.commit();
		super.onStop();

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
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

	private void signInWithGplus() {
		Log.d("", "sign in");
		int available = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (available != ConnectionResult.SUCCESS) {
			showErrorDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
			return;
		}

		if (!mResolvingError)
			mGoogleApiClient.connect();

		if (!mGoogleApiClient.isConnecting()) {
			if (mConnectionResult == null) {
				mConnectionProgressDialog.show();
			} else {
				if (mResolvingError) {
					// Already attempting to resolve an error.
					return;
				} else if (mConnectionResult.hasResolution()) {
					try {
						mResolvingError = true;
						mConnectionResult.startResolutionForResult(this,
								REQUEST_CODE_SIGN_IN);
					} catch (SendIntentException e) {
						// Riprova a connetterti.
						mConnectionResult = null;
						mGoogleApiClient.connect();
					}
				} else {
					// Show dialog using GooglePlayServicesUtil.getErrorDialog()
					showErrorDialog(mConnectionResult.getErrorCode());
					mResolvingError = true;
				}
			}
		}
	}

	private void signOutWithGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			// mGoogleApiClient.connect();
			updateUI(false);
		}
	}

	private void revokeAccessGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e("", "User access revoked!");

							mGoogleApiClient = build_GoogleApiClient();
							
							signed = false;
							// mGoogleApiClient.connect();
							updateUI(false);
						}

					});
		}
	}

	private GoogleApiClient build_GoogleApiClient() {
		// When we build the GoogleApiClient we specify where connected and
		// connection failed callbacks should be returned, which Google APIs our
		// app uses and which OAuth 2.0 scopes our app requests.
		return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			mSignInButton.setVisibility(View.GONE);
			mRLProfile.setVisibility(View.VISIBLE);
			mSignOutButton.setVisibility(View.VISIBLE);
			mRevokeAccessButton.setVisibility(View.VISIBLE);
		} else {
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

		mResolvingError = false;
		mConnectionProgressDialog.dismiss();
		Log.d("", "connected");

		getProfileInformation();
		signed = true;
		updateUI(true);
	}

	private void getProfileInformation() {
		try {

			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String name = currentPerson.getDisplayName();
				String mail = Plus.AccountApi.getAccountName(mGoogleApiClient);

				mTxtMail.setText(mail);
				mTxtName.setText(name);

				Toast.makeText(this, name, Toast.LENGTH_LONG).show();

				new LoadProfileImage(mImgProfile).execute(currentPerson
						.getImage().getUrl());

			} else {
				Toast.makeText(this, "person information is null",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Background Async task to load user profile picture from url
	 * */
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
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

		// if(mConnectionProgressDialog.isShowing()){
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERR);
			} catch (SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				mGoogleApiClient.connect();
			}
		} else {
			// Show dialog using GooglePlayServicesUtil.getErrorDialog()
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
		// }
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_RESOLVE_ERR) {
			mResolvingError = false;
			if (responseCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to
				// connect
				if (!mGoogleApiClient.isConnecting()
						&& !mGoogleApiClient.isConnected()) {
					mConnectionResult = null;
					mGoogleApiClient.connect();
				}
			}

		}
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

		public ErrorDialogFragment() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
					this.getActivity(), REQUEST_RESOLVE_ERR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((MainActivity) getActivity()).onDialogDismissed();
		}
	}

	/** End error Resolution **/

}
