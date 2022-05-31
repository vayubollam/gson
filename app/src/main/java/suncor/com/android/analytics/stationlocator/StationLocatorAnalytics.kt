package suncor.com.android.analytics.stationlocator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseParams


const val FORM_NAME_GAS_STATION_LOCATIONS = "Gas Station Locations"

object StationsAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME_LOCATION_FILTER = "my-petro-points-gas-station-locations-filter"
    private const val SCREEN_NAME_LOCATION_LOADING = "gas-station-locations-loading"
    private const val NOT_SET = "(not-set)"

    @JvmStatic
    fun logFilterLocationScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_LOCATION_FILTER)
    }

    @JvmStatic
    fun logFiltersApplied(context: Context, location: String, filterList: String) {

        if(location.isBlank() && filterList.isBlank()) return

        var locationValue = location
        if(location.isBlank()) locationValue = NOT_SET

        var filterValue = filterList
        if(filterValue.isBlank()) filterValue = NOT_SET

        val bundle = Bundle()
        bundle.putString(BaseParams.LOCATION, locationValue)
        bundle.putString(BaseParams.FILTERS_APPLIED, filterValue)
        logEvent(context, BaseEvents.LOCATION_SEARCH, bundle = bundle)
    }

    @JvmStatic
    fun logLoadingGasStationScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_LOCATION_LOADING)

    }

    @JvmStatic
    fun logLocationAccessAlertShown(context: Context, title: String) {
        logAlertDialogShown(context, title, FORM_NAME_GAS_STATION_LOCATIONS)
    }

    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(
            context,
            title,
            buttonText,
            FORM_NAME_GAS_STATION_LOCATIONS
        )
    }

}


object StationDetailsAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME_STATION_DETAIL_HOME = "station-details-home"

    @JvmStatic
    fun logScreenName(activity: Activity) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_STATION_DETAIL_HOME)
    }

    @JvmStatic
    fun logAddToFav(context: Context, location: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.LOCATION, location)
        logEvent(context, BaseEvents.STATION_ADD_TO_FAVOURITE, bundle = bundle)
    }


}


object NearestStationAnalytics : BaseAnalytics() {
    private const val SCREEN_NAME_NEAREST_STATION_LOADING = "offsite-nearest-station-loading"
    private const val SCREEN_NAME_NEAREST_STATION = "offsite-nearest-station"
    private const val FORM_NAME_NEAREST_STATION = "Nearest Station"


    @JvmStatic
    fun logLoadingScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_NEAREST_STATION_LOADING)
    }

    @JvmStatic
    fun logAlertShown(context: Context, alertTitle: String) {
        logAlertDialogShown(context, alertTitle, FORM_NAME_NEAREST_STATION)
    }


    @JvmStatic
    fun logAlertInteraction(context: Context, title: String, buttonText: String) {
        logAlertDialogInteraction(context, title, buttonText, FORM_NAME_NEAREST_STATION)
    }

    @JvmStatic
    fun logNearestStationScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_NEAREST_STATION)
    }


}

object FavouritesAnalytics: BaseAnalytics(){
    private const val SCREEN_NAME_FAV_LOADING = "my-petro-points-gas-station-locations-favourites-loading"
    private const val FORM_NAME_FAV_STATION=  "Favorite Gas Stations"

    @JvmStatic
    fun logLoadingScreenName(activity: Activity) {
        logScreenNameClass(activity, SCREEN_NAME_FAV_LOADING)
    }

    @JvmStatic
    fun logFormError(context: Context, errorMessage: String) {
        logFormErrorEvent(context,errorMessage, FORM_NAME_FAV_STATION)
    }


}