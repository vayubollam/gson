package suncor.com.android.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Locale;

import suncor.com.android.model.station.Station;

public class NavigationAppsHelper {

    public static void openNavigationApps(Context context, Station station) {
        /*
        String waze = "https://waze.com/ul?ll=" + station.getAddress().getLatitude() + "," + station.getAddress().getLongitude() + "&navigate=yes";
        String maps = "google.navigation:q=" + station.getAddress().getLatitude() + "," + station.getAddress().getLongitude();
         */
        try {

            String navigationIntentUri = "geo:" + station.getAddress().getLatitude() + ","
                    + station.getAddress().getLongitude() + "?q=" + Uri.encode(station.getAddress().getAddressLine());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationIntentUri));
            context.startActivity(mapIntent);
        } catch (ActivityNotFoundException ex) {
            final String appPackageName = "com.google.android.apps.maps";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException exception) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }


}
