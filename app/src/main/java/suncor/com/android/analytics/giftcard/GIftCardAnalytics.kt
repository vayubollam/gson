package suncor.com.android.analytics.giftcard

import android.app.Activity
import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseParams
import suncor.com.android.model.merchants.EGift



object RewardsSignedInAnalytics : BaseAnalytics(){

    const val REWARDS_SIGNED_IN_SCREEN_NAME = "my-petro-points-redeem-info-general"

}

object MerchantDetailsAnalytics : BaseAnalytics() {

     const val SCREEN_NAME_MERCHANT_DETAILS = "my-petro-points-redeem-info-"
}

object RedeemReceiptAnalytics : BaseAnalytics() {

    const val SCREEN_NAME_REDEEM_RECEIPT = "my-petro-points-redeem-info-"
    private const val REDEEM_FOR = "Redeem for "
    private const val E_GIFT_CARD = "eGift card"

    @JvmStatic
    fun logRedeemReceiptFormComplete(context: Context, formName: String, formSelection: String) {
        val bundle = Bundle()
        bundle.putString(BaseParams.FORM_SELECTION, formSelection)
        bundle.putString(BaseParams.FORM_NAME, REDEEM_FOR + formName + E_GIFT_CARD)
        logEvent(context, BaseEvents.FORM_COMPLETE, bundle)
    }
}

object RewardsDiscoveryAnalytics : BaseAnalytics() {

    const val SCREEN_NAME_REWARDS_DISCOVERY = "discover-petro-points"
    const val SCREEN_NAME_REWARDS_DISCOVERY_LOADING = "discover-petro-points-loading"

    @JvmStatic
    fun logRewardsGuestFormErrorErrorMessage(
        context: Context,
        errorMessage: String,
        formName: String
    ) {
        logFormErrorEvent(context, errorMessage, GiftCardValueConfirmationAnalytics.REDEEM_FOR + formName + GiftCardValueConfirmationAnalytics.E_GIFT_CARD)
    }

}

object RewardsGuestAnalytics : BaseAnalytics() {

    @JvmStatic
    fun logRewardsGuestFormErrorErrorMessage(
        context: Context,
        errorMessage: String,
        formName: String
    ) {
        logFormErrorEvent(context, errorMessage, GiftCardValueConfirmationAnalytics.REDEEM_FOR + formName + GiftCardValueConfirmationAnalytics.E_GIFT_CARD)
    }
}

object GiftCardValueConfirmationAnalytics : BaseAnalytics() {

    const val SCREEN_NAME_GIFT_CARD_CONFIRMATION = "my-petro-points-redeem-info-"
     const val REDEEM_FOR = "Redeem for "
     const val E_GIFT_CARD = "eGift card"
     const val CLICK_TO_REDEEM = "Click to redeem"


    @JvmStatic
    fun logGiftCardConfirmationErrorMessage(
        context: Context,
        errorMessage: String,
        formName: String
    ) {
        logFormErrorEvent(context, errorMessage, REDEEM_FOR + formName + E_GIFT_CARD)
    }

    @JvmStatic
    fun logAlertShown(context: Context, title: String, formName: String) {
        logAlertDialogShown(context, title, REDEEM_FOR + formName + E_GIFT_CARD)
    }

    @JvmStatic
    fun logAlertInteraction(
        context: Context,
        alertTitle: String,
        alertSelection: String,
        formName: String
    ) {
        logAlertDialogInteraction(context, alertTitle, alertSelection, REDEEM_FOR + formName + E_GIFT_CARD)
    }

    @JvmStatic
    fun logError(context: Context, errorMessage: String) {
        logErrorEvent(context, errorMessage)
    }
}