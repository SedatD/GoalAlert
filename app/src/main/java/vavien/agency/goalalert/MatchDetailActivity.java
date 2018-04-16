package vavien.agency.goalalert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vavien.agency.goalalert.adapters.MatchDetailAdapter;
import vavien.agency.goalalert.pojoClasses.MatchDetailPojo;

public class MatchDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        TextView textView_leauge = findViewById(R.id.textView_leauge);
        TextView textView_localTeam = findViewById(R.id.textView_localTeam);
        TextView textView_visitorTeam = findViewById(R.id.textView_visitorTeam);
        TextView textView_minute = findViewById(R.id.textView_minute);
        TextView textView_score = findViewById(R.id.textView_score);
        TextView textView_referee = findViewById(R.id.textView_referee);
        TextView textView_venue = findViewById(R.id.textView_venue);
        RecyclerView recyclerView_matchDetail = findViewById(R.id.recyclerView_matchDetail);

        String jsonArrayString = null;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bundle.getString("leauge");
            bundle.getString("localTeam");
            bundle.getString("visitorTeam");
            bundle.getString("minute");
            bundle.getString("score");

            bundle.getString("referee");
            bundle.getString("venue");

            jsonArrayString = bundle.getString("jsonArrayString");

            //
            textView_leauge.setText(bundle.getString("leauge"));
            textView_localTeam.setText(bundle.getString("localTeam"));
            textView_visitorTeam.setText(bundle.getString("visitorTeam"));
            textView_minute.setText(bundle.getString("minute"));
            textView_score.setText(bundle.getString("score"));

            textView_referee.setText(bundle.getString("referee"));
            textView_venue.setText(bundle.getString("venue"));
        }

        if (jsonArrayString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                ArrayList results = new ArrayList<MatchDetailPojo>();
                MatchDetailPojo obj;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    obj = new MatchDetailPojo(jsonObject.getString("stat"), jsonObject.getInt("localValue"), jsonObject.getInt("visitorValue"));
                    results.add(obj);
                }
                recyclerView_matchDetail.setAdapter(new MatchDetailAdapter(results, getApplicationContext()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
