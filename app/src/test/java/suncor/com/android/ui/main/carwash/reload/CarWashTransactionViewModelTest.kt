package suncor.com.android.ui.main.carwash.reload

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.google.android.gms.wallet.PaymentsClient
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.cards.CardsRepository
import suncor.com.android.data.carwash.CarwashApi
import suncor.com.android.data.payments.PaymentsRepository
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.Profile
import suncor.com.android.model.carwash.reload.TransactionReloadData
import java.lang.Boolean

@RunWith(MockitoJUnitRunner::class)
class CarWashTransactionViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private var cardsLiveData: LiveData<Resource<TransactionReloadData>>? = null

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var cardsRespository: CardsRepository

    @Mock
    private lateinit var paymentsRepository: PaymentsRepository

    @Mock
    private lateinit var settingsApi: SettingsApi

    @Mock
    private lateinit var carwashApi: CarwashApi

    @Mock
    private lateinit var paymentsClient :PaymentsClient

    private lateinit var viewModel: CarwashTransactionViewModel
    val profile = Profile()

    @Before
    fun setUp() {
        viewModel = CarwashTransactionViewModel(
            carwashApi,
            settingsApi,
            paymentsRepository,
            cardsRespository,
            sessionManager
        )


        profile.petroPointsNumber = "706934700054132011"
        profile.pointsBalance = 2500

    }

    @Test
    fun settingApi_successResponse() {
        viewModel.settings
    }

    @Test
    fun givenServerResponse200_whenReloadTransaction_saveData() {
        val cardType = "WAG"
        cardsLiveData  = viewModel.getTransactionData(cardType)

          Assert.assertEquals(Boolean.TRUE, cardsLiveData?.value?.status == Resource.Status.SUCCESS)
    }

    @Test
    fun givenServerResponseError_whenReloadTransaction_saveData() {
        val cardType = "Test"
        cardsLiveData  = viewModel.getTransactionData(cardType)

        Assert.assertEquals(Boolean.FALSE, cardsLiveData?.value?.status == Resource.Status.ERROR)
    }

    @Test
    fun test_get_petro_points_balance() {
        val petroPoints = profile.pointsBalance
        Assert.assertEquals(Boolean.TRUE, petroPoints == 2500)
    }


    @Test
    fun test_is_ready_to_pay_request_for_google_pay_success() {
        val isReadyToPayRequest = viewModel.IsReadyToPayRequestForGooglePay()
        Assert.assertEquals(Boolean.TRUE, paymentsClient.isReadyToPay(isReadyToPayRequest))
    }

    @Test
    fun test_is_ready_to_pay_request_for_google_pay_fail() {
        val isReadyToPayRequest = viewModel.IsReadyToPayRequestForGooglePay()
        Assert.assertEquals(Boolean.FALSE, paymentsClient.isReadyToPay(isReadyToPayRequest))
    }

}