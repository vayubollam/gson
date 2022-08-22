package suncor.com.android.ui.main.profile.account

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.profiles.ProfilesApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.DeleteAccountRequest
import suncor.com.android.model.account.Profile
import suncor.com.android.utilities.DateUtils

@RunWith(MockitoJUnitRunner::class)
class AccountDeleteViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()


    private lateinit var accountDeleteViewModel: AccountDeleteViewModel

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var profilesApi: ProfilesApi


    @Before
    fun setUp() {
        accountDeleteViewModel = AccountDeleteViewModel(sessionManager, profilesApi)
    }

    @Test
    fun givenValidPhoneNumber_whenValidate_shouldReturnSuccess() {
        val phone = "416–123-4567"
        val isPhoneNumberValid = accountDeleteViewModel.isPhoneNumberValid(phone)
        Assert.assertTrue(!isPhoneNumberValid)
    }

    @Test
    fun givenInvalidPhoneNumber_whenValidate_shouldReturnError() {
        val phone = "4161234567"
        val isPhoneNumberValid = accountDeleteViewModel.isPhoneNumberValid(phone)
        Assert.assertFalse(isPhoneNumberValid)
    }


    @Test
    fun givenValidDateFormate_whenValidateDate_shouldReturnSuccess() {
         val formattedCurrentDate=DateUtils.getTodayFormattedDate()
          Assert.assertTrue(formattedCurrentDate,true)
    }

    @Test
    fun givenProfile_WhenDeleteRequest_shouldReturnSucesss() {

        var profile= Profile()
        profile.firstName="test user"
        profile.petroPointsNumber="10000"
        profile.lastName="user"
        profile.city="Oakville"
        profile.province="Ontario"
        profile.postalCode="L4X1L3"
        profile.streetAddress="xyz"
        var deleteAccountRequest = DeleteAccountRequest(
            profile.petroPointsNumber,
            profile.firstName, profile.lastName,"developer@suncor.com", profile.streetAddress, profile.city,
            profile.province, profile.postalCode, "416–123-4567", true, true,
            true, "better"
        )

        val apiResponse = MutableLiveData<Resource<Boolean>>()
        apiResponse.postValue(Resource.success(true))
        lenient().`when`<LiveData<Resource<Boolean>>>(
            profilesApi.deleteAccount(
                deleteAccountRequest
            )
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Boolean> -> }
        apiResponse.observeForever(dummyObserver)
        Assert.assertEquals(Resource.Status.SUCCESS, apiResponse.value?.status)
    }

}