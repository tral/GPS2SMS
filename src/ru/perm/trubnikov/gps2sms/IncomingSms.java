package ru.perm.trubnikov.gps2sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSms extends BroadcastReceiver {
    
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
     
    public void onReceive(Context context, Intent intent) {
     
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    	if (sharedPrefs.getBoolean("prefRegexpSMS", true)) {
    	
	        // Retrieves a map of extended data from the intent.
	        final Bundle bundle = intent.getExtras();
	 
	        try {
	             
	            if (bundle != null) {
	                 
	                final Object[] pdusObj = (Object[]) bundle.get("pdus");
	                 
	                for (int i = 0; i < pdusObj.length; i++) {
	                     
	                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
	                    //String phoneNumber = currentMessage.getDisplayOriginatingAddress();
	                    //String senderNum = phoneNumber;
	                    String message = currentMessage.getDisplayMessageBody();
	                    //Log.d("gps", "senderNum: "+ senderNum + "; message: " + message);
	                     
	                    Pattern p = Pattern.compile("(\\-?\\d+\\.(\\d+)?),\\s*(\\-?\\d+\\.(\\d+)?)");
	 			        Matcher m = p.matcher(message);
	 			        
		 			    if(m.find()) {
		 			    	Intent intent_openmap = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+ m.group(0)));
		 			    	intent_openmap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 			    	context.startActivity(intent_openmap);
		 			    }
		 			     
	                   // Show Alert
	                   //Toast toast = Toast.makeText(context, clip, Toast.LENGTH_LONG);
	                   //toast.show();
	
	                } // end for loop
	              } // bundle is null
	 
	        } catch (Exception e) {
	            Log.d("gps", "Exception smsReceiver" +e);
	             
	        }
        
    	}
        
    }    
}