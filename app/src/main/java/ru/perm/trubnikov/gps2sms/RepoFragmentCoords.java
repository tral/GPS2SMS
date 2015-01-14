package ru.perm.trubnikov.gps2sms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class RepoFragmentCoords extends RepoFragment {

    protected final static int DIALOG_COORD_PROPS_ID = 5;

    protected int actionCoordsId;
    protected String[] myCoordsNames;
    protected int[] ids;


    @Override
    protected void retrieveMainData(LinearLayout layout, int pixels_b, int separators_margin) {

        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT d._id, d.name, d.coord FROM mycoords as d ORDER BY d._id DESC";

        Cursor mCur = db.rawQuery(sqlQuery, null);

        myCoords = new String[mCur.getCount()];
        myCoordsNames = new String[mCur.getCount()];
        ids = new int[mCur.getCount()];
        int i = 0;
        if (mCur.moveToFirst()) {

            int idColIndex = mCur.getColumnIndex("_id");
            int nameColIndex = mCur.getColumnIndex("name");
            int valColIndex = mCur.getColumnIndex("coord");

            do {
                initOneBtn(layout, i, pixels_b,
                        mCur.getString(valColIndex),
                        separators_margin,
                        mCur.getString(nameColIndex) + System.getProperty("line.separator") + mCur.getString(valColIndex));
                myCoordsNames[i] = mCur.getString(nameColIndex);
                ids[i] = mCur.getInt(idColIndex);
                i++;
            } while (mCur.moveToNext());
        }

        mCur.close();
        dbHelper.close();
    }

    protected View.OnLongClickListener dialogButtonsLongListener() {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                actionCoordsId = ids[v.getId()];
                getActivity().showDialog(DIALOG_COORD_PROPS_ID);
                return true;
            }
        };
    }

    protected String getMyCoordsItem(String toParse) {
        return toParse;
    }



    @Override
    protected String getDialogTitle(View v) {
        return myCoordsNames[v.getId()];
    }

    @Override
    protected void dialogAdjustment(Dialog dialog) {
        dialog.findViewById(R.id.btnSave2).setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnMap.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnMap.setLayoutParams(params); //causes layout update
    }


}


