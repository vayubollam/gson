package suncor.com.android.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import javax.inject.Inject;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.main.MainActivity;

public class SplashActivity extends DaggerAppCompatActivity implements Animation.AnimationListener {
    private final static int ENTER_ANIMATION_DURATION = 1400;
    private final static int EXIT_ANIMATION_DURATION = 900;
    private static final String LAST_APP_VERSION = "last_app_version";
    Handler delayHandler = new Handler();
    private AppCompatImageView imageRetail;
    private AppCompatImageView backgroundImage;
    private LinearLayout textLayout;
    private int delayExit = 900;

    @Inject
    SessionManager sessionManager;

    @Inject
    SuncorApplication application;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppStart appStartMode = checkAppStart();

        if (appStartMode != AppStart.FIRST_TIME) {
            sessionManager.checkLoginState();
        }

        if (application.isSplashShown()) {
            openMainActivity();
            return;
        }

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        setContentView(R.layout.activity_splash);
        backgroundImage = findViewById(R.id.img_splash_full_screen);
        imageRetail = findViewById(R.id.img_retail);
        textLayout = findViewById(R.id.text_layout);
        AppCompatTextView splashText1 = findViewById(R.id.splash_text_1);
        AppCompatTextView splashText2 = findViewById(R.id.splash_text_2);
        switch (appStartMode) {
            case NORMAL:
                splashText2.setVisibility(View.GONE);
                break;
            case FIRST_TIME:
                splashText2.setVisibility(View.VISIBLE);
                splashText1.setText(R.string.drive_safely);
                backgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.drive_safely));
                backgroundImage.setOnClickListener((v) -> {
                    startExitAnimation();
                });
                delayExit = 3000;
                break;
            case FIRST_TIME_VERSION:
                //new update show what's new if applicable
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout);
        Animation animFromLet = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        Animation animFromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        Interpolator animInterpolator = new DecelerateInterpolator(3f);
        animFromBottom.setInterpolator(animInterpolator);
        animFromLet.setInterpolator(animInterpolator);
        animZoomOut.setInterpolator(animInterpolator);
        animFromBottom.setDuration(ENTER_ANIMATION_DURATION);
        animFromLet.setDuration(ENTER_ANIMATION_DURATION);
        animZoomOut.setDuration(ENTER_ANIMATION_DURATION);
        animZoomOut.setAnimationListener(this);

        backgroundImage.startAnimation(animZoomOut);
        imageRetail.startAnimation(animFromBottom);
        textLayout.startAnimation(animFromLet);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        delayHandler.postDelayed(() -> {
            startExitAnimation();
        }, delayExit);
    }

    private void startExitAnimation() {
        float screenHeightDiff = getDifferenceHeight(getScreenHeight());
        float screenWidthDiff = getDifferenceWidth(getScreenWidth());

        ObjectAnimator toLeftAnim = ObjectAnimator.ofFloat(textLayout, "translationX", -screenWidthDiff);

        ObjectAnimator toBottomAnim = ObjectAnimator.ofFloat(imageRetail, "translationY", screenHeightDiff);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(EXIT_ANIMATION_DURATION);
        animSetXY.playTogether(toLeftAnim, toBottomAnim);
        animSetXY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                openMainActivity();
            }
        });
        animSetXY.start();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public AppStart checkAppStart() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int lastVersionCode = sharedPreferences
                .getInt(LAST_APP_VERSION, -1);
        int currentVersionCode = BuildConfig.VERSION_CODE;
        AppStart appStart = checkAppStart(currentVersionCode, lastVersionCode);
        sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply();
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else {
            return AppStart.NORMAL;
        }
    }

    private void openMainActivity() {
        delayHandler.removeCallbacksAndMessages(null);
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        delayHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delayHandler.removeCallbacksAndMessages(null);
    }

    private float getDifferenceHeight(float screenHeight) {
        int[] locations = new int[2];
        imageRetail.getLocationOnScreen(locations);
        return screenHeight - locations[1];
    }

    private float getDifferenceWidth(float screenWidth) {
        int[] locations = new int[2];
        textLayout.getLocationOnScreen(locations);
        return screenWidth - locations[0];
    }

    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }

}
