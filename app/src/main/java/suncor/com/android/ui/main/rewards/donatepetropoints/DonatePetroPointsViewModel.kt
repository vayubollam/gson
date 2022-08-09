package suncor.com.android.ui.main.rewards.donatepetropoints

import androidx.lifecycle.ViewModel
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.cards.CardFormatUtils
import javax.inject.Inject

class DonatePetroPointsViewModel @Inject constructor(
   private val  sessionManager: SessionManager
) : ViewModel(){


   fun getPetroPointsBalance(): String? {
      return CardFormatUtils.formatBalance(sessionManager.profile.pointsBalance)
   }
}