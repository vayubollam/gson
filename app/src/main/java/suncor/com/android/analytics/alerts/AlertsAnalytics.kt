package suncor.com.android.analytics.alerts

import android.content.Context
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.Errors

object AlertsAnalytics : BaseAnalytics(){
    @JvmStatic
    fun logNoInternetConnection(context: Context, formName: String) {
        logErrorEvent(context,Errors.NO_INTERNET_CONNECTION,formName)
    }

    @JvmStatic
    fun logSomethingWentWrong(context: Context, formName: String) {
        logErrorEvent(context,Errors.SOMETHING_WRONG,formName)
    }

}