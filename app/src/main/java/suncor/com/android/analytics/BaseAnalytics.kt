package suncor.com.android.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import suncor.com.android.analytics.BaseEvents.TAP_TO_CALL
import suncor.com.android.analytics.BaseParams.ERROR_MESSAGE
import suncor.com.android.analytics.BaseParams.ERROR_MESSAGE_DETAIL
import suncor.com.android.analytics.BaseParams.FORM_NAME
import suncor.com.android.analytics.BaseParams.PHONE_NUMBER_TAPPED


open class BaseAnalytics {


    fun logUserProperty() {

    }

    fun logAlertDialogInteraction(context: Context,alertTitle: String, alertSelection: String,formName : String){
        val bundle = Bundle()
        bundle.putString(BaseParams.ALERT_TITLE,alertTitle)
        bundle.putString(BaseParams.ALERT_SELECTION,alertSelection)
        bundle.putString(FORM_NAME,formName)

        FirebaseAnalytics.getInstance(context).logEvent(BaseEvents.ALERT_INTERACTION, bundle)
    }

    fun logAlertDialogShown(context: Context, alertTitle: String, formName : String){
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


    fun logFormErrorEvent(context: Context,errorMessage: String,formName: String, detailErrorMessage:String = ""){
        val bundle = Bundle()
        bundle.putString(ERROR_MESSAGE,errorMessage)
        bundle.putString(FORM_NAME,formName)
        bundle.putString(ERROR_MESSAGE_DETAIL,detailErrorMessage)
        FirebaseAnalytics.getInstance(context).logEvent(BaseEvents.FORM_ERROR, bundle)
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

    companion object{
        @JvmStatic
        fun logTapToCall(context: Context,phoneNumber:String){
            val bundle = Bundle()
            bundle.putString(PHONE_NUMBER_TAPPED, phoneNumber)
            FirebaseAnalytics.getInstance(context).logEvent(TAP_TO_CALL,bundle)
        }
    }


}