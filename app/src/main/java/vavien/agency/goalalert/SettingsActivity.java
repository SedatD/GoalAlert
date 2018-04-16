package vavien.agency.goalalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.ads.AdRequest;

public class SettingsActivity extends AppCompatActivity {
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch vibrate = (Switch) findViewById(R.id.vibrateSwitch);
        Switch message = (Switch) findViewById(R.id.goalalertSwitch);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();

        boolean booleanVibrate = preferences.getBoolean("booleanVibrate", true);
        boolean booleanMessage = preferences.getBoolean("booleanMessage", true);

        vibrate.setChecked(booleanVibrate);
        message.setChecked(booleanMessage);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //Switch ON
                    editor.putBoolean("booleanVibrate", true);
                    //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    //Switch OFF
                    editor.putBoolean("booleanVibrate", false);
                    //audioManager.setRingerMode(AudioManager.MODE_NORMAL);
                }
                editor.apply();
            }
        });

        /*if (vibrate.isChecked())
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        else
            audioManager.setRingerMode(AudioManager.MODE_NORMAL);*/


        message.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {
                if (b1) {
                    //Switch ON
                    editor.putBoolean("booleanMessage", true);
                } else {
                    //Switch OFF
                    editor.putBoolean("booleanMessage", false);
                }
                editor.apply();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        MainActivity.mInterstitialAd.loadAd(new AdRequest.Builder().build());
        finish();
    }
}
