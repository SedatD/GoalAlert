package vavien.agency.goalalert.MatchDetail;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vavien.agency.goalalert.R;

public class MatchDetailActivity2 extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MatchDetailPageAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail2);

        TextView textView_leauge = findViewById(R.id.textView_leauge);
        ImageView imageView_localTeam = findViewById(R.id.imageView_localTeam);
        ImageView imageView_visitorTeam = findViewById(R.id.imageView_visitorTeam);
        TextView textView_localTeam = findViewById(R.id.textView_localTeam);
        TextView textView_visitorTeam = findViewById(R.id.textView_visitorTeam);
        TextView textView_minute = findViewById(R.id.textView_minute);
        TextView textView_score = findViewById(R.id.textView_score);
        TextView textView_referee = findViewById(R.id.textView_referee);
        TextView textView_venue = findViewById(R.id.textView_venue);

        String ligName = null;
        String events = null;
        JSONObject jsonObject = null;
        if (getIntent().getExtras() != null) {
            ligName = getIntent().getExtras().getString("ligName");
            events = getIntent().getExtras().getString("events");
            Log.wtf("MatchDetailAct", "events : " + events);
            try {
                jsonObject = new JSONObject(getIntent().getExtras().getString("match"));
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }

        if (jsonObject != null) {
            try {
                textView_leauge.setText(ligName);
                textView_localTeam.setText(jsonObject.getJSONObject("localteam").getString("@name"));
                textView_visitorTeam.setText(jsonObject.getJSONObject("visitorteam").getString("@name"));
                textView_minute.setText(jsonObject.getString("@timer"));
                textView_score.setText(jsonObject.getJSONObject("localteam").getString("@goals") + ":" + jsonObject.getJSONObject("visitorteam").getString("@goals"));
                textView_referee.setText("Referee : " + jsonObject.getJSONObject("matchinfo").getJSONObject("referee").getString("@name"));
                textView_venue.setText("Stad : " + jsonObject.getJSONObject("matchinfo").getJSONObject("stadium").getString("@name"));

                //jsonObject.getJSONObject("localteam").getString("@id");
                //jsonObject.getJSONObject("visitorteam").getString("@id");
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }

        mSectionsPagerAdapter = new MatchDetailPageAdapter(getSupportFragmentManager(), jsonObject + "", events);

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_match_detail_activity2, container, false);

            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }

    }*/

    /*public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }*/

}
