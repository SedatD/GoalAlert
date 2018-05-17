package vavien.agency.goalalert.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import vavien.agency.goalalert.R;
import vavien.agency.goalalert.activity.MainActivity;

import static java.lang.Integer.parseInt;

/**
 * Created by SD on 28.11.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class MyService extends Service {
    private Timer timer = null;
    private TimerTask timerTask;
    private DBHelper dbHelper;
    private RequestQueue queue = null;

    public MyService(Context ctx) {
        super();
    }

    public MyService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.wtf("MyService", "onStartCommand");
        if (timer == null) {
            startTimer();
            Log.wtf("MyService", "onStartCommand - startTimer()");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("KKK");
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, to wake up every 11 second
        //timer.schedule(timerTask, 1000, 11000);
        timer.scheduleAtFixedRate(timerTask, 1000, 40000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                if (queue == null)
                    queue = Volley.newRequestQueue(getApplicationContext());
                dbHelper = new DBHelper(getApplicationContext());

                /*SQLiteDatabase sqLiteDB = dbHelper.getWritableDatabase();
                List<String> array_list = new ArrayList<String>();
                String[] stunlar = new String[]{"matchId", "id"};
                Cursor res = sqLiteDB.query("alerts", stunlar, null, null, null, null, null);
                while (res.moveToNext()) {
                    array_list.add(res.getInt(0) + " - " + res.getInt(1));
                }
                sqLiteDB.close();*/

                ArrayList<String> arrMatchId = new ArrayList<>();
                ArrayList<Integer> arrDbId = new ArrayList<>();
                for (int i = 0; i < dbHelper.getIds().size(); i++) {
                    try {
                        String[] arr = dbHelper.getIds().get(i).split(" - ");
                        //String matchId = arr[0];
                        //int dbId = Integer.parseInt(arr[1]);
                        arrMatchId.add(arr[0]);
                        arrDbId.add(Integer.parseInt(arr[1]));
                    } catch (Exception e) {
                        Log.wtf("MyService", "initialize getIds okuyamadigi icin girdi buraya : " + e);
                    }
                }
                if (arrDbId.size() != 0) {
                    //new PostMatch(arrMatchId, arrDbId).execute();
                    PostMatchNotAsc(arrMatchId, arrDbId);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Set<String> myMatchList = new HashSet<String>(arrMatchId);
                    editor.putStringSet("myMatchList", myMatchList);
                    editor.apply();
                } else {
                    stopTimerTask();
                }
            }
        };
    }

    public void callDelete(int dbidd) {
        String matchId = null;
        boolean aq = false;
        for (int qwe = 0; qwe < dbHelper.getAllCotacts().size(); qwe++) {
            try {
                String[] asd = dbHelper.getAllCotacts().get(qwe).split(" - ");
                if (parseInt(asd[0]) == dbidd) {
                    matchId = asd[5];
                    aq = dbHelper.deleteMethod(dbidd);
                }
            } catch (Exception e) {
                Log.wtf("MyService", "callDelete getAllContacts okuyamadigi icin girdi buraya : " + e);
            }
        }

        if (aq) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            Set<String> myMatchList = preferences.getStringSet("myMatchList", null);
            if (myMatchList != null && matchId != null) {
                myMatchList.remove(matchId);
                editor.putStringSet("myMatchList", myMatchList);
            }
            editor.apply();
        }

        stopTimerTask();
    }

    public void operation(String idd, String localTeam, String visitorTeam, String localScore, String visitorScore, String minute, Boolean first, Boolean second, int dbidd) {
        int total = parseInt(localScore) + parseInt(visitorScore);
        int alarmMin = -1;
        double bet = 31.31;
        String status = getString(R.string.failed);
        boolean boolStatus = false;

        for (int qwe = 0; qwe < dbHelper.getAllCotacts().size(); qwe++) {
            try {
                String[] asd = dbHelper.getAllCotacts().get(qwe).split(" - ");
                if (parseInt(asd[5]) == parseInt(idd) && parseInt(asd[0]) == dbidd) {
                    alarmMin = parseInt(asd[3]);
                    bet = Double.parseDouble(asd[4]);
                }
            } catch (Exception e) {
                Log.wtf("MyService", "operation basinda getAllContacts okuyamadigi icin girdi buraya : " + e);
            }
        }

        /*if(notot){
            işlemden çık
        }else{
            bizim standart işlem
        }*/

        switch (alarmMin) {
            case -1:
                Log.wtf("MyService", "operation da case -1 e girdi yani mevcut bir eslesme bulamadi - dbidd : " + dbidd);
                break;
            case -2: // ANY TIME
                Log.wtf("ANY TIME", "BET : " + bet + " - TOTAL : " + total);
                if (parseInt(minute) >= 0 && !second) {
                    Log.wtf("ANY TIME", "min > 0 // yani maç oynanıyor");
                    if (bet == 1.1) {
                        Log.wtf("ANY TIME", "bet == 1.1 // btts yes");
                        if (parseInt(localScore) > 0 && parseInt(visitorScore) > 0) {
                            boolStatus = true;
                            status = getString(R.string.both_teams_scored);
                            notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                            callDelete(dbidd);
                        }
                    } else if (bet < total) {
                        if (total > 0)
                            status = getString(R.string.there_is_a_scorer);
                        if (total > 1)
                            status = getString(R.string.its_over_15);
                        if (total > 2)
                            status = getString(R.string.its_over_25);
                        if (total > 3)
                            status = getString(R.string.its_over_35);
                        if (total > 4)
                            status = getString(R.string.its_over_45);
                        Log.wtf("ANY TIME", "bet < total");
                        boolStatus = true;
                        notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                        callDelete(dbidd);
                    }
                } else {
                    Log.wtf("ANY TIME", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                    boolStatus = false;
                    status = getString(R.string.full_time);
                    if (bet == 0.5)
                        status += getString(R.string.poor_match);
                    if (bet == 1.5)
                        status += getString(R.string.finished_15);
                    if (bet == 2.5)
                        status += getString(R.string.finished_25);
                    if (bet == 3.5)
                        status += getString(R.string.finished_35);
                    if (bet == 4.5)
                        status += getString(R.string.finished_45);
                    notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                    callDelete(dbidd);
                }
                break;
            case -3: // HALF TIME
                Log.wtf("HALF TIME", "BET : " + bet + " - TOTAL : " + total);
                if (parseInt(minute) >= 0) {
                    Log.wtf("HALF TIME", "min > 0 // yani maç oynanıyor");
                    if (first) {
                        boolStatus = false;
                        status = "";
                        Log.wtf("HALF TIME", "first // zamanı geldi");
                        if (bet == 1.1) {
                            Log.wtf("HALF TIME", "bet == 1.1 // btts yes");
                            if (parseInt(localScore) > 0 && parseInt(visitorScore) > 0) {
                                boolStatus = true;
                                status = getString(R.string.both_teams_scored);
                            } else {
                                boolStatus = false;
                                status = getString(R.string.one_team_didnt_score);
                            }
                        } else if (bet == -1.1) {
                            Log.wtf("HALF TIME", "bet == -1.1 // btts no");
                            if (parseInt(localScore) == 0 || parseInt(visitorScore) == 0) {
                                boolStatus = true;
                                status = getString(R.string.one_team_didnt_score);
                            } else {
                                boolStatus = false;
                                status = getString(R.string.both_teams_scored);
                            }
                        } else if (bet == -8.8) {
                            Log.wtf("HALF TIME", "bet == -8.8 // skor");
                            boolStatus = true;
                            status = getString(R.string.score_info);
                        } else if (bet == -9.9) {
                            Log.wtf("HALF TIME", "bet == -9.9 // no goal");
                            if (parseInt(localScore) == 0 && parseInt(visitorScore) == 0) {
                                boolStatus = true;
                                status = getString(R.string.no_goal);
                            } else {
                                boolStatus = false;
                                status = getString(R.string.there_is_a_scorer);
                            }
                        } else {
                            if (bet > 0) {
                                Log.wtf("HALF TIME", "else bet > 0");
                                if (bet < total) {
                                    Log.wtf("HALF TIME", "bet < total");
                                    status = getString(R.string.success);
                                    boolStatus = true;
                                    if (total > 0)
                                        status = getString(R.string.there_is_a_scorer);
                                    if (total > 1)
                                        status = getString(R.string.its_over_15);
                                    if (total > 2)
                                        status = getString(R.string.its_over_25);
                                    if (total > 3)
                                        status = getString(R.string.its_over_35);
                                    if (total > 4)
                                        status = getString(R.string.its_over_45);
                                } else {
                                    status = getString(R.string.its) + bet + " !";
                                    boolStatus = false;
                                }
                            } else if (Math.abs(bet) > total) {
                                Log.wtf("HALF TIME", "else if Math.abs(bet) > total");
                                status = getString(R.string.success);
                                boolStatus = true;
                                if (bet == -0.5)
                                    status = getString(R.string.no_goal);
                                if (bet == -1.5)
                                    status = getString(R.string.it_is_over_eksi15);
                                if (bet == -2.5)
                                    status = getString(R.string.it_is_over_eksi25);
                                if (bet == -3.5)
                                    status = getString(R.string.it_is_over_eksi35);
                                if (bet == -4.5)
                                    status = getString(R.string.it_is_over_eksi45);
                            } else {
                                status = getString(R.string.its) + bet + " !";
                                boolStatus = false;
                            }
                        }
                        String ht = getString(R.string.half_time);
                        notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, ht + status, boolStatus);
                        callDelete(dbidd);
                    }
                } else {
                    Log.wtf("HALF TIME", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                    status = getString(R.string.failed);
                    notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                    callDelete(dbidd);
                }
                break;
            case -4: // FULL TIME
                Log.wtf("FULL TIME", "BET : " + bet + " - TOTAL : " + total);
                if (parseInt(minute) >= 0) {
                    Log.wtf("FULL TIME", "min > 0 // yani maç oynanıyor");
                    if (second) {
                        boolStatus = false;
                        status = getString(R.string.full_time);
                        Log.wtf("FULL TIME", "second // zamanı geldi");
                        if (bet == 1.1) {
                            Log.wtf("FULL TIME", "bet == 1.1 // btts yes");
                            if (parseInt(localScore) > 0 && parseInt(visitorScore) > 0) {
                                boolStatus = true;
                                status = getString(R.string.both_teams_scored);
                            } else
                                status = getString(R.string.one_team_didnt_score);
                        } else if (bet == -1.1) {
                            Log.wtf("FULL TIME", "bet == -1.1 // btts no");
                            if (parseInt(localScore) == 0 || parseInt(visitorScore) == 0) {
                                boolStatus = true;
                                status = getString(R.string.one_team_didnt_score);
                            } else
                                status = getString(R.string.both_teams_scored);
                        } else if (bet == -8.8) {
                            Log.wtf("FULL TIME", "bet == -8.8 // skor");
                            boolStatus = true;
                            status = getString(R.string.score_info);
                        } else if (bet == -9.9) {
                            Log.wtf("FULL TIME", "bet == -9.9 // no goal");
                            if (parseInt(localScore) == 0 && parseInt(visitorScore) == 0) {
                                boolStatus = true;
                                status = getString(R.string.no_goal);
                            } else
                                status = getString(R.string.there_is_a_scorer);
                        } else {
                            if (bet > 0) {
                                Log.wtf("FULL TIME", "else bet > 0");
                                if (bet < total) {
                                    Log.wtf("FULL TIME", "bet < total");
                                    status = getString(R.string.success);
                                    boolStatus = true;
                                    if (total > 0)
                                        status = getString(R.string.there_is_a_scorer);
                                    if (total > 1)
                                        status = getString(R.string.its_over_15);
                                    if (total > 2)
                                        status = getString(R.string.its_over_25);
                                    if (total > 3)
                                        status = getString(R.string.its_over_35);
                                    if (total > 4)
                                        status = getString(R.string.its_over_45);
                                } else
                                    status = "It's " + bet + " !";
                            } else if (Math.abs(bet) > total) {
                                Log.wtf("FULL TIME", "else if Math.abs(bet) > total");
                                status = getString(R.string.success);
                                boolStatus = true;
                                if (bet == -0.5)
                                    status = getString(R.string.no_goal);
                                if (bet == -1.5)
                                    status = getString(R.string.it_is_over_eksi15);
                                if (bet == -2.5)
                                    status = getString(R.string.it_is_over_eksi25);
                                if (bet == -3.5)
                                    status = getString(R.string.it_is_over_eksi35);
                                if (bet == -4.5)
                                    status = getString(R.string.it_is_over_eksi45);
                            } else
                                status = getString(R.string.its) + bet + " !";
                        }
                        String ft = getString(R.string.full_time);
                        notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, ft + status, boolStatus);
                        callDelete(dbidd);
                    }
                } else {
                    Log.wtf("FULL TIME", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                    status = "Full Time Failed";
                    boolStatus = false;
                    notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                    callDelete(dbidd);
                }
                break;
            default: // NORMAL DK LAR
                Log.wtf("NORMAL DK LAR", "BET : " + bet + " - TOTAL : " + total + " - ALARM MIN : " + alarmMin);
                if (parseInt(minute) >= 0 && !second) {
                    Log.wtf("NORMAL DK LAR", "min > 0 // yani maç oynanıyor");
                    if (alarmMin <= parseInt(minute)) {
                        status = getString(R.string.failed);
                        boolStatus = false;
                        Log.wtf("NORMAL DK LAR", "zamanı geldi");
                        if (bet == 1.1) {
                            Log.wtf("NORMAL DK LAR", "bet == 1.1 // btts yes");
                            if (parseInt(localScore) > 0 && parseInt(visitorScore) > 0) {
                                boolStatus = true;
                                status = getString(R.string.both_teams_scored);
                            } else
                                status = getString(R.string.one_team_didnt_score);
                        } else if (bet == -1.1) {
                            Log.wtf("NORMAL DK LAR", "bet == -1.1 // btts no");
                            if (parseInt(localScore) == 0 || parseInt(visitorScore) == 0) {
                                boolStatus = true;
                                status = getString(R.string.one_team_didnt_score);
                            } else
                                status = getString(R.string.both_teams_scored);
                        } else if (bet == -8.8) {
                            Log.wtf("NORMAL DK LAR", "bet == -8.8 // skor");
                            boolStatus = true;
                            status = getString(R.string.score_info);
                        } else if (bet == -9.9) {
                            Log.wtf("NORMAL DK LAR", "bet == -9.9 // no goal");
                            if (parseInt(localScore) == 0 && parseInt(visitorScore) == 0) {
                                boolStatus = true;
                                status = getString(R.string.no_goal_yet);
                            } else
                                status = getString(R.string.already_goal);
                        } else {
                            if (bet > 0) {
                                Log.wtf("NORMAL DK LAR", "else bet > 0");
                                if (bet < total) {
                                    Log.wtf("NORMAL DK LAR", "bet < total");
                                    status = getString(R.string.success);
                                    boolStatus = true;
                                    if (total > 0)
                                        status = getString(R.string.its_over_05);
                                    if (total > 1)
                                        status = getString(R.string.its_over_15);
                                    if (total > 2)
                                        status = getString(R.string.its_over_25);
                                    if (total > 3)
                                        status = getString(R.string.its_over_35);
                                    if (total > 4)
                                        status = getString(R.string.its_over_45);
                                } else
                                    status = getString(R.string.its) + bet + " !";
                            } else if (Math.abs(bet) > total) {
                                Log.wtf("NORMAL DK LAR", "else if Math.abs(bet) > total");
                                status = getString(R.string.success);
                                boolStatus = true;
                                if (bet == -0.5)
                                    status = getString(R.string.no_goal);
                                if (bet == -1.5)
                                    status = getString(R.string.it_is_over_eksi15);
                                if (bet == -2.5)
                                    status = getString(R.string.it_is_over_eksi25);
                                if (bet == -3.5)
                                    status = getString(R.string.it_is_over_eksi35);
                                if (bet == -4.5)
                                    status = getString(R.string.it_is_over_eksi45);
                                if (bet == -5.5)
                                    status = getString(R.string.it_is_over_eksi55);
                            } else
                                status = getString(R.string.its) + bet + " !";
                        }
                        notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                        callDelete(dbidd);
                    }
                } else {
                    Log.wtf("NORMAL DK LAR", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                    status = getString(R.string.failed);
                    boolStatus = false;
                    notif(dbidd, localTeam, visitorTeam, parseInt(localScore), parseInt(visitorScore), minute, status, boolStatus);
                    callDelete(dbidd);
                }
                break;
        }
    }

    private void notif(int dbidd, String local, String visitor, int localScore, int visitorScore, String minute, String status, boolean boolStatus) {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.bell)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentTitle(local + " " + localScore + " - " + visitorScore + " " + visitor)
                        .setContentText(minute + "' " + status);
        mBuilder.setContentIntent(contentIntent);
        if (boolStatus)
            mBuilder.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.success));
        else
            mBuilder.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.failed));

        mBuilder.setAutoCancel(false);
        mBuilder.setPriority(Notification.PRIORITY_MAX); // max yerine high yapabiliriz çakılı kalıyor boyle
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(dbidd, mBuilder.build());

        // adamın notifi bakmak lazım
        /*int icon = R.drawable.ic_launcher;//notificationda gösterilecek icon
        long when = System.currentTimeMillis();//notificationın ne zaman gösterileceği
        String baslik = "mobilhanem.com";//notification başlık
        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(context,Anasayfa.class);
        PendingIntent pending=PendingIntent.getActivity(context, 0, intent, 0);//Notificationa tıklanınca açılacak activityi belirliyoruz
        Notification notification;
        notification = new Notification(icon, "Yeni Bildirim", when);
        notification.setLatestEventInfo(context,baslik,cevap,pending);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;//notificationa tıklanınca notificationın otomatik silinmesi için
        notification.defaults |= Notification.DEFAULT_SOUND;//notification geldiğinde bildirim sesi çalması için
        notification.defaults |= Notification.DEFAULT_VIBRATE;//notification geldiğinde bildirim titremesi için
        nm.notify(0, notification);*/
    }

    public void stopTimerTask() {
        if (timer != null && dbHelper.getAllCotacts().size() == 0) {
            Log.wtf("MyService", "timer cancel / service kill");
            timer.cancel();
            timer = null;
            timerTask.cancel();
            stopSelf();
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (manager != null) {
                manager.killBackgroundProcesses(getPackageName());
            }
        }
    }

    private void PostMatchNotAsc(final ArrayList<String> arrMatchId, final ArrayList<Integer> arrDbId) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://opucukgonder.com/tipster/index.php/Service/liveLeague", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("Response MyService", response);
                try {
                    JSONArray jsonArray_service = new JSONArray(response);
                    for (int i = 0; i < jsonArray_service.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray_service.get(i);
                        String matchId = jsonObject.getString("match_id");
                        String localTeam = jsonObject.getString("local");
                        String visitorTeam = jsonObject.getString("visitor");
                        String localScore = String.valueOf(jsonObject.getInt("localScore"));
                        String visitorScore = String.valueOf(jsonObject.getInt("visitorScore"));
                        String minute = String.valueOf(jsonObject.getInt("minute"));
                        Boolean first = jsonObject.getBoolean("first");
                        Boolean second = jsonObject.getBoolean("second");

                        //operation(arrMatchId.get(i), localTeam, visitorTeam, localScore, visitorScore, minute, first, second, arrDbId.get(i));
                        new AscOperation().execute(arrMatchId.get(i) + "", localTeam, visitorTeam, localScore, visitorScore, minute, first + "", second + "", arrDbId.get(i) + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.wtf("MyService", "request catche girdi" + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("Error.Response MyService request", error.toString());
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
                params.put("match_id", String.valueOf(arrMatchId));
                Log.wtf("MyService", "params : " + params);
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

    @SuppressLint("StaticFieldLeak")
    private class AscOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            /*for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }*/

            int total = parseInt(params[3]) + parseInt(params[4]);
            int alarmMin = -1;
            double bet = 31.31;
            String status = getString(R.string.failed);
            boolean boolStatus = false;

            for (int qwe = 0; qwe < dbHelper.getAllCotacts().size(); qwe++) {
                try {
                    String[] asd = dbHelper.getAllCotacts().get(qwe).split(" - ");
                    if (parseInt(asd[5]) == parseInt(params[0]) && parseInt(asd[0]) == parseInt(params[8])) {
                        alarmMin = parseInt(asd[3]);
                        bet = Double.parseDouble(asd[4]);
                    }
                } catch (Exception e) {
                    Log.wtf("MyService", "operation basinda getAllContacts okuyamadigi icin girdi buraya : " + e);
                }
            }

            switch (alarmMin) {
                case -1:
                    Log.wtf("MyService", "operation da case -1 e girdi yani mevcut bir eslesme bulamadi - dbidd : " + parseInt(params[8]));
                    break;
                case -2: // ANY TIME
                    Log.wtf("ANY TIME", "BET : " + bet + " - TOTAL : " + total);
                    if (parseInt(params[5]) >= 0 && !Boolean.parseBoolean(params[7])) {
                        Log.wtf("ANY TIME", "min > 0 // yani maç oynanıyor");
                        if (bet == 1.1) {
                            Log.wtf("ANY TIME", "bet == 1.1 // btts yes");
                            if (parseInt(params[3]) > 0 && parseInt(params[4]) > 0) {
                                boolStatus = true;
                                status = getString(R.string.both_teams_scored);
                                notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                                callDelete(parseInt(params[8]));
                            }
                        } else if (bet < total) {
                            if (total > 0)
                                status = getString(R.string.there_is_a_scorer);
                            if (total > 1)
                                status = getString(R.string.its_over_15);
                            if (total > 2)
                                status = getString(R.string.its_over_25);
                            if (total > 3)
                                status = getString(R.string.its_over_35);
                            if (total > 4)
                                status = getString(R.string.its_over_45);
                            Log.wtf("ANY TIME", "bet < total");
                            boolStatus = true;
                            notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                            callDelete(parseInt(params[8]));
                        }
                    } else {
                        Log.wtf("ANY TIME", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                        boolStatus = false;
                        status = getString(R.string.full_time);
                        if (bet == 0.5)
                            status += getString(R.string.poor_match);
                        if (bet == 1.5)
                            status += getString(R.string.finished_15);
                        if (bet == 2.5)
                            status += getString(R.string.finished_25);
                        if (bet == 3.5)
                            status += getString(R.string.finished_35);
                        if (bet == 4.5)
                            status += getString(R.string.finished_45);
                        notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                        callDelete(parseInt(params[8]));
                    }
                    break;
                case -3: // HALF TIME
                    Log.wtf("HALF TIME", "BET : " + bet + " - TOTAL : " + total);
                    if (parseInt(params[5]) >= 0) {
                        Log.wtf("HALF TIME", "min > 0 // yani maç oynanıyor");
                        if (Boolean.parseBoolean(params[6])) {
                            boolStatus = false;
                            status = "";
                            Log.wtf("HALF TIME", "first // zamanı geldi");
                            if (bet == 1.1) {
                                Log.wtf("HALF TIME", "bet == 1.1 // btts yes");
                                if (parseInt(params[3]) > 0 && parseInt(params[4]) > 0) {
                                    boolStatus = true;
                                    status = getString(R.string.both_teams_scored);
                                } else {
                                    boolStatus = false;
                                    status = getString(R.string.one_team_didnt_score);
                                }
                            } else if (bet == -1.1) {
                                Log.wtf("HALF TIME", "bet == -1.1 // btts no");
                                if (parseInt(params[3]) == 0 || parseInt(params[4]) == 0) {
                                    boolStatus = true;
                                    status = getString(R.string.one_team_didnt_score);
                                } else {
                                    boolStatus = false;
                                    status = getString(R.string.both_teams_scored);
                                }
                            } else if (bet == -8.8) {
                                Log.wtf("HALF TIME", "bet == -8.8 // skor");
                                boolStatus = true;
                                status = getString(R.string.score_info);
                            } else if (bet == -9.9) {
                                Log.wtf("HALF TIME", "bet == -9.9 // no goal");
                                if (parseInt(params[3]) == 0 && parseInt(params[4]) == 0) {
                                    boolStatus = true;
                                    status = getString(R.string.no_goal);
                                } else {
                                    boolStatus = false;
                                    status = getString(R.string.there_is_a_scorer);
                                }
                            } else {
                                if (bet > 0) {
                                    Log.wtf("HALF TIME", "else bet > 0");
                                    if (bet < total) {
                                        Log.wtf("HALF TIME", "bet < total");
                                        status = getString(R.string.success);
                                        boolStatus = true;
                                        if (total > 0)
                                            status = getString(R.string.there_is_a_scorer);
                                        if (total > 1)
                                            status = getString(R.string.its_over_15);
                                        if (total > 2)
                                            status = getString(R.string.its_over_25);
                                        if (total > 3)
                                            status = getString(R.string.its_over_35);
                                        if (total > 4)
                                            status = getString(R.string.its_over_45);
                                    } else {
                                        status = getString(R.string.its) + bet + " !";
                                        boolStatus = false;
                                    }
                                } else if (Math.abs(bet) > total) {
                                    Log.wtf("HALF TIME", "else if Math.abs(bet) > total");
                                    status = getString(R.string.success);
                                    boolStatus = true;
                                    if (bet == -0.5)
                                        status = getString(R.string.no_goal);
                                    if (bet == -1.5)
                                        status = getString(R.string.it_is_over_eksi15);
                                    if (bet == -2.5)
                                        status = getString(R.string.it_is_over_eksi25);
                                    if (bet == -3.5)
                                        status = getString(R.string.it_is_over_eksi35);
                                    if (bet == -4.5)
                                        status = getString(R.string.it_is_over_eksi45);
                                } else {
                                    status = getString(R.string.its) + bet + " !";
                                    boolStatus = false;
                                }
                            }
                            String ht = getString(R.string.half_time);
                            notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                            callDelete(parseInt(params[8]));
                        }
                    } else {
                        Log.wtf("HALF TIME", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                        status = getString(R.string.failed);
                        notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                        callDelete(parseInt(params[8]));
                    }
                    break;
                case -4: // FULL TIME
                    Log.wtf("FULL TIME", "BET : " + bet + " - TOTAL : " + total);
                    if (parseInt(params[5]) >= 0) {
                        Log.wtf("FULL TIME", "min > 0 // yani maç oynanıyor");
                        if (Boolean.parseBoolean(params[7])) {
                            boolStatus = false;
                            status = getString(R.string.full_time);
                            Log.wtf("FULL TIME", "second // zamanı geldi");
                            if (bet == 1.1) {
                                Log.wtf("FULL TIME", "bet == 1.1 // btts yes");
                                if (parseInt(params[3]) > 0 && parseInt(params[4]) > 0) {
                                    boolStatus = true;
                                    status = getString(R.string.both_teams_scored);
                                } else
                                    status = getString(R.string.one_team_didnt_score);
                            } else if (bet == -1.1) {
                                Log.wtf("FULL TIME", "bet == -1.1 // btts no");
                                if (parseInt(params[3]) == 0 || parseInt(params[4]) == 0) {
                                    boolStatus = true;
                                    status = getString(R.string.one_team_didnt_score);
                                } else
                                    status = getString(R.string.both_teams_scored);
                            } else if (bet == -8.8) {
                                Log.wtf("FULL TIME", "bet == -8.8 // skor");
                                boolStatus = true;
                                status = getString(R.string.score_info);
                            } else if (bet == -9.9) {
                                Log.wtf("FULL TIME", "bet == -9.9 // no goal");
                                if (parseInt(params[3]) == 0 && parseInt(params[4]) == 0) {
                                    boolStatus = true;
                                    status = getString(R.string.no_goal);
                                } else
                                    status = getString(R.string.there_is_a_scorer);
                            } else {
                                if (bet > 0) {
                                    Log.wtf("FULL TIME", "else bet > 0");
                                    if (bet < total) {
                                        Log.wtf("FULL TIME", "bet < total");
                                        status = getString(R.string.success);
                                        boolStatus = true;
                                        if (total > 0)
                                            status = getString(R.string.there_is_a_scorer);
                                        if (total > 1)
                                            status = getString(R.string.its_over_15);
                                        if (total > 2)
                                            status = getString(R.string.its_over_25);
                                        if (total > 3)
                                            status = getString(R.string.its_over_35);
                                        if (total > 4)
                                            status = getString(R.string.its_over_45);
                                    } else
                                        status = "It's " + bet + " !";
                                } else if (Math.abs(bet) > total) {
                                    Log.wtf("FULL TIME", "else if Math.abs(bet) > total");
                                    status = getString(R.string.success);
                                    boolStatus = true;
                                    if (bet == -0.5)
                                        status = getString(R.string.no_goal);
                                    if (bet == -1.5)
                                        status = getString(R.string.it_is_over_eksi15);
                                    if (bet == -2.5)
                                        status = getString(R.string.it_is_over_eksi25);
                                    if (bet == -3.5)
                                        status = getString(R.string.it_is_over_eksi35);
                                    if (bet == -4.5)
                                        status = getString(R.string.it_is_over_eksi45);
                                } else
                                    status = getString(R.string.its) + bet + " !";
                            }
                            String ft = getString(R.string.full_time);
                            notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                            callDelete(parseInt(params[8]));
                        }
                    } else {
                        Log.wtf("FULL TIME", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                        status = "Full Time Failed";
                        boolStatus = false;
                        notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                        callDelete(parseInt(params[8]));
                    }
                    break;
                default: // NORMAL DK LAR
                    Log.wtf("NORMAL DK LAR", "BET : " + bet + " - TOTAL : " + total + " - ALARM MIN : " + alarmMin);
                    if (parseInt(params[5]) >= 0 && !Boolean.parseBoolean(params[7])) {
                        Log.wtf("NORMAL DK LAR", "min > 0 // yani maç oynanıyor");
                        if (alarmMin <= parseInt(params[5])) {
                            status = getString(R.string.failed);
                            boolStatus = false;
                            Log.wtf("NORMAL DK LAR", "zamanı geldi");
                            if (bet == 1.1) {
                                Log.wtf("NORMAL DK LAR", "bet == 1.1 // btts yes");
                                if (parseInt(params[3]) > 0 && parseInt(params[4]) > 0) {
                                    boolStatus = true;
                                    status = getString(R.string.both_teams_scored);
                                } else
                                    status = getString(R.string.one_team_didnt_score);
                            } else if (bet == -1.1) {
                                Log.wtf("NORMAL DK LAR", "bet == -1.1 // btts no");
                                if (parseInt(params[3]) == 0 || parseInt(params[4]) == 0) {
                                    boolStatus = true;
                                    status = getString(R.string.one_team_didnt_score);
                                } else
                                    status = getString(R.string.both_teams_scored);
                            } else if (bet == -8.8) {
                                Log.wtf("NORMAL DK LAR", "bet == -8.8 // skor");
                                boolStatus = true;
                                status = getString(R.string.score_info);
                            } else if (bet == -9.9) {
                                Log.wtf("NORMAL DK LAR", "bet == -9.9 // no goal");
                                if (parseInt(params[3]) == 0 && parseInt(params[4]) == 0) {
                                    boolStatus = true;
                                    status = getString(R.string.no_goal_yet);
                                } else
                                    status = getString(R.string.already_goal);
                            } else {
                                if (bet > 0) {
                                    Log.wtf("NORMAL DK LAR", "else bet > 0");
                                    if (bet < total) {
                                        Log.wtf("NORMAL DK LAR", "bet < total");
                                        status = getString(R.string.success);
                                        boolStatus = true;
                                        if (total > 0)
                                            status = getString(R.string.its_over_05);
                                        if (total > 1)
                                            status = getString(R.string.its_over_15);
                                        if (total > 2)
                                            status = getString(R.string.its_over_25);
                                        if (total > 3)
                                            status = getString(R.string.its_over_35);
                                        if (total > 4)
                                            status = getString(R.string.its_over_45);
                                    } else
                                        status = getString(R.string.its) + bet + " !";
                                } else if (Math.abs(bet) > total) {
                                    Log.wtf("NORMAL DK LAR", "else if Math.abs(bet) > total");
                                    status = getString(R.string.success);
                                    boolStatus = true;
                                    if (bet == -0.5)
                                        status = getString(R.string.no_goal);
                                    if (bet == -1.5)
                                        status = getString(R.string.it_is_over_eksi15);
                                    if (bet == -2.5)
                                        status = getString(R.string.it_is_over_eksi25);
                                    if (bet == -3.5)
                                        status = getString(R.string.it_is_over_eksi35);
                                    if (bet == -4.5)
                                        status = getString(R.string.it_is_over_eksi45);
                                    if (bet == -5.5)
                                        status = getString(R.string.it_is_over_eksi55);
                                } else
                                    status = getString(R.string.its) + bet + " !";
                            }
                            notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                            callDelete(parseInt(params[8]));
                        }
                    } else {
                        Log.wtf("NORMAL DK LAR", "else min !> 0 // macın minute'ı 0dan buyuk degil // mac  bitti demek");
                        status = getString(R.string.failed);
                        boolStatus = false;
                        notif(parseInt(params[8]), params[1], params[2], parseInt(params[3]), parseInt(params[4]), params[5], status, boolStatus);
                        callDelete(parseInt(params[8]));
                    }
                    break;
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

}
