package ru.perm.trubnikov.gps2sms;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
 
public class UserSettingActivity extends PreferenceActivity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
        	  // call something for API Level 11+
        	ShowBackButton();
        }
        
        addPreferencesFromResource(R.xml.settings);
 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) 
    	   {        
    	      case android.R.id.home:            
    	         Intent intent = new Intent(this, MainActivity.class);            
    	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
    	         startActivity(intent);            
    	         return true;        
    	      default:            
    	         return super.onOptionsItemSelected(item);    
    	   }
    }
    
    @SuppressLint("NewApi")
    public void ShowBackButton() {
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
}