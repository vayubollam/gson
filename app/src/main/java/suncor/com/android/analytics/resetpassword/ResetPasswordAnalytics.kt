package suncor.com.android.analytics.resetpassword

import android.content.Context
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.Errors



object ForgotPasswordAnalytics : BaseAnalytics(){
    private const val FORM_NAME_FORGOT_PASSWORD="Forgot Password"

    @JvmStatic
    fun logError(context: Context) {
        logErrorEvent(context,Errors.SOMETHING_WRONG, FORM_NAME_FORGOT_PASSWORD)
    }

}

object ResetPasswordAnalytics : BaseAnalytics(){
    private const val FORM_NAME_RESET_PASSWORD="Reset Password"

    @JvmStatic
    fun logFormError(context: Context) {
        logFormErrorEvent(context,Errors.SOMETHING_WRONG, FORM_NAME_RESET_PASSWORD)
    }

}

