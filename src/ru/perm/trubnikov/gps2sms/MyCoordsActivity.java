package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;



public class MyCoordsActivity extends Activity {
	
	DBHelper dbHelper;

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase("1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycoords);
		
		TextView mLog = (TextView)findViewById(R.id.textViewLog);
		dbHelper = new DBHelper(this);
     	SQLiteDatabase db = dbHelper.getWritableDatabase();
     	
     	String sqlQuery = "SELECT d.name, d.coord "
     	        + " FROM mycoords as d "
     	        + " ORDER BY d._id DESC";
     	
     	Cursor mCur = db.rawQuery(sqlQuery, null);
     	
     	int i=0;
     	if (mCur.moveToFirst()) {
     		
 	        int nameColIndex = mCur.getColumnIndex("name");
 	        int valColIndex = mCur.getColumnIndex("coord");
 	        
 	        do {
 	        	if (i>0)
 	        		mLog.append(", ");
 	        	mLog.append(mCur.getString(nameColIndex) +" " + mCur.getString(valColIndex));
 	        	i++;
 	        } while (mCur.moveToNext());
    	} 
     	
     	mCur.close();
		dbHelper.close();
		
		
		
		/*
		Button btn = (Button) findViewById(R.id.button1);
		btn.requestFocus();
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});*/

	}

	// ------------------------------------------------------------------------------------------

}
