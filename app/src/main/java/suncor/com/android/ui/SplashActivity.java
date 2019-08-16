package suncor.com.android.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.databinding.ActivitySplashBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.utilities.ConnectionUtil;
import suncor.com.android.utilities.FingerprintManager;

public class SplashActivity extends DaggerAppCompatActivity implements Animation.AnimationListener {
    private final static int ENTER_ANIMATION_DURATION = 1400;
    private final static int EXIT_ANIMATION_DURATION = 900;
    private static final String LAST_APP_VERSION = "last_app_version";
    Handler delayHandler = new Handler();
    private int delayExit = 900;
    private ActivitySplashBinding binding;
    private boolean firstTimeUse = false;
    public static final String LOGINFAILED = "loginFailed";

    @Inject
    SessionManager sessionManager;

    @Inject
    SettingsApi settingsApi;

    @Inject
    SuncorApplication application;

    @Inject
    FingerprintManager fingerPrintManager;

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

        if (application.isSplashShown()) {
            openMainActivity(false);
            return;
        }

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        AppCompatTextView splashText1 = findViewById(R.id.splash_text_1);
        AppCompatTextView splashText2 = findViewById(R.id.splash_text_2);
        switch (appStartMode) {
            case NORMAL:
                splashText2.setVisibility(View.GONE);
                break;
            case FIRST_TIME:
                firstTimeUse = true;
                splashText2.setVisibility(View.VISIBLE);
                splashText1.setText(R.string.drive_safely);
                binding.imgSplashFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.safety_image));
                binding.imgSplashFullScreen.setOnClickListener((v) -> {
                    startExitAnimation(false);
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

        binding.imgSplashFullScreen.startAnimation(animZoomOut);
        binding.imgRetail.startAnimation(animFromBottom);
        binding.textLayout.startAnimation(animFromLet);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }


    @Override
    public void onAnimationEnd(Animation animation) {
        if (!firstTimeUse) {
            binding.profilePd.setVisibility(View.VISIBLE);
        }
        if (ConnectionUtil.haveNetworkConnection(this)) {
            settingsApi.retrieveSettings().observe(this, resource -> {
                if (resource.status == Resource.Status.ERROR) {
                    binding.profilePd.setVisibility(View.GONE);
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.settings_failure_dialog_title)
                            .setMessage(R.string.settings_failure_dialog_message)
                            .setPositiveButton(R.string.settings_failure_dialog_button, (dialog, which) -> {
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                }
                if (resource.status == Resource.Status.SUCCESS) {
                    handleSettingsResponse(resource.data);
                }
            });
        } else {
            delayHandler.postDelayed(() -> {
                startExitAnimation(false);

            }, delayExit);
        }
    }

    private void handleSettingsResponse(SettingsResponse settingsResponse) {
        String minVersion = settingsResponse.getSettings().getMinAndroidVersion();
        String currentVersion = BuildConfig.VERSION_NAME;
        if (currentVersion.compareTo(minVersion) < 0) {
            binding.profilePd.setVisibility(View.GONE);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.update_required_dialog_title)
                    .setMessage(R.string.update_required_dialog_message)
                    .setPositiveButton(R.string.update_required_dialog_button, (dialog, which) -> {
                        final String appPackageName = "com.petrocanada.my_petro_canada";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            if (firstTimeUse) {
                delayHandler.postDelayed(() -> {
                    startExitAnimation(false);

                }, delayExit);
            } else {
                if (fingerPrintManager.isAutoLoginActivated()) {
                    sessionManager.checkLoginState().observe(this, loginState -> {
                        binding.profilePd.setVisibility(View.GONE);
                        switch (loginState) {
                            case LOGGED_IN:
                            case LOGGED_OUT:
                                startExitAnimation(false);
                                break;
                            case ERROR:
                                startExitAnimation(true);
                                break;

                        }
                    });
                } else {
                    startExitAnimation(false);
                }

            }
        }
    }

    private void startExitAnimation(boolean loginFailed) {
        float screenHeightDiff = getDifferenceHeight(getScreenHeight());
        float screenWidthDiff = getDifferenceWidth(getScreenWidth());

        ObjectAnimator toLeftAnim = ObjectAnimator.ofFloat(binding.textLayout, "translationX", -screenWidthDiff);

        ObjectAnimator toBottomAnim = ObjectAnimator.ofFloat(binding.imgRetail, "translationY", screenHeightDiff);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(EXIT_ANIMATION_DURATION);
        animSetXY.playTogether(toLeftAnim, toBottomAnim);
        animSetXY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                openMainActivity(loginFailed);
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

    private void openMainActivity(boolean loginFailed) {
        delayHandler.removeCallbacksAndMessages(null);
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.putExtra(LOGINFAILED, loginFailed);
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
        binding.imgRetail.getLocationOnScreen(locations);
        return screenHeight - locations[1];
    }

    private float getDifferenceWidth(float screenWidth) {
        int[] locations = new int[2];
        binding.textLayout.getLocationOnScreen(locations);
        return screenWidth - locations[0];
    }

    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }

}
