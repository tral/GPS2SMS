package ru.perm.trubnikov.gps2sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Locale;

public class ConverterActivity extends BaseActivity {

    // Location manager
    private LocationManager manager;

    // My GPS states
    public static final int GPS_PROVIDER_DISABLED = 1;
    public static final int GPS_GETTING_COORDINATES = 2;
    public static final int GPS_GOT_COORDINATES = 3;
    //public static final int GPS_PROVIDER_UNAVIALABLE = 4;
    //public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
    public static final int GPS_PAUSE_SCANNING = 6;

    CheckBox mCheckBox;
    TextView mDecimals;
    TextView mUTM;
    TextView mUTMH;
    TextView mMGRS;
    TextView mDM;
    TextView mDMS;
    CoordinateConversion mCoordsConv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        // UPD: определяется в базовом классе
        //setTheme(DBHelper.determineTheme(this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        ShowBackButton();
        // GPS init
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mDecimals = (TextView) findViewById(R.id.textView1);
        mUTM = (TextView) findViewById(R.id.textViewUDCZ);
        mUTMH = (TextView) findViewById(R.id.textViewUDC);
        mMGRS = (TextView) findViewById(R.id.textViewMC);
        mDM = (TextView) findViewById(R.id.valueDM);
        mDMS = (TextView) findViewById(R.id.valueDMS);

        mDecimals.setText("—");
        mUTM.setText("—");
        mUTMH.setText("—");
        mMGRS.setText("—");
        mDM.setText("—");
        mDMS.setText("—");

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     if (isChecked) {
                                                         ConverterActivity.this._resume();
                                                     } else {
                                                         ConverterActivity.this._pause();
                                                     }
                                                 }
                                             }
        );

        mCoordsConv = new CoordinateConversion();

        //DBHelper.ShowToastT(MainActivity.this, mCoordsConv.latLon2UTM(58.01205, 56.214005),
        //      Toast.LENGTH_SHORT);

        //DBHelper.ShowToastT(MainActivity.this, a.latLon2MGRUTM(58.01205, 56.214005),Toast.LENGTH_SHORT);

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

    public void ShowBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void _pause() {
        // For Android 6.0 ask for permissions in runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (GpsHelper.canAccessLocation(ConverterActivity.this))
                this.stopLocation();
        } else
            this.stopLocation();
    }

    protected void _resume() {
        // Держать ли экран включенным?
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPrefs.getBoolean("prefKeepScreen", true)) {
            getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // For Android 6.0 ask for permissions in runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (GpsHelper.canAccessLocation(ConverterActivity.this))
                this.startLocation();
        } else
            this.startLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this._resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this._pause();
    }

    private void stopLocation() {
        try {
            manager.removeUpdates(locListener);
        } catch (SecurityException e) {
        }
    }

    private void startLocation() {
        // Возобновляем работу с GPS-приемником
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                printLocation(null, GPS_GETTING_COORDINATES);
            }
        } catch (SecurityException e) {
        }
    }

    // Location events (we use GPS only)
    private LocationListener locListener = new LocationListener() {

        public void onLocationChanged(Location argLocation) {
            printLocation(argLocation, GPS_GOT_COORDINATES);
        }

        @Override
        public void onProviderDisabled(String arg0) {
            printLocation(null, GPS_PROVIDER_DISABLED);
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
    };

    private void printLocation(Location loc, int state) {

        String accuracy;

        switch (state) {
            case GPS_PROVIDER_DISABLED:
//                GPSstate.setText(R.string.gps_state_disabled);
//                setGPSStateAccentColor();
//                enableGPSBtn.setVisibility(View.VISIBLE);
                break;
            case GPS_GETTING_COORDINATES:
//                GPSstate.setText(R.string.gps_state_in_progress);
//                setGPSStateNormalColor();
//                enableGPSBtn.setVisibility(View.INVISIBLE);
                break;
            case GPS_PAUSE_SCANNING:
//                GPSstate.setText("");
//                enableGPSBtn.setVisibility(View.INVISIBLE);
                break;
            case GPS_GOT_COORDINATES:
                if (loc != null) {

                    // Accuracy
                    if (loc.getAccuracy() < 0.0001) {
                        accuracy = "?";
                    } else if (loc.getAccuracy() > 99) {
                        accuracy = "> 99";
                    } else {
                        accuracy = String.format(Locale.US, "%2.0f",
                                loc.getAccuracy());
                    }

                    String separ = System.getProperty("line.separator");

                    double lon = loc.getLongitude();
                    double lat = loc.getLatitude();


                    String la = String
                            .format(Locale.US, "%2.7f", lat);
                    String lo = String.format(Locale.US, "%3.7f",
                            lon);

                    //  String coordsToSend = la + "," + lo;

                    //coordsToShare = DBHelper.getShareBody(MainActivity.this,
                    //       coordsToSend, accuracy);

                    mDecimals.setText(la + ", " + lo);
                    mUTM.setText(mCoordsConv.latLon2UTM(lat, lon));
                    mUTMH.setText(mCoordsConv.latLon2UTMH(lat, lon));
                    mMGRS.setText(mCoordsConv.latLon2MGRUTM(lat, lon));


                    String mod_lat = (lat >= 0) ? "N" : "S";
                    String mod_lon = (lon >= 0) ? "E" : "W";
                    double degrees_lat = Math.floor(lat);
                    double mmgg_lat = (lat - degrees_lat) * 60;
                    double minutes_lat = Math.floor(mmgg_lat);
                    double seconds_lat = Math.floor((mmgg_lat - minutes_lat) * 60);
                    double degrees_lon = Math.floor(lon);
                    double mmgg_lon = (lat - degrees_lon) * 60;
                    double minutes_lon = Math.floor(mmgg_lon);
                    double seconds_lon = Math.floor((mmgg_lon - minutes_lon) * 60);

                    String dms1 = String.format(Locale.US, "%2.0f", degrees_lat) + "°" + String.format(Locale.US, "%2.0f", minutes_lat) + "'" + String.format(Locale.US, "%2.0f", seconds_lat) + "''";
                    String dms2 = String.format(Locale.US, "%2.0f", degrees_lon) + "°" + String.format(Locale.US, "%2.0f", minutes_lon) + "'" + String.format(Locale.US, "%2.0f", seconds_lon) + "''";
                    mDMS.setText(mod_lat + " " + dms1.replace(" ", "") + ", " + mod_lon + " " + dms2.replace(" ", ""));

                    String dm1 = String.format(Locale.US, "%2.0f", degrees_lat) + "°" + String.format(Locale.US, "%2.4f", mmgg_lat) + "'";
                    String dm2 = String.format(Locale.US, "%2.0f", degrees_lon) + "°" + String.format(Locale.US, "%2.4f", mmgg_lon) + "'";
                    mDM.setText(mod_lat + " " + dm1.replace(" ", "") + ", " + mod_lon + " " + dm2.replace(" ", ""));

//                    setGPSStateNormalColor();
//
//                    btnShare.setVisibility(View.VISIBLE);
//                    btnCopy.setVisibility(View.VISIBLE);
//                    btnMap.setVisibility(View.VISIBLE);
//                    btnSave.setVisibility(View.VISIBLE);
//                    btnFav.setVisibility(View.VISIBLE);
//
//                    ShowSendButton();
//                    enableGPSBtn.setVisibility(View.INVISIBLE);

                } else {
//                    GPSstate.setText(R.string.gps_state_unavialable);
//                    setGPSStateAccentColor();
//                    enableGPSBtn.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

}
