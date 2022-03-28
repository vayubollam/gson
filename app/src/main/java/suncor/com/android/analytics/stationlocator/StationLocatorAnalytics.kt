package suncor.com.android.analytics.stationlocator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseParams




const val FORM_NAME_GAS_STATION_LOCATIONS = "Gas Station Locations"

object StationsAnalytics : BaseAnalytics(){
    private const val SCREEN_NAME_LOCATION_FILTER = "my-petro-points-gas-station-locations-filter"
    private const val SCREEN_NAME_LOCATION_LOADING = "gas-station-locations-loading"

    @JvmStatic
    fun logFilterLocationScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_LOCATION_FILTER)
    }

    @JvmStatic
    fun logFiltersApplied(context: Context, location: String, filterList: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.LOCATION, location)
        bundle.putString(BaseParams.FILTERS_APPLIED, filterList)
        logEvent(context, BaseEvents.LOCATION_SEARCH,bundle = bundle)
    }

    @JvmStatic
    fun logLoadingGasStationScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_LOCATION_LOADING)

    }

    @JvmStatic
    fun logLocationAccessAlertShown(context: Context,title:String) {
        logAlertShown(context,title, FORM_NAME_GAS_STATION_LOCATIONS)
    }

    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(context,
            title,
            buttonText,
            FORM_NAME_GAS_STATION_LOCATIONS
        )
    }

}
