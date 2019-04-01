package suncor.com.android.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import suncor.com.android.model.station.Station;

public class NavigationAppsHelper {

    public static void openNavigationApps(Context context, Station station) {
        try {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + station.getAddress().getLatitude() + "," + station.getAddress().getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            context.startActivity(mapIntent);
        } catch (ActivityNotFoundException exeption) {
            final String appPackageName = "com.google.android.apps.maps";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException exception) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }
}
