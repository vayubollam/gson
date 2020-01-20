package suncor.com.android.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import suncor.com.android.R;

public class CardsUtil {
    public static void showZeroBalanceAlert(Context context, DialogInterface.OnClickListener posListener,
                                            DialogInterface.OnClickListener negListener) {
        Dialog dialog;
        //TODO: UNCOMMENT WHEN REDEEM/BUG SINGLE TICKET IS BACK
//        if (negListener == null) {
//            dialog = new AlertDialog.Builder(context)
//                    .setTitle(R.string.zero_balance_alert_title)
//                    .setMessage(R.string.zero_balance_alert_message)
//                    .setPositiveButton(R.string.zero_balance_alert_buy, posListener)
//                    .setNegativeButton(R.string.cancel, null)
//                    .setCancelable(false)
//                    .create();
//        } else {
//            dialog = new AlertDialog.Builder(context)
//                    .setTitle(R.string.zero_balance_alert_title)
//                    .setMessage(R.string.zero_balance_alert_message)
//                    .setPositiveButton(R.string.zero_balance_alert_buy,posListener)
//                    .setNegativeButton(R.string.zero_balance_alert_view, negListener)
//                    .setNeutralButton(R.string.cancel, null)
//                    .setCancelable(false)
//                    .create();
//        }
        String analyticName = context.getString(R.string.zero_balance_alert_title)+"("+context.getString(R.string.zero_balance_alert_message)+")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName)
        );
        if (negListener == null) {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.zero_balance_alert_title)
                    .setMessage(R.string.zero_balance_alert_message)
                    .setPositiveButton(R.string.cancel, (dial, which) -> {
                        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                                new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.cancel))
                        );
                    })
                    .setCancelable(false)
                    .create();
        } else {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.zero_balance_alert_title)
                    .setMessage(R.string.zero_balance_alert_message)
                    .setPositiveButton(R.string.zero_balance_alert_view, negListener)
                    .setNegativeButton(R.string.cancel, (dial, which) -> {
                        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                                new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.cancel))
                        );
                    })
                    .setCancelable(false)
                    .create();
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showOtherCardAvailableAlert(Context context) {
        Dialog dialog;
        String analyticName = context.getString(R.string.zero_balance_alert_title)+"("+context.getString(R.string.zero_balance_alert_message)+")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName)
        );
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.zero_balance_alert_title)
                .setMessage(R.string.zero_balance_other_available_message)
                .setPositiveButton(R.string.cancel, (dial, which) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.cancel))
                    );
                })
                .setCancelable(false)
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
