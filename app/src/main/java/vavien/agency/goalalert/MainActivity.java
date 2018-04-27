package vavien.agency.goalalert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.onesignal.OneSignal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import vavien.agency.goalalert.adapters.PageAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static boolean live = false;
    //public static int huntFilter = 0;
    //public static int huntFilterMin = 0;
    static InterstitialAd mInterstitialAd;
    private Intent mServiceIntent;
    private MyService myService;
    private ViewPager viewPager;
    private PageAdapter adapter;
    private Uri imguri;

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null) {
                return netInfos.isConnected();
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        myService = new MyService(this);
        mServiceIntent = new Intent(this, myService.getClass());

        //MobileAds.initialize(this, "ca-app-pub-8446699920682817~7829108542");

        AdView adViewAlt = findViewById(R.id.adViewAlt);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewAlt.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-8446699920682817/4254084799"); // orjinal
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // test
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if (!isNetworkStatusAvialable(getApplicationContext()))
            Toast.makeText(getApplicationContext(), R.string.net_connection, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.live));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.result));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.nextmatch));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.alerts));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.viewpager);
        adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2 || tab.getPosition() == 3)
                    live = false;
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.isShown();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        finish();
                        live = false;
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    }
                });
            } else {
                finish();
                live = false;
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
            return true;
        }

        if (id == R.id.action_sozlesme) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(R.string.faq);
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    R.string.close,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return true;
        }

        if (id == R.id.action_sendUs) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(R.string.contact);
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    R.string.close,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return true;
        }

        if (id == R.id.action_share) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.stor);
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File file = new File(extStorageDirectory, "GoalAlert.png");
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imguri = Uri.fromFile(file);

            /*Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.putExtra(Intent.EXTRA_SUBJECT, "Goal Alert");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Goal Alert\n\nFor Android\nhttps://play.google.com/store/apps/details?id=vavien.agency.goalalert\n\nFor IOS\nComing Soon");

            //whatsappIntent.setType("text/html");
            //whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<h3>Goal Alert</h3><br /><p>https://play.google.com/store/apps/details?id=vavienagency.com.tipster</p>"));

            whatsappIntent.setType("image/*");
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imguri);
            whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            whatsappIntent.setPackage("com.whatsapp");
            try {
                this.startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, R.string.not_whatsap, Toast.LENGTH_SHORT).show();
            }*/

            /////
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, "Goal Alert");
                    share.putExtra(Intent.EXTRA_TEXT, "Goal Alert\n\nFor Android\nhttps://play.google.com/store/apps/details?id=vavien.agency.goalalert\n\nFor IOS\nComing Soon");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, imguri);
                    startActivity(Intent.createChooser(share, "Share Goal Alert with your friends"));
                } else {
                    requestPermission();
                }
            } else {
                requestPermission();
            }
            return true;
        }

        if (id == R.id.action_tutorials) {
            Intent intent = new Intent(this, PresentActivity.class);
            startActivity(intent);
            live = false;
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, "Goal Alert");
                    share.putExtra(Intent.EXTRA_TEXT, "Goal Alert\n\nFor Android\nhttps://play.google.com/store/apps/details?id=vavien.agency.goalalert\n\nFor IOS\nComing Soon");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, imguri);
                    startActivity(Intent.createChooser(share, "Share Goal Alert with your friends"));
                } else {
                    Toast.makeText(this, "Need Permission to Share", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        super.onDestroy();
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

}
