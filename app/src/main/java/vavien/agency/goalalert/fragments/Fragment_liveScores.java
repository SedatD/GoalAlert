package vavien.agency.goalalert.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vavien.agency.goalalert.MatchDetail.MatchDetailActivity2;
import vavien.agency.goalalert.R;
import vavien.agency.goalalert.activity.Alert2Activity;
import vavien.agency.goalalert.activity.MainActivity;
import vavien.agency.goalalert.adapters.LiveScoresRecyclerViewAdapter;
import vavien.agency.goalalert.model.LiveScoresPojo;
import vavien.agency.goalalert.util.MySingleton;

public class Fragment_liveScores extends Fragment implements View.OnClickListener {
    int[][][] matrix = new int[999][99][3];
    private int huntFilter = 0;
    private int huntFilterMin = 0;
    private JSONArray jsonArray_lastLive;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList results, dummy;
    private boolean flag = true, keyboardOpen = false, matchLenghtBool = false;
    private ImageView btn_Search, btn_CancelSearch;
    private EditText edt_Search;
    private TextView date_timeTxt, txtNoLiveMatch, textView_noFilter;
    private Button btnGeneric, btnHuntNoGoal, btnHuntDraw, btnHuntWinToNil, btnHunt05, btnHuntm25, btnHunt25, btnHuntHomeWin, btnHuntAwayWin, btnHunt030, btnHunt3060, btnHunt6090;
    private GradientDrawable gradBtn;
    private Context ctx;
    private int matchLenght = 0;
    private LiveScoresPojo aq;
    private ProgressBar progressBar;

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.tab_livescores, container, false);

        rootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootview.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootview.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                //Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    keyboardOpen = true;
                } else {
                    // keyboard is closed
                    keyboardOpen = false;
                }
            }
        });

        setRetainInstance(true);

        progressBar = rootview.findViewById(R.id.progressBar);
        progressBar.bringToFront();
        progressBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.denemetab));

        btn_Search = rootview.findViewById(R.id.searchButton);
        btn_CancelSearch = rootview.findViewById(R.id.searchCancelButton);
        edt_Search = rootview.findViewById(R.id.searchEditText);
        txtNoLiveMatch = rootview.findViewById(R.id.txtNoLiveMatch);
        textView_noFilter = rootview.findViewById(R.id.textView_noFilter);

        date_timeTxt = rootview.findViewById(R.id.date_timeTxt);

        btnHuntNoGoal = rootview.findViewById(R.id.btnHuntNoGoal);
        btnHuntDraw = rootview.findViewById(R.id.btnHuntDraw);
        btnHuntWinToNil = rootview.findViewById(R.id.btnHuntWinToNil);
        btnHunt05 = rootview.findViewById(R.id.btnHunt05);
        btnHuntm25 = rootview.findViewById(R.id.btnHuntm25);
        btnHunt25 = rootview.findViewById(R.id.btnHunt25);
        btnHuntHomeWin = rootview.findViewById(R.id.btnHuntHomeWin);
        btnHuntAwayWin = rootview.findViewById(R.id.btnHuntAwayWin);
        btnHunt030 = rootview.findViewById(R.id.btnHunt030);
        btnHunt3060 = rootview.findViewById(R.id.btnHunt3060);
        btnHunt6090 = rootview.findViewById(R.id.btnHunt6090);

        btnGeneric = rootview.findViewById(R.id.btnGeneric);
        btnGeneric.setOnClickListener(this);

        gradBtn = (GradientDrawable) btnHuntNoGoal.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHuntDraw.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHuntWinToNil.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHunt05.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHuntm25.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHunt25.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHuntHomeWin.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHuntAwayWin.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHunt030.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHunt3060.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);
        gradBtn = (GradientDrawable) btnHunt6090.getBackground();
        gradBtn.setColor(Color.TRANSPARENT);

        btnHuntNoGoal.setOnClickListener(this);
        btnHuntDraw.setOnClickListener(this);
        btnHuntWinToNil.setOnClickListener(this);
        btnHunt05.setOnClickListener(this);
        btnHuntm25.setOnClickListener(this);
        btnHunt25.setOnClickListener(this);
        btnHuntHomeWin.setOnClickListener(this);
        btnHuntAwayWin.setOnClickListener(this);
        btnHunt030.setOnClickListener(this);
        btnHunt3060.setOnClickListener(this);
        btnHunt6090.setOnClickListener(this);
        btn_Search.setOnClickListener(this);
        btn_CancelSearch.setOnClickListener(this);

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        String now = df.format(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        date_timeTxt.setText(now + " " + dayOfTheWeek);

        /*queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();*/

        MainActivity.live = true;
        huntFilter = 0;
        huntFilterMin = 0;
        getReqFuncJsnArr("http://opucukgonder.com/tipster/index.php/Service/lastLive");

        mRecyclerView = rootview.findViewById(R.id.recyclerView_liveScores);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        //mRecyclerView.addOnScrollListener(new CustomScrollListener(linearLayout_generic));

        // Code to Add an item with default animation
        //((LiveScoresRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((LiveScoresRecyclerViewAdapter) mAdapter).deleteItem(index);

        edt_Search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    hideKeyboard(v);
            }
        });

        edt_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edt_Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            final InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(edt_Search.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });

                dummy = new ArrayList<LiveScoresPojo>();
                if (results != null)
                    for (int i = 0; i < results.size(); i++) {
                        LiveScoresPojo aq = (LiveScoresPojo) results.get(i);
                        if (aq.getLocalTeam() != null)
                            if (edt_Search.getText().length() <= aq.getLocalTeam().length() || edt_Search.getText().length() <= aq.getVisitorTeam().length())
                                if (aq.getLocalTeam().toLowerCase().trim().contains(edt_Search.getText().toString().toLowerCase().trim()) || aq.getVisitorTeam().toLowerCase().trim().contains(edt_Search.getText().toString().toLowerCase().trim()))
                                    dummy.add(aq);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
        ctx = context;
    }

    public void getReqFuncJsnArr(final String url) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                //Log.wtf("Response liveScores", response.toString());
                progressBar.setVisibility(View.GONE);
                jsonArray_lastLive = response;

                if (jsonArray_lastLive.length() == 0) {
                    txtNoLiveMatch.setVisibility(View.VISIBLE);
                } else {
                    txtNoLiveMatch.setVisibility(View.GONE);
                }

                if (matchLenght != 0)
                    matchLenghtBool = matchLenght == jsonArray_lastLive.length();

                matchLenght = jsonArray_lastLive.length();

                results = new ArrayList<LiveScoresPojo>();
                LiveScoresPojo obj;
                try {
                    for (int i = 0; i < jsonArray_lastLive.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray_lastLive.get(i);
                        int leagueId = jsonObject.getInt("league_id");
                        String leagueName = jsonObject.getString("league_name");
                        String flags = jsonObject.getString("flags");
                        JSONArray jsonArray = jsonObject.getJSONArray("match");

                        obj = new LiveScoresPojo(leagueId, leagueName, flags);
                        //results.add(obj);
                        boolean isLeaugeFirst = true;

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(j);
                            int matchId = jsonObject1.getInt("match_id");
                            String localTeam = jsonObject1.getString("localteam");
                            String visitorTeam = jsonObject1.getString("visitorteam");
                            int localScore = jsonObject1.getInt("localScore");
                            int visitorScore = jsonObject1.getInt("visitorScore");
                            int minute = jsonObject1.getInt("minute");

                            //String events = jsonObject1.getString("events");

                            switch (huntFilter) {
                                case 1:
                                    if (localScore == 0 && visitorScore == 0) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 2:
                                    if (localScore == visitorScore && localScore > 0 && visitorScore > 0) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 3:
                                    if ((localScore == 0 && visitorScore > 0) || (visitorScore == 0 && localScore > 0)) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 4:
                                    if ((localScore + visitorScore) > 0 && (localScore + visitorScore) < 3) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 5:
                                    if ((localScore + visitorScore) < 3) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 6:
                                    if ((localScore + visitorScore) > 2) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 7:
                                    if (localScore > visitorScore) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                case 8:
                                    if (localScore < visitorScore) {
                                        if (methodForHuntFilterMin(minute)) {
                                            if (isLeaugeFirst) {
                                                results.add(obj);
                                                isLeaugeFirst = false;
                                            }
                                            if (flag) {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                            } else {
                                                obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                            }

                                            matrix[i][j][0] = localScore;
                                            matrix[i][j][1] = visitorScore;
                                            matrix[i][j][2] = minute;

                                            results.add(obj);
                                        }
                                    }
                                    break;
                                default:
                                    if (methodForHuntFilterMin(minute)) {
                                        if (isLeaugeFirst) {
                                            results.add(obj);
                                            isLeaugeFirst = false;
                                        }
                                        if (flag) {
                                            obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, "");
                                        } else {
                                            obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, "");
                                        }

                                        matrix[i][j][0] = localScore;
                                        matrix[i][j][1] = visitorScore;
                                        matrix[i][j][2] = minute;

                                        results.add(obj);
                                    }
                                    break;
                            }
                        }
                    }
                    flag = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (results.size() == 0)
                    textView_noFilter.setVisibility(View.VISIBLE);
                else
                    textView_noFilter.setVisibility(View.GONE);

                Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();

                if (edt_Search.getText().length() == 0)
                    dummy = null;
                if (dummy != null && dummy.size() != 0 || dummy != null && dummy.size() == 0 && edt_Search.getText().length() != 0)
                    mAdapter = new LiveScoresRecyclerViewAdapter(getContext(), dummy);
                else
                    mAdapter = new LiveScoresRecyclerViewAdapter(getContext(), results);

                mRecyclerView.setAdapter(mAdapter);

                // tüm dataları yakıp söndürüyor
                /*for (int i = 0; i <results.size(); i++) {
                    mRecyclerView.getAdapter().notifyItemChanged(i);
                }*/

                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                ((LiveScoresRecyclerViewAdapter) mAdapter).setOnItemClickListener(new LiveScoresRecyclerViewAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        if (edt_Search.getText().length() != 0) {
                            if (dummy.get(position) != null)
                                aq = (LiveScoresPojo) dummy.get(position);
                        } else {
                            if (results.get(position) != null)
                                aq = (LiveScoresPojo) results.get(position);
                        }

                        if (v.getId() == R.id.btnAlert) {
                            Log.wtf("liveScores", "getAct() : " + getActivity() + " / isAdded() : " + isAdded());
                            if (isAdded() && ctx != null) {
                                Intent intent = new Intent(ctx, Alert2Activity.class);
                                intent.putExtra("isGeneric", false);
                                intent.putExtra("local", aq.getLocalTeam());
                                intent.putExtra("visitor", aq.getVisitorTeam());
                                intent.putExtra("minute", aq.getMinute() + "");
                                intent.putExtra("localScore", aq.getLocalScore() + "");
                                intent.putExtra("visitorScore", aq.getVisitorScore() + "");
                                intent.putExtra("matchId", aq.getMatchId() + "");
                                //MainActivity.live = false;
                                startActivity(intent);
                                //getActivity().finish();
                            }
                        } else if (v.getId() == R.id.btnStats) {
                            getStats(aq.getLeagueId(), aq.getMatchId(), aq.getEvents());
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                });

                if (MainActivity.live)
                    getReqFuncJsnArr(url);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("Error.Response liveScores", error.toString());
                /*NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Log.wtf("try", "res : " + res + " / response : " + response);
                        JSONObject obj = new JSONObject(res);
                        Log.wtf("try", "obj : " + obj);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        e1.printStackTrace();
                    }
                }*/
                if (MainActivity.live)
                    getReqFuncJsnArr(url);
                /*hidepDialog();
                builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Server error, please try again").setCancelable(false).setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getReqFuncJsnArr(url);
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();*/
            }
        }

        );

        getRequest.setShouldCache(false);// no caching url...
        getRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        20000,//time to wait for it in this case 20s
                        20,//tryies in case of error
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //queue.add(getRequest);
        MySingleton.getInstance(getContext()).addToRequestQueue(getRequest);
    }

    /*private void getReqFuncJsnArr() {
        //RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/lastLiveNew",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("Response liveScores", response);
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObjectTemp = new JSONObject();
                        JSONArray lives = new JSONArray();
                        JSONArray sariCanlar = new JSONArray();
                        try {
                            jsonObjectTemp = new JSONObject(response);
                            lives = jsonObjectTemp.getJSONArray("lives");
                            sariCanlar = jsonObjectTemp.getJSONArray("sariCanlar");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        jsonArray_lastLive = lives;

                        if (jsonArray_lastLive.length() == 0) {
                            txtNoLiveMatch.setVisibility(View.VISIBLE);
                        } else {
                            txtNoLiveMatch.setVisibility(View.GONE);
                        }

                        if (matchLenght != 0)
                            matchLenghtBool = matchLenght == jsonArray_lastLive.length();

                        matchLenght = jsonArray_lastLive.length();

                        results = new ArrayList<LiveScoresPojo>();
                        LiveScoresPojo obj;
                        try {
                            for (int i = 0; i < jsonArray_lastLive.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray_lastLive.get(i);
                                int leagueId = jsonObject.getInt("league_id");
                                String leagueName = jsonObject.getString("league_name");
                                String flags = jsonObject.getString("flags");
                                JSONArray jsonArray = jsonObject.getJSONArray("match");

                                obj = new LiveScoresPojo(leagueId, leagueName, flags);
                                //results.add(obj);
                                boolean isLeaugeFirst = true;

                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(j);
                                    int matchId = jsonObject1.getInt("match_id");
                                    String localTeam = jsonObject1.getString("localteam");
                                    String visitorTeam = jsonObject1.getString("visitorteam");
                                    int localScore = jsonObject1.getInt("localScore");
                                    int visitorScore = jsonObject1.getInt("visitorScore");
                                    int minute = jsonObject1.getInt("minute");

                                    String events = jsonObject1.getString("events");

                                    switch (huntFilter) {
                                        case 1:
                                            if (localScore == 0 && visitorScore == 0) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 2:
                                            if (localScore == visitorScore && localScore > 0 && visitorScore > 0) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 3:
                                            if ((localScore == 0 && visitorScore > 0) || (visitorScore == 0 && localScore > 0)) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 4:
                                            if ((localScore + visitorScore) > 0 && (localScore + visitorScore) < 3) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 5:
                                            if ((localScore + visitorScore) < 3) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 6:
                                            if ((localScore + visitorScore) > 2) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 7:
                                            if (localScore > visitorScore) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        case 8:
                                            if (localScore < visitorScore) {
                                                if (methodForHuntFilterMin(minute)) {
                                                    if (isLeaugeFirst) {
                                                        results.add(obj);
                                                        isLeaugeFirst = false;
                                                    }
                                                    if (flag) {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                    } else {
                                                        obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                    }

                                                    matrix[i][j][0] = localScore;
                                                    matrix[i][j][1] = visitorScore;
                                                    matrix[i][j][2] = minute;

                                                    results.add(obj);
                                                }
                                            }
                                            break;
                                        default:
                                            if (methodForHuntFilterMin(minute)) {
                                                if (isLeaugeFirst) {
                                                    results.add(obj);
                                                    isLeaugeFirst = false;
                                                }
                                                if (flag) {
                                                    obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, -1, -1, -1, flags, flag, matchLenghtBool, events);
                                                } else {
                                                    obj = new LiveScoresPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, localScore, visitorScore, minute, matrix[i][j][0], matrix[i][j][1], matrix[i][j][2], flags, flag, matchLenghtBool, events);
                                                }

                                                matrix[i][j][0] = localScore;
                                                matrix[i][j][1] = visitorScore;
                                                matrix[i][j][2] = minute;

                                                results.add(obj);
                                            }
                                            break;
                                    }
                                }
                            }
                            flag = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (results.size() == 0)
                            textView_noFilter.setVisibility(View.VISIBLE);
                        else
                            textView_noFilter.setVisibility(View.GONE);

                        Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();

                        if (edt_Search.getText().length() == 0)
                            dummy = null;
                        if (dummy != null && dummy.size() != 0 || dummy != null && dummy.size() == 0 && edt_Search.getText().length() != 0)
                            mAdapter = new LiveScoresRecyclerViewAdapter(getContext(), dummy, sariCanlar);
                        else
                            mAdapter = new LiveScoresRecyclerViewAdapter(getContext(), results, sariCanlar);

                        mRecyclerView.setAdapter(mAdapter);

                        mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                        ((LiveScoresRecyclerViewAdapter) mAdapter).setOnItemClickListener(new LiveScoresRecyclerViewAdapter.MyClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {

                                if (edt_Search.getText().length() != 0)
                                    aq = (LiveScoresPojo) dummy.get(position);
                                else
                                    aq = (LiveScoresPojo) results.get(position);

                                if (v.getId() == R.id.btnAlert) {
                                    Log.wtf("liveScores", "getAct() : " + getActivity() + " / isAdded() : " + isAdded());
                                    if (isAdded() && ctx != null) {
                                        Intent intent = new Intent(ctx, Alert2Activity.class); // java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.Context android.support.v4.app.FragmentActivity.getApplicationContext()' on a null object reference
                                        intent.putExtra("isGeneric", false);
                                        intent.putExtra("local", aq.getLocalTeam());
                                        intent.putExtra("visitor", aq.getVisitorTeam());
                                        intent.putExtra("minute", aq.getMinute() + "");
                                        intent.putExtra("localScore", aq.getLocalScore() + "");
                                        intent.putExtra("visitorScore", aq.getVisitorScore() + "");
                                        intent.putExtra("matchId", aq.getMatchId() + "");
                                        //MainActivity.live = false;
                                        startActivity(intent);
                                        //getActivity().finish();
                                    }
                                } else if (v.getId() == R.id.btnStats) {
                                    getStats(aq.getLeagueId(), aq.getMatchId(), aq.getEvents());
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        if (MainActivity.live)
                            getReqFuncJsnArr();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Error.Response liveScores", error.toString());
                        if (MainActivity.live)
                            getReqFuncJsnArr();
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
                params.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                //Log.wtf("params", params + "");
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
        //queue.add(postRequest);
        MySingleton.getInstance(getContext()).addToRequestQueue(postRequest);
    }*/

    private boolean methodForHuntFilterMin(int minute) {
        boolean boolReturn = true;
        switch (huntFilterMin) {
            case 9:
                if (minute <= 30 && minute > 0)
                    boolReturn = true;
                else
                    boolReturn = false;
                break;
            case 10:
                if (minute <= 60 && minute > 30 || minute == 0)
                    boolReturn = true;
                else
                    boolReturn = false;
                break;
            case 11:
                if (minute <= 90 && minute > 60)
                    boolReturn = true;
                else
                    boolReturn = false;
                break;
        }
        return boolReturn;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHuntNoGoal:
                colorReset(1);
                break;
            case R.id.btnHuntDraw:
                colorReset(2);
                break;
            case R.id.btnHuntWinToNil:
                colorReset(3);
                break;
            case R.id.btnHunt05:
                colorReset(4);
                break;
            case R.id.btnHuntm25:
                colorReset(5);
                break;
            case R.id.btnHunt25:
                colorReset(6);
                break;
            case R.id.btnHuntHomeWin:
                colorReset(7);
                break;
            case R.id.btnHuntAwayWin:
                colorReset(8);
                break;
            case R.id.btnHunt030:
                colorResetForMin(9);
                break;
            case R.id.btnHunt3060:
                colorResetForMin(10);
                break;
            case R.id.btnHunt6090:
                colorResetForMin(11);
                break;
            case R.id.searchButton:
                if (keyboardOpen) {
                    hideKeyboard(v);
                } else {
                    date_timeTxt.setVisibility(View.INVISIBLE);
                    edt_Search.setVisibility(View.VISIBLE);
                    edt_Search.requestFocus();
                    btn_CancelSearch.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(edt_Search, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case R.id.searchCancelButton:
                date_timeTxt.setVisibility(View.VISIBLE);
                edt_Search.setVisibility(View.INVISIBLE);
                btn_CancelSearch.setVisibility(View.INVISIBLE);
                edt_Search.setText(null);
                hideKeyboard(v);
                break;
            case R.id.btnGeneric:
                if (ctx != null && results != null) {
                    Intent intent = new Intent(ctx, Alert2Activity.class);
                    intent.putExtra("isGeneric", true);
                    intent.putParcelableArrayListExtra("results", results);
                    startActivity(intent);
                    //getActivity().finish();
                }
                break;
        }
    }

    private void colorReset(int i) {
        if (huntFilter == i) {
            huntFilter = 0;
            switch (i) {
                case 1:
                    gradBtn = (GradientDrawable) btnHuntNoGoal.getBackground();
                    break;
                case 2:
                    gradBtn = (GradientDrawable) btnHuntDraw.getBackground();
                    break;
                case 3:
                    gradBtn = (GradientDrawable) btnHuntWinToNil.getBackground();
                    break;
                case 4:
                    gradBtn = (GradientDrawable) btnHunt05.getBackground();
                    break;
                case 5:
                    gradBtn = (GradientDrawable) btnHuntm25.getBackground();
                    break;
                case 6:
                    gradBtn = (GradientDrawable) btnHunt25.getBackground();
                    break;
                case 7:
                    gradBtn = (GradientDrawable) btnHuntHomeWin.getBackground();
                    break;
                case 8:
                    gradBtn = (GradientDrawable) btnHuntAwayWin.getBackground();
                    break;
            }
            gradBtn.setColor(Color.TRANSPARENT);
        } else {
            huntFilter = i;

            gradBtn = (GradientDrawable) btnHuntNoGoal.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHuntDraw.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHuntWinToNil.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHunt05.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHuntm25.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHunt25.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHuntHomeWin.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHuntAwayWin.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);

            switch (i) {
                case 1:
                    gradBtn = (GradientDrawable) btnHuntNoGoal.getBackground();
                    break;
                case 2:
                    gradBtn = (GradientDrawable) btnHuntDraw.getBackground();
                    break;
                case 3:
                    gradBtn = (GradientDrawable) btnHuntWinToNil.getBackground();
                    break;
                case 4:
                    gradBtn = (GradientDrawable) btnHunt05.getBackground();
                    break;
                case 5:
                    gradBtn = (GradientDrawable) btnHuntm25.getBackground();
                    break;
                case 6:
                    gradBtn = (GradientDrawable) btnHunt25.getBackground();
                    break;
                case 7:
                    gradBtn = (GradientDrawable) btnHuntHomeWin.getBackground();
                    break;
                case 8:
                    gradBtn = (GradientDrawable) btnHuntAwayWin.getBackground();
                    break;
            }
            gradBtn.setColor(getResources().getColor(R.color.denemetab));

        }
    }

    private void colorResetForMin(int i) {
        if (huntFilterMin == i) {
            huntFilterMin = 0;
            switch (i) {
                case 9:
                    gradBtn = (GradientDrawable) btnHunt030.getBackground();
                    break;
                case 10:
                    gradBtn = (GradientDrawable) btnHunt3060.getBackground();
                    break;
                case 11:
                    gradBtn = (GradientDrawable) btnHunt6090.getBackground();
                    break;
            }
            gradBtn.setColor(Color.TRANSPARENT);
        } else {
            huntFilterMin = i;

            gradBtn = (GradientDrawable) btnHunt030.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHunt3060.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);
            gradBtn = (GradientDrawable) btnHunt6090.getBackground();
            gradBtn.setColor(Color.TRANSPARENT);

            switch (i) {
                case 9:
                    gradBtn = (GradientDrawable) btnHunt030.getBackground();
                    break;
                case 10:
                    gradBtn = (GradientDrawable) btnHunt3060.getBackground();
                    break;
                case 11:
                    gradBtn = (GradientDrawable) btnHunt6090.getBackground();
                    break;
            }
            gradBtn.setColor(getResources().getColor(R.color.denemetab));
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void getStats(int leaugeid, final int matchid, final String events) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        //String url = "http://www.goalserve.com/getfeed/743d582d10924b1aadb0279a5e407519/commentaries/" + leaugeid + ".xml?json=1";
        String url = "http://www.goalserve.com/getfeed/743d582d10924b1aadb0279a5e407519/commentaries/1005.xml?json=1";
        Log.wtf("liveScore", "url : " + url);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.wtf("onResponse stats", response);
                        progressBar.setVisibility(View.GONE);

                        String ligName = "";
                        JSONObject match = null;
                        JSONObject tournament = null;
                        try {
                            tournament = new JSONObject(response).getJSONObject("commentaries").getJSONObject("tournament");
                            ligName = tournament.getString("@name");
                            JSONArray jsonArray = tournament.getJSONArray("match");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (matchid == jsonObject.getInt("@id"))
                                    match = jsonObject;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                if (tournament != null) {
                                    match = tournament.getJSONObject("match");
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        if (isAdded() && ctx != null && match != null) {
                            Intent intent = new Intent(ctx, MatchDetailActivity2.class);
                            intent.putExtra("ligName", ligName);
                            intent.putExtra("match", match.toString());
                            intent.putExtra("events", events);
                            startActivity(intent);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("onErrorResponse stats", error.toString());
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ctx, "There is no statistics for this match", Toast.LENGTH_SHORT).show();
                    }
                });
        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

}