package ru.perm.trubnikov.gps2sms;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class RepoSMSInFragment extends RepoFragment {

    private final static int MYCOORDS_SAVE_POINT_DIALOG_ID = 20;

    protected ArrayList<Integer> mContactIds = new ArrayList<Integer>();
    protected ImageButton btnSave;

    protected void rebuildList() {

        ArrayList<String> mFirstLine = new ArrayList<String>();
        ArrayList<String> mSecondLine = new ArrayList<String>();

        Cursor cursor = getActivity().getContentResolver()
                .query(Uri.parse("content://sms/" + getSMSSource()),
                        new String[]{"DISTINCT strftime('%d.%m.%Y %H:%M:%S', date/1000, 'unixepoch',  'localtime')", "address", "body"},
                        // "thread_id","address","person","date","body","type"
                        "body  like '%__._______,__._______' ", null,
                        "date DESC, _id DESC LIMIT 500"); // LIMIT 5

        mContactIds.clear();


        int i = 0;
        if (cursor.moveToFirst()) {

            do {
                // Имена контактов (и фото) показываем для 10-ти верхних СМС, т.к. алгоритм сопоставления номеров телефонов контактам найден только O(n^2)
                mFirstLine.add((i < 10 ? getContactName(getActivity().getApplicationContext(), cursor.getString(1)) : cursor.getString(1)));

                // coordinates & timestamp
                mSecondLine.add(GpsHelper.extractCoordinates(cursor.getString(2)) + " (" + cursor.getString(0) + ")");

                i++;

            } while (cursor.moveToNext());
        }

        cursor.close();

        mFirstLines = mFirstLine.toArray(new String[mFirstLine.size()]);
        mSecondLines = mSecondLine.toArray(new String[mSecondLine.size()]);

        setListAdapter(new RepoListAdapterSMS(
                getActivity(),
                mFirstLines,
                mSecondLines,
                mContactIds.toArray(new Integer[mContactIds.size()])
        ));

    }

    protected String getSMSSource() {
        return "inbox";
    }


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

    protected void setLongClickHandler() {
    }

    protected void dialogAdjustment(Dialog dialog) {

    }


    public String getContactName(Context context, String phoneNumber) {
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri,
                    new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            if (cursor == null) {
                return null;
            }
            String contactName = null;
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            mContactIds.add((contactName == null) ? null : cursor.getInt(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }


            return (contactName == null) ? phoneNumber : contactName;
        } catch (Exception e) {
            return phoneNumber;
        }
    }
}
