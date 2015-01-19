package ru.perm.trubnikov.gps2sms;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepoFragmentSMSIn extends RepoFragment {

    protected ImageButton btnSave;
    private final static int MYCOORDS_SAVE_POINT_DIALOG_ID = 20;


    @Override
    protected void retrieveMainData(LinearLayout layout, int pixels_b, int separators_margin) {

        Cursor cursor = getActivity().getContentResolver()
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
                        cursor.getString(0) + (i < 10 ? getContactName(getActivity().getApplicationContext(), cursor.getString(1)) : cursor.getString(1)));
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


    @Override
    protected void addExtraButtons(final Dialog dialog) {
        btnSave = (ImageButton) dialog.findViewById(R.id.btnSave2);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().showDialog(MYCOORDS_SAVE_POINT_DIALOG_ID);
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


