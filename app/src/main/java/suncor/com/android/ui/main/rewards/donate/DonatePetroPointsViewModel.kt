package suncor.com.android.ui.main.rewards.donate

import androidx.lifecycle.ViewModel
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.cards.CardFormatUtils
import javax.inject.Inject

class DonatePetroPointsViewModel @Inject constructor(val sessionManager: SessionManager) : ViewModel() {

    fun getPetroPoints(): Int {
        try {
            return sessionManager.profile.pointsBalance
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getFormattedPoints(): String{
        return CardFormatUtils.formatBalance(getPetroPoints())
    }

}