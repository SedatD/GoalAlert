package vavien.agency.goalalert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

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

        recyclerView_matchDetail.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_matchDetail.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        recyclerView_matchDetail.addItemDecoration(itemDecoration);

        String ligName = null;
        JSONObject jsonObject = null;
        if (getIntent().getExtras() != null) {
            ligName = getIntent().getExtras().getString("ligName");
            try {
                jsonObject = new JSONObject(getIntent().getExtras().getString("match"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

                /*JSONObject summaryLocal = jsonObject.getJSONObject("summary").getJSONObject("localteam");
                JSONObject summaryVisitor = jsonObject.getJSONObject("summary").getJSONObject("visitorteam");

                int localYellow = 0, localRed = 0, visitorYellow = 0, visitorRed = 0;

                if (!summaryLocal.get("yellowcards").equals(null)) {
                    try {
                        JSONArray jsonArray = summaryLocal.getJSONObject("yellowcards").getJSONArray("player");
                        localYellow = jsonArray.length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        localYellow = 1;
                    }
                }

                if (!summaryVisitor.get("yellowcards").equals(null)) {
                    try {
                        JSONArray jsonArray = summaryVisitor.getJSONObject("yellowcards").getJSONArray("player");
                        visitorYellow = jsonArray.length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        visitorYellow = 1;
                    }
                }

                if (!summaryLocal.get("redcards").equals(null)) {
                    try {
                        JSONArray jsonArray = summaryLocal.getJSONObject("redcards").getJSONArray("player");
                        localRed = jsonArray.length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        localRed = 1;
                    }
                }

                if (!summaryVisitor.get("redcards").equals(null)) {
                    try {
                        JSONArray jsonArray = summaryVisitor.getJSONObject("redcards").getJSONArray("player");
                        visitorRed = jsonArray.length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        visitorRed = 1;
                    }
                }*/

                ArrayList results = new ArrayList<MatchDetailPojo>();
                MatchDetailPojo obj;

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

                recyclerView_matchDetail.setAdapter(new MatchDetailAdapter(results, getApplicationContext()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }

    }

}
