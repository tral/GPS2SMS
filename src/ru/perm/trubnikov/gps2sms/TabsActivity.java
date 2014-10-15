package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TabHost;

public class TabsActivity extends TabActivity {

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase("1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);
		
		ShowBackButton();
		
		// получаем TabHost
        TabHost tabHost = getTabHost();
        
        // инициализация была выполнена в getTabHost
        // метод setup вызывать не нужно
        
        TabHost.TabSpec tabSpec;
        
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.tab_mycoords));
        tabSpec.setContent(new Intent(this, MyCoordsActivity.class));
        tabHost.addTab(tabSpec);
        
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.tab_mysms));
        tabSpec.setContent(new Intent(this, MySMSActivity.class));
        tabHost.addTab(tabSpec);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
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
	
	// ------------------------------------------------------------------------------------------

}
