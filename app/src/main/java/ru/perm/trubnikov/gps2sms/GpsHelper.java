package ru.perm.trubnikov.gps2sms;


import android.annotation.TargetApi;
import android.content.pm.PackageManager;

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
                hasPermission(android.Manifest.permission.READ_CONTACTS, a));
    }

}
