package suncor.com.android.analytics.enrollment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.Errors
import suncor.com.android.analytics.Errors.ENTER_DIFFERENT_EMAIL_OR_CALL


const val SCROLL_DEPTH_20 = "20"
const val SCROLL_DEPTH_40 = "40"
const val SCROLL_DEPTH_60 = "60"
const val SCROLL_DEPTH_80 = "80"
const val SCROLL_DEPTH_100 = "100"

const val FORM_NAME_ACTIVATE_PETRO_POINTS_CARD = "Activate Petro-Points Card"
const val FORM_NAME_JOIN_PETRO_POINTS = "Join Petro-Points"



object EnrollmentAnalytics : BaseAnalytics() {
    private const val EVENT_SIGNUP = "sign_up"
    private const val PARAM_SIGNUP_METHOD = "method"

    const val SCREEN_NAME_PROVINCE_SECURITY_HELP = "province-security-help"
    const val SCREEN_NAME_ACTIVATE_SUCCESS = "activate-success"
    const val SCREEN_NAME_SIGNUP_SUCCESS = "sign-up-success"
    const val SCREEN_NAME_ACTIVATE_I_HAVE_CARD = "activate-i-have-a-card"
    const val SCREEN_NAME_ACTIVATE_I_DO_NOT_HAVE_CARD = "sign-up-i-dont-have-a-card"


    private const val SCREEN_NAME_CANADA_POST_SEARCH_DESCRIPTIVE_SCREEN_NAME = "canadapost-search-address"

    const val STEP_NAME_ADDRESS = "Address"
    const val  STEP_NAME_PERSONAL_INFORMATION = "Personal Information"
    const val  STEP_NAME_COMPLETE_SIGNUP = "Complete Signup"



    @JvmStatic
    fun logInvalidEmailError(context: Context) {
        logErrorEvent(context,Errors.THE_EMAIL_HAS_ACCOUNT, FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(context,title,buttonText, FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

    @JvmStatic
    fun logAlertShown(context: Context, title: String, formName: String = FORM_NAME_ACTIVATE_PETRO_POINTS_CARD ) {
        logAlertDialogShown(context,title, formName)
    }

    @JvmStatic
    fun logCanadaPostScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_CANADA_POST_SEARCH_DESCRIPTIVE_SCREEN_NAME)
    }


    @JvmStatic
    fun logDiffEmailError(context: Context, formName: String) {
        logErrorEvent(context,ENTER_DIFFERENT_EMAIL_OR_CALL,formName)
    }

    @JvmStatic
    fun logSignupEvent(context: Context, sign_up_method: String) {
        val bundle = Bundle()
        bundle.putString(PARAM_SIGNUP_METHOD,sign_up_method)
        logEvent(context, EVENT_SIGNUP,bundle)
    }

}


object CardQuestionsAnalytics : BaseAnalytics() {
     const val SCREEN_NAME_SIGNUP_ACTIVATE = "petro-points-sign-up-activate"
     const val FORM_NAME_SIGNUP_ACTIVATE = "Petro Points Sign Up Activate"

    @JvmStatic
    fun logSomethingWentFormError(context: Context) {
        logFormErrorEvent(context, Errors.SOMETHING_WRONG, FORM_NAME_SIGNUP_ACTIVATE)
    }

}

object CardFormAnalytics : BaseAnalytics(){
    const val SCREEN_NAME_ACTIVATE_MATCH_CARD = "activate-match-card"

    @JvmStatic
    fun logCardAlreadyUsedError(context: Context) {
        logErrorEvent(context,Errors.PETRO_POINTS_ALREADY_USED,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

    @JvmStatic
    fun logInvalidCardError(context: Context) {
        logErrorEvent(context,Errors.INVALID_CARD,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
    }

}