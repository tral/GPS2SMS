package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MySMSActivity extends Activity {

	DBHelper dbHelper;
	private int[] ids;

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase(
				"1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mysms);

		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutSMS);

		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		Resources r = getApplicationContext().getResources();

		// число пикселей для высоты кнопок (относительно dp)
		int pixels_b = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());

		// число пикселей для margin'ов (относительно dp)
		int pixels_m = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

		try {

			Cursor cursor = getContentResolver()
					.query(Uri.parse("content://sms/inbox"),
							new String[] { "DISTINCT strftime('%d.%m.%Y %H:%M:%S', date/1000, 'unixepoch',  'localtime') || '\n' || body " },
							// "thread_id","address","person","date","body","type"
							"body  like '%__._______,__._______' ", null,
							"date DESC, _id DESC "); // LIMIT 5

			ids = new int[cursor.getCount()];
			int i = 0;

			if (cursor.moveToFirst()) {
				do {
					initOneBtn(layout, i, pixels_b, pixels_m,
							cursor.getString(0));
					i++;
				} while (cursor.moveToNext());
			}

			cursor.close();

		} catch (Exception e) {
			Log.d("gps",
					"EXCEPTION! " + e.toString() + " Message:" + e.getMessage());

		}

	}

	// ------------------------------------------------------------------------------------------

	protected void initOneBtn(LinearLayout layout, int i, int pixels_b,
			int pixels_m, String name) {

		LinearLayout row = new LinearLayout(this);
		row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		row.setOrientation(LinearLayout.VERTICAL);

		// Button
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				pixels_b);

		Button btnTag = new Button(this);
		// params.setMargins(-pixels_m, -pixels_m, -pixels_m, -pixels_m);
		btnTag.setLayoutParams(params);
		btnTag.setText(name);
		btnTag.setId(i);
		btnTag.setBackgroundColor(Color.TRANSPARENT);

		// Separator
		LayoutParams view_params = new LayoutParams(LayoutParams.MATCH_PARENT,
				2);

		View viewTag = new View(this);
		viewTag.setLayoutParams(view_params);
		viewTag.setBackgroundColor(Color.parseColor("#90909090"));

		ids[i] = i;
		/*
		 * btnTag.setOnLongClickListener(new View.OnLongClickListener() { public
		 * boolean onLongClick(View v) { seagullId = ids[v.getId()];
		 * showDialog(SEAGULL_PROPS_DIALOG_ID); return true; } });
		 * 
		 * btnTag.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * try { String cToSend = "tel:" + phones[v.getId()].replace("#",
		 * Uri.encode("#")); startActivityForResult(new Intent(
		 * "android.intent.action.CALL", Uri.parse(cToSend)), 1);
		 * 
		 * // This works too // Intent intent = new //
		 * Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
		 * // intent.setData(Uri.parse(cToSend)); //
		 * getApplicationContext().startActivity(intent); } catch (Exception e)
		 * { MainActivity.this.ShowToastT("EXCEPTION! " + e.toString() +
		 * " Message:" + e.getMessage(), Toast.LENGTH_LONG); }
		 * 
		 * }
		 * 
		 * });
		 */

		row.addView(btnTag);
		row.addView(viewTag);
		layout.addView(row);
	}

}
