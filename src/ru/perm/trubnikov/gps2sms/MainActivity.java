package ru.perm.trubnikov.gps2sms;

import java.util.Locale;

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
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

	// Menu
	public static final int IDM_SETTINGS = 101;
	public static final int IDM_RATE = 105;
	
	// Dialogs
    private static final int SEND_SMS_DIALOG_ID = 0;
    private final static int PHONE_DIALOG_ID = 1;
	ProgressDialog mSMSProgressDialog;

	// My GPS states
	public static final int GPS_PROVIDER_DISABLED = 1;
	public static final int GPS_GETTING_COORDINATES = 2;
	public static final int GPS_GOT_COORDINATES = 3;
	public static final int GPS_PROVIDER_UNAVIALABLE = 4;
	public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
	public static final int GPS_PAUSE_SCANNING = 6;
	
	// Send SMS Via
	public static final int SMS_SEND_VIA_SMS = 1;
	public static final int SMS_SEND_VIA_NAVITEL = 2;
	
	public static final int TOGGLE_ICON_HANGOUTS = 1;
	public static final int TOGGLE_ICON_NAVITEL = 2;
	
	// Location manager
	private LocationManager manager;
	
	// SMS thread
    ThreadSendSMS mThreadSendSMS;
	
	// Views
	TextView GPSstate;
	//Button sendBtn;
	//ImageButton navitelBtn;
	//ImageButton shareBtn;
	ImageButton sendViaToggleBtn;
	ImageButton sendpbtn;
	ImageButton send1btn;
	//ImageButton send2btn;
	//ImageButton send3btn;
	Button cont1;
	//Button cont2;
	//Button cont3;
	Button enableGPSBtn ;
	EditText plainPh;
	//Button btnSelContact;
	Menu mMenu;
	
	// Globals
	private String coordsToSend;
	private String coordsToShare;
	private String coordsToNavitel;
	private boolean enableShareBtnFlag = false;
	private int toggleButtonIcon;
	private String phoneToSendSMS;
	private int tmpSlotId;

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
    
	/*
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
	 */
	
    // Define the Handler that receives messages from the thread and update the progress
 	// SMS send thread. Result handling
     final Handler handler = new Handler() {
         public void handleMessage(Message msg) {

        	 String res_send = msg.getData().getString("res_send");
             //String res_deliver = msg.getData().getString("res_deliver");

        	 dismissDialog(SEND_SMS_DIALOG_ID);
        	 
        	 if (res_send.equalsIgnoreCase(getString(R.string.info_sms_sent))) {
        		//HideKeyboard();
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
			printLocation(argLocation, GPS_GOT_COORDINATES);
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
	} 
	
	private void Resume_GPS_Scanning() {
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
		//sendBtn.setEnabled(false);
		setImageButtonEnabled(getApplicationContext(), false, sendpbtn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		setImageButtonEnabled(getApplicationContext(), false, send1btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		//setImageButtonEnabled(getApplicationContext(), false, send2btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		//setImageButtonEnabled(getApplicationContext(), false, send3btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		setActionBarShareButtonEnabled(false);

		//setImageButtonEnabled(getApplicationContext(), false, shareBtn, R.drawable.share);
		//setImageButtonEnabled(getApplicationContext(), false, navitelBtn, R.drawable.navitel);
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			printLocation(null, GPS_GETTING_COORDINATES);
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
		case GPS_GETTING_COORDINATES :
			GPSstate.setText(R.string.gps_state_in_progress);
			GPSstate.setTextColor(Color.YELLOW);
			enableGPSBtn.setVisibility(View.INVISIBLE);
			break;
		case GPS_PAUSE_SCANNING :
			GPSstate.setText("");
			enableGPSBtn.setVisibility(View.INVISIBLE);
			break;	
		case GPS_GOT_COORDINATES :
			if (loc != null) {

				// Accuracy
				if (loc.getAccuracy() < 0.0001) {accuracy = "?"; }
					else if (loc.getAccuracy() > 99) {accuracy = "> 99";}
						else {accuracy = String.format(Locale.US, "%2.0f", loc.getAccuracy());};

				String la = String.format(Locale.US , "%2.7f", loc.getLatitude());
				String lo = String.format(Locale.US ,"%3.7f", loc.getLongitude());
				
				coordsToSend = la + "," + lo;
				coordsToNavitel = "<NavitelLoc>" + la + " " + lo + "<N>";
				coordsToShare = getString(R.string.info_latitude) + " " + la 
						+ "\t\n" + getString(R.string.info_longitude) + " " + lo
						+ "\t\n" + getString(R.string.info_accuracy) + " " + accuracy + " " +getString(R.string.info_print2) 
						+ "\t\n\t\n" + "https://www.google.com/maps/place/" + la + "," + lo; 
				
				GPSstate.setText(getString(R.string.info_print1) + " " + accuracy + " " + getString(R.string.info_print2)
						+ "\t\n" + getString(R.string.info_latitude) + " " + String.format(Locale.US , "%2.7f", loc.getLatitude()) 
						+ "\t\n" + getString(R.string.info_longitude) + " " + String.format(Locale.US ,"%3.7f", loc.getLongitude()));
				GPSstate.setTextColor(Color.GREEN);
				//sendBtn.setEnabled(true);
				setActionBarShareButtonEnabled(true);
				setImageButtonEnabled(getApplicationContext(), true, sendpbtn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
				setImageButtonEnabled(getApplicationContext(), true, send1btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
				//setImageButtonEnabled(getApplicationContext(), true, send2btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
				//setImageButtonEnabled(getApplicationContext(), true, send3btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
				//setImageButtonEnabled(getApplicationContext(), true, shareBtn, R.drawable.share);
				//setImageButtonEnabled(getApplicationContext(), true, navitelBtn, R.drawable.navitel);
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
		
		mMenu = menu;
		
		menu.add(Menu.NONE, IDM_SETTINGS, Menu.NONE, R.string.menu_item_settings);
		menu.add(Menu.NONE, IDM_RATE, Menu.NONE, R.string.menu_item_rate);
		
		 // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	
	    // Set share button state
	    menu.findItem(R.id.action_share).setEnabled(enableShareBtnFlag);
	    setMenuItemEnabled(getApplicationContext(), enableShareBtnFlag, menu.findItem(R.id.action_share), R.drawable.ic_action_share);
	    
	    // Toggle Button
	    Drawable icon;
 	    if (toggleButtonIcon == TOGGLE_ICON_HANGOUTS) {
 		    icon = getResources().getDrawable(R.drawable.hangouts);
 	    } else {
 		    icon = getResources().getDrawable(R.drawable.navitel);
 	    }
 	   
 	    menu.findItem(R.id.action_navitel).setIcon(icon);

 	    //
		
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
        	  mSMSProgressDialog.setMessage(getString(R.string.info_please_wait) + " " + phoneToSendSMS);
        	  return mSMSProgressDialog;
        	  
        case PHONE_DIALOG_ID:
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.phone_dialog, (ViewGroup)findViewById(R.id.phone_dialog_layout));
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            
            // Stored msg
            final EditText keyDlgEdit = (EditText) layout.findViewById(R.id.msg_edit_text);
    		dbHelper = new DBHelper(this);
         	keyDlgEdit.setText(dbHelper.getSmsMsg());
    		dbHelper.close();
    		
            builder.setMessage(getString(R.string.info_sms_txt));
            
            builder.setPositiveButton(getString(R.string.save_btn_txt), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                	// update 
                	dbHelper = new DBHelper(MainActivity.this);
	        		SQLiteDatabase db = dbHelper.getWritableDatabase();
	        		ContentValues cv = new ContentValues();
	                cv.put("msg", keyDlgEdit.getText().toString());
	                db.update("msg", cv, "_id = ?", new String[] { "1" });
	                dbHelper.close();
	                keyDlgEdit.selectAll(); // чтобы при повторном открытии текст был выделен
                }
            });
            
            builder.setNegativeButton(getString(R.string.cancel_btn_txt), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	keyDlgEdit.selectAll(); // чтобы при повторном открытии текст был выделен
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
            case IDM_SETTINGS:
            	showDialog(PHONE_DIALOG_ID);
                break;    
            case IDM_RATE:
            	Intent int_rate = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
            	int_rate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		getApplicationContext().startActivity(int_rate);
        		break;
            case R.id.action_share:
            	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
        	    sharingIntent.setType("text/plain");
        	    String shareBody = coordsToShare;
        	    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_topic));
        	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        	    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
            	break;
            case R.id.action_navitel:
            	refreshSendViaToggleButton(true);
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
	}
		
	
	@Override
	protected void onPause() {
		super.onPause();
		Pause_GPS_Scanning();
	}
	
	
	public void showSelectedNumber(String number, String name) {
		if (number.equalsIgnoreCase("") && name.equalsIgnoreCase("")) {
			//btnSelContact.setText(getString(R.string.select_contact_btn_txt));
			if (tmpSlotId == 1) {
				cont1.setText(getString(R.string.select_contact_btn_txt));
			}
			/*if (tmpSlotId == 2) {
				cont2.setText(getString(R.string.select_contact_btn_txt));
			}
			if (tmpSlotId == 3) {
				cont3.setText(getString(R.string.select_contact_btn_txt));
			}*/
		} else {
			//btnSelContact.setText(name + " (" + number + ")");
			//btnSelContact.setText(name);
			if (tmpSlotId == 1) {
				cont1.setText(name);
			}
			/*if (tmpSlotId == 2) {
				cont2.setText(name);
			}
			if (tmpSlotId == 3) {
				cont3.setText(name);
			}*/
		}
		
	}
	
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	  super.onActivityResult(reqCode, resultCode, data);

	  switch (reqCode) {
	    case (1001) :
	    	String number = "";
	        String name = "";
	        //int type = 0;
	        if (data != null) {
	            Uri uri = data.getData();

	            if (uri != null) {
	                Cursor c = null;
	                try {
	                	
	                    c = getContentResolver().query(uri, new String[]{ 
	                                ContactsContract.CommonDataKinds.Phone.NUMBER,  
	                                ContactsContract.CommonDataKinds.Phone.TYPE,
	                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
	                            null, null, null);

	                    if (c != null && c.moveToFirst()) {
	                        number = c.getString(0);
	                        number = number.replace("-", "").replace(" ", "").replace("(", "").replace(")", "");
	                        //type = c.getInt(1);
	                        name = c.getString(2);
	                        showSelectedNumber(number, name);

	                    	// update 
	                    	dbHelper = new DBHelper(MainActivity.this);
	                    	dbHelper.setSlot(tmpSlotId, name, number);
	    	                dbHelper.close();
	                        
	                    }
	                } finally {
	                    if (c != null) { c.close(); }
	                }
	            }
	        }
	    
	      break;
	  }
	}

	/**
	 * Sets the image button to the given state and grays-out the icon.
	 * 
	 * @param enabled The state of the button
	 * @param item The button item to modify
	 * @param iconResId The button's icon ID
	 */
	public static void setImageButtonEnabled(Context ctxt, boolean enabled, 
	        ImageButton item, int iconResId) {

	    item.setEnabled(enabled);
	    Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
	    Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
	    item.setImageDrawable(icon);
	}
	
	public static void setMenuItemEnabled(Context ctxt, boolean enabled, 
			MenuItem item, int iconResId) {

	    item.setEnabled(enabled);
	    Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
	    Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
	    item.setIcon(icon);
	}
	
	/**
	 * Mutates and applies a filter that converts the given drawable to a Gray
	 * image. This method may be used to simulate the color of disable icons in
	 * Honeycomb's ActionBar.
	 * 
	 * @return a mutated version of the given drawable with a color filter applied.
	 */
	public static Drawable convertDrawableToGrayScale(Drawable drawable) {
	    if (drawable == null) 
	        return null;

	    Drawable res = drawable.mutate();
	    res.setColorFilter(Color.GRAY, Mode.SRC_IN);
	    return res;
	}

	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Plain phone number
        plainPh = (EditText)findViewById(R.id.editText1);
        plainPh.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		v.clearFocus();
        		((EditText)v).selectAll();
            }
        });
        
        // Select contact
        cont1 = (Button)findViewById(R.id.cont1);
        //cont2 = (Button)findViewById(R.id.cont2);
        //cont3 = (Button)findViewById(R.id.cont3);
        
        cont1.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		tmpSlotId = 1;
        		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        		startActivityForResult(intent, 1001);
            }
        });
        /*cont2.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		tmpSlotId = 2;
        		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        		startActivityForResult(intent, 1001);
            }
        });
        cont3.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		tmpSlotId = 3;
        		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        		startActivityForResult(intent, 1001);
            }
        });*/
        
        // Stored phone number & name -> to button
		dbHelper = new DBHelper(this);
		plainPh.setText(dbHelper.getSlot(0, "phone"));
		tmpSlotId = 1;
     	showSelectedNumber(dbHelper.getSlot(tmpSlotId, "phone"), dbHelper.getSlot(tmpSlotId, "name"));
     	tmpSlotId = 2;
     	showSelectedNumber(dbHelper.getSlot(tmpSlotId, "phone"), dbHelper.getSlot(tmpSlotId, "name"));
     	tmpSlotId = 3;
     	showSelectedNumber(dbHelper.getSlot(tmpSlotId, "phone"), dbHelper.getSlot(tmpSlotId, "name"));
		dbHelper.close();
		
		
        // GPS init
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);		
        
        // Enable GPS button
        enableGPSBtn = (Button)findViewById(R.id.button3);
        enableGPSBtn.setVisibility(View.INVISIBLE);
        
        enableGPSBtn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
               	if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
           		}
            }
        	
        });
        
        
        // Send buttons
        sendpbtn = (ImageButton)findViewById(R.id.send_plain);
        send1btn = (ImageButton)findViewById(R.id.send1);
        //send2btn = (ImageButton)findViewById(R.id.send2);
        //send3btn = (ImageButton)findViewById(R.id.send3);
        setImageButtonEnabled(getApplicationContext(), false, sendpbtn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		setImageButtonEnabled(getApplicationContext(), false, send1btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		//setImageButtonEnabled(getApplicationContext(), false, send2btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		//setImageButtonEnabled(getApplicationContext(), false, send3btn, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? R.drawable.hangouts : R.drawable.navitel);
		
		sendpbtn.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		sendSMS((getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? coordsToSend : coordsToNavitel, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? true : false, 0);
            }
        });
		send1btn.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		sendSMS((getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? coordsToSend : coordsToNavitel, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? true : false, 1);
            }
        });
		/*send2btn.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		sendSMS((getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? coordsToSend : coordsToNavitel, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? true : false, 2);
            }
        });
		send3btn.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		sendSMS((getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? coordsToSend : coordsToNavitel, (getIntDbParam("sendvia") == SMS_SEND_VIA_SMS) ? true : false, 3);
            }
        });*/

		
        // send Via SMS or Navitel Toggle Button 
        /*sendViaToggleBtn = (ImageButton)findViewById(R.id.sendViaToggleButton);
        refreshSendViaToggleButton(false);
        
        sendViaToggleBtn.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
        		refreshSendViaToggleButton(true); 
            }
        });
        
        sendViaToggleBtn.setOnLongClickListener(new OnLongClickListener() { 
            @Override
            public boolean onLongClick(View v) {
            	MainActivity.this.ShowToast(R.string.toggle_sms_navitel_info, Toast.LENGTH_LONG);
                return true;
            }
        });*/
        
       
    	// GPS-state TextView init
        GPSstate = (TextView)findViewById(R.id.textView1);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	GPSstate.setTextColor(Color.YELLOW);
        } else {
        	GPSstate.setTextColor(Color.RED);
        }
        
    }
    
	// ------------------------------------------------------------------------------------------
    
    protected void sendSMS(String lCoords, boolean addText, int Receiver) {
    	
    	dbHelper = new DBHelper(MainActivity.this);
    	String smsMsg = lCoords;
    	if (addText) {
    		smsMsg = dbHelper.getSmsMsg() + " " +smsMsg;
    	}
    	
    	if (Receiver == 0) {
    		String plph = plainPh.getText().toString().replace("-", "").replace(" ", "").replace("(", "").replace(")", "");
    		dbHelper.setSlot(Receiver, plph, plph);
    	}
    	
    	phoneToSendSMS = dbHelper.getSlot(Receiver, "phone");
		dbHelper.close();

		if (phoneToSendSMS.equalsIgnoreCase("")) {
			if (Receiver == 0)
				MainActivity.this.ShowToast(R.string.error_no_phone_number, Toast.LENGTH_LONG);
			else
				MainActivity.this.ShowToast(R.string.error_contact_is_not_selected, Toast.LENGTH_LONG);
		} else {
        	showDialog(SEND_SMS_DIALOG_ID);

			// Запускаем новый поток для отправки SMS
			mThreadSendSMS = new ThreadSendSMS(handler, getApplicationContext());
			mThreadSendSMS.setMsg(smsMsg);
			mThreadSendSMS.setPhone(phoneToSendSMS);
			mThreadSendSMS.setState(ThreadSendSMS.STATE_RUNNING);
			mThreadSendSMS.start();
		}
        
    }

    
    private long getIntDbParam(String param) {
    	dbHelper = new DBHelper(MainActivity.this);
        long val = dbHelper.getSettingsParamInt(param);
        dbHelper.close();
        return val;
    }
    
    
    private void setIntDbParam(String param, long val) {
    	dbHelper = new DBHelper(MainActivity.this);
        dbHelper.setSettingsParamInt(param, val);
        dbHelper.close();
    }
    
    
    private void refreshSendViaToggleButton(boolean toggle) {
    	
    	//Drawable navitelIconT = getResources().getDrawable(R.drawable.navitel);
 	    //Drawable hangoutsIconT = getResources().getDrawable(R.drawable.hangouts);
 	    Drawable navitelIcon = getResources().getDrawable(R.drawable.navitel);
	    Drawable hangoutsIcon = getResources().getDrawable(R.drawable.hangouts);
 	    
 	    if (!sendpbtn.isEnabled()) {
 	    	navitelIcon = convertDrawableToGrayScale(navitelIcon);
 	    	hangoutsIcon = convertDrawableToGrayScale(hangoutsIcon);
 	    } 
 	    
        long sendvia = getIntDbParam("sendvia");

 		if (sendvia == SMS_SEND_VIA_SMS) {
 			if (toggle) {
 				setIntDbParam("sendvia", SMS_SEND_VIA_NAVITEL);
 				//sendViaToggleBtn.setImageDrawable(hangoutsIconT);
 				toggleButtonIcon = TOGGLE_ICON_HANGOUTS;
 				sendpbtn.setImageDrawable(navitelIcon);
 				send1btn.setImageDrawable(navitelIcon);
 				//send2btn.setImageDrawable(navitelIcon);
 				//send3btn.setImageDrawable(navitelIcon);
 				
 			} else {
 				//sendViaToggleBtn.setImageDrawable(navitelIconT);
 				toggleButtonIcon = TOGGLE_ICON_NAVITEL;
 				sendpbtn.setImageDrawable(hangoutsIcon);
 				send1btn.setImageDrawable(hangoutsIcon);
 				//send2btn.setImageDrawable(hangoutsIcon);
 				//send3btn.setImageDrawable(hangoutsIcon);
 			}
 		}
 		
 		if (sendvia == SMS_SEND_VIA_NAVITEL) {
 			if (toggle) {
 				setIntDbParam("sendvia", SMS_SEND_VIA_SMS);
 				//sendViaToggleBtn.setImageDrawable(navitelIconT);
 				toggleButtonIcon = TOGGLE_ICON_NAVITEL;
 				sendpbtn.setImageDrawable(hangoutsIcon);
 				send1btn.setImageDrawable(hangoutsIcon);
 				//send2btn.setImageDrawable(hangoutsIcon);
 				//send3btn.setImageDrawable(hangoutsIcon);
 				
 			} else {
 				//sendViaToggleBtn.setImageDrawable(hangoutsIconT);
 				toggleButtonIcon = TOGGLE_ICON_HANGOUTS;
 				sendpbtn.setImageDrawable(navitelIcon);
 				send1btn.setImageDrawable(navitelIcon);
 				//send2btn.setImageDrawable(navitelIcon);
 				//send3btn.setImageDrawable(navitelIcon);
 			}
 		}
 		
 		setActionBarToggleBtnIcon();
    	
    }

    private void setActionBarToggleBtnIcon() {
    	
		/*
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			invalidateOptionsMenu();			
		} else {
			supportInvalidateOptionsMenu();
		}*/
		
		 if (mMenu != null) {
		       MenuItem item = mMenu.findItem(R.id.action_navitel);
		       if (item != null) {

		    	   Drawable icon;
		    	   if (toggleButtonIcon == TOGGLE_ICON_HANGOUTS) {
		    		   icon = getResources().getDrawable(R.drawable.hangouts);
		    	   } else {
		    		   icon = getResources().getDrawable(R.drawable.navitel);
		    	   }
		    	   
		    	   item.setIcon(icon);

		           ActivityCompat.invalidateOptionsMenu(this);
		        }
		    }

		 //Log.d("gps", "test");

	}
    
    
	private void setActionBarShareButtonEnabled(boolean state) {

		enableShareBtnFlag = state;
		
		/*
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			invalidateOptionsMenu();			
		} else {
			supportInvalidateOptionsMenu();
		}*/
		
		 if (mMenu != null) {
		       MenuItem item = mMenu.findItem(R.id.action_share);
		       if (item != null) {
		    	    item.setEnabled(enableShareBtnFlag);
				    setMenuItemEnabled(getApplicationContext(), enableShareBtnFlag, item, R.drawable.ic_action_share);
		            ActivityCompat.invalidateOptionsMenu(this);
		        }
		    }
	}
    

    
    
}
