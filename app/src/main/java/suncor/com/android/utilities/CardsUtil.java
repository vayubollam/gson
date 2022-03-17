package suncor.com.android.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

import suncor.com.android.R;

public class CardsUtil {
    public static void showZeroBalanceAlert(Context context, DialogInterface.OnClickListener posListener,
                                            DialogInterface.OnClickListener negListener) {
        Dialog dialog;
        // UNCOMMENT WHEN REDEEM/BUG SINGLE TICKET IS BACK
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
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
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
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
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

    public static void showSuspendedCardAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.reload_card_alet_title)).setMessage(context.getResources().getString(R.string.reload_card_alert_description))
                .setPositiveButton(context.getResources().getString(R.string.reload_card_alert_visit_web), (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.petro_canada_reload_url)));
                    context.startActivity(browserIntent);
                }).setNegativeButton(context.getResources().getString(R.string.reload_card_alert_cancel), (dialog, which) -> {
                    AnalyticsUtils.logEvent(context,AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, context.getString(R.string.reload_card_alet_title)),
                            new Pair<>(AnalyticsUtils.Param.alertSelection,context.getString(R.string.reload_card_alert_description)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME,AnalyticsUtils.getCardFormName()));
                });
        builder.show();
    }

    public static void ShowSuspendedCardAlertForActivateWash(Context context){
        AlertDialog alertWashDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.carwash_zero_error_alert_title)
                .setMessage(R.string.carwash_zero_error_alert_message)
                .setNegativeButton(R.string.carwash_zero_alert_close, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.carwash_zero_alert_buy, (dialog, which) -> {
                    dialog.dismiss();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(context.getString(R.string.petro_canada_carwash)));
                    context.startActivity(browserIntent);
                }).create();
        alertWashDialog.setCanceledOnTouchOutside(false);
        alertWashDialog.show();
    }
}
