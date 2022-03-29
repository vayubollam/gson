package suncor.com.android.analytics.enrollment

import android.app.Activity
import android.content.Context
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.Errors


const val SCROLL_DEPTH_20 = "20"
const val SCROLL_DEPTH_40 = "40"
const val SCROLL_DEPTH_60 = "60"
const val SCROLL_DEPTH_80 = "80"
const val SCROLL_DEPTH_100 = "100"



object EnrollmentAnalytics : BaseAnalytics() {

    private const val SCREEN_NAME_PROVINCE_SECURITY_HELP = "province-security-help"
    private const val SCREEN_NAME_CANADA_POST_SEARCH_DESCRIPTIVE_SCREEN_NAME = "canadapost-search-address"

    private const val FORM_NAME_ACTIVATE_PETRO_POINTS_CARD = "Activate Petro-Points Card"

    const val STEP_NAME_ADDRESS = "Address"
    const val  STEP_NAME_PERSONAL_INFORMATION = "Personal Information"


    @JvmStatic
    fun logSecurityQuesScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_PROVINCE_SECURITY_HELP)
    }

    @JvmStatic
    fun logInvalidEmailError(context: Context) {
        logErrorEvent(context,Errors.THE_EMAIL_HAS_ACCOUNT, FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(context,title,buttonText, FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

    @JvmStatic
    fun logAlertShown(context: Context, title: String) {
        logAlertDialogShown(context,title, FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

    @JvmStatic
    fun logCanadaPostScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_CANADA_POST_SEARCH_DESCRIPTIVE_SCREEN_NAME)
    }

}


object CardQuestionsAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME_SIGNUP_ACTIVATE = "petro-points-sign-up-activate"
    private const val FORM_NAME_SIGNUP_ACTIVATE = "Petro Points Sign Up Activate"

    @JvmStatic
    fun logScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_SIGNUP_ACTIVATE)
    }

    @JvmStatic
    fun logSomethingWentFormError(context: Context) {
        logFormErrorEvent(context, Errors.SOMETHING_WRONG, FORM_NAME_SIGNUP_ACTIVATE)
    }

}