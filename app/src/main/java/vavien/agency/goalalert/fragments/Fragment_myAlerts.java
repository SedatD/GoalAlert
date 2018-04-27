package vavien.agency.goalalert.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import vavien.agency.goalalert.DBHelper;
import vavien.agency.goalalert.R;
import vavien.agency.goalalert.adapters.AlarmListAdapter;
import vavien.agency.goalalert.pojoClasses.AlertsPojo;

/**
 * Created by albur on 6.10.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class Fragment_myAlerts extends Fragment implements View.OnClickListener {
    private DBHelper mydb;
    private TextView textViewNoAlert;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View alertview = inflater.inflate(R.layout.tab_myalerts, container, false);

        textViewNoAlert = alertview.findViewById(R.id.textViewNoAlert);
        listView = alertview.findViewById(R.id.lw_alertTab);
        ImageButton imageButton_reklam = alertview.findViewById(R.id.imageButton_reklam);
        imageButton_reklam.setOnClickListener(this);

        listView.invalidateViews();

        //when you add the fragmentB to your Activity , make sure you set a Tag for it
        //Fragment_myAlerts f = (Fragment_myAlerts) getActivity().getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_B);
        //f.getMyListAdapter().notifyDataSetChanged();

        ShowAlarmData();

        return alertview;
    }

    public void ShowAlarmData() {
        List<AlertsPojo> alarms = new ArrayList<>();
        mydb = new DBHelper(getActivity().getApplicationContext());
        if (mydb.getFragmentViewList(getActivity().getApplicationContext()).size() != 0) {
            List<String> array_list = mydb.getFragmentViewList(getActivity().getApplicationContext());
            for (int i = 0; i < array_list.size(); i++) {
                String[] arr = mydb.getFragmentViewList(getActivity().getApplicationContext()).get(i).split(" - ");
                int dbId = Integer.parseInt(arr[0]);
                String matchId = arr[1];
                String mainText;
                if (arr.length == 5)
                    mainText = arr[2] + " - " + arr[3] + " - " + arr[4];
                else
                    mainText = arr[2] + " - " + arr[3];
                alarms.add(new AlertsPojo(dbId, mainText, matchId));
            }
        }

        if (alarms.size() == 0)
            textViewNoAlert.setVisibility(View.VISIBLE);

        AlarmListAdapter alarmListAdapter = new AlarmListAdapter(getActivity(), alarms, new AlarmListAdapter.onDoneClick() {
            @Override
            public void onClick(View v, int position, int dbidd, String matchId) {
                boolean aq = mydb.deleteMethod(dbidd);
                if (aq) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Set<String> myMatchList = preferences.getStringSet("myMatchList", null);
                    if (myMatchList != null && matchId != null) {
                        myMatchList.remove(matchId);
                        editor.putStringSet("myMatchList", myMatchList);
                        Log.wtf("Frag_myAlerts", "shared dan sildi - shared : " + myMatchList);
                    }
                    editor.apply();
                }

                ShowAlarmData();
            }
        });

        alarmListAdapter.notifyDataSetChanged();
        listView.setAdapter(alarmListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_reklam:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.betxscore.betxscore")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.betxscore.betxscore")));
                }
                break;
        }
    }

}
