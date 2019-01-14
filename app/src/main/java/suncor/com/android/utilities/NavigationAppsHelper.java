package suncor.com.android.utilities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import suncor.com.android.dataObjects.Station;

public class NavigationAppsHelper {

    public static void openNavigationApps(Activity activity, Station station) {
        try {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + station.getAddress().getLatitude() + "," + station.getAddress().getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            activity.startActivity(mapIntent);
        } catch (ActivityNotFoundException exeption) {
            final String appPackageName = "com.google.android.apps.maps";
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException exception) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }
}
