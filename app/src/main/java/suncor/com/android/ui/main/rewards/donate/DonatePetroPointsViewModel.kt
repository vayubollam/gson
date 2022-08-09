package suncor.com.android.ui.main.rewards.donate

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.cards.CardFormatUtils
import javax.inject.Inject

class DonatePetroPointsViewModel @Inject constructor(val sessionManager: SessionManager) :
    ViewModel() {

    var enableDeduction: ObservableBoolean = ObservableBoolean(false)
    var enableIncrement: ObservableBoolean = ObservableBoolean(true)
    var enableDonation: ObservableBoolean = ObservableBoolean(false)
    var donationPoints: ObservableInt = ObservableInt(0)
    var donateAmount: ObservableInt = ObservableInt(0)

    fun getPetroPoints(): Int {
        try {
            return sessionManager.profile.pointsBalance
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getFormattedPoints(): String {
        return CardFormatUtils.formatBalance(getPetroPoints())
    }

    fun getFormattedDonatePoints(): String {
        return CardFormatUtils.formatBalance(donationPoints.get());
    }

    fun incrementAmount() {
        if (donateAmount.get() == getDonationLimit()) return
        donateAmount.set(donateAmount.get() + 1)
        updateData()
    }

    private fun updateData() {
        donationPoints.set(getPointsFromDollar())
        donationPoints.notifyChange()
    }

    private fun getPointsFromDollar(): Int {
        return donateAmount.get() * 1000
    }

    fun decrementAmount() {
        if (donateAmount.get() <= 0) return
        donateAmount.set(donateAmount.get() - 1)
        updateData()
    }

    private fun getDonationLimit(): Int {
        return getPetroPoints() / 1000
    }

}