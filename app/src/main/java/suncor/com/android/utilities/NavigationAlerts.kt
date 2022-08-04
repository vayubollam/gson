package suncor.com.android.utilities

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class NavigationConsentAlerts {

    companion object Alerts {
        @JvmStatic
        fun createAlert(
            context: Context,
            title: String,
            message: String,
            positiveText: String,
            negativeText: String,
            url: String,
            listener: Consumer<String>
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(positiveText) { _: DialogInterface?, _: Int ->
                    listener.accept(
                        url
                    )
                }
                .setNegativeButton(negativeText) { dialog, _ -> dialog.cancel() }
                .show()
        }
    }
}
