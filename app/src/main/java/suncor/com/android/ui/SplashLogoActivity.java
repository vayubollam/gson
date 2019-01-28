package suncor.com.android.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import suncor.com.android.R;

public class SplashLogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_logo);
        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(() -> {
            switch (checkAppStart()) {
                case NORMAL:
                    proceedToHSplash();
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
        }, SPLASH_DISPLAY_LENGTH);
    }


    private void showSafetyMessage() {
        startActivity(new Intent(SplashLogoActivity.this, SafeDriveActivity.class));
        finish();
    }

    private void proceedToHSplash() {
        startActivity(new Intent(SplashLogoActivity.this, SplashActivity.class));
        finish();
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
}
