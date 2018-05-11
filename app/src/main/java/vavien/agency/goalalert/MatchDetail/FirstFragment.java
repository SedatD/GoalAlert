package vavien.agency.goalalert.MatchDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vavien.agency.goalalert.R;
import vavien.agency.goalalert.adapters.MatchDetailAdapter;
import vavien.agency.goalalert.model.MatchDetailPojo;

/**
 * Created by SD
 * on 9.05.2018.
 */

public class FirstFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public FirstFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FirstFragment newInstance(int sectionNumber, String jsonObjectString) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("jsonObjectString", jsonObjectString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_detail_1, container, false);

        TextView textView = rootView.findViewById(R.id.section_label);
        RecyclerView recyclerView_matchDetail = rootView.findViewById(R.id.recyclerView_matchDetail);

        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        recyclerView_matchDetail.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_matchDetail.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView_matchDetail.addItemDecoration(itemDecoration);

        String jsonObjectString = getArguments().getString("jsonObjectString");
        try {
            JSONObject jsonObject = new JSONObject(jsonObjectString);
            fillList(recyclerView_matchDetail, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void fillList(RecyclerView recyclerView_matchDetail, JSONObject jsonObject) {
        ArrayList results = new ArrayList<MatchDetailPojo>();
        MatchDetailPojo obj;

        try {

            JSONObject statsLocal = jsonObject.getJSONObject("stats").getJSONObject("localteam");
            JSONObject statsVisitor = jsonObject.getJSONObject("stats").getJSONObject("visitorteam");

            String text = "shots total";
            int l = statsLocal.getJSONObject("shots").getInt("@total");
            int v = statsVisitor.getJSONObject("shots").getInt("@total");
            float t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "shots ongoal";
            l = statsLocal.getJSONObject("shots").getInt("@ongoal");
            v = statsVisitor.getJSONObject("shots").getInt("@ongoal");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "shots offgoal";
            l = statsLocal.getJSONObject("shots").getInt("@offgoal");
            v = statsVisitor.getJSONObject("shots").getInt("@offgoal");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "shots blocked";
            l = statsLocal.getJSONObject("shots").getInt("@blocked");
            v = statsVisitor.getJSONObject("shots").getInt("@blocked");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "shots insidebox";
            l = statsLocal.getJSONObject("shots").getInt("@insidebox");
            v = statsVisitor.getJSONObject("shots").getInt("@insidebox");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "shots outsidebox";
            l = statsLocal.getJSONObject("shots").getInt("@outsidebox");
            v = statsVisitor.getJSONObject("shots").getInt("@outsidebox");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "fouls";
            l = statsLocal.getJSONObject("fouls").getInt("@total");
            v = statsVisitor.getJSONObject("fouls").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "corners";
            l = statsLocal.getJSONObject("corners").getInt("@total");
            v = statsVisitor.getJSONObject("corners").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "offsides";
            l = statsLocal.getJSONObject("offsides").getInt("@total");
            v = statsVisitor.getJSONObject("offsides").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "possestiontime";
            l = Integer.parseInt(statsLocal.getJSONObject("possestiontime").getString("@total").split("%")[0]);
            v = Integer.parseInt(statsVisitor.getJSONObject("possestiontime").getString("@total").split("%")[0]);
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "yellowcards";
            l = statsLocal.getJSONObject("yellowcards").getInt("@total");
            v = statsVisitor.getJSONObject("yellowcards").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "redcards";
            l = statsLocal.getJSONObject("redcards").getInt("@total");
            v = statsVisitor.getJSONObject("redcards").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "saves";
            l = statsLocal.getJSONObject("saves").getInt("@total");
            v = statsVisitor.getJSONObject("saves").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "passes total";
            l = statsLocal.getJSONObject("passes").getInt("@total");
            v = statsVisitor.getJSONObject("passes").getInt("@total");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "passes accurate";
            l = statsLocal.getJSONObject("passes").getInt("@accurate");
            v = statsVisitor.getJSONObject("passes").getInt("@accurate");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            text = "passes pct";
            l = statsLocal.getJSONObject("passes").getInt("@pct");
            v = statsVisitor.getJSONObject("passes").getInt("@pct");
            t = (float) (v / ((double) l + (double) v));
            obj = new MatchDetailPojo(text, l, v, t);
            results.add(obj);

            recyclerView_matchDetail.setAdapter(new MatchDetailAdapter(results, getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
