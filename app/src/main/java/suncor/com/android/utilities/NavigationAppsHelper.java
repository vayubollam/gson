package suncor.com.android.utilities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dialogs.OpenWithDialog;

public class NavigationAppsHelper {

    private FragmentActivity activity;
    private SharedPreferences prefs;

    public NavigationAppsHelper(FragmentActivity activity) {
        this.activity = activity;
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void openNavigationApps(Station station) {
        Boolean always = prefs.getBoolean("always", false);
        if (always) {
            int choice = prefs.getInt("choice", 0);
            if (choice == 1) {
                openGoogleMAps(station);
            }
            if (choice == 2) {
                openWaze(station);
            }
            if (choice == 0) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", station.getAddress().getLatitude());
                bundle.putDouble("lng", station.getAddress().getLongitude());
                OpenWithDialog openWithDialog = new OpenWithDialog();
                openWithDialog.setArguments(bundle);
                openWithDialog.show(activity.getSupportFragmentManager(), "choosing");
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putDouble("lat", station.getAddress().getLatitude());
            bundle.putDouble("lng", station.getAddress().getLongitude());
            OpenWithDialog openWithDialog = new OpenWithDialog();
            openWithDialog.setArguments(bundle);
            openWithDialog.show(activity.getSupportFragmentManager(), "choosing");
        }
    }

    private boolean isGoogleMapsInstalled() {
        try {
            return activity.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void openGoogleMAps(Station station) {
        if (isGoogleMapsInstalled()) {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + station.getAddress().getLatitude() + "," + station.getAddress().getLongitude());//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            activity.startActivity(mapIntent);
        } else {
            final String appPackageName = "com.google.android.apps.maps";
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    private void openWaze(Station station) {
        if (isWazeInstalled()) {
            String url = "waze://?ll=" + station.getAddress().getLatitude() + "," + station.getAddress().getLongitude() + "&navigate=yes";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mapIntent.setPackage("com.waze");
            activity.startActivity(mapIntent);
        } else {
            final String appPackageName = "com.waze";
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    private boolean isWazeInstalled() {
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo("com.waze", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
