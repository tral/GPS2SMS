package ru.perm.trubnikov.gps2sms;

import java.util.Locale;

import ru.perm.trubnikov.gps2sms.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

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
	Button enableGPSBtn ;
	CheckBox checkBox;
	public static EditText smsEdit;
	
	// Globals
	private String phoneNumber;
	private String coordsToSend;
	
    // Database
    DBHelper dbHelper;
	
    
	// Small util to show text messages by resource id
	protected void ShowToast(int txt, int lng) {
		Toast toast = Toast.makeText(MainActivity.this, txt, lng);
	    toast.setGravity(Gravity.TOP, 0, 0);
	    toast.show();
	}
	
	// Small util to show text messages
	protected void ShowToastT(String txt, int lng) {
		Toast toast = Toast.makeText(MainActivity.this, txt, lng);
	    toast.setGravity(Gravity.TOP, 0, 0);
	    toast.show();
	}
    
	protected void HideKeyboard() {
		
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        //	check if no view has focus:
        View v=this.getCurrentFocus();
        if(v!=null)
        	inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
	}
	
	
	protected void ShowKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(smsEdit, InputMethodManager.SHOW_IMPLICIT);
		
	}

    // Define the Handler that receives messages from the thread and update the progress
 	// SMS send thread. Result handling
     final Handler handler = new Handler() {
         public void handleMessage(Message msg) {

        	 String res_send = msg.getData().getString("res_send");
             //String res_deliver = msg.getData().getString("res_deliver");

        	 dismissDialog(SEND_SMS_DIALOG_ID);
        	 
        	 if (res_send.equalsIgnoreCase(getString(R.string.info_sms_sent))) {
        		HideKeyboard();
        		Intent intent = new Intent(MainActivity.this, AnotherMsgActivity.class);
     	     	startActivity(intent);
        	 } else {
            	 MainActivity.this.ShowToastT(res_send, Toast.LENGTH_SHORT);
        	 }
        	 
         }
     };  


	// Location events (we use GPS only)
	private LocationListener locListener = new LocationListener() {
		
		public void onLocationChanged(Location argLocation) {
			printLocation(argLocation, GPS_GOT_COORDINATS);
		}
	
		@Override
		public void onProviderDisabled(String arg0) {
			printLocation(null, GPS_PROVIDER_DISABLED);
		}
	
		@Override
		public void onProviderEnabled(String arg0) {
		}
	
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	};
	
	private void Pause_GPS_Scanning() {
		manager.removeUpdates(locListener);
		if (!checkBox.isChecked()) {
			sendBtn.setEnabled(true);
		}
	} 
	
	private void Resume_GPS_Scanning() {
		if (checkBox.isChecked()) {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
			sendBtn.setEnabled(false);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				printLocation(null, GPS_GETTING_COORDINATS);
			}
		}
	} 
	
	private void printLocation(Location loc, int state) {
		
		String accuracy;
		
		switch (state) {
		case GPS_PROVIDER_DISABLED :
			GPSstate.setText(R.string.gps_state_disabled);
			GPSstate.setTextColor(Color.RED);
			enableGPSBtn.setVisibility(View.VISIBLE);
			break;
		case GPS_GETTING_COORDINATS :
			GPSstate.setText(R.string.gps_state_in_progress);
			GPSstate.setTextColor(Color.YELLOW);
			enableGPSBtn.setVisibility(View.INVISIBLE);
			break;
		case GPS_PAUSE_SCANNING :
			GPSstate.setText("");
			enableGPSBtn.setVisibility(View.INVISIBLE);
			break;	
		case GPS_GOT_COORDINATS :
			if (loc != null) {

				coordsToSend = String.format(Locale.US , "%2.5f", loc.getLatitude()) + " " + String.format(Locale.US ,"%3.5f", loc.getLongitude());

				// Accuracy
				if (loc.getAccuracy() < 0.0001) {accuracy = "?"; }
					else if (loc.getAccuracy() > 99) {accuracy = "> 99";}
						else {accuracy = String.format("%2.0f", loc.getAccuracy());};
				
				GPSstate.setText("Координаты получены, точность: " + accuracy + " м. ");
				//+ "\t\nШирота: " + loc.getLatitude() + "Долгота: " + loc.getLongitude());
				GPSstate.setTextColor(Color.GREEN);
				sendBtn.setEnabled(true);
				enableGPSBtn.setVisibility(View.INVISIBLE);
				
			}
			else {
				GPSstate.setText(R.string.gps_state_unavialable);
				GPSstate.setTextColor(Color.RED);
				enableGPSBtn.setVisibility(View.VISIBLE);
			}
			break;
		}
	
	}
		
	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		//return true;
	
		menu.add(Menu.NONE, IDM_SETTINGS, Menu.NONE, R.string.menu_item_settings);
		menu.add(Menu.NONE, IDM_RULES, Menu.NONE, R.string.menu_item_rules);
		return(super.onCreateOptionsMenu(menu));
	}
		
		
	// Dialogs
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        
        case SEND_SMS_DIALOG_ID:
        	  mSMSProgressDialog = new ProgressDialog(MainActivity.this);
        	  //mCatProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	  mSMSProgressDialog.setCanceledOnTouchOutside(false);
        	  mSMSProgressDialog.setCancelable(false);
        	  mSMSProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	  mSMSProgressDialog.setMessage("Дождитесь окончания отправки SMS...");
        	  return mSMSProgressDialog;
        	  
        case RULES_DIALOG_ID:
        	
            LayoutInflater inflater_rules = getLayoutInflater();
            View layout_rules = inflater_rules.inflate(R.layout.rules_dialog, (ViewGroup)findViewById(R.id.rules_dialog_layout));
            
            AlertDialog.Builder builder_rules = new AlertDialog.Builder(this);
            builder_rules.setView(layout_rules);
            
            // Stored phone number
            //final EditText keyDlgEdit = (EditText) layout_phone.findViewById(R.id.phone_edit_text);
    		
            TextView rulesView = (TextView) layout_rules.findViewById(R.id.textView1);
            
            rulesView.setText(R.string.rules_str);
    		
            //builder_rules.setMessage("Порядок извещения других участников сервиса об экстренном событии");
            /*
            builder_rules.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                	
                }
            });*/
            
            builder_rules.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                	ShowKeyboard();
                    }
            });
            
            builder_rules.setCancelable(false);
            
            return builder_rules.create();
            
        case PHONE_DIALOG_ID:
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.phone_dialog, (ViewGroup)findViewById(R.id.phone_dialog_layout));
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            
            // Stored phone number
            final EditText keyDlgEdit = (EditText) layout.findViewById(R.id.phone_edit_text);
    		dbHelper = new DBHelper(this);
         	keyDlgEdit.setText(dbHelper.getPhone());
    		dbHelper.close();
    		
            builder.setMessage("Номер телефона для SMS");
            
            builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                	// update phone number
                	dbHelper = new DBHelper(MainActivity.this);
	        		SQLiteDatabase db = dbHelper.getWritableDatabase();
	        		ContentValues cv = new ContentValues();
	                cv.put("phone", keyDlgEdit.getText().toString());
	                db.update("phone", cv, "_id = ?", new String[] { "1" });
	                dbHelper.close();
	                phoneNumber = keyDlgEdit.getText().toString();
	                keyDlgEdit.selectAll(); // чтобы при повторном открытии номер был выделен
                }
            });
            
            builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    }
            });
            
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            // show keyboard automatically
            keyDlgEdit.selectAll();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return dialog;

        }
        return null;
    }
		
    // Menu
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case IDM_RULES:
            	HideKeyboard();
            	showDialog(RULES_DIALOG_ID);
                break;
            case IDM_SETTINGS:
            	showDialog(PHONE_DIALOG_ID);
                break;    
            default:
                return false;
        }
        
        return true;
    }
    
    
	@Override
	protected void onResume() {
		super.onResume();
		Resume_GPS_Scanning();
		//ShowKeyboard();
			
    //    }
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		Pause_GPS_Scanning();
//		 try {
	            //unregisterReceiver(sendBroadcastReceiver);
//	            unregisterReceiver(deliveryBroadcastReciever);
//	     } catch (Exception e) {
//	            e.printStackTrace();
//	     }
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    try {
//	        unregisterReceiver(sendBroadcastReceiver);
//	        unregisterReceiver(deliveryBroadcastReciever);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	protected void getContactInfo(Intent intent) {
		Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
		String contactId;
		String name;
		String ph="";
		   while (cursor.moveToNext()) 
		   {           
		       contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		       name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 

		       String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

		       if ( hasPhone.equalsIgnoreCase("1"))
		           hasPhone = "true";
		       else
		           hasPhone = "false" ;

		       if (Boolean.parseBoolean(hasPhone)) 
		       {
		        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
		        while (phones.moveToNext()) 
		        {
		          ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		        }
		        phones.close();
		       }
		      
		  }  //while (cursor.moveToNext())        
		   cursor.close();
		   
		   TextView textView2 = (TextView)findViewById(R.id.textView2);
	       textView2.setText(ph);
		   
	}
	
	public void showSelectedNumber(int type, String number, String name) {
	    Toast.makeText(this, type + ": " + number+ ":" + name, Toast.LENGTH_LONG).show();      
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	  super.onActivityResult(reqCode, resultCode, data);

	  switch (reqCode) {
	    case (1001) :
	    	String number = "";
	        String name = "";
	        int type = 0;
	        if (data != null) {
	            Uri uri = data.getData();

	            if (uri != null) {
	                Cursor c = null;
	                //Cursor c2  = null;
	                try {/*
	                	c2 =  managedQuery(uri, null, null, null, null);
	                	if (c2 != null && c2.moveToFirst()) {
	                		name = c2.getString(c2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	                	}*/
	                	
	                    c = getContentResolver().query(uri, new String[]{ 
	                                ContactsContract.CommonDataKinds.Phone.NUMBER,  
	                                ContactsContract.CommonDataKinds.Phone.TYPE,
	                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
	                            null, null, null);

	                    if (c != null && c.moveToFirst()) {
	                        number = c.getString(0);
	                        type = c.getInt(1);
	                        name = c.getString(2);
	                        showSelectedNumber(type, number, name);
	                    }
	                } finally {
	                    if (c != null) { c.close(); }
	                    //if (c2 != null) { c2.close(); }
	                }
	            }
	        }
	    	
	    
	      break;
	  }
	}
	

	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Stored phone number
		dbHelper = new DBHelper(this);
     	phoneNumber = dbHelper.getPhone();
		dbHelper.close();
        
        // Checkbox
        checkBox = (CheckBox)findViewById(R.id.checkBox1);
        checkBox.setChecked(true);
    	checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        if ( isChecked ) {
    	        	Resume_GPS_Scanning();
    	        } else {
    	        	Pause_GPS_Scanning();
    	        	// только здесь, т.к. скрыть текст нужно только 
    	        	// при отключении флага, а не при паузе активности к примеру
    	    		printLocation(null, GPS_PAUSE_SCANNING); 
    	        }
    	    }
    	});
    	
        // GPS init
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);		
      
        //Prepare SMS Listeners, prepare Send button 
        smsEdit = (EditText)findViewById(R.id.editText2);
        smsEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        sendBtn = (Button)findViewById(R.id.button1);
        sendBtn.setEnabled(false);
        
        sendBtn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
                if (smsEdit.getText().toString().equals("")
                        | smsEdit.getText().toString().equals(null)) {
                    MainActivity.this.ShowToast(R.string.error_sms_empty, Toast.LENGTH_LONG);
                } else {
                	
                	showDialog(SEND_SMS_DIALOG_ID);
                	
                	//sendSMS(phoneNumber, smsEdit.getText().toString());
                	String message = smsEdit.getText().toString();
    	    		if (checkBox.isChecked()) {
    	    			message = message + " " + coordsToSend;
                	}

					// Запускаем новый поток для отправки SMS
					mThreadSendSMS = new ThreadSendSMS(handler, getApplicationContext());
					mThreadSendSMS.setMsg(message);
					mThreadSendSMS.setPhone(phoneNumber);
					mThreadSendSMS.setState(ThreadSendSMS.STATE_RUNNING);
					mThreadSendSMS.start();
                }
            }
        });
     
        // Enable GPS button
        enableGPSBtn = (Button)findViewById(R.id.button3);
        enableGPSBtn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
               	if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           			startActivity(new Intent(
           		        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
           		}
            }
        });
        
    	// GPS-state TextView
        GPSstate = (TextView)findViewById(R.id.textView1);
        GPSstate.setTextColor(Color.GREEN);
        enableGPSBtn.setVisibility(View.INVISIBLE);
        
        // Show rules immediately after launch?
        dbHelper = new DBHelper(this);
     	if (dbHelper.needToSplashRules()) {
     		SQLiteDatabase db = dbHelper.getWritableDatabase();
    		ContentValues cv = new ContentValues();
            cv.put("rules", 0);
            db.update("rules", cv, "_id = ?", new String[] { "1" });
            HideKeyboard();
     		showDialog(RULES_DIALOG_ID);
     	}
		dbHelper.close();

		//DEL
		Button btn2 = (Button)findViewById(R.id.button2);
		btn2.setOnClickListener(new OnClickListener() {

	        	@Override
	            public void onClick(View v) {
	        		
	        		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	        		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
	        		startActivityForResult(intent, 1001);
	        		
	        		//Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	        		//startActivityForResult(intent, 1001);
	            }
	        });

		
        
    }
    
	// ------------------------------------------------------------------------------------------
    
}
