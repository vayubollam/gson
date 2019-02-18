package suncor.com.android.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.home.HomeActivity;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    private RelativeLayout SafetyMessageLayout;
    Handler safetyMessageHandler = new Handler();
    private Animation animZoomOut, animFromLet, animFromBottom;

    private AppCompatImageView img_retail;

    private AppCompatTextView txt_splash;
    private int delay = 1000;
    private float screenWidthDiff, screenHeightDiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SessionManager.getInstance().isUserLoggedIn()) {
            SessionManager.getInstance().checkLoginState();
        }

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
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout);
        animFromLet = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        animFromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        animFromBottom.setDuration(delay);
        animFromLet.setDuration(delay);
        animZoomOut.setDuration(delay);
        animZoomOut.setAnimationListener(this);


        img_splash.startAnimation(animZoomOut);
        img_retail.startAnimation(animFromBottom);
        txt_splash.startAnimation(animFromLet);
    }


    private void showSafetyMessage() {
        setContentView(R.layout.activity_safe_drive);
        SafetyMessageLayout = findViewById(R.id.SafetyMessageLayout);
        SafetyMessageLayout.setOnClickListener(v -> {
            openHomeActivity();
        });
        //for testing purposes
        int SPLASH_DISPLAY_LENGTH = 3000;
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
        screenHeightDiff = getDifferenceHeight(getScreenHeight());
        screenWidthDiff = getDifferenceWidth(getScreenWidth());
        safetyMessageHandler.postDelayed(() -> {
            ObjectAnimator toLeftAnim = ObjectAnimator.ofFloat(txt_splash, "translationX", -screenWidthDiff);
            toLeftAnim.setDuration(delay);
            toLeftAnim.start();
            ObjectAnimator toBottomAnim = ObjectAnimator.ofFloat(img_retail, "translationY", screenHeightDiff);
            toBottomAnim.setDuration(delay / 2);
            toBottomAnim.start();
        }, delay / 2);
        safetyMessageHandler.postDelayed(() -> {
            openHomeActivity();
        }, delay / 2);
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

    @Override
    protected void onPause() {
        super.onPause();
        safetyMessageHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        safetyMessageHandler.removeCallbacksAndMessages(null);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private float getDifferenceHeight(float screenHeight) {
        int[] locations = new int[2];
        img_retail.getLocationOnScreen(locations);
        return screenHeight - locations[1];
    }

    private float getDifferenceWidth(float screenWidth) {
        int[] locations = new int[2];
        txt_splash.getLocationOnScreen(locations);
        return screenWidth - locations[0];
    }

}
