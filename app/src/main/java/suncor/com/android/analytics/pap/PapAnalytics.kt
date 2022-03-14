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

}


