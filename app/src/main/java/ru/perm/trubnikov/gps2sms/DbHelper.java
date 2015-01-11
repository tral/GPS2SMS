package ru.perm.trubnikov.gps2sms;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;


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

    /*public static int getRndColor() {
        Random rand = new Random();
        // Чтобы не генерился слишком светлый фон, иначе символы нечитаемы
        int rc = rand.nextInt(230);
        int g = rand.nextInt(230);
        int b = rand.nextInt(230);

        int randomColor = Color.rgb(rc, g, b);
        return randomColor;
    }*/

    public static String getShareBody(Context context, String crds,
                                      String accuracy) {

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

        //if (!label.equalsIgnoreCase("")) {
        //	geo = geo + "(" + label + ")";
        //}

        intent.setData(Uri.parse(geo));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

		/*
         * String uri = String.format(Locale.ENGLISH,
		 * DBHelper.getGoogleMapsLink(crds)); Intent intent = new
		 * Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * context.startActivity(intent);
		 */

    }

    public static void clipboardCopy(Context context, String crds1,
                                     String crds2, String crds3) {
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String clip = sharedPrefs.getString("prefClipboard", "2");

        if (clip.equalsIgnoreCase("1")) {
            clipboard.setText(crds1);
        }
        if (clip.equalsIgnoreCase("2")) {
            clipboard.setText(crds2);
        }
        if (clip.equalsIgnoreCase("3")) {
            clipboard.setText(crds3);
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