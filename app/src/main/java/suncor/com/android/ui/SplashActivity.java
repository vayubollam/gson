package suncor.com.android.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.ui.home.HomeActivity;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    private RelativeLayout SafetyMessageLayout;
    Handler safetyMessageHandler = new Handler();
    private Animation animZoomOut;

    private AppCompatImageView img_retail;

    private AppCompatTextView txt_splash;
    private int delay = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SuncorApplication.splashShown) {
            openHomeActivity();
        } else {


            switch (checkAppStart()) {
                case NORMAL:
                    showSplach();
                    break;
                case FIRST_TIME_VERSION:
                    //new update show what's new if applicable
                    break;
                case FIRST_TIME:
                    showSafetyMessage();
                    break;
                default:
                    break;
            }
        }

    }

    private void showSplach() {
        setContentView(R.layout.activity_splash);
        AppCompatImageView img_splash = findViewById(R.id.img_splash_full_screen);
        img_retail = findViewById(R.id.img_retail);

        txt_splash = findViewById(R.id.txt_splash);
        // load the animation
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoomout);

        // set animation listener
        animZoomOut.setAnimationListener(this);


        img_splash.startAnimation(animZoomOut);
        img_retail.startAnimation(animZoomOut);
        txt_splash.startAnimation(animZoomOut);
    }


    private void showSafetyMessage() {
        setContentView(R.layout.activity_safe_drive);
        SafetyMessageLayout = findViewById(R.id.SafetyMessageLayout);
        SafetyMessageLayout.setOnClickListener(v -> {
            openHomeActivity();
        });
        //for testing purposes
        int SPLASH_DISPLAY_LENGTH = 30000;
        //real value (3 sec)
        //int SPLASH_DISPLAY_LENGTH = 3000;
        safetyMessageHandler.postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }



    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(() -> {
            SuncorApplication.splashShown = true;
            Intent homeActivity = new Intent(SplashActivity.this, HomeActivity.class);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            homeActivity.addCategory(Intent.CATEGORY_HOME);
            startActivity(homeActivity);
            ActivityCompat.finishAffinity(SplashActivity.this);

        }, delay);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }

    private static final String LAST_APP_VERSION = "last_app_version";

    public AppStart checkAppStart() {
        PackageInfo pInfo;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        AppStart appStart = AppStart.NORMAL;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersionCode = sharedPreferences
                    .getInt(LAST_APP_VERSION, -1);
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);
            sharedPreferences.edit()
                    .putInt(LAST_APP_VERSION, currentVersionCode).commit();
        } catch (PackageManager.NameNotFoundException e) {
        }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }

    private void openHomeActivity() {
        safetyMessageHandler.removeCallbacksAndMessages(null);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
