package ru.perm.trubnikov.gps2sms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSActivity extends CoordinatesRepoActivity {

    protected ImageButton btnSave;

    @Override
    protected void retrieveMainData(LinearLayout layout, int pixels_b, int separators_margin) {

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
                        cursor.getString(2),
                        separators_margin,
                        cursor.getString(0) + (i < 10 ? getContactName(getApplicationContext(), cursor.getString(1)) : cursor.getString(1)));
                i++;
            } while (cursor.moveToNext());
        }

        cursor.close();

    }

    protected View.OnLongClickListener dialogButtonsLongListener() {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return true;
            }
        };
    }

    protected String getMyCoordsItem(String toParse) {
        Pattern p = Pattern.compile("(\\-?\\d+\\.(\\d+)?),\\s*(\\-?\\d+\\.(\\d+)?)");
        Matcher m = p.matcher(toParse);
        return m.find() ? m.group(0) : "0,0";
    }

    protected AlertDialog secondDialog() {
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

    @Override
    protected void addExtraButtons(final Dialog dialog) {
        btnSave = (ImageButton) dialog.findViewById(R.id.btnSave2);
        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDialog(DIALOG_ID);
            }
        });
    }

    protected String getSMSSource() {
        return "inbox";
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
            return (contactName == null) ? phoneNumber : contactName;
        } catch (Exception e) {
            return phoneNumber;
        }
    }

}
