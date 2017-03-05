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
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

class DBHelper extends SQLiteOpenHelper {

    private String defSmsMsg;

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "rupermtrubnikovgps2smsDB", null, 4);
        defSmsMsg = context.getString(R.string.default_sms_msg);
    }

    public void setMyccordName(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("mycoords", cv, "_id = ?",
                new String[]{Integer.toString(id)});
        db.close();
    }

    public String getMyccordName(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("mycoords", null, "_id=" + id, null, null, null,
                null);

        String res = "";
        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("name");
            res = c.getString(idx);
        }

        c.close();
        db.close();

        return res;
    }

    public void deleteMyccord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("mycoords", "_id = " + id, null);
        db.close();
    }

    public String getSlot(int id, String col) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("slots", null, "_id=" + id, null, null, null, null);

        String res = "";
        if (c.moveToFirst()) {
            int idx = c.getColumnIndex(col);
            res = c.getString(idx);
        }
        c.close();
        db.close();
        return res;
    }

    public void setSlot(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        // Log.d("gps", "save! " + name + " " + phone + " " + id);
        db.update("slots", cv, "_id = ?", new String[]{Integer.toString(id)});
        db.close();
    }

    public void insertMyCoord(String name, String coord) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("name", name);
        cv.put("coord", coord);
        // Log.d("gps", "save! " + name + " " + phone + " " + id);
        db.insert("mycoords", null, cv);
        db.close();
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

        switch (sharedPrefs.getString("prefAppTheme", "1")) { // determineAccendcolor актуализировать вместе с этим методом!
            case "1":
                return R.style.AppBaseThemeDark;
            case "2":
                return R.style.AppBaseThemeLight;
            case "3":
                return R.style.AppThemeYellow;
            case "4":
                return R.style.AppThemePink;
            case "5":
                return R.style.AppThemeTeal;
            case "6":
                return R.style.AppThemeGrey;
            default:
                return R.style.AppBaseThemeDark;

        }
    }

    public static int determineAccendcolor(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (sharedPrefs.getString("prefAppTheme", "1")) {
            case "1":
                return context.getResources().getColor(R.color.accent_dt);
            case "2":
                return context.getResources().getColor(R.color.accent_lt);
            case "3":
                return context.getResources().getColor(R.color.accent_yellow);
            case "4":
                return context.getResources().getColor(R.color.accent_pink);
            case "5":
                return context.getResources().getColor(R.color.accent_teal);
            case "6":
                return context.getResources().getColor(R.color.accent_grey);
            default:
                return context.getResources().getColor(R.color.accent_dt);

        }
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

    public static String getFragmentTag(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
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