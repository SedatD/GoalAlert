package vavien.agency.goalalert;

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
        context.startService(new Intent(context, MyService.class));
    }
}
