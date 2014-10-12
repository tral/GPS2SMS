package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AnotherMsgActivity extends Activity {


	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
      	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

 		if (sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase("2")) {
 			setTheme(R.style.ThemeLight); 			
 		} else {
 			setTheme(R.style.AppTheme);
 		}
    	
        setContentView(R.layout.activity_another_msg);
        
        Button btn = (Button)findViewById(R.id.button1);
        btn.requestFocus();
        btn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
        		finish();
            }
        });
       
    }
    
	// ------------------------------------------------------------------------------------------
    
}
