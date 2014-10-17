package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MyCoordsActivity extends Activity {

	private final static int MYCOORDS_PROPS_DIALOG_ID = 5;

	DBHelper dbHelper;
	private int actionCoordsId;
	private String actionCoords;
	private String[] myCoords;
	private String[] myCoordsNames;
	private int[] ids;
	
	private ImageButton btnShare;
	private ImageButton btnCopy;
	private ImageButton btnMap;

	@Override
	protected void onResume() {
		super.onResume();
		refillMainScreen();
	}

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

		refillMainScreen();

	}

	protected void refillMainScreen() {

		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutCoords);

		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		Resources r = getApplicationContext().getResources();

		// число пикселей для высоты кнопок (относительно dp)
		int pixels_b = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());

		try {

			dbHelper = new DBHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			String sqlQuery = "SELECT d._id, d.name, d.coord FROM mycoords as d ORDER BY d._id DESC";

			Cursor mCur = db.rawQuery(sqlQuery, null);

			myCoords = new String[mCur.getCount()];
			myCoordsNames = new String[mCur.getCount()];
			ids = new int[mCur.getCount()];
			int i = 0;
			if (mCur.moveToFirst()) {

				int idColIndex = mCur.getColumnIndex("_id");
				int nameColIndex = mCur.getColumnIndex("name");
				int valColIndex = mCur.getColumnIndex("coord");

				do {
					initOneBtn(layout, i, pixels_b,
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
			String name, String coord, int id) {

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
		
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		btnTag.setTextColor(sharedPrefs.getString("prefAppTheme", "1")
				.equalsIgnoreCase("1") ? Color.parseColor("#FFFFFF") : Color
				.parseColor("#000000"));
		
		btnTag.setBackgroundColor(Color.TRANSPARENT);

		// Separator
		LayoutParams view_params = new LayoutParams(LayoutParams.MATCH_PARENT,
				2);

		View viewTag = new View(this);
		viewTag.setLayoutParams(view_params);
		viewTag.setBackgroundColor(Color.parseColor("#90909090"));

		myCoords[i] = coord;
		myCoordsNames[i] = name;
		ids[i] = id;

		btnTag.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				actionCoordsId = ids[v.getId()];
				showDialog(MYCOORDS_PROPS_DIALOG_ID);
				return true;
			}
		});

		btnTag.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// selected coordinates
				actionCoords = myCoords[v.getId()];

				// custom dialog
				final Dialog dialog = new Dialog(MyCoordsActivity.this);
				dialog.setContentView(R.layout.options1_mycoords_dialog);
				dialog.setTitle(myCoordsNames[v.getId()]);
				dialog.show();

				btnShare = (ImageButton) dialog.findViewById(R.id.btnShare1);
				btnCopy = (ImageButton) dialog.findViewById(R.id.btnCopy1);
				btnMap = (ImageButton) dialog.findViewById(R.id.btnMap1);

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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MYCOORDS_PROPS_DIALOG_ID:
			return mycoordProps();
		}
		return null;
	}

	protected AlertDialog mycoordProps() {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.options2_mycoords_dialog,
				(ViewGroup) findViewById(R.id.options2_mycoords_dialog_layout));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		final EditText nameEdit = (EditText) layout
				.findViewById(R.id.mycoords_name);

		builder.setPositiveButton(getString(R.string.save_btn_txt),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dbHelper = new DBHelper(MyCoordsActivity.this);
						dbHelper.setMyccordName(actionCoordsId, nameEdit
								.getText().toString());
						dbHelper.close();
						refillMainScreen();
					}
				});

		builder.setNegativeButton(getString(R.string.del_btn_txt),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dbHelper = new DBHelper(MyCoordsActivity.this);
						dbHelper.deleteMyccord(actionCoordsId);
						dbHelper.close();
						refillMainScreen();
					}
				});

		builder.setNeutralButton(getString(R.string.cancel_btn_txt),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		builder.setCancelable(true);

		AlertDialog dialog = builder.create();
		// show keyboard automatically
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return dialog;
	}

	// Update DialogData
	protected void onPrepareDialog(int id, Dialog dialog) {
		AlertDialog aDialog = (AlertDialog) dialog;

		switch (id) {

		case MYCOORDS_PROPS_DIALOG_ID:
			try {
				EditText e1 = (EditText) dialog
						.findViewById(R.id.mycoords_name);
				e1.requestFocus();

				dbHelper = new DBHelper(this);
				e1.setText(dbHelper.getMyccordName(actionCoordsId));
				dbHelper.close();
				e1.selectAll();
			} catch (Exception e) {
				Log.d("gps",
						"EXCEPTION! " + e.toString() + " Message:"
								+ e.getMessage());
			}

			break;

		default:
			break;
		}
	};

}
