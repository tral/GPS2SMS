package ru.perm.trubnikov.gps2sms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.util.List;

public class ChooseFavActivity extends ActionBarActivity {

    private String[] myActNames;
    private String[] myPackages;

    @Override
    protected void onResume() {
        super.onResume();
        refillMainScreen();
    }

    // ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        setTheme(sharedPrefs.getString("prefAppTheme", "1").equalsIgnoreCase(
                "1") ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        setTitle(R.string.choose_fav_app);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_fav);

        refillMainScreen();

        ShowBackButton();

    }

    protected void refillMainScreen() {

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutFav);

        if (layout.getChildCount() > 0)
            layout.removeAllViews();

        Resources r = getApplicationContext().getResources();

        // число пикселей для высоты кнопок (относительно dp)
        int pixels_b = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
        int separators_margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());


        try {


            int i = 0;

            // List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);

            myActNames = new String[resInfo.size()];
            myPackages = new String[resInfo.size()];

            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {

                    // if (info.activityInfo.packageName.toLowerCase().equalsIgnoreCase(namePckg) || namePckg.equalsIgnoreCase("")) {

                    // Intent targeted = new Intent(android.content.Intent.ACTION_SEND);
                    //targeted.setType("text/plain"); // put here your mime type

                    //   if (info.activityInfo.packageName.toLowerCase().contains(nameApp) ||
                    //           info.activityInfo.name.toLowerCase().contains(nameApp)) {

                    //  if ((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){}else{
                    // targeted.putExtra(Intent.EXTRA_TEXT, "My body of post/email");
                    // targeted.putExtra(Intent.EXTRA_SUBJECT, "My subject");


                    //Log.d("gps", info.activityInfo.loadLabel(getPackageManager()) + " - " + info.activityInfo.packageName.toLowerCase() + " " + info.activityInfo.name.toLowerCase());

                    //  targeted.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    //  targeted.setPackage(info.activityInfo.packageName);
                    //   targeted.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    CharSequence label = info.loadLabel(getPackageManager());
                    // Intent extraIntents = new LabeledIntent(targeted, info.activityInfo.packageName, label, info.icon);

                    // targetedShareIntents.add(extraIntents);

                    initOneBtn(layout, i, pixels_b,
                            label.toString(),
                            info.activityInfo.packageName.toLowerCase(),
                            info.activityInfo.name.toLowerCase(),
                            separators_margin);
                    i++;
                    //}
                    //                  targetedShareIntents.add(targeted);
                    // }
                    //   }
                }

                //  Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
                //  chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                // startActivity(chooserIntent);
            }


        } catch (Exception e) {
            Log.d("gps",
                    "EXCEPTION! " + e.toString() + " Message:" + e.getMessage());

        }

    }


    // ------------------------------------------------------------------------------------------

    protected void initOneBtn(LinearLayout layout, int i, int pixels_b,
                              String name, String pkg, String act, int separator_margin) {

        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.VERTICAL);

        // Button
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                pixels_b);

        Button btnTag = new Button(this);
        // params.setMargins(-pixels_m, -pixels_m, -pixels_m, -pixels_m);
        btnTag.setLayoutParams(params);
        //btnTag.setText(name + System.getProperty("line.separator") + pkg);
        btnTag.setText(name);
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

        myPackages[i] = pkg;
        myActNames[i] = act;
        //  ids[i] = i;

       /* btnTag.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                actionCoordsId = ids[v.getId()];
                showDialog(MYCOORDS_PROPS_DIALOG_ID);
                return true;
            }
        });*/


        btnTag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ChooseFavActivity.this.getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("prefFavPackage", myPackages[v.getId()]);
                editor.putString("prefFavAct", myActNames[v.getId()]);
                editor.commit();
                finish();
                // custom dialog
                /*final Dialog dialog = new Dialog(ChooseFavActivity.this);
                dialog.setContentView(R.layout.options1_mycoords_dialog);
                dialog.setTitle(myCoordsNames[v.getId()]);
                dialog.show();

                btnShare = (ImageButton) dialog.findViewById(R.id.btnShare1);
                btnCopy = (ImageButton) dialog.findViewById(R.id.btnCopy1);
                btnMap = (ImageButton) dialog.findViewById(R.id.btnMap1);
*/


            }
        });

        row.addView(btnTag);
        row.addView(viewTag);
        layout.addView(row);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // call something for API Level 11+
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
    }
    /*
    @TargetApi(11)
    public void ShowBackButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // call something for API Level 11+
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }*/

}
