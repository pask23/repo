package com.pask.thedarksider;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE = "com.pask.thedarksider.MESSAGE";
	TextView mTv;
	String labelTempR = "0";
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        
        mTv=(TextView) findViewById(R.id.magic_word);
        mTv.setText("0");
        
        
        
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

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
    /*public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        	View rootView = inflater.inflate(R.layout.activity_calculator, container, false);
            return rootView;
        }
    }*/
    
    
    public void onClick(View view){
    	String inDigit = ((Button)view).getText().toString();
   	 	Log.d("value", inDigit);
   	 	char pre = labelTempR.charAt(labelTempR.length()-1);
    	switch(view.getId()){    	
    		case R.id.btnCanc:
    			mTv.setText("0");
    			labelTempR = "0";
	    		break;
	    	case R.id.btn0:
	    	
	    	case R.id.btn1:
	    		
	    	case R.id.btn2:
	    	
	    	case R.id.btn3:
	    		
	    	case R.id.btn4:
	    	
	    	case R.id.btn5:
	    	
	    	case R.id.btn6:
	    	
	    	case R.id.btn7:
	    	
	    	case R.id.btn8:
	    	
	    	case R.id.btn9:
	    		 if(labelTempR.equals("0")){
					 labelTempR = inDigit;
				 }else{
					 labelTempR += inDigit;
				 }
				 mTv.setText(labelTempR);
				 break;
    	 
	    	case R.id.btnPlus:
	    		if(pre == '+' || pre== '-' || pre=='-' || pre=='/' ){
	    			labelTempR=labelTempR.substring(0,labelTempR.length()-1);
	    		}
	    		labelTempR += inDigit;
	    		mTv.setText(labelTempR);
	    		break;
	    	case R.id.btnSub:
	    		if(pre == '+' || pre== '-' || pre=='-' || pre=='/' ){
	    			labelTempR=labelTempR.substring(0,labelTempR.length()-1);
	    		}
    			labelTempR += inDigit;
	    		mTv.setText(labelTempR);
	    		
		    	break;
	    	case R.id.btnDiv:
	    		if(pre == '+' || pre== '-' || pre=='-' || pre=='/' ){
	    			labelTempR=labelTempR.substring(0,labelTempR.length()-1);
	    		}
    			labelTempR += inDigit;
	    		mTv.setText(labelTempR);
	    		
	    		break;
	    	case R.id.btnMolt:
	    		if(pre == '+' || pre== '-' || pre=='-' || pre=='/' ){
	    			labelTempR=labelTempR.substring(0,labelTempR.length()-1);
	    		}
    			labelTempR += inDigit;
	    		mTv.setText(labelTempR);
	    		
	    		break;
	    	case R.id.btnEqu:
	    		if(pre == '+' || pre== '-' || pre=='-' || pre=='/' ){
	    			labelTempR=labelTempR.substring(0,labelTempR.length()-1);
	    		}
	    		
	    		mTv.setText(Double.toString(compute()));
	    		actionOnResult();
	    		break;
	    	
    	}
    }
    
    private double compute(){
    	Calculable calc;
    	double result = 0.0d;
		try {
			calc = new ExpressionBuilder(labelTempR).build();
			result = calc.calculate();
		} catch (UnknownFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnparsableExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return result;
    }
    
    
    /*public void sendMessage(View view){
    	Intent intent;
    	EditText magicWord = (EditText) findViewById(R.id.magic_word);
    	String message = magicWord.getText().toString();
    	Log.d("v",message);
    	if(message.equals("100")){
    		intent = new Intent(this, DarkListActivity.class);
    	}
    	else{ 
    		intent = new Intent(this,DisplayMessageActivity.class);
    		intent.putExtra(EXTRA_MESSAGE, message);
    	}
    	startActivity(intent);    
    }*/
    
    private void actionOnResult(){
    	String message = (String) mTv.getText();
    	Log.d("v",message);
    	if(message.equals("9669.0")){
    		Intent intent = new Intent(this, DarkListActivity.class);
    		startActivity(intent); 
    	}
    	labelTempR = message;
    	   
    }

}
