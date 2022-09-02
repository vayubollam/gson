package suncor.com.android.ui.main.rewards.donatepetropoints.donate

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import suncor.com.android.data.redeem.donate.DonateRepository
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.redeem.response.Program
import suncor.com.android.ui.common.cards.CardFormatUtils
import java.util.*
import javax.inject.Inject

class DonatePetroPointsViewModel @Inject constructor(
    val sessionManager: SessionManager,
    val repository: DonateRepository
) :
    ViewModel() {

    lateinit var program: Program
    val enableDeduction: ObservableBoolean = ObservableBoolean(false)
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val enableIncrement: ObservableBoolean = ObservableBoolean(getPetroPoints() >= 1000)
    val enableDonation: ObservableBoolean = ObservableBoolean(false)
    private val donationPoints: ObservableInt = ObservableInt(0)
    val donateAmount: ObservableInt = ObservableInt(0)
    val formattedDonationPoints: ObservableField<String> =
        ObservableField(getFormattedDonatePoints())
    val isFrench = ((Locale.getDefault() == Locale.CANADA_FRENCH))

    val formattedDonationAmount: ObservableField<String> =
        ObservableField(getFormattedDonateAmount())

    fun donatePoints(): MutableLiveData<Resource<Unit>> {
        return repository.makeDonateCall(
            program.programId,
            getPetroPointsId(),
            getPointsFromDollar()
        )
    }

    fun incrementAmount() {
        if (donateAmount.get() >= getDonationLimit()) return
        donateAmount.set(donateAmount.get() + 1)
        updateData()
    }

    fun decrementAmount() {
        if (donateAmount.get() <= 0) return
        donateAmount.set(donateAmount.get() - 1)
        updateData()
    }

    fun updateData(updateFromKeyboard: Boolean = false) {
        donationPoints.set(getPointsFromDollar())
        formattedDonationPoints.set(getFormattedDonatePoints())
        if (donateAmount.get() > 0) {
            enableDonation.set(true)
            enableDeduction.set(true)
        } else {
            enableDonation.set(false)
            enableDeduction.set(false)
        }

        if (donateAmount.get() >= getDonationLimit()) {
            enableIncrement.set(false)
        } else {
            enableIncrement.set(true)
        }

        if (!updateFromKeyboard)
            formattedDonationAmount.set(getFormattedDonateAmount())
    }

    private fun getFormattedDonateAmount(): String {
        return if (donateAmount.get() > 0) donateAmount.get().toString()
        else ""
    }


    fun getPointsFromDollar(): Int {
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

    private fun getPetroPointsId(): String {
        try {
            return sessionManager.profile.petroPointsNumber
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    fun getFormattedPoints(): String {
        return CardFormatUtils.formatBalance(getPetroPoints())
    }

    private fun getFormattedDonatePoints(): String {
        return CardFormatUtils.formatBalance(donationPoints.get())
    }

    fun rectifyValuesOnKeyboardGone() {
        if (donateAmount.get() < 0) donateAmount.set(0)
        if (donateAmount.get() > getDonationLimit()) donateAmount.set(getDonationLimit())
        updateData()
    }

}