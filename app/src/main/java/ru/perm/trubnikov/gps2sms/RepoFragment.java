package ru.perm.trubnikov.gps2sms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

/**
 * Created by A on 14.01.2015.
 */
abstract class RepoFragment extends Fragment {


    protected static final int ACT_RESULT_FAV = 1003;

    DBHelper dbHelper;
    protected String actionCoords;
    protected String[] myCoords;

    protected ImageButton btnShare;
    protected ImageButton btnCopy;
    protected ImageButton btnMap;
    protected ImageButton btnFav;

    abstract void retrieveMainData(LinearLayout layout, int pixels_b, int separators_margin);

 //   abstract AlertDialog secondDialog();

    abstract String getMyCoordsItem(String toParse);

    abstract View.OnLongClickListener dialogButtonsLongListener();

    @Override
    public void onResume() {
        super.onResume();
        refillMainScreen();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_repo, container, false);
        return rootView;
    }


    // Activity created, we can work with UI
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refillMainScreen();
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case ACT_RESULT_FAV:
                //setFavBtnIcon();
                DBHelper.updateFavIcon(getActivity(), btnFav);
                break;
        }
    }


/*
    final void setFavBtnIcon() {

        try {

            SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String act = localPrefs.getString("prefFavAct", "");

            if (act.equalsIgnoreCase("")) {
                return;
            }

            Intent icon_intent = new Intent(android.content.Intent.ACTION_SEND);
            icon_intent.setType("text/plain");

            List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(icon_intent, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    if (info.activityInfo.name.toLowerCase().equalsIgnoreCase(act)) {
                        Drawable icon = info.activityInfo.loadIcon(getActivity().getPackageManager());
                        btnFav.setImageDrawable(icon);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            //
        }
    }*/

    final void refillMainScreen() {

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.linearLayoutRepo);

        if (layout.getChildCount() > 0)
            layout.removeAllViews();

        Resources r = getActivity().getApplicationContext().getResources();

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

        LinearLayout row = new LinearLayout(getActivity());
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.VERTICAL);

        // Button
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                pixels_b);

        Button btnTag = new Button(getActivity());
        btnTag.setLayoutParams(params);
        btnTag.setText(btnTitle);
        btnTag.setId(i);

        // Only for Android LOWER than 3.0 !
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            btnTag.setTextColor(sharedPrefs.getString("prefAppTheme", "1")
                    .equalsIgnoreCase("1") ? Color.parseColor("#FFFFFF") : Color
                    .parseColor("#000000"));
        }

        btnTag.setBackgroundColor(Color.TRANSPARENT);

        // Separator
        LinearLayout.LayoutParams view_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                2);
        view_params.setMargins(separator_margin, 0, separator_margin, 0);
        View viewTag = new View(getActivity());
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
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.repo_buttons_dialog);
                dialog.setTitle(getDialogTitle(v));
                btnMap = (ImageButton) dialog.findViewById(R.id.btnMap2);
                btnShare = (ImageButton) dialog.findViewById(R.id.btnShare2);
                btnCopy = (ImageButton) dialog.findViewById(R.id.btnCopy2);
                btnFav = (ImageButton) dialog.findViewById(R.id.btnFav2);
                dialogAdjustment(dialog);
                //setFavBtnIcon();
                DBHelper.updateFavIcon(getActivity(), btnFav);
                dialog.show();

                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.shareCoordinates(getActivity(),
                                DBHelper.getShareBody(getActivity(),
                                        actionCoords, ""));
                    }
                });

                btnCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.clipboardCopy(getActivity().getApplicationContext(), actionCoords);
                        DBHelper.ShowToastT(getActivity(), getString(R.string.text_copied), Toast.LENGTH_LONG);
                    }
                });

                btnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        DBHelper.openOnMap(getActivity().getApplicationContext(), actionCoords);
                    }
                });

                btnFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (!DBHelper.shareFav(getActivity(),
                                DBHelper.getShareBody(getActivity(), actionCoords, ""))) {
                            Intent intent = new Intent(getActivity(), ChooseFavActivity.class);
                            startActivityForResult(intent, ACT_RESULT_FAV);
                        }
                        ;
                    }
                });

                btnFav.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(getActivity(), ChooseFavActivity.class);
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


