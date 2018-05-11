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

public class SecondFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public SecondFragment() {
    }

    public static SecondFragment newInstance(int sectionNumber, String events) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("events", events);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_detail_2, container, false);

        TextView textView = rootView.findViewById(R.id.section_label);
        TextView textView_bos = rootView.findViewById(R.id.textView_bos);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        String events = getArguments().getString("events");

        ArrayList results = new ArrayList<EventsPojo>();
        EventsPojo obj;

        try {
            JSONArray jsonArray = new JSONObject(events).getJSONArray("event");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //jsonObject.getString("@team");
                //jsonObject.getString("@type");
                //jsonObject.getString("@minute");
                //jsonObject.getString("@player");
                obj = new EventsPojo(jsonObject.getString("@team"), jsonObject.getString("@type"), jsonObject.getString("@minute"), jsonObject.getString("@player"));
                results.add(obj);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            try {
                JSONObject jsonObject = new JSONObject(events).getJSONObject("event");
                obj = new EventsPojo(jsonObject.getString("@team"), jsonObject.getString("@type"), jsonObject.getString("@minute"), jsonObject.getString("@player"));
                results.add(obj);
            } catch (JSONException e) {
                e.printStackTrace();
                textView_bos.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }

        recyclerView.setAdapter(new EventAdapter(results, getContext()));

        return rootView;
    }

}
