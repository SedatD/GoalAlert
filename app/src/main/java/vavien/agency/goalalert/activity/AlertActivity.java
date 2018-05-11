package vavien.agency.goalalert.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import vavien.agency.goalalert.util.DBHelper;
import vavien.agency.goalalert.util.MyService;
import vavien.agency.goalalert.R;
import vavien.agency.goalalert.model.LiveScoresPojo;

public class AlertActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    int spnPos = 0;
    private String local, visitor, matchId, minute, localScore, visitorScore;
    private Double bet = 0.0;
    private Button btn_setTime;
    private Button btn_noGoal, btn_05, btn_15, btn_25, btn_35, btn_45, btn_55, btn_e15, btn_e25, btn_e35, btn_e45, btn_e55, btn_btts_yes1, btn_btts_no1, btn_Score;
    private GradientDrawable btn_score, btts_yes, btts_no, btnno_GoalColor, btn05Color, btn15Color, btn25Color, btn35Color, btn45Color, btn55Color, btne15Color, btne25Color, btne35Color, btne45Color, btne55Color, btnsetTimeColor;
    private Spinner spn_choseMinute;
    private TextView date_timeTxt, txt_yourAlert, txt_Teams, txt_yourAlertBet;
    private DBHelper mydb;
    private MyService myService;
    private Intent mServiceIntent;
    private String choose;
    private Boolean isGeneric;
    private ArrayList results = new ArrayList<LiveScoresPojo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        //MainActivity.live = false;

        AdView adViewAlertAct = findViewById(R.id.adViewAlertAct);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewAlertAct.loadAd(adRequest);

        choose = getString(R.string.choose);
        String anytime = getString(R.string.any_time);
        String halftime = getString(R.string.half_time);
        String fulltime = getString(R.string.full_time);
        String[] spinnerItems = new String[]{
                choose,
                anytime,
                halftime,
                fulltime,
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
                        halftime,
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
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            } else if (bet == -1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            } else if (bet == -8.8) {
                                                //skor alarmı - genericte buraya hiç girmeyecek silinebilir
                                                insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                count++;
                                            } else if (bet == -9.9) {
                                                if (lsp.getLocalScore() == 0 && lsp.getVisitorScore() == 0) {
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            } else if ((lsp.getLocalScore() + lsp.getVisitorScore()) < Math.abs(bet)) {
                                                insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                count++;
                                            }
                                        }
                                    } else if (spnPos == -2) {
                                        // any time
                                        if (bet == 1.1) {
                                            if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                count++;
                                            }
                                        } else if (bet == -1.1) {
                                            if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                count++;
                                            }
                                        } else if (bet == -9.9) {
                                            if (lsp.getLocalScore() == 0 && lsp.getVisitorScore() == 0) {
                                                insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                count++;
                                            }
                                        } else {
                                            if (Math.abs(bet) > (lsp.getLocalScore() + lsp.getVisitorScore())) {
                                                insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                count++;
                                            }
                                        }
                                    } else if (spnPos == -3) {
                                        // half time
                                        if (lsp.getMinute() < 45 && lsp.getMinute() != 0) {
                                            if (bet == 1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            } else if (bet == -1.1) {
                                                if (lsp.getLocalScore() == 0 || lsp.getVisitorScore() == 0) {
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            } else if (bet == -9.9) {
                                                if (lsp.getLocalScore() == 0 && lsp.getVisitorScore() == 0) {
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            } else {
                                                if (Math.abs(bet) > (lsp.getLocalScore() + lsp.getVisitorScore())) {
                                                    insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                                    count++;
                                                }
                                            }
                                        }
                                    } else if (spnPos == -4) {
                                        // full time - genericte buraya hiç girmeyecek silinebilir
                                        insert(lsp.getLocalTeam(), lsp.getVisitorTeam(), spnPos, bet, lsp.getMatchId() + "");
                                        count++;
                                    }
                                }
                            }
                            if (count == 0) {
                                Toast.makeText(this, "No available matches", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(this, count + " Alarm Seted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Alarm cant seted. Try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mydb = new DBHelper(this);
                        mydb.insertContact(local, visitor, spnPos, bet, matchId);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        Set<String> myMatchList = preferences.getStringSet("myMatchList", null);
                        if (myMatchList == null)
                            myMatchList = new HashSet<>();
                        myMatchList.add(matchId);
                        editor.putStringSet("myMatchList", myMatchList);
                        editor.apply();

                        myService = new MyService(this);
                        mServiceIntent = new Intent(this, myService.getClass());

                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                if (!isMyServiceRunning(MyService.class)) {
                                    startService(mServiceIntent);
                                    Log.wtf("again", "servisi başlattı");
                                }
                                //startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                requestPermission();
                            }
                        } else {
                            requestPermission();
                        }
                    }
                }
                break;
        }
    }

    private void insert(String local, String visitor, int spnPos, double bet, String matchId) {
        mydb = new DBHelper(this);
        mydb.insertContact(local, visitor, spnPos, bet, matchId);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> myMatchList = preferences.getStringSet("myMatchList", null);
        if (myMatchList == null)
            myMatchList = new HashSet<>();
        myMatchList.add(matchId);
        editor.putStringSet("myMatchList", myMatchList);
        editor.apply();

        myService = new MyService(this);
        mServiceIntent = new Intent(this, myService.getClass());

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                if (!isMyServiceRunning(MyService.class)) {
                    startService(mServiceIntent);
                    Log.wtf("again", "servisi başlattı");
                }
                //startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                requestPermission();
            }
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//
        //int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED);
        //If the app does have this permission, then return true//
        //If the app doesn’t have this permission, then return false//
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isMyServiceRunning(MyService.class)) {
                        startService(mServiceIntent);
                        Log.wtf("again", "servisi başlattı");
                    }
                    //startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Log.wtf("AlertAct", "else den service'i baslattı yani permissionı alamadan servise gitti");
                    if (!isMyServiceRunning(MyService.class)) {
                        startService(mServiceIntent);
                        Log.wtf("again", "servisi başlattı");
                    }
                    //startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.wtf("AlertAct - isMyServiceRunning?", true + "");
                    return true;
                }
            }
        }
        Log.wtf("AlertAct - isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}