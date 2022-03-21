package suncor.com.android.analytics.pap

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.Errors


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

object FuellingAnalytics : BaseAnalytics(){
    private const val BUTTON_TEXT_CANCEL = "Cancel"
    private const val SCREEN_NAME_PAP_AUTH_LOADING = "pay-at-pump-fuelling-authorizing-loading"
    private const val SCREEN_NAME_PAP_AUTHORIZING =  "pay-at-pump-fuelling-authorizing"
    private const val SCREEN_NAME_PAP_FUELING_WILL_BEGIN = "pay-at-pump-fuelling-will-begin"
    private const val SCREEN_NAME_PAP_FUELING_HAS_BEGUN =  "pay-at-pump-fuelling-has-begun"
    private const val SCREEN_NAME_PAP_FUELING_CANCELLED = "pay-at-pump-fuelling-transaction-cancelled"
    private const val SCREEN_NAME_PAP_FUELING_ALMOST_COMPLETE = "pay-at-pump-fuelling-almost-complete"
    private const val PAP_FUELING_COMPLETE = "Fuelling Complete"

    private const val FORM_NAME_PAP = "Pay at Pump"


    @JvmStatic
    fun logPapAuthLoadingScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_AUTH_LOADING)
    }

    @JvmStatic
    fun logPapAuthScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_AUTHORIZING)
    }

    @JvmStatic
    fun logPapWillBeginScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_FUELING_WILL_BEGIN)
    }

    @JvmStatic
    fun logPapHasBegunScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_FUELING_HAS_BEGUN)
    }

    @JvmStatic
    fun logPapTransactionCancelScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_FUELING_CANCELLED)
    }

    @JvmStatic
    fun logPapAlmostComplete(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_PAP_FUELING_ALMOST_COMPLETE)
    }

    @JvmStatic
    fun logCancelButtonTap(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.BUTTON_TEXT, BUTTON_TEXT_CANCEL)
        logEvent(context, BaseEvents.BUTTON_TAP, bundle)
    }

    @JvmStatic
    fun logStopSessionAlertInteraction(context: Context) {
        logAlertDialogInteraction(context,
            "Stop this session? You can start another fill through the app or at the pump.",
            "Stop session",
            FORM_NAME_PAP
        )
    }

    @JvmStatic
    fun logSomethingWentWrongMessage(context: Context) {
        logErrorEvent(context,
            Errors.SOMETHING_WRONG,
            FORM_NAME_PAP
        )
    }

    @JvmStatic
    fun logTransactionCancelAlertInteraction(context: Context) {
        logAlertDialogInteraction(context,
            "Transaction cancelled (Your transaction was cancelled. You have not been charged.)",
            "Cancel",
            FORM_NAME_PAP
        )
    }

    @JvmStatic
    fun logFuellingFormComplete(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.FORM_SELECTION, PAP_FUELING_COMPLETE)
        bundle.putString(BaseParams.FORM_NAME, FORM_NAME_PAP)
        logEvent(context, BaseEvents.FORM_COMPLETE, bundle)
    }

    @JvmStatic
    fun logFuellingUpFormStep(context: Context) {
        val bundle = Bundle()
        bundle.putString(BaseParams.FORM_SELECTION, "Fuelling up")
        bundle.putString(BaseParams.FORM_NAME, FORM_NAME_PAP)
        logEvent(context, BaseEvents.FORM_STEP, bundle)
    }


}



