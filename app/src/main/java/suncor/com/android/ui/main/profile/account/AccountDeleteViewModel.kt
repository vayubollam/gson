package suncor.com.android.ui.main.profile.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import suncor.com.android.R
import suncor.com.android.data.profiles.ProfilesApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.DeleteAccountRequest
import suncor.com.android.model.account.Profile
import suncor.com.android.ui.common.input.PhoneInputField
import suncor.com.android.utilities.DateUtils
import javax.inject.Inject

class AccountDeleteViewModel @Inject constructor(private val sessionManager: SessionManager,
                                                 private val profilesApi: ProfilesApi) : ViewModel(){

    val phoneField =
        PhoneInputField(R.string.profile_personnal_informations_phone_field_invalid_format)

    public fun getUserName(): String? {
        return sessionManager.profile.firstName
    }

    public fun refreshProfile() {
        sessionManager.profile!!.accountDeleteDateTime = DateUtils.getTodayFormattedDate();
    }

    public fun getProfile(): Profile {
        return sessionManager.profile
    }

    public fun deleteApi(deleteAccountRequest: DeleteAccountRequest): LiveData<Resource<Boolean>> {
        return profilesApi.deleteAccount(deleteAccountRequest);
    }

    fun isPhoneNumberValid(text : String) : Boolean{
        if (!phoneField.isValid) {
            phoneField.showError = true
            return false
        }
        return true
    }



}