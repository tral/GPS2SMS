package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MyCoordsActivity extends Activity {

	DBHelper dbHelper;
	private String actionCoords;
	private String[] myCoords;
	private int[] ids;

	private ImageButton btnShare;
	private ImageButton btnCopy;
	private ImageButton btnMap;

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase(
				"1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycoords);

		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutCoords);

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

			dbHelper = new DBHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			String sqlQuery = "SELECT d._id, d.name, d.coord FROM mycoords as d  ORDER BY d._id DESC";

			Cursor mCur = db.rawQuery(sqlQuery, null);

			myCoords = new String[mCur.getCount()];
			ids = new int[mCur.getCount()];
			int i = 0;
			if (mCur.moveToFirst()) {

				int idColIndex = mCur.getColumnIndex("_id");
				int nameColIndex = mCur.getColumnIndex("name");
				int valColIndex = mCur.getColumnIndex("coord");

				do {
					initOneBtn(layout, i, pixels_b, pixels_m,
							mCur.getString(nameColIndex),
							mCur.getString(valColIndex),
							mCur.getInt(idColIndex));
					i++;
				} while (mCur.moveToNext());
			}

			mCur.close();
			dbHelper.close();

		} catch (Exception e) {
			Log.d("gps",
					"EXCEPTION! " + e.toString() + " Message:" + e.getMessage());

		}

	}

	// ------------------------------------------------------------------------------------------

	protected void initOneBtn(LinearLayout layout, int i, int pixels_b,
			int pixels_m, String name, String coord, int id) {

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
		btnTag.setText(name + System.getProperty("line.separator") + coord);
		btnTag.setId(i);
		btnTag.setBackgroundColor(Color.TRANSPARENT);

		// Separator
		LayoutParams view_params = new LayoutParams(LayoutParams.MATCH_PARENT,
				2);

		View viewTag = new View(this);
		viewTag.setLayoutParams(view_params);
		viewTag.setBackgroundColor(Color.parseColor("#90909090"));

		myCoords[i] = coord;
		ids[i] = id;
		/*
		 * btnTag.setOnLongClickListener(new View.OnLongClickListener() { public
		 * boolean onLongClick(View v) { seagullId = ids[v.getId()];
		 * showDialog(SEAGULL_PROPS_DIALOG_ID); return true; } });
		 */
		btnTag.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				actionCoords = myCoords[v.getId()];

				// custom dialog
				final Dialog dialog = new Dialog(MyCoordsActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.options1_coords_dialog);
				// dialog.setTitle("Custom Dialog");

				// set the custom dialog components - text, image and button
				// TextView text = (TextView)
				// dialog.findViewById(R.id.textDialog);
				// text.setText("Custom dialog Android example.");
				// ImageView image = (ImageView)
				// dialog.findViewById(R.id.imageDialog);
				// image.setImageResource(R.drawable.image0);

				dialog.show();

				btnShare = (ImageButton) dialog.findViewById(R.id.btnShare1);
				btnCopy = (ImageButton) dialog.findViewById(R.id.btnCopy1);
				btnMap = (ImageButton) dialog.findViewById(R.id.btnMap1);

				// Button declineButton = (Button)
				// dialog.findViewById(R.id.declineButton);

				btnShare.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						DBHelper.shareCoordinates(MyCoordsActivity.this,
								DBHelper.getShareBody(MyCoordsActivity.this,
										actionCoords, ""));
					}
				});
				btnCopy.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						DBHelper.clipboardCopy(getApplicationContext(),
								actionCoords,
								DBHelper.getGoogleMapsLink(actionCoords),
								DBHelper.getOSMLink(actionCoords));
						DBHelper.ShowToastT(MyCoordsActivity.this,
								getString(R.string.text_copied),
								Toast.LENGTH_LONG);

					}
				});
				btnMap.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						DBHelper.openOnMap(getApplicationContext(),
								actionCoords);
					}
				});

			}

		});

		row.addView(btnTag);
		row.addView(viewTag);
		layout.addView(row);
	}
}
