package suncor.com.android.utilities;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import suncor.com.android.R;

public class IndependentStationAlertUtil {
    public static void showIndependentStationAlert(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.carwash_independent_alert_title))
                .setMessage(context.getString(R.string.carwash_independent_alert_message))
                .setPositiveButton(context.getString(R.string.ok), null)
                .show();
    }

}
