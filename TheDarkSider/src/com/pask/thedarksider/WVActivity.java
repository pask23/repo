package com.pask.thedarksider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WVActivity extends ActionBarActivity{

	private WebView myWebView;
	private String mimeType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wv);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new MyBrowser());
		
		WebSettings setting = myWebView.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setLoadsImagesAutomatically(true);
		myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		myWebView.loadUrl("http://www.xnxx.com");
	}
	
	//device back button to navigate backward
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		//check if the key event was the back button and if there is history
		if((keyCode==KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()){
			myWebView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wv, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_wv, container,
					false);
			return rootView;
		}
	}
	
	private class MyBrowser extends WebViewClient {
		   @Override
		   public boolean shouldOverrideUrlLoading(WebView view, String url) {
			  Log.d("url", url);
			  ConnectWebPageTask task = new ConnectWebPageTask();
			  String rmimtype = null;
			try {
				rmimtype = task.execute(url).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			  //while(task.getStatus() != AsyncTask.Status.FINISHED){}
			  if(rmimtype.equals("video/*") || rmimtype.equals("video/avi") || rmimtype.equals("video/mp4")){ 
				  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				  intent.setDataAndType(Uri.parse(url), rmimtype);
				  startActivity(intent);
			  }
			  else			  
				  view.loadUrl(url);
			  mimeType=null;
		      return true;
		   }
	}
	
	private class ConnectWebPageTask extends AsyncTask<String, Void, String>{
		@Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return getContenType(urls[0]);
            } catch (Exception e) {
                return null;
            }
        }
		
		// onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mimeType=result;
       }
	}

	private String getContenType(String string_url){
		String mimeType = null;
		try
		{
			URL url = new URL(string_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(1000);
			conn.setConnectTimeout(1500);
			conn.setRequestMethod("GET");
			conn.connect();
			mimeType = conn.getContentType();
			if(mimeType == null)
				mimeType = URLConnection.guessContentTypeFromName(string_url);
		}catch(Exception e){
			
		}
		Log.i("mime type", mimeType);
		return mimeType;
	}
	

}
