package suncor.com.android.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import suncor.com.android.analytics.BaseAnalytics.BaseParams.ERROR_MESSAGE
import suncor.com.android.analytics.BaseAnalytics.BaseParams.ERROR_MESSAGE_DETAIL
import suncor.com.android.analytics.BaseAnalytics.BaseParams.FORM_NAME


open class BaseAnalytics {

    object BaseEvents {
        const val BUTTON_TAP = "button_tap"
        const val INFO_TAP = "info_tap"
        const val INTER_SITE = "intersite"
        const val ERROR_LOG = "error_log"

        /* Forms */
        const val FORM_START = "form_start"
        const val FORM_COMPLETE = "form_complete"
        const val FORM_STEP = "form_step"

        const val PAYMENT_PREAUTHORIZE = "payment_preauthorize"


        /* Alerts */
        const val ALERT = "alert"
        const val ALERT_INTERACTION = "alert_interaction"

    }

    object BaseParams {
        const val BUTTON_TEXT = "buttonText"

        const val FORM_NAME = "formName"
        const val FORM_SELECTION = "formSelection"

        const val INFO_TEXT = "infoText"

        const val ALERT_TITLE = "alertTitle"
        const val ALERT_SELECTION = "alertSelection"

        const val INTER_SITE_URL = "intersiteURL"
        const val ERROR_MESSAGE = "errorMessage"
        const val ERROR_MESSAGE_DETAIL = "detailErrorMessage"

        const val PAYMENT_METHOD = "paymentMethod"
        const val FUEL_AMOUNT_SELECTION = "fuelAmountSelection"

    }

    fun logUserProperty() {

    }

    fun logAlertDialogInteraction(context: Context,alertTitle: String, alertSelection: String,formName : String){
        val bundle = Bundle()
        bundle.putString(BaseParams.ALERT_TITLE,alertTitle)
        bundle.putString(BaseParams.ALERT_SELECTION,alertSelection)
        bundle.putString(FORM_NAME,formName)

        FirebaseAnalytics.getInstance(context).logEvent(BaseEvents.ALERT_INTERACTION, bundle)
    }

    fun logAlertShown(context: Context,alertTitle: String, formName : String){
        val bundle = Bundle()
        bundle.putString(BaseParams.ALERT_TITLE,alertTitle)
        bundle.putString(FORM_NAME,formName)
        FirebaseAnalytics.getInstance(context).logEvent(BaseEvents.ALERT, bundle)
    }

    fun logErrorEvent(context: Context,errorMessage: String,formName: String, detailErrorMessage:String = ""){
        val bundle = Bundle()
        bundle.putString(ERROR_MESSAGE,errorMessage)
        bundle.putString(FORM_NAME,formName)
        bundle.putString(ERROR_MESSAGE_DETAIL,detailErrorMessage)
        FirebaseAnalytics.getInstance(context).logEvent(BaseEvents.ERROR_LOG, bundle)
    }

    fun logEvent(context: Context, eventName: String, bundle: Bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
    }

    open fun logScreenNameClass(activity: Activity, screenName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.componentName.className)
        FirebaseAnalytics.getInstance(activity)
            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    //    // TODO: This is how it should be done(Screen class is not related to the activity)
//    fun logScreenName(context: Context, className: String, screenName: String?) {
//        val bundle = Bundle()
//        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
//        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS,className)
//        FirebaseAnalytics.getInstance(context)
//            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
//    }


}