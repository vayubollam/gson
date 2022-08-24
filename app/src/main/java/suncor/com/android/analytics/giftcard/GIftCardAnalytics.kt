package suncor.com.android.analytics.giftcard

import android.content.Context
import android.os.Bundle
import suncor.com.android.analytics.BaseAnalytics
import suncor.com.android.analytics.BaseEvents
import suncor.com.android.analytics.BaseParams

// Common Constant of the module goes into file level
const val REDEEM_FOR = "Redeem for "
const val E_GIFT_CARD = " eGift card"

object RewardsSignedInAnalytics : BaseAnalytics(){
    const val REWARDS_SIGNED_IN_SCREEN_NAME = "my-petro-points-redeem-info-general"
}

object MerchantDetailsAnalytics : BaseAnalytics()

object RedeemReceiptAnalytics : BaseAnalytics() {

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
}

object RewardsGuestAnalytics : BaseAnalytics(){
    const val REWARDS_GUEST_FORM_NAME = "Discover Petro-Points Guest"
}

object GiftCardValueConfirmationAnalytics : BaseAnalytics(){
    const val CLICK_TO_REDEEM = "Click to redeem"
}