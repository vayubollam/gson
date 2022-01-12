package suncor.com.android.utilities;

import android.app.Dialog;
import android.content.Context;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import suncor.com.android.R;
import suncor.com.android.model.station.Station;

public class StationsUtil {
    public static void showIndependentStationAlert(Context context) {
        String analyticsTitle = context.getString(R.string.carwash_independent_alert_title)+"("+context.getString(R.string.carwash_independent_alert_message)+")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle,analyticsTitle)
        );
        Dialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.carwash_independent_alert_title))
                .setMessage(context.getString(R.string.carwash_independent_alert_message))
                .setPositiveButton(context.getString(R.string.ok), (dial, which)->{
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle,analyticsTitle),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.ok))
                    );
                })
                .setCancelable(false)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
