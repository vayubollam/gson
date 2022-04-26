package suncor.com.android.analytics.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseEvents.LOGIN
import suncor.com.android.analytics.BaseParams
import suncor.com.android.analytics.Errors


object LoginAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME_LOGIN = "Login"
    private const val FORM_NAME_LOGIN = "Login"

    @JvmStatic
    fun logLoginEvent(context: Context, userId: String, buildNumber: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.USER_ID, userId)
        bundle.putString(BaseParams.BUILD_NUMBER, userId)
        logEvent(context, LOGIN, bundle)
    }

    @JvmStatic
    fun logLoginScreenName(activity: Activity) {
        logScreenNameClass(activity,SCREEN_NAME_LOGIN)
    }

    @JvmStatic
    fun logError(context: Context, title: String) {
        logErrorEvent(context,title, FORM_NAME_LOGIN)
    }

    @JvmStatic
    fun logAlert(context: Context, title: String) {
        logAlertDialogShown(context,title, FORM_NAME_LOGIN)
    }

    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(context,title,buttonText, FORM_NAME_LOGIN)
    }


}


const val FORM_NAME_LOGIN_FORCE_NEW_PASSWORD = "Login Force New Password"

object CreatePasswordAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME_FORCE_PASSWORD_CHANGE = "login-force-new-password"

    @JvmStatic
    fun logScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_FORCE_PASSWORD_CHANGE)
    }

    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(context, title, buttonText, FORM_NAME_LOGIN_FORCE_NEW_PASSWORD)
    }

    @JvmStatic
    fun logInvalidPwdAlertShown(context: Context, title: String) {
        logAlertDialogShown(context, title, FORM_NAME_LOGIN_FORCE_NEW_PASSWORD)
    }

    @JvmStatic
    fun logResetPwd(context: Context, userId: String, buildNumber: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.USER_ID, userId)
        bundle.putString(BaseParams.BUILD_NUMBER, userId)
        logEvent(context, BaseEvents.PASSWORD_RESET, bundle)
    }

    @JvmStatic
    fun logInvalidPwdError(context: Context) {
        logErrorEvent(context, Errors.INVALID_PASSWORD, FORM_NAME_LOGIN_FORCE_NEW_PASSWORD)
    }


}