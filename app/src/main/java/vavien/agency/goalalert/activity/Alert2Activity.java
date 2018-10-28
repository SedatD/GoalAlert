package vavien.agency.goalalert.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vavien.agency.goalalert.R;
import vavien.agency.goalalert.model.LiveScoresPojo;

public class Alert2Activity extends AppCompatActivity implements View.OnClickListener {
    int spnPos = 0;
    private String local, visitor, matchId, minute, localScore, visitorScore;
    private Double bet = 0.0;
    private Button btn_setTime;
    private Button btn_noGoal, btn_05, btn_15, btn_25, btn_35, btn_45, btn_55, btn_e15, btn_e25, btn_e35, btn_e45, btn_e55, btn_btts_yes1, btn_btts_no1, btn_Score;
    private GradientDrawable btn_score, btts_yes, btts_no, btnno_GoalColor, btn05Color, btn15Color, btn25Color, btn35Color, btn45Color, btn55Color, btne15Color, btne25Color, btne35Color, btne45Color, btne55Color, btnsetTimeColor;
    private Spinner spn_choseMinute;
    private TextView date_timeTxt, txt_yourAlert, txt_Teams, txt_yourAlertBet;
    private String choose;
    private Boolean isGeneric;
    private ArrayList results = new ArrayList<LiveScoresPojo>();
    private ArrayList multipleLspArray = new ArrayList<LiveScoresPojo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert2);

        AdView adViewAlertAct = findViewById(R.id.adViewAlertAct);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewAlertAct.loadAd(adRequest);

        choose = getString(R.string.choose);
        String anytime = getString(R.string.any_time);
        //String halftime = getString(R.string.half_time);
        //String fulltime = getString(R.string.full_time);
        String[] spinnerItems = new String[]{
                choose,
                anytime,
                //halftime,
                //fulltime,
                "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90"};

        Resources();
        ButtonsText();
        ClickListeners();
        ClickableFalse();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isGeneric = extras.getBoolean("isGeneric");
            if (isGeneric) {
                results = extras.getParcelableArrayList("results");
                //results = getIntent().getParcelableArrayListExtra("results");
                txt_Teams.setText(getString(R.string.set_multi_long));
                spinnerItems = new String[]{
                        choose,
                        anytime,
                        //halftime,
                        "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90"};
            } else {
                local = extras.getString("local");
                visitor = extras.getString("visitor");
                matchId = extras.getString("matchId");
                minute = extras.getString("minute");
                localScore = extras.getString("localScore");
                visitorScore = extras.getString("visitorScore");

                if (Integer.parseInt(minute) == 0) {
                    minute = getResources().getString(R.string.half_time);
                    txt_Teams.setText(local + "  " + localScore + " - " + visitorScore + "  " + visitor + "    " + minute);
                } else {
                    txt_Teams.setText(local + "  " + localScore + " - " + visitorScore + "  " + visitor + "    " + minute + "'");
                }
            }
        } else {
            //startActivity(new Intent(this, MainActivity.class));
            finish();
        }


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spn_choseMinute.setAdapter(spinnerArrayAdapter);
        spn_choseMinute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                buttonTransparent();

                txt_yourAlertBet.setText("");
                bet = 0.0;

                if (spn_choseMinute.getItemAtPosition(i).equals(choose)) {
                    ClickableFalse();
                    txt_yourAlert.setText(R.string.your_alert_choose);
                    spnPos = -1;
                } else if (spn_choseMinute.getItemAtPosition(i).equals(getString(R.string.any_time))) {
                    ClickableFalse();
                    changeButtonColorForAnyTime();
                    txt_yourAlert.setText(R.string.your_alert_any_time);
                    spnPos = -2;
                } else if (spn_choseMinute.getItemAtPosition(i).equals(getString(R.string.half_time))) {
                    changeButtonColor();
                    txt_yourAlert.setText(R.string.your_alert_half_time);
                    spnPos = -3;
                } else if (spn_choseMinute.getItemAtPosition(i).equals(getString(R.string.full_time))) {
                    changeButtonColor();
                    txt_yourAlert.setText(R.string.your_alert_full_time);
                    spnPos = -4;
                } else {
                    changeButtonColor();
                    String yourAlert = getString(R.string.your_alert);
                    txt_yourAlert.setText("" + yourAlert + " " + spn_choseMinute.getItemAtPosition(i).toString() + " min ");
                    spnPos = Integer.parseInt(spn_choseMinute.getItemAtPosition(i).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        String now = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        date_timeTxt.setText(now + " " + dayOfTheWeek);
    }

    public void buttonTransparent() {
        btts_yes = (GradientDrawable) btn_btts_yes1.getBackground();
        btts_yes.setColor(Color.TRANSPARENT);

        btts_no = (GradientDrawable) btn_btts_no1.getBackground();
        btts_no.setColor(Color.TRANSPARENT);

        btn_score = (GradientDrawable) btn_Score.getBackground();
        btn_score.setColor(Color.TRANSPARENT);

        btnno_GoalColor = (GradientDrawable) btn_noGoal.getBackground();
        btnno_GoalColor.setColor(Color.TRANSPARENT);

        btn05Color = (GradientDrawable) btn_05.getBackground();
        btn05Color.setColor(Color.TRANSPARENT);

        btn15Color = (GradientDrawable) btn_15.getBackground();
        btn15Color.setColor(Color.TRANSPARENT);

        btn25Color = (GradientDrawable) btn_25.getBackground();
        btn25Color.setColor(Color.TRANSPARENT);

        btn35Color = (GradientDrawable) btn_35.getBackground();
        btn35Color.setColor(Color.TRANSPARENT);

        btn45Color = (GradientDrawable) btn_45.getBackground();
        btn45Color.setColor(Color.TRANSPARENT);

        btn55Color = (GradientDrawable) btn_55.getBackground();
        btn55Color.setColor(Color.TRANSPARENT);

        btne15Color = (GradientDrawable) btn_e15.getBackground();
        btne15Color.setColor(Color.TRANSPARENT);

        btne25Color = (GradientDrawable) btn_e25.getBackground();
        btne25Color.setColor(Color.TRANSPARENT);

        btne35Color = (GradientDrawable) btn_e35.getBackground();
        btne35Color.setColor(Color.TRANSPARENT);

        btne45Color = (GradientDrawable) btn_e45.getBackground();
        btne45Color.setColor(Color.TRANSPARENT);

        btne55Color = (GradientDrawable) btn_e55.getBackground();
        btne55Color.setColor(Color.TRANSPARENT);

        btnsetTimeColor = (GradientDrawable) btn_setTime.getBackground();
        btnsetTimeColor.setColor(Color.TRANSPARENT);
    }

    public void changeButtonColor() {
        btts_yes = (GradientDrawable) btn_btts_yes1.getBackground();
        btts_yes.setColor(ContextCompat.getColor(this, R.color.black));

        btts_no = (GradientDrawable) btn_btts_no1.getBackground();
        btts_no.setColor(ContextCompat.getColor(this, R.color.red));

        if (!isGeneric) {
            btn_score = (GradientDrawable) btn_Score.getBackground();
            btn_score.setColor(ContextCompat.getColor(this, R.color.red));
        }

        btnno_GoalColor = (GradientDrawable) btn_noGoal.getBackground();
        btnno_GoalColor.setColor(ContextCompat.getColor(this, R.color.black));

        btn05Color = (GradientDrawable) btn_05.getBackground();
        btn05Color.setColor(ContextCompat.getColor(this, R.color.red));

        btn15Color = (GradientDrawable) btn_15.getBackground();
        btn15Color.setColor(ContextCompat.getColor(this, R.color.black));

        btn25Color = (GradientDrawable) btn_25.getBackground();
        btn25Color.setColor(ContextCompat.getColor(this, R.color.red));

        btn35Color = (GradientDrawable) btn_35.getBackground();
        btn35Color.setColor(ContextCompat.getColor(this, R.color.black));

        btn45Color = (GradientDrawable) btn_45.getBackground();
        btn45Color.setColor(ContextCompat.getColor(this, R.color.red));

        btn55Color = (GradientDrawable) btn_55.getBackground();
        btn55Color.setColor(ContextCompat.getColor(this, R.color.black));

        btne15Color = (GradientDrawable) btn_e15.getBackground();
        btne15Color.setColor(ContextCompat.getColor(this, R.color.red));

        btne25Color = (GradientDrawable) btn_e25.getBackground();
        btne25Color.setColor(ContextCompat.getColor(this, R.color.black));

        btne35Color = (GradientDrawable) btn_e35.getBackground();
        btne35Color.setColor(ContextCompat.getColor(this, R.color.red));

        btne45Color = (GradientDrawable) btn_e45.getBackground();
        btne45Color.setColor(ContextCompat.getColor(this, R.color.black));

        btne55Color = (GradientDrawable) btn_e55.getBackground();
        btne55Color.setColor(ContextCompat.getColor(this, R.color.red));

        btnsetTimeColor = (GradientDrawable) btn_setTime.getBackground();
        btnsetTimeColor.setColor(ContextCompat.getColor(this, R.color.loginButton));

        btn_noGoal.setClickable(true);
        btn_btts_yes1.setClickable(true);
        btn_btts_no1.setClickable(true);
        if (!isGeneric) {
            btn_Score.setClickable(true);
        }
        btn_05.setClickable(true);
        btn_15.setClickable(true);
        btn_25.setClickable(true);
        btn_35.setClickable(true);
        btn_45.setClickable(true);
        btn_55.setClickable(true);
        btn_e15.setClickable(true);
        btn_e25.setClickable(true);
        btn_e35.setClickable(true);
        btn_e45.setClickable(true);
        btn_e55.setClickable(true);
    }

    public void changeButtonColorForAnyTime() {
        btts_yes.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        btn_btts_yes1.setClickable(true);

        btn05Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        btn_05.setClickable(true);

        btn15Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        btn_15.setClickable(true);

        btn25Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        btn_25.setClickable(true);

        btn35Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        btn_35.setClickable(true);

        btn45Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        btn_45.setClickable(true);

        btn55Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        btn_55.setClickable(true);

        btnsetTimeColor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.set_time));
        btn_setTime.setClickable(true);
    }

    public void Resources() {
        spn_choseMinute = findViewById(R.id.spinner);
        btn_setTime = findViewById(R.id.btn_setTime);
        date_timeTxt = findViewById(R.id.date_timeTxt1);
        txt_yourAlert = findViewById(R.id.txt_yourAlert);
        txt_Teams = findViewById(R.id.txt_teams);
        txt_yourAlertBet = findViewById(R.id.txt_yourAlertBet);

        //İnclude 5
        View view5 = findViewById(R.id.include_5);
        btn_Score = view5.findViewById(R.id.buttonOne);
        btn_btts_yes1 = view5.findViewById(R.id.buttonTwo);
        btn_btts_no1 = view5.findViewById(R.id.buttonThree);

        // İnclude 1
        View view1 = findViewById(R.id.include_1);
        btn_noGoal = view1.findViewById(R.id.buttonOne);
        btn_05 = view1.findViewById(R.id.buttonTwo);
        btn_15 = view1.findViewById(R.id.buttonThree);

        // İnclude 2
        View view2 = findViewById(R.id.include_2);
        btn_25 = view2.findViewById(R.id.buttonOne);
        btn_35 = view2.findViewById(R.id.buttonTwo);
        btn_45 = view2.findViewById(R.id.buttonThree);


        // İnclude 3
        View view3 = findViewById(R.id.include_3);
        btn_55 = view3.findViewById(R.id.buttonOne);
        btn_e15 = view3.findViewById(R.id.buttonTwo);
        btn_e25 = view3.findViewById(R.id.buttonThree);

        // İnclude 4
        View view4 = findViewById(R.id.include_4);
        btn_e35 = view4.findViewById(R.id.buttonOne);
        btn_e45 = view4.findViewById(R.id.buttonTwo);
        btn_e55 = view4.findViewById(R.id.buttonThree);
    }

    public void ButtonsText() {
        btn_noGoal.setText(R.string.no_goal);
        btn_btts_yes1.setText(R.string.btts_yes);
        btn_btts_no1.setText(R.string.btts_no);
        btn_Score.setText(R.string.score);
        btn_05.setText("0,5+");
        btn_15.setText("1,5+");
        btn_25.setText("2,5+");
        btn_35.setText("3,5+");
        btn_45.setText("4,5+");
        btn_55.setText("5,5+");
        btn_e15.setText("-1,5");
        btn_e25.setText("-2,5");
        btn_e35.setText("-3,5");
        btn_e45.setText("-4,5");
        btn_e55.setText("-5,5");
    }

    public void ClickListeners() {
        btn_setTime.setOnClickListener(this);

        btn_btts_yes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btts_yes = (GradientDrawable) btn_btts_yes1.getBackground();
                btts_yes.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_btts_yes1.getText());
                bet = 1.1;
            }
        });

        btn_btts_no1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btts_no = (GradientDrawable) btn_btts_no1.getBackground();
                btts_no.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_btts_no1.getText());
                bet = -1.1;
            }
        });

        btn_Score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn_score = (GradientDrawable) btn_Score.getBackground();
                btn_score.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_Score.getText());
                bet = -8.8;
            }
        });

        btn_noGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btnno_GoalColor = (GradientDrawable) btn_noGoal.getBackground();
                btnno_GoalColor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_noGoal.getText());
                bet = -9.9;
            }
        });

        btn_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn05Color = (GradientDrawable) btn_05.getBackground();
                btn05Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_05.getText());
                bet = 0.5;
            }
        });

        btn_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn15Color = (GradientDrawable) btn_15.getBackground();
                btn15Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_15.getText());
                bet = 1.5;
            }
        });

        btn_25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn25Color = (GradientDrawable) btn_25.getBackground();
                btn25Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_25.getText());
                bet = 2.5;
            }
        });

        btn_35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn35Color = (GradientDrawable) btn_35.getBackground();
                btn35Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_35.getText());
                bet = 3.5;
            }
        });

        btn_45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn45Color = (GradientDrawable) btn_45.getBackground();
                btn45Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_45.getText());
                bet = 4.5;
            }
        });

        btn_55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btn55Color = (GradientDrawable) btn_55.getBackground();
                btn55Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_55.getText());
                bet = 5.5;
            }
        });

        btn_e15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btne15Color = (GradientDrawable) btn_e15.getBackground();
                btne15Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_e15.getText());
                bet = -1.5;
            }
        });

        btn_e25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btne25Color = (GradientDrawable) btn_e25.getBackground();
                btne25Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_e25.getText());
                bet = -2.5;
            }
        });

        btn_e35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btne35Color = (GradientDrawable) btn_e35.getBackground();
                btne35Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_e35.getText());
                bet = -3.5;
            }
        });

        btn_e45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btne45Color = (GradientDrawable) btn_e45.getBackground();
                btne45Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_e45.getText());
                bet = -4.5;
            }
        });

        btn_e55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spn_choseMinute.getSelectedItem().equals(getString(R.string.any_time)))
                    changeButtonColorForAnyTime();
                else
                    changeButtonColor();
                btne55Color = (GradientDrawable) btn_e55.getBackground();
                btne55Color.setColor(ContextCompat.getColor(getApplicationContext(), R.color.toolbar));

                txt_yourAlertBet.setText(btn_e55.getText());
                bet = -5.5;
            }
        });
    }

    public void ClickableFalse() {
        btn_noGoal.setClickable(false);
        btn_btts_yes1.setClickable(false);
        btn_btts_no1.setClickable(false);
        btn_Score.setClickable(false);
        btn_05.setClickable(false);
        btn_15.setClickable(false);
        btn_25.setClickable(false);
        btn_35.setClickable(false);
        btn_45.setClickable(false);
        btn_55.setClickable(false);
        btn_e15.setClickable(false);
        btn_e25.setClickable(false);
        btn_e35.setClickable(false);
        btn_e45.setClickable(false);
        btn_e55.setClickable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setTime:
                if (spnPos == -1) {
                    Toast.makeText(this, R.string.please_choose, Toast.LENGTH_SHORT).show();
                } else if (bet == 0.0) {
                    Toast.makeText(this, R.string.please_choose, Toast.LENGTH_SHORT).show();
                } else {
                    if (isGeneric) {
                        if (results != null) {
                            LiveScoresPojo lsp;
                            int count = 0;
                            for (int i = 0; i < results.size(); i++) {
                                lsp = (LiveScoresPojo) results.get(i);
                                if (lsp.getMatchId() != -1) {
                                    if (spnPos > 0) {
                                        // dk bazlı
                                        int min = lsp.getMinute();
                                        if (min == 0)
                                            min = 45;
                                        if (spnPos > min) {
                                            if (bet == 1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            } else if (bet == -1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            } else if (bet == -8.8) {
                                                //skor alarmı - genericte buraya hiç girmeyecek silinebilir
                                                //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                multipleLspArray.add(lsp);
                                                count++;
                                            } else if (bet == -9.9) {
                                                if (lsp.getLocalScore() == 0 && lsp.getVisitorScore() == 0) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            } else if ((lsp.getLocalScore() + lsp.getVisitorScore()) < Math.abs(bet)) {
                                                //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                multipleLspArray.add(lsp);
                                                count++;
                                            }
                                        }
                                    } else if (spnPos == -2) {
                                        // any time
                                        if (bet == 1.1) {
                                            if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                multipleLspArray.add(lsp);
                                                count++;
                                            }
                                        } else if (bet == -1.1) {
                                            if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                multipleLspArray.add(lsp);
                                                count++;
                                            }
                                        } else if (bet == -9.9) {
                                            if (lsp.getLocalScore() == 0 && lsp.getVisitorScore() == 0) {
                                                //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                multipleLspArray.add(lsp);
                                                count++;
                                            }
                                        } else {
                                            if (Math.abs(bet) > (lsp.getLocalScore() + lsp.getVisitorScore())) {
                                                //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                multipleLspArray.add(lsp);
                                                count++;
                                            }
                                        }
                                    } else if (spnPos == -3) {
                                        // half time
                                        if (lsp.getMinute() < 45 && lsp.getMinute() != 0) {
                                            if (bet == 1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            } else if (bet == -1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            } else if (bet == -9.9) {
                                                if (lsp.getLocalScore() == 0 && lsp.getVisitorScore() == 0) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            } else {
                                                if (Math.abs(bet) > (lsp.getLocalScore() + lsp.getVisitorScore())) {
                                                    //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    multipleLspArray.add(lsp);
                                                    count++;
                                                }
                                            }
                                        }
                                    } else if (spnPos == -4) {
                                        // full time - genericte buraya hiç girmeyecek silinebilir
                                        //insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                        multipleLspArray.add(lsp);
                                        count++;
                                    }
                                }
                            }
                            if (count == 0) {
                                Toast.makeText(this, "No available matches", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, count + " Alarm Seted", Toast.LENGTH_SHORT).show();
                                String langDef = "en";
                                String lang = Locale.getDefault().getLanguage();
                                if (lang.equals("de") || lang.equals("es") || lang.equals("fr") || lang.equals("pt") || lang.equals("ru") || lang.equals("tr"))
                                    langDef = lang;
                                newMethodPostMultiple(multipleLspArray, spnPos, langDef);
                            }
                        } else {
                            Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String langDef = "en";
                        String lang = Locale.getDefault().getLanguage();
                        if (lang.equals("de") || lang.equals("es") || lang.equals("fr") || lang.equals("pt") || lang.equals("ru") || lang.equals("tr"))
                            langDef = lang;
                        newMethodPost(matchId, local, visitor, bet, spnPos, langDef);
                    }
                }
                break;
        }
    }

    private void newMethodPost(final String match_id, final String localteam, final String visitorteam, final Double bet, final int bet_minute, final String lang) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/setAlertForAndroid", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("Alert2 newMethodPost response : ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean("result");
                    Log.wtf("Alert2 newMethodPost isSuccess : ", isSuccess + "");
                    if (isSuccess)
                        startActivity(new Intent(Alert2Activity.this, MainActivity.class));
                    else
                        Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    Log.wtf("Alert2 newMethodPost", "request catche girdi" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("Error.Response Alert2 newMethodPost", error.toString());
                Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                    jsonObject.put("match_id", match_id);
                    jsonObject.put("localteam", localteam);
                    jsonObject.put("visitorteam", visitorteam);
                    jsonObject.put("bet", bet + "");
                    jsonObject.put("bet_minute", bet_minute + "");
                    jsonObject.put("lang", lang + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("androidParams", jsonObject + "");
                Log.wtf("newMethodPost", "params : " + params);
                return params;
            }
        };
        postRequest.setShouldCache(false);// no caching url...
        postRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,//time to wait for it in this case 10s
                        20,//tryies in case of error
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //MySingleton.getInstance(ctx).addToRequestQueue(postRequest);
        queue.add(postRequest);
    }

    private void newMethodPostMultiple(final ArrayList multipleLspArray, final int bet_minute, final String lang) {
        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < multipleLspArray.size(); i++) {
            jsonObject = new JSONObject();
            LiveScoresPojo lsp = (LiveScoresPojo) multipleLspArray.get(i);
            try {
                jsonObject.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                jsonObject.put("match_id", lsp.getMatchId());
                jsonObject.put("localteam", lsp.getLocalTeam());
                jsonObject.put("visitorteam", lsp.getVisitorTeam());
                jsonObject.put("bet", bet + "");
                jsonObject.put("bet_minute", bet_minute + "");
                jsonObject.put("lang", lang + "");

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final JSONArray finalJsonArray = jsonArray;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/groupAlert",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("Alert2 newMethodPostMultiple response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean isSuccess = jsonObject.getBoolean("result");
                            if (isSuccess)
                                startActivity(new Intent(Alert2Activity.this, MainActivity.class));
                            else
                                Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.wtf("Alert2 newMethodPostMultiple", "request catche girdi" + e);
                            Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Error.Response Alert2 newMethodPostMultiple", error.toString());
                        Toast.makeText(Alert2Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //params.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                //params.put("lang", lang);
                params.put("multipleLspArray", finalJsonArray + "");
                Log.wtf("newMethodPostMultiple", "params : " + params);
                return params;
            }
        };
        postRequest.setShouldCache(false);// no caching url...
        postRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,//time to wait for it in this case 10s
                        20,//tryies in case of error
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
