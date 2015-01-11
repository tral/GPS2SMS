package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSActivity extends Activity {

    private final static int SAVE_POINT_DIALOG_ID = 10;
    private static final int ACT_RESULT_FAV = 1003;

    DBHelper dbHelper;
    private String actionCoords;
    private String[] myCoords;

    private ImageButton btnShare;
    private ImageButton btnCopy;
    private ImageButton btnMap;
    private ImageButton btnSave;
    private ImageButton btnFav;

    @Override
    protected void onResume() {
        super.onResume();
        refillMainScreen();
    }

    protected String getSMSSource() {
        return "inbox";
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
        setContentView(R.layout.activity_mysms);

        refillMainScreen();

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case ACT_RESULT_FAV:
                setFavBtnIcon();
                break;
        }
    }

    private void setFavBtnIcon() {

        try {

            SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String act = localPrefs.getString("prefFavAct", "");

            if (act.equalsIgnoreCase("")) {
                return;
            }

            Intent icon_intent = new Intent(android.content.Intent.ACTION_SEND);
            icon_intent.setType("text/plain");

            List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(icon_intent, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    if (info.activityInfo.name.toLowerCase().equalsIgnoreCase(act)) {
                        Drawable icon = info.activityInfo.loadIcon(this.getPackageManager());
                        btnFav.setImageDrawable(icon);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            //
        }
    }

    protected void refillMainScreen() {

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutSMS);

        if (layout.getChildCount() > 0)
            layout.removeAllViews();

        Resources r = getApplicationContext().getResources();

        // число пикселей для высоты кнопок (относительно dp)
        int pixels_b = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
        int separators_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        try {

            Cursor cursor = getContentResolver()
                    .query(Uri.parse("content://sms/" + getSMSSource()),
                            new String[]{"DISTINCT strftime('%d.%m.%Y %H:%M:%S', date/1000, 'unixepoch',  'localtime') || '\n' ", "address", "body"},
                            // "thread_id","address","person","date","body","type"
                            "body  like '%__._______,__._______' ", null,
                            "date DESC, _id DESC LIMIT 500"); // LIMIT 5

            myCoords = new String[cursor.getCount()];

            int i = 0;
            if (cursor.moveToFirst()) {

                do {
                    // Имена контактов показываем для 10-ти верхних СМС, т.к. алгоритм сопоставления номеров телефонов контактам найден только O(n^2)
                    initOneBtn(layout, i, pixels_b,
                            cursor.getString(0),
                            i < 10 ? getContactName(getApplicationContext(), cursor.getString(1)) : cursor.getString(1),
                            cursor.getString(2),
                            separators_margin);
                    i++;
                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {
            Log.d("gps",
                    "EXCEPTION! " + e.toString() + " Message:" + e.getMessage());

        }

    }

    public String getContactName(Context context, String phoneNumber) {
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor == null) {
                return null;
            }
            String contactName = null;
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return contactName;
        } catch (Exception e) {
            return phoneNumber;
        }
    }


    // ------------------------------------------------------------------------------------------

    protected void initOneBtn(LinearLayout layout, int i, int pixels_b,
                              String name, String phNumber, String body, int separator_margin) {

        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.VERTICAL);

        // Button
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                pixels_b);

        Button btnTag = new Button(this);
        btnTag.setLayoutParams(params);
        btnTag.setText(name + phNumber);
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
        view_params.setMargins(separator_margin, 0, separator_margin, 0);
        View viewTag = new View(this);
        viewTag.setLayoutParams(view_params);
        viewTag.setBackgroundColor(Color.parseColor("#90909090"));

        Pattern p = Pattern
                .compile("(\\-?\\d+\\.(\\d+)?),\\s*(\\-?\\d+\\.(\\d+)?)");
        Matcher m = p.matcher(body);

        myCoords[i] = m.find() ? m.group(0) : "0,0";

        btnTag.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                // actionCoordsId = ids[v.getId()];
                // showDialog(MYCOORDS_PROPS_DIALOG_ID);
                return true;
            }
        });

        btnTag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // selected coordinates
                actionCoords = myCoords[v.getId()];

                // custom dialog
                final Dialog dialog = new Dialog(MySMSActivity.this);
                dialog.setContentView(R.layout.options1_mysms_dialog);
                dialog.setTitle(getString(R.string.mysms_actions));
                dialog.show();

                btnShare = (ImageButton) dialog.findViewById(R.id.btnShare2);
                btnCopy = (ImageButton) dialog.findViewById(R.id.btnCopy2);
                btnMap = (ImageButton) dialog.findViewById(R.id.btnMap2);
                btnSave = (ImageButton) dialog.findViewById(R.id.btnSave2);
                btnFav = (ImageButton) dialog.findViewById(R.id.btnFav2);
                setFavBtnIcon();

                btnSave.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showDialog(SAVE_POINT_DIALOG_ID);
                    }
                });

                btnShare.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.shareCoordinates(MySMSActivity.this, DBHelper
                                .getShareBody(MySMSActivity.this, actionCoords,
                                        ""));
                    }
                });

                btnCopy.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.clipboardCopy(getApplicationContext(), actionCoords);
                        DBHelper.ShowToastT(MySMSActivity.this, getString(R.string.text_copied), Toast.LENGTH_LONG);
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

                btnFav.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (!DBHelper.shareFav(MySMSActivity.this, actionCoords)) {
                            Intent intent = new Intent(MySMSActivity.this, ChooseFavActivity.class);
                            startActivityForResult(intent, ACT_RESULT_FAV);
                        }
                        ;
                    }
                });
                btnFav.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(MySMSActivity.this, ChooseFavActivity.class);
                        startActivityForResult(intent, ACT_RESULT_FAV);
                        return true;
                    }
                });

            }
        });

        row.addView(btnTag);
        row.addView(viewTag);
        layout.addView(row);
    }

    // Dialogs
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case SAVE_POINT_DIALOG_ID:
                LayoutInflater inflater_sp = getLayoutInflater();
                View layout_sp = inflater_sp.inflate(R.layout.save_point_dialog,
                        (ViewGroup) findViewById(R.id.save_point_dialog_layout));

                AlertDialog.Builder builder_sp = new AlertDialog.Builder(this);
                builder_sp.setView(layout_sp);

                final EditText lPointName = (EditText) layout_sp
                        .findViewById(R.id.point_edit_text);

                builder_sp.setPositiveButton(getString(R.string.save_btn_txt),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper = new DBHelper(MySMSActivity.this);
                                dbHelper.insertMyCoord(lPointName.getText()
                                        .toString(), actionCoords);
                                dbHelper.close();
                                lPointName.setText(""); // Чистим
                                DBHelper.ShowToast(MySMSActivity.this,
                                        R.string.point_saved, Toast.LENGTH_LONG);
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
        return null;
    }

}
