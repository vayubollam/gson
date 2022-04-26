package suncor.com.android.analytics.misc

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseParams


object MainActivityAnalytics : BaseAnalytics() {
    @JvmStatic
    fun logNavigation(context: Context, actionMenu: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.ACTIONBAR_TAP,actionMenu)
        logEvent(context,BaseEvents.NAVIGATION,bundle)
    }

    const val FORM_NAME_HOME = "Home"





}