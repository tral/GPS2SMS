package ru.perm.trubnikov.gps2sms;

import ru.perm.trubnikov.gps2sms.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AnotherMsgActivity extends Activity {

	// Menu
	public static final int IDM_SETTINGS = 101;
	public static final int IDM_RULES = 102;
	
	// Dialogs
    private static final int SEND_SMS_DIALOG_ID = 0;
    private final static int PHONE_DIALOG_ID = 1;
    private final static int RULES_DIALOG_ID = 2;
	ProgressDialog mSMSProgressDialog;

	// My GPS states
	public static final int GPS_PROVIDER_DISABLED = 1;
	public static final int GPS_GETTING_COORDINATS = 2;
	public static final int GPS_GOT_COORDINATS = 3;
	public static final int GPS_PROVIDER_UNAVIALABLE = 4;
	public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
	public static final int GPS_PAUSE_SCANNING = 6;
	
	// Location manager
	private LocationManager manager;
	
	// SMS thread
    ThreadSendSMS mThreadSendSMS;
	
	// Views
	TextView GPSstate;
	Button sendBtn;
	CheckBox checkBox;
	EditText smsEdit;
	
	// Globals
	private String phoneNumber;
	private String coordsToSend;
	
  


		
	
    
    
	@Override
	protected void onResume() {
		super.onResume();
		//Resume_GPS_Scanning();
		
		//smsEdit = (EditText)findViewById(R.id.editText2);
		/*
		MainActivity.smsEdit.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(smsEdit.getWindowToken(),0); 
		        }   
		    }, 100);
		*/
		
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		//Pause_GPS_Scanning();

	}
	

	


	
	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_msg);
        
        
        
        Button btn = (Button)findViewById(R.id.button1);
        btn.requestFocus();
        btn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
        		MainActivity.smsEdit.setText("");
        		finish();
            }
        });
       
    }
    
	// ------------------------------------------------------------------------------------------
    
}
