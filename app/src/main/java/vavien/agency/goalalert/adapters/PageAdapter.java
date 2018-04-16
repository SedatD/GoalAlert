package vavien.agency.goalalert.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import vavien.agency.goalalert.fragments.Fragment_nextMatch;
import vavien.agency.goalalert.fragments.Fragment_result;
import vavien.agency.goalalert.fragments.Fragment_liveScores;
import vavien.agency.goalalert.fragments.Fragment_myAlerts;

public class PageAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment_liveScores tab1 = new Fragment_liveScores();
                return tab1;
            case 1:
                Fragment_result tab2 = new Fragment_result();
                return tab2;
            case 2:
                Fragment_nextMatch tab4 = new Fragment_nextMatch();
                return tab4;
            case 3:
                Fragment_myAlerts tab3 = new Fragment_myAlerts();
                return tab3;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}