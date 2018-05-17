package vavien.agency.goalalert.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by SD on 28.11.2017.
 * dilmacsedat@gmail.com
 * :)
 */

public class MyBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("MyBroadcastReceiver", " onReceive Broadcast");
        DBHelper dbHelper = new DBHelper(context);
        if (!isMyServiceRunning(MyService.class, context) && dbHelper.getAllCotacts().size() != 0) {
            Log.wtf("MyBroadcastReceiver", " onReceive Broadcast - servisi başlattı");
            context.startService(new Intent(context, MyService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.wtf("MyBroadcastReceiver - isMyServiceRunning?", true + "");
                    return true;
                }
            }
        }
        Log.wtf("MyBroadcastReceiver - isMyServiceRunning?", false + "");
        return false;
    }

}
