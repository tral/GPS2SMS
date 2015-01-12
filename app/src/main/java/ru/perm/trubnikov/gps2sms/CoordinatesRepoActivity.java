package ru.perm.trubnikov.gps2sms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

abstract class CoordinatesRepoActivity extends Activity {

    protected final static int DIALOG_ID = 5;
    protected static final int ACT_RESULT_FAV = 1003;

    DBHelper dbHelper;
    protected String actionCoords;
    protected String[] myCoords;

    protected ImageButton btnShare;
    protected ImageButton btnCopy;
    protected ImageButton btnMap;
    protected ImageButton btnFav;

    abstract void retrieveMainData(LinearLayout layout, int pixels_b, int separators_margin);

    abstract AlertDialog secondDialog();

    abstract String getMyCoordsItem(String toParse);

    abstract View.OnLongClickListener dialogButtonsLongListener();

    @Override
    protected void onResume() {
        super.onResume();
        refillMainScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase(
                "1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ID:
                return secondDialog();
        }
        return null;
    }

    final void setFavBtnIcon() {

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

    final void refillMainScreen() {

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutRepo);

        if (layout.getChildCount() > 0)
            layout.removeAllViews();

        Resources r = getApplicationContext().getResources();

        // число пикселей для высоты кнопок (относительно dp)
        int pixels_b = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
        int separators_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());

        try {
            retrieveMainData(layout, pixels_b, separators_margin);
        } catch (Exception e) {
            Log.d("gps", "EXCEPTION! " + e.toString() + " Message:" + e.getMessage());

        }

    }

    final void initOneBtn(LinearLayout layout, int i, int pixels_b,
                          String toParseCoord, int separator_margin, String btnTitle) {

        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.VERTICAL);

        // Button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                pixels_b);

        Button btnTag = new Button(this);
        btnTag.setLayoutParams(params);
        btnTag.setText(btnTitle);
        btnTag.setId(i);

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        btnTag.setTextColor(sharedPrefs.getString("prefAppTheme", "1")
                .equalsIgnoreCase("1") ? Color.parseColor("#FFFFFF") : Color
                .parseColor("#000000"));

        btnTag.setBackgroundColor(Color.TRANSPARENT);

        // Separator
        LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                2);
        view_params.setMargins(separator_margin, 0, separator_margin, 0);
        View viewTag = new View(this);
        viewTag.setLayoutParams(view_params);
        viewTag.setBackgroundColor(Color.parseColor("#90909090"));

        myCoords[i] = getMyCoordsItem(toParseCoord);

        btnTag.setOnLongClickListener(dialogButtonsLongListener());
        btnTag.setOnClickListener(dialogButtonsListener());

        row.addView(btnTag);
        row.addView(viewTag);
        layout.addView(row);
    }

    final View.OnClickListener dialogButtonsListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // selected coordinates
                actionCoords = myCoords[v.getId()];

                // custom dialog
                final Dialog dialog = new Dialog(CoordinatesRepoActivity.this);
                dialog.setContentView(R.layout.repo_buttons_dialog);
                dialog.setTitle(getDialogTitle(v));
                btnMap = (ImageButton) dialog.findViewById(R.id.btnMap2);
                btnShare = (ImageButton) dialog.findViewById(R.id.btnShare2);
                btnCopy = (ImageButton) dialog.findViewById(R.id.btnCopy2);
                btnFav = (ImageButton) dialog.findViewById(R.id.btnFav2);
                dialogAdjustment(dialog);
                setFavBtnIcon();
                dialog.show();

                btnShare.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.shareCoordinates(CoordinatesRepoActivity.this,
                                DBHelper.getShareBody(CoordinatesRepoActivity.this,
                                        actionCoords, ""));
                    }
                });

                btnCopy.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.clipboardCopy(getApplicationContext(), actionCoords);
                        DBHelper.ShowToastT(CoordinatesRepoActivity.this, getString(R.string.text_copied), Toast.LENGTH_LONG);
                    }
                });

                btnMap.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.openOnMap(getApplicationContext(), actionCoords);
                    }
                });

                btnFav.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (!DBHelper.shareFav(CoordinatesRepoActivity.this,
                                DBHelper.getShareBody(CoordinatesRepoActivity.this, actionCoords, ""))) {
                            Intent intent = new Intent(CoordinatesRepoActivity.this, ChooseFavActivity.class);
                            startActivityForResult(intent, ACT_RESULT_FAV);
                        }
                        ;
                    }
                });

                btnFav.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(CoordinatesRepoActivity.this, ChooseFavActivity.class);
                        startActivityForResult(intent, ACT_RESULT_FAV);
                        return true;
                    }
                });

                addExtraButtons(dialog);

            }
        };

    }

    protected void dialogAdjustment(Dialog dialog) {
    }

    protected void addExtraButtons(Dialog dialog) {
    }

    protected String getDialogTitle(View v) {
        return getString(R.string.mysms_actions);
    }

}
