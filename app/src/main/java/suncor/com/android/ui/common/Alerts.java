package suncor.com.android.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import suncor.com.android.R;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.ConnectionUtil;

public class Alerts {
    public static AlertDialog prepareGeneralErrorDialog(Context context) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);

        String analyticsName = context.getString(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                + "(" + context.getString(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message) + ")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)

                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.ok))
                    );
                    dialog.dismiss();
                });
        return builder.create();
    }

    public static AlertDialog prepareGeneralErrorDialogWithTryAgain(Context context, DialogInterface.OnClickListener listener) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);
        String analyticsName = context.getString(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                + "(" + context.getString(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message) + ")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)
                .setPositiveButton(R.string.msg_001_dialog_try_again, listener)
                .setNegativeButton(R.string.msg_001_dialog_cancel, (dialog, which) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.msg_001_dialog_cancel))
                    );
                    dialog.dismiss();
                });
        return builder.create();
    }

    public static AlertDialog prepareCustomDialogWithTryAgain(String title, String message, Context context, DialogInterface.OnClickListener listener) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, title+"("+message+")")
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.msg_001_dialog_try_again, listener)
                .setNegativeButton(R.string.msg_001_dialog_cancel, (dialog, which) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, title+"("+message+")"),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.msg_001_dialog_cancel))
                    );
                    dialog.dismiss();
                });
        return builder.create();
    }

    public static AlertDialog prepareCustomDialog(String title, String message, Context context, DialogInterface.OnClickListener listener) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, title+"("+message+")")
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.cancel, listener)
                .setOnDismissListener(dialogInterface -> {
                    listener.onClick(dialogInterface, 0);
                });
        return builder.create();
    }
}
