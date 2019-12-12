package suncor.com.android.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import suncor.com.android.R;

public class CardsUtil {
    public static void showZeroBalanceAlert(Context context, DialogInterface.OnClickListener posListener,
                                            DialogInterface.OnClickListener negListener) {
        //TODO: posListener is for single ticket redeem flow in the future
        Dialog dialog;
        if (negListener == null) {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.zero_balance_alert_title)
                    .setMessage(R.string.zero_balance_alert_message)
                    .setPositiveButton(R.string.cancel, null)
                    .setCancelable(false)
                    .create();
        } else {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.zero_balance_alert_title)
                    .setMessage(R.string.zero_balance_alert_message)
                    .setPositiveButton(R.string.zero_balance_alert_view, negListener)
                    .setNegativeButton(R.string.cancel, null)
                    .setCancelable(false)
                    .create();
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void showOtherCardAvailableAlert(Context context) {
        Dialog dialog;
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.zero_balance_alert_title)
                .setMessage(R.string.zero_balance_other_available_message)
                .setPositiveButton(R.string.cancel, null)
                .setCancelable(false)
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
