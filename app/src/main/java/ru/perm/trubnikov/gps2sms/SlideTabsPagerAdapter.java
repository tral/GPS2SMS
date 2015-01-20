package ru.perm.trubnikov.gps2sms;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SlideTabsPagerAdapter extends FragmentPagerAdapter {

    public SlideTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new RepoCoordsFragment();//RepoFragmentCoords();
            case 1:
                return new RepoSMSInFragment(); //RepoFragmentSMSIn();
            case 2:
                return new RepoSMSOutFragment(); //RepoFragmentSMSOut();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
