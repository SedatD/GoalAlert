package vavien.agency.goalalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import java.util.Locale;

import static vavien.agency.goalalert.SplashActivity.mInterstitialAd2;


public class PresentActivity extends AppCompatActivity implements View.OnClickListener {
    public ImageView back_Image;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);

        nextButton = findViewById(R.id.devamBtn);
        back_Image = findViewById(R.id.img);

        String ydil = Locale.getDefault().getLanguage();

        switch (ydil) {
            case "en":
                back_Image.setImageResource(R.drawable.tutorial_ing);
                break;
            case "tr":
                back_Image.setImageResource(R.drawable.tutorial_tr);
                break;
            default:
                back_Image.setImageResource(R.drawable.tutorial_ing);
                break;

        }
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mInterstitialAd2 != null) {
            if (mInterstitialAd2.isLoaded()) {
                mInterstitialAd2.show();
                mInterstitialAd2.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(PresentActivity.this, MainActivity.class));
                        finish();
                    }
                });
            } else {
                startActivity(new Intent(PresentActivity.this, MainActivity.class));
                finish();
            }
            mInterstitialAd2.loadAd(new AdRequest.Builder().build());
        } else {
            startActivity(new Intent(PresentActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }

}
