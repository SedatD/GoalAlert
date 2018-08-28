package vavien.agency.goalalert.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import vavien.agency.goalalert.R;

public class SplashActivity extends AppCompatActivity {

    static InterstitialAd mInterstitialAd2;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView_sp_logo = findViewById(R.id.imageView_sp_logo);

        MobileAds.initialize(this, "ca-app-pub-8446699920682817~7829108542");

        mInterstitialAd2 = new InterstitialAd(this);
        mInterstitialAd2.setAdUnitId("ca-app-pub-8446699920682817/8990625904"); // orjinal
        //mInterstitialAd2.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // test
        mInterstitialAd2.loadAd(new AdRequest.Builder().build());

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final boolean myBoolean = preferences.getBoolean("isFirstTimer", true);

        setAnimation(imageView_sp_logo, 1500);

        Thread mSplashThread;
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(1750);
                    }
                } catch (InterruptedException ex) {
                    Log.wtf("SplashAct", "catche düştü");
                } finally {

                    if (myBoolean) {
                        intent = new Intent(getApplicationContext(), PresentActivity.class);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isFirstTimer", false);
                        editor.apply();
                    } else
                        intent = new Intent(getApplicationContext(), MainActivity.class);

                    startActivity(intent);
                    finish();

                }
            }
        };
        mSplashThread.start();
    }

    private void setAnimation(View viewToAnimate, int time) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(time);
        viewToAnimate.startAnimation(anim);
    }

}
