package suncor.com.android.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import suncor.com.android.analytics.BaseEvents.FORM_STEP
import suncor.com.android.analytics.BaseEvents.SCROLL
import suncor.com.android.analytics.BaseEvents.TAP_TO_CALL
import suncor.com.android.analytics.BaseParams.ERROR_MESSAGE
import suncor.com.android.analytics.BaseParams.ERROR_MESSAGE_DETAIL
import suncor.com.android.analytics.BaseParams.FORM_NAME
import suncor.com.android.analytics.BaseParams.FORM_SELECTION
import suncor.com.android.analytics.BaseParams.PHONE_NUMBER_TAPPED
import suncor.com.android.analytics.BaseParams.SCROLL_DEPTH_THRESHOLD
import suncor.com.android.analytics.BaseParams.STEP_NAME

abstract class BaseAnalytics {

    fun logUserProperty() {

    }


    companion object {

        const val BUTTON_TEXT_OK = "ok"
        const val BUTTON_TEXT_CANCEL = "cancel"
        const val BUTTON_TEXT_ENABLE = "Enable"

        @JvmStatic
        fun logEvent(context: Context, eventName: String, bundle: Bundle) {
            FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
        }

        /*
                alert
                1. alertTitle
                2. formName
        * */
        @JvmStatic
        fun logAlertDialogShown(context: Context, alertTitle: String, formName: String) {
            val bundle = Bundle()
            bundle.putString(BaseParams.ALERT_TITLE, alertTitle.take(100))
            bundle.putString(FORM_NAME, formName)
            logEvent(context, BaseEvents.ALERT, bundle)
        }

        /*
        alert_interaction
         1. alertTitle
         2. alertSelection
         3. formName
        * */
        @JvmStatic
        fun logAlertDialogInteraction(
            context: Context,
            alertTitle: String,
            alertSelection: String,
            formName: String
        ) {
            val bundle = Bundle()
            bundle.putString(BaseParams.ALERT_TITLE, alertTitle.take(100))
            bundle.putString(BaseParams.ALERT_SELECTION, alertSelection)
            bundle.putString(FORM_NAME, formName)

            logEvent(context, BaseEvents.ALERT_INTERACTION, bundle)
        }

        /*
        tap_to_call
        1. phoneNumberTapped

        * */

        @JvmStatic
        fun logTapToCall(context: Context, phoneNumber: String) {
            val bundle = Bundle()
            bundle.putString(PHONE_NUMBER_TAPPED, phoneNumber)
            logEvent(context, TAP_TO_CALL, bundle)
        }

        /*
        scroll
        1.scrollDepthThreshold
        * */
        @JvmStatic
        fun logScrollDepth(context: Context, depth: String) {
            val bundle = Bundle()
            bundle.putString(SCROLL_DEPTH_THRESHOLD, depth)
            logEvent(context, SCROLL, bundle)
        }

        /*
        form_step

         stepName
         formSelection
         formName
        * */
        @JvmStatic
        fun logFormStep(
            context: Context,
            formName: String,
            stepName: String = ""
        ) {
            val bundle = Bundle()
            bundle.putString(FORM_NAME, formName)
            bundle.putString(STEP_NAME, stepName)
            logEvent(context, FORM_STEP, bundle)
        }

        /*
        screen_view
        1. screen_name
        2. screen_class
        * */
        @JvmStatic
        fun logScreenNameClass(context: Context, screenName: String?, className: String) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            logEvent(context, FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }

        /*
        form_start
        1. formName
        * */
        @JvmStatic
        fun logFormStart(context: Context, formName: String) {
            val bundle = Bundle()
            bundle.putString(FORM_NAME, formName)
            logEvent(context, BaseEvents.FORM_START, bundle)
        }

        /*
               intersite
               1. intersiteURL

               * */
        @JvmStatic
        fun logInterSiteURL(context: Context, url: String) {
            val bundle = Bundle()
            bundle.putString(BaseParams.INTER_SITE_URL, url)
            logEvent(context, BaseEvents.INTER_SITE, bundle)
        }

        /*
        timer30
        * */
        @JvmStatic
        fun logTimer30Event(context: Context) {
            logEvent(context, BaseEvents.TIMER_30, Bundle())
        }

        /*
        button_tap
        1. buttonText
        * */
        @JvmStatic
        fun logButtonTap(context: Context, buttonText: String) {
            val bundle = Bundle()
            bundle.putString(BaseParams.BUTTON_TEXT, buttonText)
            logEvent(context, BaseEvents.BUTTON_TAP, bundle)
        }

        /*
           error_log

           errorMessage
           detailErrorMessage
           formName
           *
           * */
        @JvmStatic
        fun logErrorEvent(
            context: Context,
            errorMessage: String,
            formName: String,
            detailErrorMessage: String = ""
        ) {
            val bundle = Bundle()
            bundle.putString(ERROR_MESSAGE, errorMessage)
            bundle.putString(FORM_NAME, formName)
            bundle.putString(ERROR_MESSAGE_DETAIL, detailErrorMessage)
            logEvent(context, BaseEvents.ERROR_LOG, bundle)
        }

        /**
         * Use this event to log the errors while submitting a form.
         */
        @JvmStatic
        fun logFormErrorEvent(
            context: Context,
            errorMessage: String,
            formName: String,
        ) {
            val bundle = Bundle()
            bundle.putString(ERROR_MESSAGE, errorMessage)
            bundle.putString(FORM_NAME, formName)
            logEvent(context, BaseEvents.FORM_ERROR, bundle)
        }

    }


}