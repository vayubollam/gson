package suncor.com.android.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import suncor.com.android.SuncorApplication;

public class PermissionManager {
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

    public void setFirstTimeAsking(String permission, boolean firsttime) {
        firstTimeAsking(permission, firsttime);
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

}
