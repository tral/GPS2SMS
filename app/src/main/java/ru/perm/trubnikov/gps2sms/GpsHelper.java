package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return (mod + dms.replace(" ", ""));
    }

    public static String latLonToDM (double val, boolean is_lat) {

        String mod = (is_lat) ? ((val >= 0) ? "N" : "S") : ((val >= 0) ? "E" : "W");

        double degrees = Math.floor(val);
        double mmgg = (val - degrees) * 60;

        String dm = String.format(Locale.US, "%2.0f", degrees) + "°" + String.format(Locale.US, "%2.4f", mmgg) + "'";
        return (mod + dm.replace(" ", ""));
    }

    public static String getLinkByProvType(String settVal, String crds) {
        switch (settVal) {
            case "2":
                return GpsHelper.getGoogleMapsLink(crds);
            case "3":
                return GpsHelper.getOSMLink(crds);
            case "4":
                return GpsHelper.getNavitelMessage(crds);
            case "5":
                return GpsHelper.getYandexMapsLink(crds);
            case "6":
                return GpsHelper.getDmCoords(crds);
            case "7":
                return GpsHelper.getDmsCoords(crds);
            default:
                return crds;
        }
    }

    public static String getDmCoords(String crds) {
        String[] split = crds.split(",");
        return GpsHelper.latLonToDM(Double.parseDouble(split[0]), true) + ", " + GpsHelper.latLonToDM(Double.parseDouble(split[1]), false);
    }

    public static String getDmsCoords(String crds) {
        String[] split = crds.split(",");
        return GpsHelper.latLonToDMS(Double.parseDouble(split[0]), true) + ", " + GpsHelper.latLonToDMS(Double.parseDouble(split[1]), false);
    }

    public static String getNavitelMessage(String crds) {
        return "<NavitelLoc>" + crds + "<N>";
    }

    public static String getGoogleMapsLink(String crds) {
        return "https://maps.google.com/maps?q=loc:" + crds;
    }

    public static String getYandexMapsLink(String crds) {
        return "https://yandex.ru/maps/?mode=search&text=" + crds.replace(",", "%20");
    }

    public static String getOSMLink(String crds) {
        crds = crds.replace(",", "&mlon=");
        return "https://openstreetmap.org/?mlat=" + crds + "&zoom=17";
    }

    public static void clipboardCopy(Context context, String crds) {
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        clipboard.setText(getLinkByProvType(sharedPrefs.getString("prefClipboard", "2"), crds));
    }

    public static void shareCoordinates(Context context, String crds) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                context.getString(R.string.share_topic));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, crds);
        context.startActivity(Intent.createChooser(sharingIntent,
                context.getString(R.string.share_via)));
    }

    public static String extractCoordinates(String message) {
        message = message.replace("&mlon=", ",");
        Pattern p = Pattern.compile("(\\-?\\d+\\.(\\d+)?),\\s*(\\-?\\d+\\.(\\d+)?)");
        Matcher m = p.matcher(message);
        return m.find() ? m.group(0) : "0,0";
    }

    public static String getShareBody(Context context, String crds, String accuracy) {

        String separ = System.getProperty("line.separator");
        String crds1 = crds.replace(",",
                separ + context.getString(R.string.info_longitude) + ": ");

        String res = context.getString(R.string.info_latitude) + ": " + crds1;

        if (!accuracy.equalsIgnoreCase("")) {
            res = res + separ + context.getString(R.string.info_accuracy) + ": "
                    + accuracy + " " + context.getString(R.string.info_print2);
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return res + separ + separ + getLinkByProvType(sharedPrefs.getString("prefShareButtonsContent", "2"), crds);
    }

    public static Intent getIntentForMap(String crds) {
        String uri = getGoogleMapsLink(crds);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void openOnMap(Context context, String crds) {

        // http://developer.android.com/guide/components/intents-common.html
        // Example: "geo:0,0?q=34.99,-106.61(Treasure)"
        // с Меткой глючат Яндекс.Карты

        Intent intent = GpsHelper.getIntentForMap(crds);
        context.startActivity(intent);
    }

}
