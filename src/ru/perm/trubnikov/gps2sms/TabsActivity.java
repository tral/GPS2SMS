package ru.perm.trubnikov.gps2sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class TabsActivity extends TabActivity {

	private final static int MYCOORDS_ADD_POINT_DIALOG_ID = 15;

	// ------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Определение темы должно быть ДО super.onCreate и setContentView
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase(
				"1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

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

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mycoords_actions, menu);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_add_coords:
			showDialog(MYCOORDS_ADD_POINT_DIALOG_ID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MYCOORDS_ADD_POINT_DIALOG_ID:
			return addpointDlg();
		}
		return null;
	}

	protected AlertDialog addpointDlg() {

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.addpoint,
				(ViewGroup) findViewById(R.id.addpoint_dialog_layout));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);

		final EditText pEdit = (EditText) layout.findViewById(R.id.point_name);
		final EditText laEdit = (EditText) layout.findViewById(R.id.point_la);
		final EditText loEdit = (EditText) layout.findViewById(R.id.point_lo);

		builder.setPositiveButton(getString(R.string.save_btn_txt),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						Pattern p = Pattern
								.compile("(\\-?\\d+\\.(\\d+)?),\\s*(\\-?\\d+\\.(\\d+)?)");
						Matcher m = p.matcher(laEdit.getText().toString() + ","
								+ loEdit.getText().toString());

						if (m.find()) {

							DBHelper dbHelper = new DBHelper(TabsActivity.this);
							dbHelper.insertMyCoord(pEdit.getText().toString(),
									m.group(0));
							dbHelper.close();

						} else {
							DBHelper.ShowToastT(
									TabsActivity.this.getApplicationContext(),
									getString(R.string.add_point_error),
									Toast.LENGTH_SHORT);
						}

						TabHost tabHost = getTabHost();
						tabHost.setCurrentTabByTag("tag1");
						MyCoordsActivity myActivity = (MyCoordsActivity) getCurrentActivity();
						myActivity.refillMainScreen();

						pEdit.requestFocus();
						pEdit.setText("");
						laEdit.setText("");
						loEdit.setText("");
					}
				});

		builder.setNegativeButton(getString(R.string.cancel_btn_txt),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						pEdit.requestFocus();
						pEdit.setText("");
						laEdit.setText("");
						loEdit.setText("");
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

	@TargetApi(11)
	public void ShowBackButton() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// call something for API Level 11+
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// ------------------------------------------------------------------------------------------

}
