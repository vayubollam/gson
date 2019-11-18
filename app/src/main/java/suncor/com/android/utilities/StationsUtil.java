package suncor.com.android.utilities;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import suncor.com.android.R;
import suncor.com.android.model.station.Station;

public class StationsUtil {
    public static void showIndependentStationAlert(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.carwash_independent_alert_title))
                .setMessage(context.getString(R.string.carwash_independent_alert_message))
                .setPositiveButton(context.getString(R.string.ok), null)
                .show();
    }

    public static Station filterNearestCarWashStation(List<Station> stations) {
        for (Station station : stations) {
            if (station.hasWashOptions()) {
                return station;
            }
        }
        return null;
    }

}
