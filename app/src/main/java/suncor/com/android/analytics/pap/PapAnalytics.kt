package suncor.com.android.analytics.pap

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics


object ReceiptAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME = "pay-at-pump-receipt"
    private const val SCREEN_NAME_LOADING = "pay-at-pump-receipt-loading"
    private const val BUTTON_TEXT_SHARE_RECEIPT = "Share receipt"
    private const val BUTTON_TEXT_VIEW_RECEIPT =  "View Receipt"

    object Event {
        const val PAYMENT_COMPLETE = "payment_complete"
    }

    object Param {
        const val PAYMENT_METHOD = "paymentMethod"
    }

    @JvmStatic
    fun logScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME)
    }

    @JvmStatic
    fun logLoadingScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_LOADING)
    }

    @JvmStatic
    fun logPaymentComplete(context: Context, paymentMethod: String) {
        val bundle = Bundle()
        bundle.putString(Param.PAYMENT_METHOD, paymentMethod)
        logEvent(context, Event.PAYMENT_COMPLETE, bundle)
    }

    @JvmStatic
    fun logReceiptButtonTap(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.BUTTON_TEXT, BUTTON_TEXT_SHARE_RECEIPT)
        logEvent(context, BaseEvents.BUTTON_TAP, bundle)
    }

    @JvmStatic
    fun logViewReceiptButtonTap(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.BUTTON_TEXT, BUTTON_TEXT_VIEW_RECEIPT)
        logEvent(context, BaseEvents.BUTTON_TAP, bundle)
    }

}

object SelectPumpAnalytics : BaseAnalytics(){
    private const val SCREEN_NAME = "pay-at-pump-select-pump"
    private const val SCREEN_NAME_LOADING = "pay-at-pump-select-pump-loading"
    private const val SCREEN_NAME_SELECT_PUMP_HELP = "pay-at-pump-select-pump-help"
    private const val FORM_NAME_PAP = "Pay at Pump"
    private const val INFO_TEXT_SELECT_PUMP = "select pump number info"
    private const val ALERT_TITLE_APP_PAY_NOT_AVAILABLE = "In-app payment unavailable(You can\\’t use your app to pay at this station.)"
    private const val ALERT_SELECTION_CANCEL = "Cancel"


    @JvmStatic
    fun logScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME)
    }

    @JvmStatic
    fun logLoadingScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_LOADING)
    }

    @JvmStatic
    fun logFormStart(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.FORM_NAME, FORM_NAME_PAP)
        logEvent(context,BaseEvents.FORM_START,bundle)
    }

    @JvmStatic
    fun logSelectPumpInfoTap(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.INFO_TEXT, INFO_TEXT_SELECT_PUMP)
        logEvent(context,BaseEvents.INFO_TAP,bundle)
    }

    @JvmStatic
    fun logInAppPaymentUnAvailableAlert(context: Context) {
        logAlertDialogInteraction(
            context,
            ALERT_TITLE_APP_PAY_NOT_AVAILABLE,
            ALERT_SELECTION_CANCEL,
            FORM_NAME_PAP
        )
    }

    @JvmStatic
    fun logSelectPumpHelpScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_SELECT_PUMP_HELP)
    }
}

object FuelUpAnalytics: BaseAnalytics(){
    private const val INFO_TEXT_SELECT_PUMP = "select pump number info"
    private const val SCREEN_NAME_PAP_PRE_AUTH_LOADING = "pay-at-pump-preauthorize-loading"


    @JvmStatic
    fun logSelectPumpInfoTap(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.INFO_TEXT, INFO_TEXT_SELECT_PUMP)
        logEvent(context,BaseEvents.INFO_TAP,bundle)
    }

    @JvmStatic
    fun logPAPreAuthLoadingScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_PRE_AUTH_LOADING)
    }

    @JvmStatic
    fun logInterSiteURL(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.INTER_SITE_URL, INFO_TEXT_SELECT_PUMP)
        logEvent(context,BaseEvents.INTER_SITE,bundle)
    }


}



