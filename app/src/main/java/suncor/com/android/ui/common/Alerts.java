package suncor.com.android.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import suncor.com.android.R;
import suncor.com.android.analytics.Errors;
import suncor.com.android.analytics.alerts.AlertsAnalytics;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.ConnectionUtil;

public class Alerts {
    public static AlertDialog prepareGeneralErrorDialog(Context context, String formName) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);

        if (hasInternetConnection) AlertsAnalytics.logSomethingWentWrong(context, formName);
        else AlertsAnalytics.logNoInternetConnection(context, formName);

        String analyticsName = (hasInternetConnection ? Errors.SOMETHING_WRONG : Errors.NO_INTERNET_CONNECTION)
                + "(" + (hasInternetConnection ? Errors.DETAIL_PLEASE_TRY_AGAIN : Errors.DETAIL_GO_ONLINE_TRY_AGAIN) + ")";

        AlertsAnalytics.logAlertDialogShown(context, analyticsName, formName);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)

                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    AlertsAnalytics.logAlertDialogInteraction(context, analyticsName, context.getString(R.string.ok), formName);
                    dialog.dismiss();
                });
        return builder.create();
    }

    public static AlertDialog prepareGeneralErrorDialog(Context context, String formName , DialogInterface.OnClickListener listener) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.formError,
                new Pair<>(AnalyticsUtils.Param.errorMessage,hasInternetConnection ? context.getString( R.string.msg_e001_title) : context.getString( R.string.msg_e002_title))
                ,new Pair<>(AnalyticsUtils.Param.formName, formName));

        String analyticsName = context.getString(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                + "(" + context.getString(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message) + ")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.formName, formName)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)

                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.ok)),
                            new Pair<>(AnalyticsUtils.Param.formName, formName)
                    );
                    listener.onClick(dialog, which);
                });
        return builder.create();
    }

    public static AlertDialog prepareGeneralErrorDialogWithTryAgain(Context context, DialogInterface.OnClickListener listener, String formName) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);
        if (hasInternetConnection) AlertsAnalytics.logNoInternetConnection(context, formName);
        else AlertsAnalytics.logSomethingWentWrong(context, formName);

        String analyticsName = (hasInternetConnection ? Errors.SOMETHING_WRONG : Errors.NO_INTERNET_CONNECTION)
                + "(" + (hasInternetConnection ? Errors.DETAIL_PLEASE_TRY_AGAIN : Errors.DETAIL_GO_ONLINE_TRY_AGAIN) + ")";

        AlertsAnalytics.logAlertDialogShown(context, analyticsName, formName);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)
                .setPositiveButton(R.string.msg_001_dialog_try_again, listener)
                .setNegativeButton(R.string.msg_001_dialog_cancel, (dialog, which) -> {
                    AlertsAnalytics.logAlertDialogInteraction(context, analyticsName, context.getString(R.string.cancel), formName);
                    dialog.dismiss();
                });
        return builder.create();
    }

    public static AlertDialog prepareCustomDialogWithTryAgain(String title, String message, Context context, DialogInterface.OnClickListener listener, String formName) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.FORMERROR,
                new Pair<>(AnalyticsUtils.Param.errorMessage, title + "(" + message + ")")
                , new Pair<>(AnalyticsUtils.Param.FORMNAME, formName));

        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, title + "(" + message + ")"),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, formName)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.msg_001_dialog_try_again, listener)
                .setNegativeButton(R.string.msg_001_dialog_cancel, (dialog, which) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, title + "(" + message + ")"),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.msg_001_dialog_cancel)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, formName)
                    );
                    dialog.dismiss();
                });
        return builder.create();
    }

    public static AlertDialog prepareCustomDialog(String title, String message, Context context, DialogInterface.OnClickListener listener,
                                                  String formName) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, title + "(" + message + ")"),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, formName)
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

    public static AlertDialog prepareCustomDialog(Context context, String title, String message,
                                                  String positiveButton, String negativeButton,
                                                  DialogInterface.OnClickListener positiveListener, String formName) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, title + "(" + message + ")"),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, formName)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveListener)
                .setNegativeButton(negativeButton, (dialogInterface, i) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, title + "(" + message + ")"),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, negativeButton),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, formName));
                    {
                        dialogInterface.dismiss();
                    }
                })
                .setOnDismissListener(DialogInterface::dismiss);
        return builder.create();
    }

    public static AlertDialog prepareCustomDialogOk(String title, String message, Context context, DialogInterface.OnClickListener listener,
                                                    String formName) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, title + "(" + message + ")"),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, formName)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, listener)
                .setOnDismissListener(dialogInterface -> {
                    listener.onClick(dialogInterface, 0);
                });
        return builder.create();
    }

}
