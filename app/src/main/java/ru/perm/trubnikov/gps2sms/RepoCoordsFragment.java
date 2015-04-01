package ru.perm.trubnikov.gps2sms;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class RepoCoordsFragment extends RepoFragment {

    protected final static int DIALOG_COORD_PROPS_ID = 5;

    protected ArrayList<Integer> mPointIds = new ArrayList<Integer>();
    protected int actionCoordsId;

    protected void setLongClickHandler () {
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                //Toast.makeText(getActivity(), "On long click listener, arg2=" + arg2 + " arg3=" + arg3 + " mId(arg2)="+mPointIds.get(arg2), Toast.LENGTH_LONG).show();


                actionCoordsId = mPointIds.get(arg2);
                getActivity().showDialog(DIALOG_COORD_PROPS_ID);
                return true;
            }
        });
    }

    protected void rebuildList() {

        ArrayList<String> mFirstLine = new ArrayList<String>();
        ArrayList<String> mSecondLine = new ArrayList<String>();

        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT d._id, d.name, d.coord FROM mycoords as d ORDER BY d._id DESC";

        Cursor mCur = db.rawQuery(sqlQuery, null);

        if (mCur.moveToFirst()) {

            int idColIndex = mCur.getColumnIndex("_id");
            int nameColIndex = mCur.getColumnIndex("name");
            int valCoordIndex = mCur.getColumnIndex("coord");
            mPointIds.clear();

            do {
                mFirstLine.add(mCur.getString(nameColIndex));
                mSecondLine.add(mCur.getString(valCoordIndex));
                mPointIds.add(mCur.getInt(idColIndex));


            } while (mCur.moveToNext());
        }

        mCur.close();
        dbHelper.close();

        mFirstLines = mFirstLine.toArray(new String[mFirstLine.size()]);
        mSecondLines = mSecondLine.toArray(new String[mSecondLine.size()]);

        setListAdapter(new RepoListAdapter(
                getActivity(),
                mFirstLines,
                mSecondLines
        ));
    }


    @Override
    protected String getDialogTitle(String s) {
        return s;
    }

    protected void dialogAdjustment(Dialog dialog) {
        dialog.findViewById(R.id.btnSave2).setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnMap.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnMap.setLayoutParams(params); //causes layout update
    }

    protected void addExtraButtons(final Dialog dialog) {
    }


}
