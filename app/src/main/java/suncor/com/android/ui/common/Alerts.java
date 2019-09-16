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
        AnalyticsUtils.logEvent(context, "error_log", new Pair<>("errorMessage", hasInternetConnection ? context.getString(R.string.msg_e001_title) : context.getString(R.string.msg_e002_title)));
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)

                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public static AlertDialog prepareGeneralErrorDialogWithTryAgain(Context context, DialogInterface.OnClickListener listener) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);
        AnalyticsUtils.logEvent(context, "error_log", new Pair<>("errorMessage", hasInternetConnection ? context.getString(R.string.msg_e001_title) : context.getString(R.string.msg_e002_title)));
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)
                .setPositiveButton(R.string.msg_001_dialog_try_again, listener)
                .setNegativeButton(R.string.msg_001_dialog_cancel, (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public static AlertDialog prepareCustomDialogWithTryAgain(String title, String message, Context context, DialogInterface.OnClickListener listener) {
        AnalyticsUtils.logEvent(context, "error_log", new Pair<>("errorMessage", title));
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.msg_001_dialog_try_again, listener)
                .setNegativeButton(R.string.msg_001_dialog_cancel, (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}
