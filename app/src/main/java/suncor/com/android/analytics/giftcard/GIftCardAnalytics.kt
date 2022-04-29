package suncor.com.android.analytics.giftcard

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseParams

object RewardsSignedInAnalytics : BaseAnalytics()

object MerchantDetailsAnalytics : BaseAnalytics() {

    private const val SCREEN_NAME_MERCHANT_DETAILS = "my-petro-points-redeem-info-"

    @JvmStatic
    fun logMerchantDetailsScreenName(activity: Activity, cardName: String) {
        logScreenNameClass(activity, screenName = SCREEN_NAME_MERCHANT_DETAILS + cardName)
    }
}

object RedeemReceiptAnalytics : BaseAnalytics() {

    private const val SCREEN_NAME_REDEEM_RECEIPT = "my-petro-points-redeem-info-"

    @JvmStatic
    fun logMerchantDetailsScreenName(activity: Activity, cardName: String) {
        logScreenNameClass(
            activity,
            screenName = SCREEN_NAME_REDEEM_RECEIPT + cardName
        )
    }

    @JvmStatic
    fun logRedeemReceiptFormComplete(context: Context, formName: String, formSelection: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.FORM_SELECTION, formSelection)
        bundle.putString(BaseParams.FORM_NAME, formName)
        logEvent(context, BaseEvents.FORM_COMPLETE, bundle)
    }
}

object RewardsDiscoveryAnalytics : BaseAnalytics() {

    private const val SCREEN_NAME_REWARDS_DISCOVERY = "discover-petro-points"
    private const val SCREEN_NAME_REWARDS_DISCOVERY_LOADING = "discover-petro-points-loading"

    @JvmStatic
    fun logRewardDiscoveryScreenName(activity: Activity) {
        logScreenNameClass(
            activity,
            screenName = SCREEN_NAME_REWARDS_DISCOVERY
        )
    }

    @JvmStatic
    fun logRewardDiscoveryLoadingScreenName(activity: Activity) {
        logScreenNameClass(
            activity,
            screenName = SCREEN_NAME_REWARDS_DISCOVERY_LOADING
        )
    }
}

object RewardsGuestAnalytics : BaseAnalytics() {

    private const val SCREEN_NAME_REWARDS_GUEST = "discover-petro-points"
    private const val SCREEN_NAME_REWARDS_DISCOVERY_LOADING = "discover-petro-points-loading"

    @JvmStatic
    fun logRewardGuestScreenName(activity: Activity) {
        logScreenNameClass(
            activity,
            screenName = SCREEN_NAME_REWARDS_GUEST
        )
    }

    @JvmStatic
    fun logRewardGuestLoadingScreenName(activity: Activity) {
        logScreenNameClass(
            activity,
            screenName = SCREEN_NAME_REWARDS_DISCOVERY_LOADING
        )
    }
}

object GiftCardValueConfirmationAnalytics : BaseAnalytics() {

    private const val SCREEN_NAME_GIFT_CARD_CONFIRMATION = "my-petro-points-redeem-info-"

    @JvmStatic
    fun logGiftCardValueConfirmationScreenName(activity: Activity, cardName: String) {
        logScreenNameClass(
            activity,
            screenName = SCREEN_NAME_GIFT_CARD_CONFIRMATION + cardName
        )
    }

    @JvmStatic
    fun logGiftCardConfirmationErrorMessage(
        context: Context,
        errorMessage: String,
        formName: String
    ) {
        logFormErrorEvent(context, errorMessage, formName)
    }

    @JvmStatic
    fun logAlertShown(context: Context, title: String, formName: String) {
        logAlertDialogShown(context, title, formName)
    }

    @JvmStatic
    fun logAlertInteraction(
        context: Context,
        alertTitle: String,
        alertSelection: String,
        formName: String
    ) {
        logAlertDialogInteraction(context, alertTitle, alertSelection, formName)
    }

    @JvmStatic
    fun logError(context: Context, errorMessage: String) {
        logErrorEvent(context, errorMessage)
    }
}