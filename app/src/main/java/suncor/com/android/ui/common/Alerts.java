package suncor.com.android.ui.common;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import suncor.com.android.R;
import suncor.com.android.utilities.ConnectionUtil;

public class Alerts {
    public static AlertDialog prepareGeneralErrorDialog(Context context) {
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}
