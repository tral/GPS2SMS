package ru.perm.trubnikov.gps2sms;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by A on 20.01.2015.
 */
public class RepoListAdapterSMS extends RepoListAdapter {

    protected final Integer[] contactids;

    public RepoListAdapterSMS(Context context, String[] firstlines, String[] secondlines, Integer[] contactids) {
        super(context, firstlines, secondlines);
        this.contactids = contactids;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = super.getView(position, convertView, parent);

        //imageView.setImageDrawable(icons[position]);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        if (contactids.length > position) {
            if (contactids[position] != null) {
                Uri u = getPhotoUri(contactids[position]);
                if (u != null) {
                    imageView.setImageURI(u);
                    if (imageView.getDrawable() == null)
                        imageView.setImageResource(R.drawable.ic_launcher);
                    //Log.d("gps1", u.toString());
                } else {
                    //imageView.setImageResource(R.drawable.ic_contact_picture_2);
                }
            }

        }

        return rowView;
    }


    public Uri getPhotoUri(int contactId) {
        try {
            Cursor cur = this.context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }

            cur.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }
}
