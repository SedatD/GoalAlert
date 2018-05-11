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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vavien.agency.goalalert.R;

/**
 * Created by SD
 * on 9.05.2018.
 */

public class ThirdFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ThirdFragment() {
    }

    public static ThirdFragment newInstance(int sectionNumber, String jsonObjectString) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("jsonObjectString", jsonObjectString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_detail_3, container, false);

        TextView textView = rootView.findViewById(R.id.section_label);
        TextView textView_formation_local = rootView.findViewById(R.id.textView_formation_local);
        TextView textView_formation_visitor = rootView.findViewById(R.id.textView_formation_visitor);
        TextView textView_ilk = rootView.findViewById(R.id.textView_ilk);
        TextView textView_subs = rootView.findViewById(R.id.textView_subs);
        RecyclerView recyclerView_teams_local = rootView.findViewById(R.id.recyclerView_teams_local);
        RecyclerView recyclerView_teams_visitor = rootView.findViewById(R.id.recyclerView_teams_visitor);
        RecyclerView recyclerView_subs_local = rootView.findViewById(R.id.recyclerView_subs_local);
        RecyclerView recyclerView_subs_visitor = rootView.findViewById(R.id.recyclerView_subs_visitor);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

        recyclerView_teams_local.setHasFixedSize(true);
        recyclerView_teams_visitor.setHasFixedSize(true);
        recyclerView_subs_local.setHasFixedSize(true);
        recyclerView_subs_visitor.setHasFixedSize(true);

        recyclerView_teams_local.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_teams_visitor.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_subs_local.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_subs_visitor.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView_teams_local.addItemDecoration(itemDecoration);
        recyclerView_teams_visitor.addItemDecoration(itemDecoration);
        recyclerView_subs_local.addItemDecoration(itemDecoration);
        recyclerView_subs_visitor.addItemDecoration(itemDecoration);

        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        String jsonObjectString = getArguments().getString("jsonObjectString");
        try {
            JSONObject jsonObject = new JSONObject(jsonObjectString);

            textView_ilk.setVisibility(View.VISIBLE);
            textView_subs.setVisibility(View.VISIBLE);

            String formationLocal = jsonObject.getJSONObject("teams").getJSONObject("localteam").getString("@formation");
            String formationVisitor = jsonObject.getJSONObject("teams").getJSONObject("visitorteam").getString("@formation");

            textView_formation_local.setText(formationLocal);
            textView_formation_visitor.setText(formationVisitor);

            JSONArray jsonArrayTeamsLocal = jsonObject.getJSONObject("teams").getJSONObject("localteam").getJSONArray("player");
            JSONArray jsonArrayTeamsVisitor = jsonObject.getJSONObject("teams").getJSONObject("visitorteam").getJSONArray("player");

            fillList(recyclerView_teams_local, jsonArrayTeamsLocal);
            fillList(recyclerView_teams_visitor, jsonArrayTeamsVisitor);

            JSONArray jsonArraySubsLocal = jsonObject.getJSONObject("substitutes").getJSONObject("localteam").getJSONArray("player");
            JSONArray jsonArraySubsVisitor = jsonObject.getJSONObject("substitutes").getJSONObject("visitorteam").getJSONArray("player");

            fillList(recyclerView_subs_local, jsonArraySubsLocal);
            fillList(recyclerView_subs_visitor, jsonArraySubsVisitor);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void fillList(RecyclerView recyclerView_matchDetail, JSONArray jsonArray) {
        ArrayList results = new ArrayList<KadroPojo>();
        KadroPojo obj;

        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String pos = jsonObject.getString("@pos");
                String number = jsonObject.getString("@number");
                String name = jsonObject.getString("@name");

                obj = new KadroPojo(pos, number, name);
                results.add(obj);
            }

            recyclerView_matchDetail.setAdapter(new KadroAdapter(results, getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
