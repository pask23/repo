package com.pask.test.asynctask;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {
	  private ImageView mImageView;
	  private EditText mUrl;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        mImageView = (ImageView) findViewById(R.id.result);
	        mUrl = (EditText) findViewById(R.id.url);
	    }


	    /*@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.my, menu);
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
	    }*/

	    public void onClick(View view)
	    {
	    	Log.d("","onClick");
	    	if(mUrl.getText().toString()!=null){
	    		Log.d("",mUrl.getText().toString());
	    		new DownloadImageTask().execute(mUrl.getText().toString());
	    		  
	    	}
	    		  }

	    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	        private ProgressDialog mProgressDialog;

	        protected void onPreExecute(){
	            mProgressDialog = new ProgressDialog(MainActivity.this);
	            mProgressDialog.setMessage("Processing...");
	            mProgressDialog.show();
	        }
	        /**
	         * The system calls this to perform work in a worker thread and
	         * delivers it the parameters given to AsyncTask.execute()
	         */
	        protected Bitmap doInBackground(String... urls) {
	            return loadImageFromNetwork(urls[0]);
	        }

	        /**
	         * The system calls this to perform work in the UI thread and delivers
	         * the result from doInBackground()
	         */
	        protected void onPostExecute(Bitmap result) {
	            mProgressDialog.dismiss();
	            mImageView.setImageBitmap(result);
	        }
	    }

	    private Bitmap loadImageFromNetwork(String url){
	        try
	        {
	            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
	            return bitmap;
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }


	}