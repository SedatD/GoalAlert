package vavien.agency.goalalert.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vavien.agency.goalalert.R;
import vavien.agency.goalalert.adapters.AlarmListAdapter;
import vavien.agency.goalalert.model.AlarmListPojo;

/**
 * Created by albur on 6.10.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class Fragment_myAlerts extends Fragment {
    //private TextView textViewNoAlert;
    private ListView listView, lw_alertTabCompleted;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View alertview = inflater.inflate(R.layout.tab_myalerts, container, false);

        //textViewNoAlert = alertview.findViewById(R.id.textViewNoAlert);
        listView = alertview.findViewById(R.id.lw_alertTab);
        lw_alertTabCompleted = alertview.findViewById(R.id.lw_alertTabCompleted);
        progressBar = alertview.findViewById(R.id.progressBar);
        //progressBar.bringToFront();
        progressBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.denemetab));

        if (isAdded())
            getAlarms("pending");

        return alertview;
    }

    private void getAlarms(final String status) {
        if (getActivity().getApplicationContext() == null)
            return;
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/getAlarms",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("Fragment_myAlerts getAlarms response ", response);
                        try {

                            //progressBar.setVisibility(View.GONE);

                            List<AlarmListPojo> alarms = new ArrayList<>();
                            String text = "";

                            JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                if (!isAdded())
                                    return;

                                String aq = jsonObject.getString("bet_minute");
                                if (aq.equals("-2"))
                                    aq = getResources().getString(R.string.any_time);
                                else
                                    aq = aq + "'";

                                String aq2 = jsonObject.getString("bet");
                                if (aq2.equals("-9.9"))
                                    aq2 = getResources().getString(R.string.no_goal);
                                if (aq2.equals("-8.8"))
                                    aq2 = getResources().getString(R.string.score);
                                if (aq2.equals("1.1"))
                                    aq2 = getResources().getString(R.string.btts_yes);
                                if (aq2.equals("-1.1"))
                                    aq2 = getResources().getString(R.string.btts_no);

                                text = jsonObject.getString("localteam") + " - " + jsonObject.getString("visitorteam") + " / " + aq + " / " + aq2;
                                alarms.add(new AlarmListPojo(jsonObject.getInt("id"), text));
                            }

                            /*if (alarms.size() == 0)
                                textViewNoAlert.setVisibility(View.VISIBLE);*/

                            if (alarms.size() != 0) {
                                listView.setVisibility(View.VISIBLE);
                                AlarmListAdapter alarmListAdapter = new AlarmListAdapter(getActivity(), alarms, new AlarmListAdapter.onDoneClick() {
                                    @Override
                                    public void onClick(View v, int position, int id) {
                                        deleteAlarms(id);
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });

                                alarmListAdapter.notifyDataSetChanged();
                                listView.setAdapter(alarmListAdapter);
                            } else {
                                listView.setVisibility(View.INVISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.wtf("Fragment_myAlerts getAlarms", "request catche girdi" + e);
                            Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                        if (!isAdded())
                            return;
                        getAlarmsCompleted("completed");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Fragment_myAlerts getAlarms Error.Response", error.toString());
                        //Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                params.put("status", status);
                Log.wtf("Fragment_myAlerts getAlarms params ", params + "");
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

    private void getAlarmsCompleted(final String status) {
        if (!isAdded())
            return;
        if (getActivity().getApplicationContext() == null)
            return;
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/getAlarms",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("Fragment_myAlerts getAlarms response ", response);
                        try {

                            progressBar.setVisibility(View.GONE);

                            List<AlarmListPojo> alarms = new ArrayList<>();
                            String text;

                            JSONObject jsonObject;
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                if (!isAdded())
                                    return;

                                String aq = jsonObject.getString("bet_minute");
                                if (aq.equals("-2"))
                                    aq = getResources().getString(R.string.any_time);
                                else
                                    aq = aq + "'";

                                String aq2 = jsonObject.getString("bet");
                                if (aq2.equals("-9.9"))
                                    aq2 = getResources().getString(R.string.no_goal);
                                if (aq2.equals("-8.8"))
                                    aq2 = getResources().getString(R.string.score);

                                text = jsonObject.getString("localteam") + " - " + jsonObject.getString("visitorteam") + " / " + aq + " / " + aq2;
                                alarms.add(new AlarmListPojo(jsonObject.getInt("id"), text));
                            }

                            /*if (alarms.size() == 0)
                                textViewNoAlert.setVisibility(View.VISIBLE);*/


                            if (alarms.size() != 0) {
                                lw_alertTabCompleted.setVisibility(View.VISIBLE);
                                AlarmListAdapter alarmListAdapter = new AlarmListAdapter(getActivity(), alarms, new AlarmListAdapter.onDoneClick() {
                                    @Override
                                    public void onClick(View v, int position, int id) {
                                        deleteAlarms(id);
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });

                                alarmListAdapter.notifyDataSetChanged();
                                lw_alertTabCompleted.setAdapter(alarmListAdapter);
                            } else {
                                lw_alertTabCompleted.setVisibility(View.INVISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.wtf("Fragment_myAlerts getAlarms", "request catche girdi" + e);
                            Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Fragment_myAlerts getAlarms Error.Response", error.toString());
                        //Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                params.put("status", status);
                Log.wtf("Fragment_myAlerts getAlarms params ", params + "");
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

    private void deleteAlarms(final int id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/deleteAlarms",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("Fragment_myAlerts deleteAlarms response ", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("result") && isAdded())
                                getAlarms("pending");
                            //else
                            //Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.wtf("Fragment_myAlerts deleteAlarms", "request catche girdi" + e);
                            //Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("Fragment_myAlerts deleteAlarms Error.Response", error.toString());
                        Toast.makeText(getActivity(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("deviceid", OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                params.put("id", id + "");
                Log.wtf("Fragment_myAlerts getAlarms params ", params + "");
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

}
