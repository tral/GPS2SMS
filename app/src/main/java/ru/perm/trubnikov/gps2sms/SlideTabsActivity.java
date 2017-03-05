package ru.perm.trubnikov.gps2sms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SlideTabsActivity extends BaseActivity implements OnTabChangeListener, OnPageChangeListener {
    private final static int MYCOORDS_SAVE_POINT_DIALOG_ID = 20;
    private final static int MYCOORDS_ADD_POINT_DIALOG_ID = 15;
    protected final static int DIALOG_COORD_PROPS_ID = 5;

    private ViewPager mViewPager;
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        //setTheme(DBHelper.determineTheme(this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidetabs);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Tab Initialization
        initialiseTabHost();
        SlideTabsPagerAdapter mAdapter = new SlideTabsPagerAdapter(getSupportFragmentManager());

        // Fragments and ViewPager Initialization
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(SlideTabsActivity.this);

        ShowBackButton();

    }

    // Method to add a TabHost
    private static void AddTab(SlideTabsActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new SlideTabsFactory(activity));
        tabHost.addTab(tabSpec);
    }

    // Manages the Tab changes, synchronizing it with Pages
    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // Manages the Page changes, synchronizing it with Tabs
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);
    }

    @Override
    public void onPageSelected(int arg0) {
    }


    // Tabs Creation
    private void initialiseTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        SlideTabsActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec(getString(R.string.tab_mycoords)).setIndicator(getString(R.string.tab_mycoords)));
        SlideTabsActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec(getString(R.string.tab_mysms)).setIndicator(getString(R.string.tab_mysms)));
        SlideTabsActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec(getString(R.string.tab_mysms_out)).setIndicator(getString(R.string.tab_mysms_out)));

        mTabHost.setOnTabChangedListener(this);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_COORD_PROPS_ID:
                return secondDialog();
            case MYCOORDS_ADD_POINT_DIALOG_ID:
                return addPointDlg();
            case MYCOORDS_SAVE_POINT_DIALOG_ID:
                return savePointDlg();
        }
        return null;
    }


    // Update DialogData
    protected void onPrepareDialog(int id, Dialog dialog) {
        //AlertDialog aDialog = (AlertDialog) dialog;

        switch (id) {

            case DIALOG_COORD_PROPS_ID:
                try {
                    EditText e1 = (EditText) dialog
                            .findViewById(R.id.mycoords_name);
                    e1.requestFocus();

                    DBHelper dbHelper = new DBHelper(SlideTabsActivity.this);
                    RepoCoordsFragment fragment = (RepoCoordsFragment) getSupportFragmentManager().findFragmentByTag(DBHelper.getFragmentTag(R.id.viewpager, 0));
                    e1.setText(dbHelper.getMyccordName(fragment.actionCoordsId));
                    dbHelper.close();
                    e1.selectAll();
                } catch (Exception e) {
                    Log.d("gps", "EXCEPTION! " + e.toString() + " Message:" + e.getMessage());
                }

                break;

            default:
                break;
        }
    }

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


    // Dialog for adding new point
    protected AlertDialog addPointDlg() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.repo_addpoint,
                (ViewGroup) findViewById(R.id.repo_addpoint_dialog_layout));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);

        final EditText pEdit = (EditText) layout.findViewById(R.id.point_name);
        final EditText laEdit = (EditText) layout.findViewById(R.id.point_la);
        final EditText loEdit = (EditText) layout.findViewById(R.id.point_lo);

        AdjustAddDialogColors(layout);

        builder.setPositiveButton(getString(R.string.save_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String Coordinates = GpsHelper.extractCoordinates(laEdit.getText().toString() + "," + loEdit.getText().toString());

                        if (!Coordinates.equalsIgnoreCase("0,0")) {

                            DBHelper dbHelper = new DBHelper(SlideTabsActivity.this);
                            dbHelper.insertMyCoord(pEdit.getText().toString(), Coordinates);
                            dbHelper.close();

                        } else {
                            DBHelper.ShowToastT(
                                    SlideTabsActivity.this.getApplicationContext(),
                                    getString(R.string.add_point_error),
                                    Toast.LENGTH_SHORT);
                        }

                        mTabHost.setCurrentTab(0);
                        RepoCoordsFragment fragment = (RepoCoordsFragment) getSupportFragmentManager().findFragmentByTag(DBHelper.getFragmentTag(R.id.viewpager, 0));
                        //fragment.refillMainScreen();

                        fragment.rebuildList();

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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    // Dialog for rename & delete point
    protected AlertDialog secondDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.repo_point_props_dialog,
                (ViewGroup) findViewById(R.id.repo_point_props_dialog_layout));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);

        AdjustPropDialogColors(layout);

        final EditText nameEdit = (EditText) layout
                .findViewById(R.id.mycoords_name);

        builder.setPositiveButton(getString(R.string.save_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        RepoCoordsFragment fragment = (RepoCoordsFragment) getSupportFragmentManager().findFragmentByTag(DBHelper.getFragmentTag(R.id.viewpager, 0));

                        DBHelper dbHelper = new DBHelper(SlideTabsActivity.this);
                        dbHelper.setMyccordName(fragment.actionCoordsId, nameEdit
                                .getText().toString());
                        dbHelper.close();
                        fragment.rebuildList();
                        //fragment.refillMainScreen();
                    }
                });

        builder.setNegativeButton(getString(R.string.del_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RepoCoordsFragment fragment = (RepoCoordsFragment) getSupportFragmentManager().findFragmentByTag(DBHelper.getFragmentTag(R.id.viewpager, 0));
                        DBHelper dbHelper = new DBHelper(SlideTabsActivity.this);
                        dbHelper.deleteMyccord(fragment.actionCoordsId);
                        dbHelper.close();
                        //fragment.refillMainScreen();
                        fragment.rebuildList();
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

    protected AlertDialog savePointDlg() {
        LayoutInflater inflater_sp = getLayoutInflater();
        View layout_sp = inflater_sp.inflate(R.layout.repo_save_point_dialog,
                (ViewGroup) findViewById(R.id.repo_save_point_dialog_layout));

        AlertDialog.Builder builder_sp = new AlertDialog.Builder(this);
        builder_sp.setView(layout_sp);

        final EditText lPointName = (EditText) layout_sp
                .findViewById(R.id.point_edit_text);

        builder_sp.setPositiveButton(getString(R.string.save_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //!!!!!!!!!!!! RepoFragmentSMSOut, судя по всему, нормально приводится к RepoSMSInFragmentNew
                        RepoSMSInFragment fragment = (RepoSMSInFragment) getSupportFragmentManager().findFragmentByTag(DBHelper.getFragmentTag(R.id.viewpager, mViewPager.getCurrentItem()));

                        DBHelper dbHelper = new DBHelper(SlideTabsActivity.this);
                        dbHelper.insertMyCoord(lPointName.getText().toString(), fragment.actionCoords);
                        dbHelper.close();
                        lPointName.setText(""); // Чистим
                        DBHelper.ShowToast(SlideTabsActivity.this, R.string.point_saved, Toast.LENGTH_LONG);

                        mTabHost.setCurrentTab(0);
                        RepoCoordsFragment fragment0 = (RepoCoordsFragment) getSupportFragmentManager().findFragmentByTag(DBHelper.getFragmentTag(R.id.viewpager, 0));
                        //fragment0.refillMainScreen();
                        fragment0.rebuildList();
                    }
                });

        builder_sp.setNegativeButton(getString(R.string.cancel_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lPointName.setText(""); // Чистим
                        dialog.cancel();
                    }
                });

        builder_sp.setCancelable(true);
        AlertDialog dialog = builder_sp.create();
        dialog.setTitle(getString(R.string.save_point_dlg_header));
        // show keyboard automatically
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;

    }

    protected void AdjustAddDialogColors(View layout) {
        // Only for Android LOWER than 3.0 !
        // Hack for lower Android versions to make text visible
        // Dialog background is DIFFERENT in Android 2.1 and Android 2.3
        // That's why we use gray color everywhere for Android < 3.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            TextView tv1 = (TextView) layout.findViewById(R.id.textView1);
            TextView tv2 = (TextView) layout.findViewById(R.id.textView2);
            TextView tv3 = (TextView) layout.findViewById(R.id.textView3);
            EditText et1 = (EditText) layout.findViewById(R.id.point_name);
            EditText et2 = (EditText) layout.findViewById(R.id.point_la);
            EditText et3 = (EditText) layout.findViewById(R.id.point_lo);

            tv1.setTextColor(Color.parseColor("#9E9E9E"));
            tv2.setTextColor(Color.parseColor("#9E9E9E"));
            tv3.setTextColor(Color.parseColor("#9E9E9E"));
            et1.setTextColor(Color.parseColor("#9E9E9E"));
            et2.setTextColor(Color.parseColor("#9E9E9E"));
            et3.setTextColor(Color.parseColor("#9E9E9E"));
            et1.setHintTextColor(Color.parseColor("#9E9E9E"));
            et2.setHintTextColor(Color.parseColor("#9E9E9E"));
            et3.setHintTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    protected void AdjustPropDialogColors(View layout) {
        // Only for Android LOWER than 3.0 !
        // Hack for lower Android versions to make text visible
        // Dialog background is DIFFERENT in Android 2.1 and Android 2.3
        // That's why we use gray color everywhere for Android < 3.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            EditText et1 = (EditText) layout.findViewById(R.id.mycoords_name);

            et1.setTextColor(Color.parseColor("#9E9E9E"));
            et1.setHintTextColor(Color.parseColor("#9E9E9E"));
        }
    }


    public void ShowBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
