package ru.perm.trubnikov.gps2sms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyCoordsActivity extends CoordinatesRepoActivity {

    protected int actionCoordsId;
    protected String[] myCoordsNames;
    protected int[] ids;

    @Override
    protected void retrieveMainData(LinearLayout layout, int pixels_b, int separators_margin) {

        dbHelper = new DBHelper(this);
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
                showDialog(DIALOG_ID);
                return true;
            }
        };
    }

    protected String getMyCoordsItem(String toParse) {
        return toParse;
    }

    protected AlertDialog secondDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.repo_point_props_dialog,
                (ViewGroup) findViewById(R.id.repo_point_props_dialog_layout));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);

        final EditText nameEdit = (EditText) layout
                .findViewById(R.id.mycoords_name);

        builder.setPositiveButton(getString(R.string.save_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dbHelper = new DBHelper(MyCoordsActivity.this);
                        dbHelper.setMyccordName(actionCoordsId, nameEdit
                                .getText().toString());
                        dbHelper.close();
                        refillMainScreen();
                    }
                });

        builder.setNegativeButton(getString(R.string.del_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper = new DBHelper(MyCoordsActivity.this);
                        dbHelper.deleteMyccord(actionCoordsId);
                        dbHelper.close();
                        refillMainScreen();
                    }
                });

        builder.setNeutralButton(getString(R.string.cancel_btn_txt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        // show keyboard automatically
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
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

    // Update DialogData
    protected void onPrepareDialog(int id, Dialog dialog) {
        //AlertDialog aDialog = (AlertDialog) dialog;

        switch (id) {

            case DIALOG_ID:
                try {
                    EditText e1 = (EditText) dialog
                            .findViewById(R.id.mycoords_name);
                    e1.requestFocus();

                    dbHelper = new DBHelper(this);
                    e1.setText(dbHelper.getMyccordName(actionCoordsId));
                    dbHelper.close();
                    e1.selectAll();
                } catch (Exception e) {
                    Log.d("gps", "EXCEPTION! " + e.toString() + " Message:" + e.getMessage());
                }

                break;

            default:
                break;
        }
    }

}
