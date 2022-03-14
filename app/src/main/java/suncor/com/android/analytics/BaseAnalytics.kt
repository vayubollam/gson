package suncor.com.android.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics


open class BaseAnalytics {

    object BaseParams {
        const val BUTTON_TEXT = "buttonText"

    }

    object BaseEvents {
        const val BUTTON_TAP = "button_tap"

    }


    fun logEvent(context: Context, eventName: String, bundle: Bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
    }

//    // TODO: This is how it should be done
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