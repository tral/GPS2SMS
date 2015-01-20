package ru.perm.trubnikov.gps2sms;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class DBHelper extends SQLiteOpenHelper {

    private String defSmsMsg;

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "rupermtrubnikovgps2smsDB", null, 4);
        defSmsMsg = context.getString(R.string.default_sms_msg);
    }
/*
    public long getSettingsParamInt(String param) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("settings", null, "param = '" + param + "'", null,
                null, null, null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("val_int");
            return c.getLong(idx);
        }

        return 0;
    }

    public String getSettingsParamTxt(String param) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("settings", null, "param = '" + param + "'", null,
                null, null, null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("val_txt");
            return c.getString(idx);
        }

        return "";
    }

    public void setSettingsParamInt(String param, long val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("val_int", val);
        db.update("settings", cv, "param = ?", new String[]{param});
    }

    public void setSettingsParamTxt(String param, String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("val_txt", val);
        db.update("settings", cv, "param = ?", new String[]{param});
    }*/

    public void setMyccordName(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("mycoords", cv, "_id = ?",
                new String[]{Integer.toString(id)});
    }

    public String getMyccordName(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("mycoords", null, "_id=" + id, null, null, null,
                null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("name");
            return c.getString(idx);
        }

        return "";
    }

    public void deleteMyccord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("mycoords", "_id = " + id, null);
    }

   /* public String getPhone() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("phone", null, "_id=1", null, null, null, null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("phone");
            String phone = c.getString(idx);
            return phone;
        }

        return "";
    }*/

    public String getName() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("contact", null, "_id=1", null, null, null, null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("contact");
            return c.getString(idx);
        }

        return "";
    }

    /*public String getSmsMsg() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("msg", null, "_id=1", null, null, null, null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("msg");
            return c.getString(idx);
        }

        return "";
    }*/

    public String getSlot(int id, String col) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("slots", null, "_id=" + id, null, null, null, null);

        if (c.moveToFirst()) {
            int idx = c.getColumnIndex(col);
            return c.getString(idx);
        }

        return "";
    }

    public void setSlot(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        // Log.d("gps", "save! " + name + " " + phone + " " + id);
        db.update("slots", cv, "_id = ?", new String[]{Integer.toString(id)});
    }

    public void insertMyCoord(String name, String coord) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("name", name);
        cv.put("coord", coord);
        // Log.d("gps", "save! " + name + " " + phone + " " + id);
        db.insert("mycoords", null, cv);
    }


    public static void updateFavIcon(Context context, ImageButton btn) {

        try {

            SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String act = localPrefs.getString("prefFavAct", "");

            if (act.equalsIgnoreCase("")) {
                return;
            }

            Intent icon_intent = new Intent(android.content.Intent.ACTION_SEND);
            icon_intent.setType("text/plain");

            List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(icon_intent, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    if (info.activityInfo.name.toLowerCase().equalsIgnoreCase(act)) {
                        Drawable icon = info.activityInfo.loadIcon(context.getPackageManager());
                        btn.setImageDrawable(icon);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            //
        }

    }


    public static int determineTheme(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (sharedPrefs.getString("prefAppTheme", "1")) {
            case "1":
                return R.style.AppBaseThemeDark;
            case "2":
                return R.style.AppBaseThemeLight;
            default:
                return R.style.AppBaseThemeDark;

        }
    }

    public static int determineAccendcolor(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (sharedPrefs.getString("prefAppTheme", "1")) {
            case "1":
                return context.getResources().getColor(R.color.accent_yellow/*accent_dt*/);
            case "2":
                return context.getResources().getColor(R.color.accent_lt);
            default:
                return context.getResources().getColor(R.color.accent_dt);

        }
    }


    public static String extractCoordinates(String message) {
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

        res = res + separ + separ + DBHelper.getGoogleMapsLink(crds);

        return res;
    }

    public static String getNavitelMessage(String crds) {
        // ??? "<NavitelLoc>" + (loc.getLatitude() > 0 ? "N" : "S") + la + "° " + (loc.getLongitude() > 0 ? "E" : "W") + lo + "°<N>";
        return "<NavitelLoc>" + crds + "<N>";
    }

    public static String getGoogleMapsLink(String crds) {
        // gGoogleMapsLink = "https://www.google.com/maps/place/" +
        // coordsToSend;
        return "http://maps.google.com/maps?q=loc:" + crds;
    }

    public static String getOSMLink(String crds) {
        crds = crds.replace(",", "&mlon=");
        return "http://openstreetmap.org/?mlat=" + crds + "&zoom=17";
    }

    // Small util to show text messages
    public static void ShowToast(Context context, int txt, int lng) {
        Toast toast = Toast.makeText(context, txt, lng);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public static void ShowToastT(Context context, String txt, int lng) {
        Toast toast = Toast.makeText(context, txt, lng);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public static boolean shareFav(Context context, String crds) {
        boolean found = false;
        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                //Log.d("gps", info.activityInfo.name.toLowerCase() + " - " + pckg);
                if (info.activityInfo.name.toLowerCase().equalsIgnoreCase(localPrefs.getString("prefFavAct", ""))) { //|| info.activityInfo.name.toLowerCase().contains(pckg))
                    share.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.share_topic));
                    share.putExtra(android.content.Intent.EXTRA_TEXT, crds);
                    share.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    //share.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                    share.setPackage(info.activityInfo.packageName);

                    found = true;
                    break;
                }
            }

            if (found) {
                context.startActivity(Intent.createChooser(share, ""));
            }
        }

        return found;
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

    public static void openOnMap(Context context, String crds) {


        // http://developer.android.com/guide/components/intents-common.html
        // Example: "geo:0,0?q=34.99,-106.61(Treasure)"
        // с Меткой глючат Яндекс.Карты

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String geo = "geo:0,0?q=" + crds;

        intent.setData(Uri.parse(geo));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public static void clipboardCopy(Context context, String crds) {
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        clipboard.setText(getLinkByProvType(sharedPrefs.getString("prefClipboard", "2"), crds));
    }

    public static String getLinkByProvType(String settVal, String crds) {
        switch (settVal) {
            case "2":
                return DBHelper.getGoogleMapsLink(crds);
            case "3":
                return DBHelper.getOSMLink(crds);
            case "4":
                return DBHelper.getNavitelMessage(crds);
            default:
                return crds;
        }
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {

        ContentValues cv = new ContentValues();

        // номер телефона для отправки SMS
        db.execSQL("create table phone (" + "_id integer primary key,"
                + "phone text" + ");");

        // Договорились, что телефон хранится в таблице с _id=1
        cv.put("_id", 1);
        cv.put("phone", ""); // без "+7" !!!
        db.insert("phone", null, cv);

        db.execSQL("create table contact (" + "_id integer primary key,"
                + "contact text" + ");");

        // Договорились, что хранится в таблице с _id=1
        cv.clear();
        cv.put("_id", 1);
        cv.put("contact", "");
        db.insert("contact", null, cv);

        db.execSQL("create table msg (" + "_id integer primary key,"
                + "msg text" + ");");

        // Договорились, что хранится в таблице с _id=1
        cv.clear();
        cv.put("_id", 1);
        cv.put("msg", defSmsMsg);
        db.insert("msg", null, cv);

        Upgrade_1_to_2(db);
        Upgrade_2_to_3(db);
        Upgrade_3_to_4(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion <= 1) {
            Upgrade_1_to_2(db);
        }

        if (oldVersion <= 2) {
            Upgrade_2_to_3(db);
        }

        if (oldVersion <= 3) {
            Upgrade_3_to_4(db);
        }

    }

    public void Upgrade_1_to_2(SQLiteDatabase db) {

        ContentValues cv = new ContentValues();

        // Появилось с БД версии 2
        // таблица настроек
        db.execSQL("create table settings ("
                + "_id integer primary key autoincrement," + "param text,"
                + "val_txt text," + "val_int integer" + ");");

        cv.clear();
        cv.put("param", "sendvia"); // Посылать СМС/Навител
        cv.put("val_txt", "");
        cv.put("val_int", 1);
        db.insert("settings", null, cv);

        // Слоты контактов, plain phone - нулевой слот
        db.execSQL("create table slots (" + "_id integer primary key,"
                + "name text," + "phone text" + ");");

        cv.clear();
        cv.put("_id", 0);
        cv.put("name", "");
        cv.put("phone", "");
        db.insert("slots", null, cv);

        cv.clear();
        cv.put("_id", 1);
        cv.put("name", "");
        cv.put("phone", "");
        db.insert("slots", null, cv);

        cv.clear();
        cv.put("_id", 2);
        cv.put("name", "");
        cv.put("phone", "");
        db.insert("slots", null, cv);

        cv.clear();
        cv.put("_id", 3);
        cv.put("name", "");
        cv.put("phone", "");
        db.insert("slots", null, cv);

    }

    public void Upgrade_2_to_3(SQLiteDatabase db) {

        ContentValues cv = new ContentValues();

        cv.clear();
        cv.put("param", "keepscreen"); // Держать ли экран всегда включенным
        cv.put("val_txt", "");
        cv.put("val_int", 1);
        db.insert("settings", null, cv);

    }

    public void Upgrade_3_to_4(SQLiteDatabase db) {

        db.execSQL("create table mycoords ("
                + "_id integer primary key autoincrement," + "name text,"
                + "coord text" + ");");

    }

}