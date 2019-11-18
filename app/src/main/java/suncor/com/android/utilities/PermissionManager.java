package suncor.com.android.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import suncor.com.android.SuncorApplication;
import suncor.com.android.ui.main.carwash.CarWashCardFragment;

public class PermissionManager {
    private static final String LOCATION_ALERT = "location_alter";
    SuncorApplication context;
    UserLocalSettings userLocalSettings;

    @Inject
    public PermissionManager(SuncorApplication context, UserLocalSettings userLocalSettings) {
        this.context = context;
        this.userLocalSettings = userLocalSettings;
    }

    public boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public void checkPermission(Context context, String permission, PermissionAskListener listener) {
        if (shouldAskPermission(context, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, permission)) {
                listener.onPermissionPreviouslyDenied();
            } else {
                if (isFirstTimeAsking(permission)) {
                    listener.onNeedPermission();
                } else {
                    listener.onPermissionPreviouslyDeniedWithNeverAskAgain();
                }
            }
        } else {
            listener.onPermissionGranted();
        }
    }

    public void checkCarWashPermission(Context context, String permission, CarWashPermissionListener listener) {
        if (isFirstTimeAsking(permission)) {
            setIsFirstTimeAccessCarWash(permission, false);
            if (shouldAskPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
                listener.onFirstTimeAccessCarWash();
        }
    }

    public void setFirstTimeAsking(String permission, boolean firsttime) {
        firstTimeAsking(permission, firsttime);
    }

    public boolean isAlertShown() {
        return userLocalSettings.getBool(LOCATION_ALERT, false);
    }

    public void setAlertShown(boolean isShown) {
        userLocalSettings.setBool(LOCATION_ALERT, isShown);
    }


    private boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }


    public interface PermissionAskListener {
        void onNeedPermission();

        void onPermissionPreviouslyDenied();

        void onPermissionPreviouslyDeniedWithNeverAskAgain();

        void onPermissionGranted();
    }

    public interface CarWashPermissionListener {
        void onFirstTimeAccessCarWash();
    }

    public static void openAppSettings(Activity activity) {

        Uri packageUri = Uri.fromParts("package", activity.getPackageName(), null);

        Intent applicationDetailsSettingsIntent = new Intent();

        applicationDetailsSettingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        applicationDetailsSettingsIntent.setData(packageUri);
        applicationDetailsSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(applicationDetailsSettingsIntent);

    }

    private void firstTimeAsking(String permission, boolean isFirstTime) {
        userLocalSettings.setBool(permission, isFirstTime);
    }

    private boolean isFirstTimeAsking(String permission) {
        return userLocalSettings.getBool(permission, true);
    }

    public void setIsFirstTimeAccessCarWash(String key, boolean isFirstTime) {
        userLocalSettings.setBool(key, isFirstTime);
    }

}
