package suncor.com.android.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import suncor.com.android.analytics.BaseAnalytics.BaseEvents.ALERT_INTERACTION


open class BaseAnalytics {

    object BaseEvents {
        const val BUTTON_TAP = "button_tap"
        const val FORM_START = "form_start"
        const val INFO_TAP = "info_tap"
        const val ALERT_INTERACTION = "alert_interaction"

    }

    object BaseParams {
        const val BUTTON_TEXT = "buttonText"
        const val FORM_NAME = "formName"
        const val INFO_TEXT = "infoText"
        const val ALERT_TITLE = "alertTitle"
        const val ALERT_SELECTION = "alertSelection"

    }

    fun logAlertDialogInteraction(context: Context,alertTitle: String, alertSelection: String,formName : String){
        val bundle = Bundle()
        bundle.putString(BaseParams.ALERT_TITLE,alertTitle)
        bundle.putString(BaseParams.ALERT_SELECTION,alertSelection)
        bundle.putString(BaseParams.FORM_NAME,formName)

        FirebaseAnalytics.getInstance(context).logEvent(BaseEvents.ALERT_INTERACTION, bundle)
    }

    fun logEvent(context: Context, eventName: String, bundle: Bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
    }

//    // TODO: This is how it should be done(Screen class is not related to the activity)
//    fun logScreenName(context: Context, className: String, screenName: String?) {
//        val bundle = Bundle()
//        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
//        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS,className)
//        FirebaseAnalytics.getInstance(context)
//            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
//    }

    open fun logScreenNameClass(activity: Activity, screenName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.componentName.className)
        FirebaseAnalytics.getInstance(activity)
            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logUserProperty() {

    }

}