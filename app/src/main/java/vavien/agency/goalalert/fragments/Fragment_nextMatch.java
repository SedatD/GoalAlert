package vavien.agency.goalalert.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import vavien.agency.goalalert.R;
import vavien.agency.goalalert.adapters.NextMatchRecyclerViewAdapter;
import vavien.agency.goalalert.model.NextMatchPojo;

/**
 * Created by albur on 16.10.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class Fragment_nextMatch extends Fragment implements View.OnClickListener {
    private RequestQueue queue;
    private JSONArray jsonArray_nextMatch;
    private ArrayList results = null;
    private DateFormat dateFormat;
    private SimpleDateFormat simpleDateFormat;
    private Calendar calendar;
    private TextView date_timeTxt;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int day;
    private TextView txtNoLiveMatch;
    private TextView date_timeTxt_bottom;
    private EditText edt_Search;
    private ArrayList dummy;
    private boolean keyboardOpen = false;
    private ImageView btn_Search, btn_CancelSearch;
    private ImageView prev,next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.tab_nextmatch, container, false);

        rootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootview.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootview.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    keyboardOpen = true;
                } else {
                    // keyboard is closed
                    keyboardOpen = false;
                }
            }
        });

        day = 1;

        date_timeTxt = (TextView) rootview.findViewById(R.id.date_timeTxt);
        dateFormat = new SimpleDateFormat("dd/MM/yy");
        simpleDateFormat = new SimpleDateFormat("EEEE");
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        date_timeTxt.setText(dateFormat.format(calendar.getTime()) + " " + simpleDateFormat.format(calendar.getTime()));

        prev = rootview.findViewById(R.id.prev);
        next = rootview.findViewById(R.id.next);
        btn_Search = rootview.findViewById(R.id.searchButton);
        btn_CancelSearch = rootview.findViewById(R.id.searchCancelButton);

        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        btn_Search.setOnClickListener(this);
        btn_CancelSearch.setOnClickListener(this);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_nextMatch);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        txtNoLiveMatch = rootview.findViewById(R.id.txtNoLiveMatch);
        edt_Search = rootview.findViewById(R.id.searchEditText);

        edt_Search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
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
                            final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(edt_Search.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });

                dummy = new ArrayList<NextMatchPojo>();
                if (results != null)
                    for (int i = 0; i < results.size(); i++) {
                        NextMatchPojo aq = (NextMatchPojo) results.get(i);
                        if (aq.getLocalTeam() != null)
                            if (edt_Search.getText().length() <= aq.getLocalTeam().length() || edt_Search.getText().length() <= aq.getVisitorTeam().length())
                                if (aq.getLocalTeam().toLowerCase().trim().contains(edt_Search.getText().toString().toLowerCase().trim()) || aq.getVisitorTeam().toLowerCase().trim().contains(edt_Search.getText().toString().toLowerCase().trim()))
                                    dummy.add(aq);
                    }

                Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();

                if (edt_Search.getText().length() == 0)
                    dummy = null;
                if (dummy != null && dummy.size() != 0 || dummy != null && dummy.size() == 0 && edt_Search.getText().length() != 0)
                    mAdapter = new NextMatchRecyclerViewAdapter(getContext(), dummy);
                else if (results != null)
                    mAdapter = new NextMatchRecyclerViewAdapter(getContext(), results);

                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        queue = Volley.newRequestQueue(getContext());
        postReqFuncStr();

        return rootview;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void postReqFuncStr() {
        StringRequest getRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/next_matches", new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.wtf("Response nextMatch", response);
                try {
                    results = new ArrayList<NextMatchPojo>();
                    NextMatchPojo obj;
                    jsonArray_nextMatch = new JSONArray(response);

                    if (jsonArray_nextMatch.length() == 0)
                        txtNoLiveMatch.setVisibility(View.VISIBLE);
                    else
                        txtNoLiveMatch.setVisibility(View.GONE);

                    for (int i = 0; i < jsonArray_nextMatch.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray_nextMatch.get(i);
                        int leagueId = jsonObject.getInt("league_id");
                        String leagueName = jsonObject.getString("league_name");
                        String flags = jsonObject.getString("flags");
                        JSONArray jsonArray = jsonObject.getJSONArray("match");
                        obj = new NextMatchPojo(leagueId, leagueName, flags);
                        results.add(obj);

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(j);
                            int matchId = jsonObject1.getInt("match_id");
                            int hour = jsonObject1.getInt("hour");
                            int minute = jsonObject1.getInt("minute");
                            String localTeam = jsonObject1.getString("localteam");
                            String visitorTeam = jsonObject1.getString("visitorteam");

                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
                            String timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
                            //timeZone.substring(0, 3) + ":"+ timeZone.substring(3, 5);
                            char ch = timeZone.charAt(0);
                            if (ch == '+') {
                                hour = hour + Integer.valueOf(timeZone.substring(1, 3));
                                minute = minute + Integer.valueOf(timeZone.substring(3, 5));
                                if (minute > 60) {
                                    minute = minute % 60;
                                    hour++;
                                }
                                hour = hour % 24;
                            } else {
                                hour = hour - Integer.valueOf(timeZone.substring(1, 3));
                                minute = minute - Integer.valueOf(timeZone.substring(3, 5));
                                if (minute < 0) {
                                    minute = 60 + minute;
                                    hour--;
                                }
                                hour = hour < 0 ? 24 + hour : hour;
                            }

                            /*String hourLast = String.valueOf(hour);
                            String minuteLast = String.valueOf(minute);
                            if (hour == 0)
                                hourLast = "00";
                            if (minute == 0)
                                minuteLast = "00";*/

                            String hourLast = hour == 0 ? "00" : String.valueOf(hour);
                            String minuteLast = minute == 0 ? "00" : String.valueOf(minute);

                            hourLast = hourLast.length() == 1 ? "0" + hourLast : hourLast;
                            minuteLast = minuteLast.length() == 1 ? "0" + minuteLast : minuteLast;

                            obj = new NextMatchPojo(leagueId, leagueName, matchId, localTeam, visitorTeam, hourLast, minuteLast, flags);
                            results.add(obj);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                //mAdapter = new NextMatchRecyclerViewAdapter(getContext(), results);

                if (edt_Search.getText().length() == 0)
                    dummy = null;
                if (dummy != null && dummy.size() != 0 || dummy != null && dummy.size() == 0 && edt_Search.getText().length() != 0)
                    mAdapter = new NextMatchRecyclerViewAdapter(getContext(), dummy);
                else
                    mAdapter = new NextMatchRecyclerViewAdapter(getContext(), results);

                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("Error.Response nextMatch", error.toString());
                postReqFuncStr();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("day", day + "");
                return params;
            }
        };
        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev:
                if (day != 1) {
                    --day;
                    Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
                    calendar.add(Calendar.DATE, -1);
                    date_timeTxt.setText(dateFormat.format(calendar.getTime()) + " " + simpleDateFormat.format(calendar.getTime()));
                    postReqFuncStr();
                }
                break;
            case R.id.next:
                if (day != 8) {
                    ++day;
                    Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
                    calendar.add(Calendar.DATE, +1);
                    date_timeTxt.setText(dateFormat.format(calendar.getTime()) + " " + simpleDateFormat.format(calendar.getTime()));
                    postReqFuncStr();
                }
                break;
            case R.id.searchButton:
                if (keyboardOpen) {
                    hideKeyboard(v);
                } else {
                    date_timeTxt.setVisibility(View.INVISIBLE);
                    edt_Search.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.INVISIBLE);

                    edt_Search.requestFocus();
                    btn_CancelSearch.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(edt_Search, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case R.id.searchCancelButton:
                date_timeTxt.setVisibility(View.VISIBLE);
                edt_Search.setVisibility(View.INVISIBLE);
                btn_CancelSearch.setVisibility(View.INVISIBLE);
                edt_Search.setText(null);
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                hideKeyboard(v);
                break;
            default:
                break;
        }
    }
}
