package vavien.agency.goalalert.MatchDetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by SD
 * on 9.05.2018.
 */

public class MatchDetailPageAdapter extends FragmentPagerAdapter {
    private String jsonObjectString,events;

    MatchDetailPageAdapter(FragmentManager fm,String jsonObjectString,String events) {
        super(fm);
        this.jsonObjectString = jsonObjectString;
        this.events = events;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FirstFragment.newInstance(position + 1,jsonObjectString);
            case 1:
                return SecondFragment.newInstance(position + 1,events);
            case 2:
                return ThirdFragment.newInstance(position + 1,jsonObjectString);
            default:
                return FirstFragment.newInstance(position + 1, jsonObjectString);
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}
