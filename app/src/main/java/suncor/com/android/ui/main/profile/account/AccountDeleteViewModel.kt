package suncor.com.android.ui.main.profile.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import suncor.com.android.data.profiles.ProfilesApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.DeleteAccountRequest
import suncor.com.android.model.account.Profile
import javax.inject.Inject

class AccountDeleteViewModel @Inject constructor(private val sessionManager: SessionManager,
                                                 private val profilesApi: ProfilesApi) : ViewModel(){

    public fun getUserName(): String? {
        return sessionManager.profile!!.firstName
    }

    public fun getProfile(): Profile {
        return sessionManager.profile
    }

    public fun deleteApi(deleteAccountRequest: DeleteAccountRequest): LiveData<Resource<Boolean>> {
        return profilesApi.deleteAccount(deleteAccountRequest);
    }

}