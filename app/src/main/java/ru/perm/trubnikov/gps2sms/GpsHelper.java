package ru.perm.trubnikov.gps2sms;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;

import java.util.Locale;

public class GpsHelper {

    @TargetApi(23)
    public static boolean hasPermission(String perm, BaseActivity a) {
        return (PackageManager.PERMISSION_GRANTED == a.checkSelfPermission(perm));
    }

    @TargetApi(23)
    public static boolean canAccessLocation(BaseActivity a) {
        // All needed permissions
        return (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, a) && hasPermission(android.Manifest.permission.SEND_SMS, a) &&
                hasPermission(android.Manifest.permission.RECEIVE_SMS, a) && hasPermission(android.Manifest.permission.READ_SMS, a) &&
                hasPermission(android.Manifest.permission.READ_CONTACTS, a) && hasPermission(android.Manifest.permission.READ_PHONE_STATE, a));
    }

    public static String latLonToDMS (double val, boolean is_lat) {

        String mod = (is_lat) ? ((val >= 0) ? "N" : "S") : ((val >= 0) ? "E" : "W");

        double degrees = Math.floor(val);
        double mmgg = (val - degrees) * 60;
        double minutes = Math.floor(mmgg);
        double seconds = Math.floor((mmgg - minutes) * 60);

        String dms = String.format(Locale.US, "%2.0f", degrees) + "°" + String.format(Locale.US, "%2.0f", minutes) + "'" + String.format(Locale.US, "%2.0f", seconds) + "''";
        return (mod + " " + dms.replace(" ", ""));
    }

    public static String latLonToDM (double val, boolean is_lat) {

        String mod = (is_lat) ? ((val >= 0) ? "N" : "S") : ((val >= 0) ? "E" : "W");

        double degrees = Math.floor(val);
        double mmgg = (val - degrees) * 60;

        String dm = String.format(Locale.US, "%2.0f", degrees) + "°" + String.format(Locale.US, "%2.4f", mmgg) + "'";
        return (mod + " " + dm.replace(" ", ""));
    }

}
