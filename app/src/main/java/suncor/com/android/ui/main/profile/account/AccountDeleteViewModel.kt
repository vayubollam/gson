package suncor.com.android.ui.main.profile.account

import androidx.lifecycle.ViewModel
import suncor.com.android.mfp.SessionManager
import javax.inject.Inject

class AccountDeleteViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel(){

    public fun getUserName(): String? {
        return sessionManager.profile!!.firstName
    }

}