package suncor.com.android.ui.main.rewards.donate

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.cards.CardFormatUtils
import javax.inject.Inject

class DonatePetroPointsViewModel @Inject constructor(val sessionManager: SessionManager) :
    ViewModel() {

    val enableDeduction: ObservableBoolean = ObservableBoolean(false)
    val enableIncrement: ObservableBoolean = ObservableBoolean(true)
    val enableDonation: ObservableBoolean = ObservableBoolean(false)
    val donationPoints: ObservableInt = ObservableInt(0)
    val donateAmount: ObservableInt = ObservableInt(0)
    val formattedDonationPoints: ObservableField<String> =
        ObservableField(getFormattedDonatePoints())


    fun incrementAmount() {
        if (donateAmount.get() == getDonationLimit()) return
        donateAmount.set(donateAmount.get() + 1)
        updateData()
    }

    fun decrementAmount() {
        if (donateAmount.get() <= 0) return
        donateAmount.set(donateAmount.get() - 1)
        updateData()
    }

    private fun updateData() {
        donationPoints.set(getPointsFromDollar())
        formattedDonationPoints.set(getFormattedDonatePoints())
        if(donateAmount.get()>0){
            enableDonation.set(true)
            enableDeduction.set(true)
        }else{
            enableDonation.set(false)
            enableDeduction.set(false)
        }
    }




    private fun getPointsFromDollar(): Int {
        return donateAmount.get() * 1000
    }

    private fun getDonationLimit(): Int {
        return getPetroPoints() / 1000
    }

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

    private fun getFormattedDonatePoints(): String {
        return CardFormatUtils.formatBalance(donationPoints.get());
    }

}