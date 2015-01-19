package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseFavListFragment extends ListFragment {

    protected ArrayList<Drawable> mIcons;
    protected ArrayList<String> mPackages;
    protected ArrayList<String> mActivities;
    protected ArrayList<String> mDescrs;
    protected ArrayList<String> mLabels;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPackages = new ArrayList<String>();
        mActivities = new ArrayList<String>();
        mLabels = new ArrayList<String>();
        mDescrs = new ArrayList<String>();
        mIcons = new ArrayList<Drawable>();

        getDataRows();
        ChooseFavListAdapter adapter = new ChooseFavListAdapter(
                getActivity(),
                mPackages.toArray(new String[mPackages.size()]),
                mLabels.toArray(new String[mLabels.size()]),
                mDescrs.toArray(new String[mDescrs.size()]),
                mIcons.toArray(new Drawable[mIcons.size()])
        );

        setListAdapter(adapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        String lPackage = (String) getListAdapter().getItem(position);
        int item_id = (int) getListAdapter().getItemId(position);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("prefFavPackage", lPackage);
        editor.putString("prefFavAct", mActivities.get(item_id));
        editor.commit();
        getActivity().finish();

        //Toast.makeText(getActivity(), "item_id = " + item_id + " pckg: " + lPackage + " act: " + mActivities.get(item_id), Toast.LENGTH_SHORT).show();

    }


    protected void getDataRows() {

        try {

            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> resInfo = pm.queryIntentActivities(share, 0);

            // Sort
            Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(pm));

            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    mPackages.add(info.activityInfo.packageName.toLowerCase());
                    mActivities.add(info.activityInfo.name.toLowerCase());
                    mLabels.add(info.activityInfo.loadLabel(pm).toString());
                    mDescrs.add(info.activityInfo.applicationInfo.loadLabel(pm).toString());
                    mIcons.add(info.activityInfo.applicationInfo.loadIcon(pm));
                }
            }

        } catch (Exception e) {
            Log.d("gps",
                    "EXCEPTION! " + e.toString() + " Message:" + e.getMessage());

        }

    }


}
