package ru.perm.trubnikov.gps2sms;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;



public class UserSettingActivity extends PreferenceActivity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	ShowBackButton();
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
    
    @TargetApi(11)
    public void ShowBackButton() {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    		// call something for API Level 11+
    		getActionBar().setDisplayHomeAsUpEnabled(true);
    	}
    }
    
}